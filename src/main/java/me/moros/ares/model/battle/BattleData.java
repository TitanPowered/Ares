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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import me.moros.ares.model.battle.BattleStat.Keys;

public class BattleData implements Comparable<BattleData> {
  private final Map<String, Double> data;
  private BattleScore score;

  private BattleData(BattleScore score) {
    data = new ConcurrentHashMap<>();
    for (BattleStat stat : Keys.VALUES) {
      data.put(stat.key(), stat.defaultValue());
    }
    this.score = score;
  }

  public BattleScore score() {
    return score;
  }

  public BattleData score(UnaryOperator<BattleScore> function) {
    this.score = function.apply(score);
    return this;
  }

  public double value(BattleStat stat) {
    return data.getOrDefault(stat.key(), stat.defaultValue());
  }

  public BattleData value(BattleStat stat, DoubleUnaryOperator function) {
    data.compute(stat.key(), (k, v) -> v == null ? stat.defaultValue() : function.applyAsDouble(v));
    return this;
  }

  public static BattleData create() {
    return new BattleData(BattleScore.ZERO);
  }

  public static BattleData create(BattleScore score) {
    return new BattleData(Objects.requireNonNull(score));
  }

  @Override
  public int compareTo(BattleData o) {
    return score.compareTo(o.score);
  }
}
