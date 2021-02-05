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
import me.moros.atlas.cf.checker.index.qual.Positive;
import me.moros.atlas.cf.checker.nullness.qual.NonNull;
import me.moros.atlas.cf.checker.nullness.qual.Nullable;

public class TimedVictory implements BattleVictory {
	private final long endTime;

	public TimedVictory(@Positive long duration) {
		this.endTime = System.currentTimeMillis() + duration;
	}

	public @Nullable Participant apply(@NonNull Battle battle) {
		ScoreEntry top = battle.getTopEntry();
		if (top != null && System.currentTimeMillis() >= endTime) return top.getParticipant();
		return null;
	}
}
