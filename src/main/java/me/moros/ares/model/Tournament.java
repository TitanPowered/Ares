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
import me.moros.atlas.kyori.adventure.text.Component;

import java.util.Collection;

public interface Tournament {
	@NonNull String getName();

	@NonNull Component getDisplayName();

	boolean isOpen();

	void start();

	void finish();

	boolean addParticipant(@NonNull Participant participant);

	boolean removeParticipant(@NonNull Participant participant);

	default boolean hasParticipant(@NonNull Participant participant) {
		return getParticipants().contains(participant);
	}

	@NonNull Collection<@NonNull Participant> getParticipants();

	boolean addBattle(@NonNull Battle battle);

	@NonNull Collection<@NonNull Battle> getCurrentBattles();
}
