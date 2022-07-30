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

package me.moros.ares.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.ares.model.participant.Participant;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ParticipantRegistry implements Registry<Participant> {
  private final Map<UUID, Participant> participants;
  private final Map<String, UUID> names;

  ParticipantRegistry() {
    participants = new ConcurrentHashMap<>();
    names = new ConcurrentHashMap<>();
  }

  public boolean invalidate(UUID uuid) {
    Participant p = participants.remove(uuid);
    if (p != null) {
      names.remove(p.name().toLowerCase(Locale.ROOT));
      return true;
    }
    return false;
  }

  public boolean register(Participant participant) {
    if (!participant.isValid() || participants.containsKey(participant.uuid())) {
      return false;
    }
    participants.put(participant.uuid(), participant);
    names.put(participant.name().toLowerCase(Locale.ROOT), participant.uuid());
    return true;
  }

  public @Nullable Participant get(UUID uuid) {
    return participants.get(uuid);
  }

  public @Nullable Participant get(String name) {
    UUID uuid = names.get(name.toLowerCase(Locale.ROOT));
    return uuid == null ? null : participants.get(uuid);
  }

  public Participant get(Player player) {
    return Objects.requireNonNull(participants.get(player.getUniqueId()));
  }

  public Stream<Participant> stream() {
    return participants.values().stream();
  }

  @Override
  public Iterator<Participant> iterator() {
    return Collections.unmodifiableCollection(participants.values()).iterator();
  }
}
