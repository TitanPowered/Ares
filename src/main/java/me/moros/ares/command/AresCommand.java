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

import java.util.Collection;
import java.util.List;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.arguments.standard.StringArgument.StringMode;
import cloud.commandframework.meta.CommandMeta;
import me.moros.ares.Ares;
import me.moros.ares.game.Game;
import me.moros.ares.locale.Message;
import me.moros.ares.model.Battle;
import me.moros.ares.model.Participant;
import me.moros.ares.model.SimpleTournament;
import me.moros.ares.model.Tournament;
import me.moros.ares.model.victory.ScoreVictory;
import me.moros.ares.registry.Registries;
import me.moros.ares.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AresCommand {
  private final Ares plugin;
  private final Game game;
  private final CommandManager manager;

  AresCommand(Ares plugin, Game game, CommandManager manager) {
    this.plugin = plugin;
    this.game = game;
    this.manager = manager;
    construct();
  }

  private void construct() {
    var builder = manager.commandBuilder("ares")
      .meta(CommandMeta.DESCRIPTION, "Base command for Ares");
    var participantArg = manager.argumentBuilder(Participant.class, "participant");
    var tournamentArg = manager.argumentBuilder(Tournament.class, "tournament")
      .asOptionalWithDefault("default");

    //noinspection ConstantConditions
    manager.command(builder.handler(c -> manager.help().queryCommands("", c.getSender())))
      .command(builder.literal("confirm")
        .meta(CommandMeta.DESCRIPTION, "Confirm a pending command")
        .handler(manager.confirmationHandler())
      ).command(builder.literal("reload")
        .meta(CommandMeta.DESCRIPTION, "Reload the plugin")
        .permission(CommandPermissions.RELOAD)
        .handler(c -> onReload(c.getSender()))
      ).command(builder.literal("version", "v")
        .meta(CommandMeta.DESCRIPTION, "View version info about Ares")
        .permission(CommandPermissions.VERSION)
        .handler(c -> onVersion(c.getSender()))
      ).command(builder.literal("list", "ls")
        .meta(CommandMeta.DESCRIPTION, "View all currently active tournaments")
        .permission(CommandPermissions.LIST)
        .handler(c -> onList(c.getSender()))
      ).command(builder.literal("join", "j")
        .meta(CommandMeta.DESCRIPTION, "Join an open tournaments")
        .permission(CommandPermissions.JOIN)
        .senderType(Player.class)
        .argument(tournamentArg.build())
        .handler(c -> onJoin((Player) c.getSender(), c.get("tournament")))
      ).command(builder.literal("create", "c", "new", "n")
        .meta(CommandMeta.DESCRIPTION, "Create a new tournament")
        .permission(CommandPermissions.CREATE)
        .argument(StringArgument.single("name"))
        .handler(c -> onCreate(c.getSender(), c.get("name")))
      ).command(builder.literal("start", "init", "s")
        .meta(CommandMeta.DESCRIPTION, "Start an open tournament")
        .permission(CommandPermissions.CREATE)
        .argument(tournamentArg.build())
        .handler(c -> onStart(c.getSender(), c.get("tournament")))
      ).command(builder.literal("duel", "d")
        .meta(CommandMeta.DESCRIPTION, "Duel another participant")
        .permission(CommandPermissions.DUEL)
        .senderType(Player.class)
        .argument(participantArg.build())
        .handler(c -> onDuel((Player) c.getSender(), c.get("participant")))
      ).command(builder.literal("help", "h")
        .meta(CommandMeta.DESCRIPTION, "View info about a command")
        .permission(CommandPermissions.HELP)
        .argument(StringArgument.optional("query", StringMode.GREEDY))
        .handler(c -> manager.help().queryCommands(c.getOrDefault("query", ""), c.getSender()))
      );
  }

  private void onReload(CommandSender sender) {
    plugin.translationManager().reload();
    Message.RELOAD.send(sender);
  }

  private void onVersion(CommandSender user) {
    String link = "https://github.com/PrimordialMoros/Ares";
    Component version = Message.brand(Component.text("Version: ", NamedTextColor.DARK_AQUA))
      .append(Component.text(plugin.version(), NamedTextColor.GREEN))
      .hoverEvent(HoverEvent.showText(Message.VERSION_COMMAND_HOVER.build(plugin.author(), link)))
      .clickEvent(ClickEvent.openUrl(link));
    user.sendMessage(version);
  }

  private void onList(CommandSender user) {
    Collection<Tournament> tournaments = Registries.TOURNAMENTS.stream().filter(Tournament::isOpen).toList();
    if (tournaments.isEmpty()) {
      Message.TOURNAMENT_LIST_EMPTY.send(user);
      return;
    }
    Message.TOURNAMENT_LIST_HEADER.send(user);
    for (Tournament tournament : tournaments) {
      user.sendMessage(Component.text("> ", NamedTextColor.DARK_GRAY).append(tournament.displayName()));
    }
  }

  // TODO allow specifying team members?
  private void onJoin(Player player, Tournament tournament) {
    Participant participant = Participant.of(player);
    if (tournament.hasParticipant(participant)) {
      participant.sendMessage(Message.TOURNAMENT_ALREADY_JOINED.build(tournament.displayName()));
      return;
    }
    if (tournament.addParticipant(participant)) {
      participant.sendMessage(Message.TOURNAMENT_JOIN_SUCCESS.build(tournament.displayName()));
    } else {
      participant.sendMessage(Message.TOURNAMENT_JOIN_FAIL.build(tournament.displayName()));
    }
  }

  private void onCreate(CommandSender user, String name) {
    // TODO Offer tournament presets and build instead of providing simple tournament
    String validatedName = TextUtil.sanitizeInput(name);
    if (validatedName.isEmpty()) {
      Message.TOURNAMENT_CREATE_INVALID_NAME.send(user, name);
      return;
    }
    Tournament tournament = new SimpleTournament(validatedName);
    Registries.TOURNAMENTS.register(tournament);
  }

  private void onStart(CommandSender user, Tournament tournament) {
    if (tournament.start()) {
      Message.TOURNAMENT_START_SUCCESS.send(user, tournament.displayName());
      int size = tournament.size();
      if (size % 2 != 0) {
        Message.TOURNAMENT_START_ODD.send(user, size);
      }
    } else {
      Message.TOURNAMENT_START_FAIL.send(user, tournament.displayName());
    }
  }

  // TODO require duel accept, add time/rules/arena
  private void onDuel(Player player, Participant other) {
    if (other.contains(player)) {
      Message.SELF_BATTLE.send(player);
      return;
    }
    if (game.battleManager().inBattle(player)) {
      Message.SELF_IN_BATTLE.send(player);
      return;
    } else if (other.members().anyMatch(game.battleManager()::inBattle)) {
      Message.OTHER_IN_BATTLE.send(player, other.name());
      return;
    }
    Battle.createBattle(List.of(Participant.of(player), other))
      .ifPresent(b -> b.start(game.battleManager(), new ScoreVictory(3)));
  }
}
