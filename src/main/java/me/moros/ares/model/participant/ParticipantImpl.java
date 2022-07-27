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

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ParticipantImpl implements Participant {
  private final UUID uuid;
  private final String name;
  private final Set<LivingEntity> entities;

  ParticipantImpl(Set<LivingEntity> entities) {
    var first = entities.stream().min(Comparator.comparing(Entity::getUniqueId)).orElseThrow();
    this.name = first.getName();
    this.uuid = first.getUniqueId();
    this.entities = Set.copyOf(entities);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public @NonNull UUID uuid() {
    return uuid;
  }

  @Override
  public boolean contains(LivingEntity entity) {
    return entities.contains(entity);
  }

  @Override
  public Stream<LivingEntity> stream() {
    return entities.stream();
  }

  @Override
  public int size() {
    return entities.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ParticipantImpl other) {
      return uuid.equals(other.uuid) && name.equals(other.name) && entities.equals(other.entities);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int result = uuid.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + entities.hashCode();
    return result;
  }

  @Override
  public @NonNull Iterable<? extends Audience> audiences() {
    return entities;
  }
}
