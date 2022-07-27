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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.participant.CachedParticipants;
import me.moros.ares.model.participant.Participant;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultedBattle implements Battle {
  private final BattleData data;
  private final CachedParticipants cache;

  DefaultedBattle(Participant participant) {
    this(participant, new BattleScore(1));
  }

  DefaultedBattle(Participant participant, BattleScore score) {
    this.cache = new CachedParticipants(Set.of(participant));
    this.data = new BattleData(participant, score, s -> {
    });
  }

  @Override
  public CachedParticipants cache() {
    return cache;
  }

  @Override
  public BattleData topEntry() {
    return data;
  }

  @Override
  public boolean start(BattleManager manager, BattleRules rules, @Nullable Consumer<Battle> consumer) {
    if (consumer != null) {
      consumer.accept(this);
    }
    return true;
  }

  @Override
  public CompletableFuture<Void> runSteps(BattleManager manager) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public Collection<BattleData> complete(BattleManager manager) {
    return List.of(data);
  }

  @Override
  public Stage stage() {
    return Stage.COMPLETED;
  }

  @Override
  public Participant testVictory() {
    return data.participant();
  }

  @Override
  public Iterator<BattleData> iterator() {
    return List.of(data).iterator();
  }
}
