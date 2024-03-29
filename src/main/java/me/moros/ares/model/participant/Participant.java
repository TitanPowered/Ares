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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.moros.ares.model.ValueReference;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface Participant extends Identity, ForwardingAudience {
  String name();

  boolean contains(LivingEntity entity);

  Stream<LivingEntity> stream();

  int size();

  default boolean isValid() {
    return size() > 0 && stream().allMatch(Participant::isValidEntity);
  }

  static Participant dummy() {
    return DummyParticipant.INSTANCE;
  }

  static boolean isValidEntity(LivingEntity entity) {
    return (entity instanceof Player player && player.isOnline()) || entity.isValid();
  }

  static Participant of(LivingEntity entity) {
    return of(Set.of(entity));
  }

  static Participant of(Collection<LivingEntity> entities) {
    Set<LivingEntity> filtered = entities.stream().filter(Participant::isValidEntity).collect(Collectors.toSet());
    return filtered.isEmpty() ? Participant.dummy() : new ParticipantImpl(filtered);
  }

  static Set<Participant> unique(Collection<Participant> participants, ValueReference<Participant> error) {
    Set<LivingEntity> entities = new HashSet<>();
    Set<Participant> unique = new HashSet<>();
    int totalSize = 0;
    for (Participant participant : participants) {
      if (participant.isValid()) {
        totalSize += participant.size();
        participant.stream().forEach(entities::add);
        if (entities.size() == totalSize) {
          unique.add(participant);
          continue;
        }
      }
      error.set(participant);
      break;
    }
    return unique;
  }
}
