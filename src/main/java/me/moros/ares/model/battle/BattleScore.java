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

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public final class BattleScore implements Comparable<BattleScore> {
  public static final Comparator<BattleScore> COMPARATOR = Comparator.comparingInt(BattleScore::value);
  public static final BattleScore ZERO = new BattleScore(0);

  private final int value;

  public BattleScore(int value) {
    if (value < 0) {
      throw new IllegalArgumentException("Score cannot be negative");
    }
    this.value = value;
  }

  public BattleScore increment() {
    return increment(1);
  }

  public BattleScore increment(int value) {
    return (value > 0) ? new BattleScore(this.value + value) : this;
  }

  public BattleScore add(BattleScore other) {
    return increment(other.value);
  }

  private int value() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public int compareTo(@NotNull BattleScore o) {
    return COMPARATOR.compare(this, o);
  }
}
