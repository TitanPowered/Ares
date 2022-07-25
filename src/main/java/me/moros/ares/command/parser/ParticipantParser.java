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

package me.moros.ares.command.parser;

import java.util.List;
import java.util.Queue;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ParticipantParser implements ArgumentParser<CommandSender, Participant> {
  @Override
  public ArgumentParseResult<Participant> parse(CommandContext<CommandSender> commandContext, Queue<String> inputQueue) {
    String input = inputQueue.peek();
    if (input == null) {
      return ArgumentParseResult.failure(new NoInputProvidedException(ParticipantParser.class, commandContext));
    }
    inputQueue.remove();
    Participant participant;
    Player player = Bukkit.getPlayer(input);
    if (player != null) {
      participant = Registries.PARTICIPANTS.get(player.getUniqueId());
    } else {
      participant = Registries.PARTICIPANTS.stream().filter(p -> input.equalsIgnoreCase(p.name()))
        .findFirst().orElse(null);
    }
    if (participant != null) {
      return ArgumentParseResult.success(participant);
    }
    return ArgumentParseResult.failure(new Throwable("Could not any entity matching " + input));
  }

  @Override
  public List<String> suggestions(final CommandContext<CommandSender> commandContext, final String input) {
    return Registries.PARTICIPANTS.stream().map(Participant::name).toList();
  }
}
