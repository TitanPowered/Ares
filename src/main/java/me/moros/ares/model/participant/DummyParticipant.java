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

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class DummyParticipant implements Participant {
  private static final UUID DEFAULT_UUID = new UUID(0, 0);
  static final DummyParticipant INSTANCE = new DummyParticipant();

  private DummyParticipant() {
  }

  @Override
  public String name() {
    return "dummy";
  }

  @Override
  public boolean contains(LivingEntity entity) {
    return false;
  }

  @Override
  public Stream<LivingEntity> stream() {
    return Stream.of();
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public @NonNull UUID uuid() {
    return DEFAULT_UUID;
  }

  @Override
  public @NonNull Iterable<? extends Audience> audiences() {
    return List.of();
  }
}
