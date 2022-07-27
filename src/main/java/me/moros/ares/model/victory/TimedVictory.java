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

package me.moros.ares.model.victory;

import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.participant.Participant;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TimedVictory implements BattleVictory {
  private final long endTime;

  private TimedVictory(long duration) {
    this.endTime = System.currentTimeMillis() + duration;
  }

  public @Nullable Participant apply(Battle battle) {
    if (System.currentTimeMillis() >= endTime) {
      return battle.topEntry().participant();
    }
    return null;
  }

  public static BattleVictory of(long duration) {
    if (duration <= 0) {
      return b -> null;
    }
    return new TimedVictory(duration);
  }
}
