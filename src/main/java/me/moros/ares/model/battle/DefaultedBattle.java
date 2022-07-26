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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.participant.Participant;

public class DefaultedBattle implements Battle {
  private final Participant participant;
  private final BattleData data;

  DefaultedBattle(Participant participant) {
    this(participant, new BattleScore(1));
  }

  DefaultedBattle(Participant participant, BattleScore score) {
    this.participant = participant;
    this.data = BattleData.create(score);
  }

  @Override
  public Map<Participant, BattleScore> scores() {
    return Map.of(participant, data.score());
  }

  @Override
  public Stream<Participant> participants() {
    return Stream.of(participant);
  }

  @Override
  public Entry<Participant, BattleScore> topEntry() {
    return Map.entry(participant, data.score());
  }

  @Override
  public boolean start(BattleManager manager, BattleRules rules) {
    return false;
  }

  @Override
  public CompletableFuture<Void> runSteps(BattleManager manager) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public Map<Participant, BattleData> complete(BattleManager manager) {
    return Map.of(participant, data);
  }

  @Override
  public Stage stage() {
    return Stage.COMPLETED;
  }

  @Override
  public Participant testVictory() {
    return participant;
  }

  @Override
  public void forEachEntry(BiConsumer<Participant, BattleData> consumer) {
    consumer.accept(participant, data);
  }

  @Override
  public Iterator<Participant> iterator() {
    return List.of(participant).iterator();
  }
}
