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

//TODO add options for amount of participants, modes, rules, score etc
public class TournamentBuilder {
	private int teamSize = 1;
	private int teamAmount = 2;
	private int rounds = 3;
	private int scoreToWin = 3;
	private long duration = 0;

	public boolean isValid() {
		// Validate tournament builder
		return true;
	}

	public Tournament build() {
		return null;
	}
}
