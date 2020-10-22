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
import me.moros.atlas.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class DummyParticipant implements Participant {
	static final DummyParticipant INSTANCE = new DummyParticipant();

	private DummyParticipant() {
	}

	@Override
	public @NonNull Collection<@NonNull Player> getPlayers() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasPlayer(@NonNull Player player) {
		return false;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Optional<Battle> matchWith(@NonNull Participant other) {
		return Optional.empty();
	}

	@Override
	public @NonNull Iterable<? extends Audience> audiences() {
		return Collections.emptyList();
	}
}
