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

package me.moros.ares.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.victory.BattleVictory;

public class DefaultedBattle implements Battle {
  private final Participant participant;
  private final BattleScore score;

  DefaultedBattle(Participant participant) {
    this(participant, BattleScore.ZERO);
  }

  DefaultedBattle(Participant participant, BattleScore score) {
    this.participant = participant;
    this.score = score;
  }

  @Override
  public Map<Participant, BattleScore> scores() {
    return Map.of(participant, score);
  }

  @Override
  public Stream<Participant> participants() {
    return Stream.of(participant);
  }

  @Override
  public boolean setScore(Participant participant, UnaryOperator<BattleScore> function) {
    return false;
  }

  @Override
  public Entry<Participant, BattleScore> topEntry() {
    return Map.entry(participant, score);
  }

  @Override
  public boolean start(BattleManager manager, BattleVictory condition) {
    return false;
  }

  @Override
  public Map<Participant, BattleScore> complete(BattleManager manager) {
    return scores();
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
  public Iterator<Participant> iterator() {
    return Collections.emptyIterator();
  }
}
