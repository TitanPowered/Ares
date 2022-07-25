/*
 * Copyright 2020-2022 Moros
 *
 * This file is part of Ares.
 *
 * Ares is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ares is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Ares. If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.ares.model.participant;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface Participant extends Identity, ForwardingAudience {
  String name();

  boolean contains(LivingEntity entity);

  default boolean isValid() {
    return members().allMatch(Participant::isValidEntity);
  }

  Stream<LivingEntity> members();

  static Participant dummy() {
    return DummyParticipant.INSTANCE;
  }

  static boolean isValidEntity(LivingEntity entity) {
    return (entity instanceof Player player && player.isOnline()) || entity.isValid();
  }

  static Participant of(LivingEntity entity) {
    return of(Set.of(entity));
  }

  static Participant of(Collection<LivingEntity> members) {
    Set<LivingEntity> filteredMembers = members.stream().filter(Participant::isValidEntity).collect(Collectors.toSet());
    return filteredMembers.isEmpty() ? Participant.dummy() : new ParticipantImpl(filteredMembers);
  }
}
