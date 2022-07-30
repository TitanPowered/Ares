/*
 * Copyright 2020-2022 Moros
 *
 * This file is part of Ares.
 *
 * Ares is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ares is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Ares. If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.ares.model.battle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.ValueReference;
import me.moros.ares.model.participant.CachedParticipants;
import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Battle extends Iterable<BattleData>, ForwardingAudience {
  JoinConfiguration SEP = JoinConfiguration.separator(Component.text(" vs "));

  CachedParticipants cache();

  BattleData topEntry();

  default boolean start(BattleManager manager, BattleRules rules) {
    return start(manager, rules, null);
  }

  boolean start(BattleManager manager, BattleRules rules, @Nullable Consumer<Battle> consumer);

  CompletableFuture<Void> runSteps(BattleManager manager);

  Collection<BattleData> complete(BattleManager manager);

  Stage stage();

  @Nullable Participant testVictory();

  static Battle createBattle(Collection<Participant> parties) {
    return createBattle(parties, new ValueReference<>());
  }

  static Battle createBattle(Collection<Participant> parties, ValueReference<Participant> error) {
    Set<Participant> unique = Participant.unique(parties, error);
    int size = unique.size();
    if (size < 1) {
      throw new RuntimeException("A battle requires at least 2 unique participants! Provided " + size);
    }
    if (size == 1) {
      new DefaultedBattle(unique.iterator().next());
    }
    return new BattleImpl(unique);
  }

  default Component details() {
    BattleData top = topEntry();
    Predicate<BattleData> style = d -> d.score().compareTo(BattleScore.ZERO) > 0 && d.compareTo(top) >= 0;
    Collection<Component> participants = new ArrayList<>();
    forEach(d -> participants.add(Component.text(d.participant().name(), color(style.test(d)))));
    return Component.join(SEP, participants).color(stage().color());
  }

  private TextColor color(boolean top) {
    return top ? NamedTextColor.GREEN : NamedTextColor.RED;
  }

  @Override
  default @NonNull Iterable<? extends Audience> audiences() {
    return cache().audiences();
  }

  enum Stage {
    CREATED(NamedTextColor.GRAY),
    STARTING(NamedTextColor.YELLOW),
    ONGOING(NamedTextColor.YELLOW),
    COMPLETED(NamedTextColor.GRAY);

    private final TextColor color;

    Stage(TextColor color) {
      this.color = color;
    }

    public TextColor color() {
      return color;
    }
  }
}
