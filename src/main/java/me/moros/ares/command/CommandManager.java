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

package me.moros.ares.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.execution.preprocessor.CommandPreprocessingContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import me.moros.ares.Ares;
import me.moros.ares.command.parser.ParticipantParser;
import me.moros.ares.command.parser.TournamentParser;
import me.moros.ares.game.Game;
import me.moros.ares.locale.Message;
import me.moros.ares.model.Participant;
import me.moros.ares.model.Tournament;
import org.bukkit.command.CommandSender;

public final class CommandManager extends PaperCommandManager<CommandSender> {
  private final MinecraftHelp<CommandSender> help;
  private final CommandConfirmationManager<CommandSender> confirmationManager;

  public CommandManager(Ares plugin, Game game) throws Exception {
    super(plugin, AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().withSynchronousParsing().build(), Function.identity(), Function.identity());
    registerParsers();
    registerExceptionHandler();
    registerAsynchronousCompletions();
    commandSuggestionProcessor(this::suggestionProvider);

    help = MinecraftHelp.createNative("/ares help", this);
    help.setMaxResultsPerPage(8);

    confirmationManager = createConfirmationManager();
    confirmationManager.registerConfirmationProcessor(this);

    new AresCommand(plugin, game, this);
  }

  public MinecraftHelp<CommandSender> help() {
    return help;
  }

  private void registerParsers() {
    parserRegistry().registerParserSupplier(TypeToken.get(Participant.class), options -> new ParticipantParser());
    parserRegistry().registerParserSupplier(TypeToken.get(Tournament.class), options -> new TournamentParser());
  }

  private void registerExceptionHandler() {
    new MinecraftExceptionHandler<CommandSender>()
      .withInvalidSyntaxHandler()
      .withInvalidSenderHandler()
      .withNoPermissionHandler()
      .withArgumentParsingHandler()
      .withCommandExecutionHandler()
      .withDecorator(Message::brand)
      .apply(this, AudienceProvider.nativeAudience());
  }

  private CommandConfirmationManager<CommandSender> createConfirmationManager() {
    return new CommandConfirmationManager<>(30L,
      TimeUnit.SECONDS,
      ctx -> Message.CONFIRM_REQUIRED.send(ctx.getCommandContext().getSender()),
      Message.CONFIRM_NO_PENDING::send
    );
  }

  CommandExecutionHandler<CommandSender> confirmationHandler() {
    return confirmationManager.createConfirmationExecutionHandler();
  }

  private List<String> suggestionProvider(CommandPreprocessingContext<CommandSender> context, List<String> strings) {
    String input;
    if (context.getInputQueue().isEmpty()) {
      input = "";
    } else {
      input = context.getInputQueue().peek().toLowerCase(Locale.ROOT);
    }
    List<String> suggestions = new LinkedList<>();
    for (String suggestion : strings) {
      if (suggestion.toLowerCase(Locale.ROOT).startsWith(input)) {
        suggestions.add(suggestion);
      }
    }
    return suggestions;
  }
}
