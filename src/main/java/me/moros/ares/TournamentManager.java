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

package me.moros.ares;

import com.google.common.collect.ImmutableSet;
import me.moros.ares.model.Tournament;
import me.moros.atlas.cf.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class TournamentManager {
	private final Map<String, Tournament> tournaments;

	public TournamentManager() {
		tournaments = new ConcurrentHashMap<>();
	}

	public int getTournamentCount() {
		return tournaments.size();
	}

	public Optional<Tournament> getTournament(@NonNull String name) {
		return Optional.ofNullable(tournaments.get(name.toLowerCase()));
	}

	public @NonNull Collection<String> getTournamentNames() {
		return ImmutableSet.copyOf(tournaments.keySet());
	}

	public @NonNull Collection<Tournament> getTournaments() {
		return ImmutableSet.copyOf(tournaments.values());
	}

	public void createTournament(@NonNull Tournament tournament) {
		tournaments.put(tournament.getName().toLowerCase(), tournament);
	}
}
