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

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ParticipantImpl implements Participant {
  private final UUID uuid;
  private final String name;
  private final Set<LivingEntity> members;

  ParticipantImpl(Collection<LivingEntity> members) {
    var first = members.iterator().next();
    this.name = first.getName();
    this.uuid = first.getUniqueId();
    this.members = Set.copyOf(members);
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
    return members.contains(entity);
  }

  @Override
  public Stream<LivingEntity> members() {
    return members.stream();
  }

  @Override
  public @NonNull Iterable<? extends Audience> audiences() {
    return members;
  }
}
