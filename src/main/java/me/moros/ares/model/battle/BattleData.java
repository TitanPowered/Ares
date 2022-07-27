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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import me.moros.ares.model.battle.BattleStat.Keys;
import me.moros.ares.model.participant.Participant;

public class BattleData implements Comparable<BattleData> {
  private final Participant participant;
  private final Map<String, Double> data;
  private final Consumer<BattleData> onScoreChange;
  private BattleScore score;

  BattleData(Participant participant, BattleScore score, Consumer<BattleData> onScoreChange) {
    this.participant = participant;
    this.data = new ConcurrentHashMap<>();
    this.onScoreChange = onScoreChange;
    for (BattleStat stat : Keys.VALUES) {
      this.data.put(stat.key(), stat.defaultValue());
    }
    this.score = score;
  }

  public Participant participant() {
    return participant;
  }

  public BattleScore score() {
    return score;
  }

  public BattleScore score(UnaryOperator<BattleScore> function) {
    score = function.apply(score);
    onScoreChange.accept(this);
    return score;
  }

  public double value(BattleStat stat) {
    return data.getOrDefault(stat.key(), stat.defaultValue());
  }

  public BattleData value(BattleStat stat, DoubleUnaryOperator function) {
    data.compute(stat.key(), (k, v) -> v == null ? stat.defaultValue() : function.applyAsDouble(v));
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BattleData other) {
      return participant.equals(other.participant);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return participant.hashCode();
  }

  @Override
  public int compareTo(BattleData o) {
    return score.compareTo(o.score);
  }
}
