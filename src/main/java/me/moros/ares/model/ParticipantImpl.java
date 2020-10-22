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

import com.google.common.collect.ImmutableSet;
import me.moros.ares.Ares;
import me.moros.atlas.cf.checker.nullness.qual.NonNull;
import me.moros.atlas.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParticipantImpl implements Participant {
	private final Map<Player, Audience> players;

	private ParticipantImpl(@NonNull Player player) {
		this.players = Collections.singletonMap(player, Ares.getAudiences().player(player));
	}

	private ParticipantImpl(@NonNull Collection<@NonNull Player> players) {
		this.players = players.stream().collect(Collectors.toConcurrentMap(Function.identity(), p -> Ares.getAudiences().player(p)));
	}

	@Override
	public @NonNull Collection<@NonNull Player> getPlayers() {
		return ImmutableSet.copyOf(players.keySet());
	}

	@Override
	public @NonNull Iterable<? extends Audience> audiences() {
		return ImmutableSet.copyOf(players.values());
	}

	public static @NonNull Participant of(@NonNull Player player) {
		return player.isOnline() ? new ParticipantImpl(player) : Participant.dummy();
	}

	public static @NonNull Participant of(@NonNull Collection<@NonNull Player> players) {
		return players.isEmpty() ? Participant.dummy() : new ParticipantImpl(players);
	}
}
