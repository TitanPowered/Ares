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

package me.moros.ares.model.tournament;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.Battle.Stage;
import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Round implements Iterable<Battle> {
  private final Collection<Battle> battles;
  private final Iterator<Battle> iterator;
  private final int parties;

  private Round(List<Participant> input, int parties) {
    this.parties = parties;
    int size = input.size();
    battles = new ArrayList<>();
    for (int i = 0; i < size; i += parties) {
      int end = Math.min(size, i + parties);
      battles.add(Battle.createBattle(input.subList(i, end)));
    }
    iterator = battles.iterator();
  }

  public @Nullable Round nextRound() {
    List<Participant> filteredParticipants = new ArrayList<>();
    for (Battle b : battles) {
      if (b.stage() != Stage.COMPLETED) {
        return null;
      }
      filteredParticipants.add(b.topEntry().participant());
    }
    return of(filteredParticipants, parties);
  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  public @Nullable Battle next() {
    return iterator.next();
  }

  public Stream<Battle> stream() {
    return battles.stream();
  }

  @Override
  public Iterator<Battle> iterator() {
    return Collections.unmodifiableCollection(battles).iterator();
  }

  public static @Nullable Round of(List<Participant> input, int parties) {
    if (input.size() < 2) {
      return null;
    }
    return new Round(input, parties);
  }

  public Collection<Component> details() {
    return stream().map(Battle::details).toList();
  }
}
