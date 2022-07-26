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
import me.moros.ares.model.tournament.Tournament;
import me.moros.ares.registry.Registries;
import org.bukkit.command.CommandSender;

public final class TournamentParser implements ArgumentParser<CommandSender, Tournament> {
  @Override
  public ArgumentParseResult<Tournament> parse(CommandContext<CommandSender> commandContext, Queue<String> inputQueue) {
    String input = inputQueue.peek();
    if (input == null) {
      return ArgumentParseResult.failure(new NoInputProvidedException(TournamentParser.class, commandContext));
    }
    inputQueue.remove();
    Tournament tournament;
    if (input.equalsIgnoreCase("")) {
      tournament = Registries.TOURNAMENTS.stream().filter(Tournament::isOpen).findFirst().orElse(null);
    } else {
      tournament = Registries.TOURNAMENTS.get(input);
    }
    if (tournament != null) {
      return ArgumentParseResult.success(tournament);
    }
    return ArgumentParseResult.failure(new Throwable("Could not find any tournaments matching " + input));
  }

  @Override
  public List<String> suggestions(final CommandContext<CommandSender> commandContext, final String input) {
    return Registries.TOURNAMENTS.stream().map(Tournament::name).toList();
  }
}
