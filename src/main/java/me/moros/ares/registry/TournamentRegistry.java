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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.ares.model.Tournament;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class TournamentRegistry implements Registry<Tournament> {
  private final Map<String, Tournament> tournaments;

  TournamentRegistry() {
    tournaments = new ConcurrentHashMap<>();
  }

  public boolean register(Tournament tournament) {
    if (!contains(tournament)) {
      tournaments.put(tournament.name(), tournament);
      return true;
    }
    return false;
  }

  public boolean invalidate(Tournament tournament) {
    return tournaments.remove(tournament.name()) != null;
  }

  public boolean contains(Tournament tournament) {
    return tournaments.containsKey(tournament.name());
  }

  public @Nullable Tournament get(@Nullable String id) {
    return (id == null || id.isEmpty()) ? null : tournaments.get(id.toLowerCase(Locale.ROOT));
  }

  public int size() {
    return tournaments.size();
  }

  public Collection<String> keys() {
    return Set.copyOf(tournaments.keySet());
  }

  public Stream<Tournament> stream() {
    return tournaments.values().stream();
  }

  @Override
  public Iterator<Tournament> iterator() {
    return Collections.unmodifiableCollection(tournaments.values()).iterator();
  }
}
