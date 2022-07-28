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
import java.util.Iterator;
import java.util.Set;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CachedParticipants implements ForwardingAudience {
  private final Collection<Participant> participants;
  private final Collection<LivingEntity> entities;

  public CachedParticipants(Collection<Participant> participants) {
    this.participants = Set.copyOf(participants);
    this.entities = Set.copyOf(participants.stream().flatMap(Participant::stream).toList());
  }

  public Collection<Participant> participants() {
    return participants;
  }

  public Collection<LivingEntity> entities() {
    return entities;
  }

  public Iterator<Participant> iterator() {
    return participants.iterator();
  }

  public Iterator<LivingEntity> entityIterator() {
    return entities.iterator();
  }

  @Override
  public @NonNull Iterable<? extends Audience> audiences() {
    return participants;
  }
}
