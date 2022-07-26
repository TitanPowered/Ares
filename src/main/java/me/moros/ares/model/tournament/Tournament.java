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

import java.util.Collection;
import java.util.stream.Stream;

import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Tournament {
  String name();

  default Component displayName() {
    Component hover = text().append(text("Participants: ", AQUA))
      .append(text(size(), GOLD)).append(newline())
      .append(text("Registrations: ", AQUA))
      .append(isOpen() ? text("Open", GREEN) : text("Closed", RED)).build();
    return text().append(text(name(), AQUA)).hoverEvent(HoverEvent.showText(hover)).build();
  }

  boolean isOpen();

  boolean start(BattleRules rules);

  void update();

  default boolean finish() {
    return finish(true);
  }

  boolean finish(boolean sendFeedback);

  boolean addParticipant(Participant participant);

  boolean removeParticipant(Participant participant);

  boolean hasParticipant(Participant participant);

  Stream<Participant> participants();

  boolean addBattle(Battle battle);

  int size();

  Collection<Component> details();
}
