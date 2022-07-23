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

package me.moros.ares.model.victory;

import me.moros.ares.model.Battle;
import me.moros.ares.model.Participant;
import me.moros.ares.model.ScoreEntry;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ScoreVictory implements BattleVictory {
  private final int score;

  public ScoreVictory(@Positive int score) {
    this.score = score;
  }

  public @Nullable Participant apply(Battle battle) {
    ScoreEntry top = battle.topEntry();
    if (top != null && top.getBattleScore().getScore() >= score) return top.getParticipant();
    return null;
  }
}
