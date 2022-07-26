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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Battle extends Iterable<Participant> {
  Map<Participant, BattleScore> scores();

  Stream<Participant> participants();

  Entry<Participant, BattleScore> topEntry();

  boolean start(BattleManager manager, BattleRules rules);

  CompletableFuture<Void> runSteps(BattleManager manager);

  Map<Participant, BattleData> complete(BattleManager manager);

  Stage stage();

  @Nullable Participant testVictory();

  void forEachEntry(BiConsumer<Participant, BattleData> consumer);

  static Optional<Battle> createBattle(Collection<Participant> parties) {
    if (!parties.isEmpty() && parties.stream().allMatch(Participant::isValid)) {
      Collection<LivingEntity> col = parties.stream().flatMap(Participant::members).toList();
      int unique = Set.copyOf(col).size();
      if (col.size() == unique) {
        Battle battle = unique > 1 ? new BattleImpl(parties) : new DefaultedBattle(parties.iterator().next());
        return Optional.of(battle);
      }
    }
    return Optional.empty();
  }

  enum Stage {
    CREATED(NamedTextColor.WHITE),
    STARTING(NamedTextColor.RED),
    ONGOING(NamedTextColor.YELLOW),
    COMPLETED(NamedTextColor.GREEN);

    private final TextColor color;

    Stage(TextColor color) {
      this.color = color;
    }

    public TextColor color() {
      return color;
    }
  }
}
