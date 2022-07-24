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

package me.moros.ares.model;

import java.util.stream.Stream;

import net.kyori.adventure.text.Component;

// TODO create implementations
public interface Tournament {
  String name();

  Component displayName();

  boolean isOpen();

  boolean start();

  boolean finish();

  boolean addParticipant(Participant participant);

  boolean removeParticipant(Participant participant);

  boolean hasParticipant(Participant participant);

  Stream<Participant> participants();

  boolean addBattle(Battle battle);

  Stream<Battle> currentBattles();

  int size();
}
