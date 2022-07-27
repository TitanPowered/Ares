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
import java.util.stream.Stream;

import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface Tournament extends Iterable<Round> {
  String name();

  default Component displayName() {
    Component hover = Component.text()
      .append(Component.text("Participants: ", NamedTextColor.AQUA))
      .append(Component.text(size(), NamedTextColor.GOLD)).append(Component.newline())
      .append(Component.text("Status: ", NamedTextColor.AQUA))
      .append(status().display()).build();
    return Component.text(name(), NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(hover));
  }

  Status status();

  default boolean canRegister() {
    return status() == Status.OPEN;
  }

  boolean auto();

  boolean auto(boolean value);

  boolean start(BattleRules rules);

  boolean update();

  boolean finish(boolean sendFeedback);

  boolean add(Participant participant);

  boolean remove(Participant participant);

  boolean contains(Participant participant);

  Stream<Participant> participants();

  int size();

  default Collection<Component> details() {
    Collection<Component> components = new ArrayList<>();
    components.add(displayName());
    int roundCounter = 0;
    for (Round round : this) {
      ++roundCounter;
      components.add(Component.text("Round " + roundCounter, NamedTextColor.GOLD));
      if (components.addAll(round.details())) {
        components.add(Component.newline());
      }
    }
    return components;
  }

  enum Status {
    OPEN("Registrations Open", NamedTextColor.GREEN),
    CLOSED("Registrations Closed", NamedTextColor.RED),
    COMPLETED("Completed", NamedTextColor.GRAY);

    private final Component display;

    Status(String value, TextColor color) {
      this.display = Component.text(value, color);
    }

    public Component display() {
      return display;
    }
  }
}
