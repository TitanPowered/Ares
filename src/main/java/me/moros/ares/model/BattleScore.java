/*
 *   Copyright 2020 Moros <https://github.com/PrimordialMoros>
 *
 *    This file is part of Ares.
 *
 *   Ares is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Ares is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Ares.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.ares.model;

public final class BattleScore {
  public static final BattleScore ZERO = new BattleScore(0);

  private final int score;

  private BattleScore(int score) {
    this.score = score;
  }

  public BattleScore increment() {
    return increment(1);
  }

  public BattleScore increment(int value) {
    return (value > 0) ? new BattleScore(score + value) : this;
  }

  public BattleScore add(BattleScore other) {
    return (other.score > 0) ? new BattleScore(score + other.score) : this;
  }

  public int getScore() {
    return score;
  }
}
