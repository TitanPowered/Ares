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

import me.moros.atlas.checker.checker.nullness.qual.NonNull;
import me.moros.atlas.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public interface Participant extends ForwardingAudience {
	default boolean hasPlayer(@NonNull Player player) {
		return getPlayers().contains(player);
	}

	default boolean isValid() {
		return getPlayers().stream().anyMatch(Player::isOnline);
	}

	default Optional<Battle> matchWith(@NonNull Participant other) { // TODO add support for multi party (free for all) matches
		if (this.equals(other)) return Optional.empty();
		if (!isValid() || !other.isValid()) return Optional.empty();
		return Optional.of(new Battle(Arrays.asList(this, other)));
	}

	@NonNull Collection<@NonNull Player> getPlayers();

	static @NonNull Participant dummy() {
		return DummyParticipant.INSTANCE;
	}
}
