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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import me.moros.atlas.checker.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO link to specific arena
public class Battle {
	private final Map<Participant, BattleScore> battleParties;

	private boolean started = false;

	Battle(@NonNull Collection<@NonNull Participant> battleParties) {
		this(battleParties, BattleScore.ZERO);
	}

	Battle(@NonNull Collection<@NonNull Participant> battleParties, @NonNull BattleScore startingScore) {
		this.battleParties = battleParties.stream().collect(Collectors.toConcurrentMap(Function.identity(), p -> startingScore));
	}

	public @NonNull Collection<@NonNull Participant> getParticipants() {
		return ImmutableSet.copyOf(battleParties.keySet());
	}

	public @NonNull BattleScore getScore(@NonNull Participant participant) {
		return battleParties.getOrDefault(participant, BattleScore.ZERO);
	}

	public boolean start() {
		if (started) return false;
		// TODO add preparation steps
		return started = true;
	}

	public @NonNull Map<@NonNull Participant, @NonNull BattleScore> complete() {
		// TODO cleanup after battle
		return ImmutableMap.copyOf(battleParties);
	}
}
