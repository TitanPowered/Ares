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
import me.moros.atlas.cf.checker.nullness.qual.NonNull;
import me.moros.atlas.cf.checker.nullness.qual.Nullable;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO link to specific arena
public class Battle {
	private final Map<Participant, BattleScore> parties;
	private ScoreEntry top;

	private boolean started = false;

	private Battle(Collection<Participant> parties) {
		this(parties, BattleScore.ZERO);
	}

	private Battle(Collection<Participant> parties, BattleScore startingScore) {
		this.parties = parties.stream().collect(Collectors.toConcurrentMap(Function.identity(), p -> startingScore));
	}

	public @NonNull Map<Participant, BattleScore> getScores() {
		return ImmutableMap.copyOf(parties);
	}

	public @NonNull Collection<Participant> getParticipants() {
		return ImmutableSet.copyOf(parties.keySet());
	}

	public boolean changeScore(@NonNull Participant participant, @NonNull BattleScore score) {
		BattleScore prev = parties.get(participant);
		if (prev == null || prev.getScore() >= score.getScore()) return false;
		if (score.getScore() > top.getBattleScore().getScore()) top = new ScoreEntry(participant, score);
		parties.put(participant, score);
		return true;
	}

	public @Nullable ScoreEntry getTopEntry() {
		return top;
	}

	public boolean start() {
		if (started) return false;
		// TODO add preparation steps
		return started = true;
	}

	public @NonNull Map<Participant, BattleScore> complete() {
		// TODO cleanup after battle
		return getScores();
	}

	public static Optional<Battle> createBattle(@NonNull Collection<Participant> parties) {
		Set<LivingEntity> uniques = new HashSet<>();
		Collection<Participant> filteredParties = new HashSet<>();
		for (Participant participant : parties) {
			if (!participant.isValid()) continue;
			filteredParties.add(participant);
			for (LivingEntity entity : participant.getMembers()) {
				if (uniques.contains(entity)) return Optional.empty();
				uniques.add(entity);
			}
		}
		if (filteredParties.isEmpty()) return Optional.empty();
		return Optional.of(new Battle(filteredParties));
	}
}
