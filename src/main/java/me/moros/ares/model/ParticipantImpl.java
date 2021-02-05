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
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParticipantImpl implements Participant {
	private final Map<LivingEntity, Audience> members;

	private ParticipantImpl(@NonNull LivingEntity member) {
		this.members = Collections.singletonMap(member, Ares.getAudiences().sender(member));
	}

	private ParticipantImpl(@NonNull Collection<LivingEntity> members) {
		this.members = members.stream().collect(Collectors.toConcurrentMap(Function.identity(), m -> Ares.getAudiences().sender(m)));
	}

	@Override
	public @NonNull Collection<LivingEntity> getMembers() {
		return ImmutableSet.copyOf(members.keySet());
	}

	@Override
	public @NonNull Iterable<? extends Audience> audiences() {
		return ImmutableSet.copyOf(members.values());
	}

	public static @NonNull Participant of(@NonNull LivingEntity entity) {
		return Participant.isValidEntity(entity) ? new ParticipantImpl(entity) : Participant.dummy();
	}

	public static @NonNull Participant of(@NonNull Collection<LivingEntity> members) {
		Set<LivingEntity> filteredMembers = members.stream().filter(Participant::isValidEntity).collect(Collectors.toSet());
		return filteredMembers.isEmpty() ? Participant.dummy() : new ParticipantImpl(filteredMembers);
	}
}
