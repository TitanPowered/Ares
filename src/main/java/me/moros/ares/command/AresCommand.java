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
import java.util.Set;
import java.util.stream.Stream;

import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.arguments.standard.StringArgument.StringMode;
import cloud.commandframework.meta.CommandMeta;
import me.moros.ares.Ares;
import me.moros.ares.game.Game;
import me.moros.ares.locale.Message;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.model.tournament.SimpleTournament;
import me.moros.ares.model.tournament.Tournament;
import me.moros.ares.model.tournament.Tournament.Status;
import me.moros.ares.registry.Registries;
import me.moros.ares.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
      .asOptionalWithDefault("-");
    var rulesArg = manager.argumentBuilder(BattleRules.class, "rules")
      .asOptionalWithDefault("default");

    //noinspection ConstantConditions
    manager.command(builder.handler(c -> manager.help().queryCommands("", c.getSender())))
      .command(builder.literal("reload")
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
      ).command(builder.literal("join")
        .meta(CommandMeta.DESCRIPTION, "Join an open tournament")
        .permission(CommandPermissions.JOIN)
        .senderType(Player.class)
        .argument(tournamentArg.build())
        .handler(c -> onJoin((Player) c.getSender(), c.get("tournament")))
      ).command(builder.literal("add")
        .meta(CommandMeta.DESCRIPTION, "Add a participant to an open tournament")
        .permission(CommandPermissions.MANAGE)
        .argument(participantArg.build())
        .argument(tournamentArg.build())
        .handler(c -> onAdd(c.getSender(), c.get("participant"), c.get("tournament")))
      ).command(builder.literal("remove", "rm")
        .meta(CommandMeta.DESCRIPTION, "Remove a participant from an open tournament")
        .permission(CommandPermissions.MANAGE)
        .argument(participantArg.build())
        .argument(tournamentArg.build())
        .handler(c -> onRemove(c.getSender(), c.get("participant"), c.get("tournament")))
      ).command(builder.literal("create")
        .meta(CommandMeta.DESCRIPTION, "Create a new tournament")
        .permission(CommandPermissions.MANAGE)
        .argument(StringArgument.single("name"))
        .handler(c -> onCreate(c.getSender(), c.get("name")))
      ).command(builder.literal("start")
        .meta(CommandMeta.DESCRIPTION, "Start an open tournament")
        .permission(CommandPermissions.MANAGE)
        .argument(tournamentArg.build())
        .argument(rulesArg.build())
        .handler(c -> onStart(c.getSender(), c.get("tournament"), c.get("rules")))
      ).command(builder.literal("auto")
        .meta(CommandMeta.DESCRIPTION, "Set tournament autopilot")
        .permission(CommandPermissions.MANAGE)
        .argument(BooleanArgument.of("auto"))
        .argument(tournamentArg.build())
        .handler(c -> onAuto(c.getSender(), c.get("tournament"), c.get("auto")))
      ).command(builder.literal("next")
        .meta(CommandMeta.DESCRIPTION, "Proceed to the next battle in the specified tournament")
        .permission(CommandPermissions.MANAGE)
        .argument(tournamentArg.build())
        .handler(c -> onNext(c.getSender(), c.get("tournament")))
      ).command(builder.literal("cancel")
        .meta(CommandMeta.DESCRIPTION, "Cancel the specified tournament")
        .permission(CommandPermissions.MANAGE)
        .argument(tournamentArg.build())
        .handler(c -> onCancel(c.getSender(), c.get("tournament")))
      ).command(builder.literal("match")
        .meta(CommandMeta.DESCRIPTION, "Create a duel between two participants")
        .permission(CommandPermissions.MANAGE)
        .argument(participantArg.build())
        .argument(manager.argumentBuilder(Participant.class, "participant2"))
        .argument(rulesArg.build())
        .handler(c -> onMatch(c.getSender(), c.get("participant"), c.get("participant2"), c.get("rules")))
      ).command(builder.literal("duel")
        .meta(CommandMeta.DESCRIPTION, "Duel another participant")
        .permission(CommandPermissions.DUEL)
        .senderType(Player.class)
        .argument(participantArg.build())
        .argument(rulesArg.build())
        .handler(c -> onDuel((Player) c.getSender(), c.get("participant"), c.get("rules")))
      ).command(builder.literal("details")
        .meta(CommandMeta.DESCRIPTION, "View details about a tournament")
        .permission(CommandPermissions.LIST)
        .argument(tournamentArg.build())
        .handler(c -> onDetails(c.getSender(), c.get("tournament")))
      ).command(builder.literal("leave")
        .meta(CommandMeta.DESCRIPTION, "Leave the current battle")
        .permission(CommandPermissions.LEAVE)
        .senderType(Player.class)
        .handler(c -> onLeave((Player) c.getSender()))
      ).command(builder.literal("debugspawn")
        .meta(CommandMeta.DESCRIPTION, "Spawn and register a test entity")
        .permission(CommandPermissions.DEBUG)
        .senderType(Player.class)
        .argument(EnumArgument.of(EntityType.class, "type"))
        .argument(StringArgument.single("name"))
        .argument(IntegerArgument.optional("amount", 1))
        .handler(c -> onDebug((Player) c.getSender(), c.get("type"), c.get("name"), c.get("amount")))
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
    Collection<Tournament> tournaments = Registries.TOURNAMENTS.stream().toList();
    if (tournaments.isEmpty()) {
      Message.TOURNAMENT_LIST_EMPTY.send(user);
      return;
    }
    Message.TOURNAMENT_LIST_HEADER.send(user);
    for (Tournament tournament : tournaments) {
      Component entry = Component.text("> ", NamedTextColor.DARK_GRAY)
        .append(tournament.displayName().decoration(TextDecoration.STRIKETHROUGH, !tournament.canRegister()));
      user.sendMessage(entry);
    }
  }

  // TODO allow specifying team members?
  private void onJoin(Player player, Tournament tournament) {
    Participant participant = Registries.PARTICIPANTS.get(player);
    if (tournament.contains(participant)) {
      participant.sendMessage(Message.TOURNAMENT_ALREADY_JOINED.build(tournament.displayName()));
      return;
    }
    if (tournament.add(participant)) {
      participant.sendMessage(Message.TOURNAMENT_JOIN_SUCCESS.build(tournament.displayName()));
    } else {
      participant.sendMessage(Message.TOURNAMENT_JOIN_FAIL.build(tournament.displayName()));
    }
  }

  private void onAdd(CommandSender user, Participant participant, Tournament tournament) {
    if (tournament.add(participant)) {
      user.sendMessage(Message.TOURNAMENT_ADD_SUCCESS.build(participant.name(), tournament.displayName()));
    } else {
      user.sendMessage(Message.TOURNAMENT_ADD_FAIL.build(participant.name(), tournament.displayName()));
    }
  }

  private void onRemove(CommandSender user, Participant participant, Tournament tournament) {
    if (tournament.remove(participant)) {
      user.sendMessage(Message.TOURNAMENT_REMOVE_SUCCESS.build(participant.name(), tournament.displayName()));
    } else {
      user.sendMessage(Message.TOURNAMENT_REMOVE_FAIL.build(participant.name(), tournament.displayName()));
    }
  }

  private void onCreate(CommandSender user, String name) {
    String validatedName = TextUtil.sanitizeInput(name);
    if (validatedName.isEmpty()) {
      Message.TOURNAMENT_CREATE_INVALID_NAME.send(user, name);
      return;
    }
    long delay = plugin.configManager().properties().battleInterval();
    Tournament tournament = new SimpleTournament(validatedName, delay, game.battleManager());
    if (Registries.TOURNAMENTS.register(tournament)) {
      Message.TOURNAMENT_CREATE_SUCCESS.send(user, name);
    } else {
      Message.TOURNAMENT_CREATE_FAIL.send(user, name);
    }
  }

  private void onStart(CommandSender user, Tournament tournament, BattleRules rules) {
    if (!tournament.canRegister()) {
      Message.TOURNAMENT_CLOSED.send(user, tournament.displayName());
      return;
    }
    if (tournament.start(rules)) {
      Message.TOURNAMENT_START_SUCCESS.send(user, tournament.displayName());
      int size = tournament.size();
      if (size % 2 != 0) {
        Message.TOURNAMENT_START_ODD.send(user, size);
      }
    } else {
      Message.TOURNAMENT_START_FAIL.send(user, tournament.displayName());
    }
  }

  private void onAuto(CommandSender user, Tournament tournament, boolean auto) {
    if (tournament.auto(auto)) {
      Message.TOURNAMENT_AUTO_ON.send(user, tournament.displayName());
    } else {
      Message.TOURNAMENT_AUTO_OFF.send(user, tournament.displayName());
    }
  }

  private void onNext(CommandSender user, Tournament tournament) {
    if (tournament.status() != Status.CLOSED) {
      Message.TOURNAMENT_NOT_STARTED.send(user, tournament.displayName());
      return;
    }
    if (!tournament.update()) {
      Message.TOURNAMENT_PROGRESSING.send(user, tournament.displayName());
    }
  }

  private void onCancel(CommandSender user, Tournament tournament) {
    tournament.finish(false);
    Message.TOURNAMENT_CANCEL.send(user, tournament.displayName());
  }

  private void onMatch(CommandSender user, Participant first, Participant second, BattleRules rules) {
    if (!first.equals(second) && first.isValid() && second.isValid()) {
      boolean valid = Stream.concat(first.stream(), second.stream()).map(LivingEntity::getUniqueId)
        .noneMatch(game.battleManager()::inBattle);
      if (valid && Battle.createBattle(Set.of(first, second)).start(game.battleManager(), rules)) {
        return;
      }
    }
    Message.BATTLE_ERROR.send(user);
  }

  // TODO require duel accept
  private void onDuel(Player player, Participant other, BattleRules rules) {
    if (other.contains(player)) {
      Message.SELF_BATTLE.send(player);
      return;
    }
    if (game.battleManager().inBattle(player)) {
      Message.SELF_IN_BATTLE.send(player);
      return;
    } else if (other.stream().anyMatch(game.battleManager()::inBattle)) {
      Message.OTHER_IN_BATTLE.send(player, other.name());
      return;
    }
    Battle.createBattle(Set.of(Registries.PARTICIPANTS.get(player), other)).start(game.battleManager(), rules);
  }

  private void onDetails(CommandSender user, Tournament tournament) {
    tournament.details().forEach(user::sendMessage);
  }

  private void onLeave(Player player) {
    Battle battle = game.battleManager().battle(player);
    if (battle == null) {
      Message.LEAVE_BATTLE_FAIL.send(player);
      return;
    }
    game.battleManager().cancelBattle(battle, player);
    Message.LEAVE_BATTLE_SUCCESS.send(battle.cache(), player.getName());
  }

  private void onDebug(Player player, EntityType type, String name, int amount) {
    Block block = player.getTargetBlockExact(32);
    if (block == null) {
      Location origin = player.getEyeLocation();
      block = origin.add(origin.getDirection().multiply(5)).getBlock();
    }
    if (type.isAlive()) {
      int counter = 0;
      for (int i = 1; i <= Math.max(1, amount); i++) {
        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(block.getLocation(), type);
        if (entity.isValid()) {
          ++counter;
          entity.customName(Component.text(name + counter));
          entity.setCustomNameVisible(true);
          Participant participant = Participant.of(entity);
          Registries.PARTICIPANTS.register(participant);
        }
      }
      player.sendMessage(Component.text("Spawned " + counter + " " + type.name()));
      return;
    }
    player.sendMessage(Component.text("Unable to spawn entity"));
  }
}
