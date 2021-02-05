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

import me.moros.atlas.cf.checker.nullness.qual.NonNull;

public class ScoreEntry {
	private final Participant participant;
	private final BattleScore score;

	public ScoreEntry(@NonNull Participant participant, @NonNull BattleScore score) {
		this.participant = participant;
		this.score = score;
	}

	public @NonNull Participant getParticipant() {
		return participant;
	}

	public @NonNull BattleScore getBattleScore() {
		return score;
	}
}
