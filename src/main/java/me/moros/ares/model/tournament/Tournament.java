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

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.battle.BattleScore;
import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.*;
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

  void update(BattleManager manager);

  boolean finish();

  boolean addParticipant(Participant participant);

  boolean removeParticipant(Participant participant);

  boolean hasParticipant(Participant participant);

  Stream<Participant> participants();

  boolean addBattle(Battle battle);

  Stream<Battle> currentBattles();

  int size();

  void skip(BattleManager manager);

  default Collection<Component> details() {
    Collection<Component> components = new ArrayList<>();
    components.add(displayName());
    for (Battle battle : currentBattles().toList()) {
      BattleScore top = battle.topEntry().getValue();
      Collection<Component> participants = new ArrayList<>();
      battle.forEachEntry((p, d) -> {
        Style style = Style.style().color(battle.stage().color())
          .decoration(TextDecoration.BOLD, d.score().compareTo(top) >= 0).build();
        participants.add(text(p.name(), style));
      });
      components.add(join(JoinConfiguration.separator(text(" vs ")), participants));
    }
    return components;
  }
}
