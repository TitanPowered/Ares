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

package me.moros.ares.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import me.moros.ares.model.Replacement;
import me.moros.ares.model.Replacer;
import me.moros.ares.model.Token;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.BattleData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StepParser {
  private StepParser() {
  }

  public static void parseAndExecute(Collection<String> steps, Battle battle, Collection<BattleData> data) {
    BattleData top = battle.topEntry();
    Replacer<BattleData> replacer = replacer(top, data);
    Replacer<LivingEntity> replacer2 = replacerEntity(top, battle.cache().entities());
    for (String step : steps) {
      String[] arr = step.split(":", 2);
      if (arr.length != 2) {
        return;
      }
      String value = arr[1];
      if (value.isEmpty()) {
        return;
      }
      switch (arr[0].toLowerCase(Locale.ROOT)) {
        case "msg" -> msg(battle, replacer.replaceAll(value));
        case "broadcast" -> msg(Bukkit.getServer(), replacer.replaceAll(value));
        case "cmd" -> battle.cache().entities().forEach(e -> dynamicCommand(e, value, replacer2, false));
        case "console" -> battle.cache().entities().forEach(e -> dynamicCommand(e, value, replacer2, true));
        case "global" -> command(value);
        case "teleport" -> {
          Location loc = location(value.split("\\s+"));
          if (loc != null) {
            battle.cache().entities().forEach(e -> teleport(e, loc.clone()));
          }
        }
      }
    }
  }

  private static void msg(Audience audience, String msg) {
    Component component = MiniMessage.miniMessage().deserialize(msg);
    audience.sendMessage(component);
  }

  private static void dynamicCommand(LivingEntity entity, String cmd, Replacer<LivingEntity> replacer, boolean console) {
    CommandSender sender = console ? Bukkit.getConsoleSender() : entity;
    Bukkit.dispatchCommand(sender, replacer.apply(entity, cmd));
  }

  private static void command(String cmd) {
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
  }

  private static void teleport(LivingEntity entity, Location location) {
    location.setWorld(entity.getWorld());
    entity.teleportAsync(location);
  }

  public static @Nullable Location location(String[] args) {
    if (args.length < 3) {
      return null;
    }
    double[] coords = new double[3];
    for (int i = 0; i < 3; i++) {
      try {
        coords[i] = Double.parseDouble(args[i]);
      } catch (Exception e) {
        return null;
      }
    }

    float[] dir = new float[]{0, 0};
    for (int i = 3; i < args.length; i++) {
      try {
        dir[i - 3] = Float.parseFloat(args[i]);
      } catch (Exception ignore) {
      }
    }
    return new Location(null, coords[0], coords[1], coords[2], dir[0], dir[1]);
  }

  private static Replacer<BattleData> replacer(BattleData top, Collection<BattleData> data) {
    String topName = top.participant().name();
    String topUuid = top.participant().uuid().toString();
    String topScore = top.score().toString();
    return new Replacer<>(Map.ofEntries(
      entry(Token.NAME, d -> d.participant().name()),
      entry(Token.UUID, d -> d.participant().uuid().toString()),
      entry(Token.SCORE, d -> d.score().toString()),
      entry(Token.WINNER_NAME, d -> topName),
      entry(Token.WINNER_UUID, d -> topUuid),
      entry(Token.WINNER_SCORE, d -> topScore)
    ), data);
  }

  private static Replacer<LivingEntity> replacerEntity(BattleData top, Collection<LivingEntity> data) {
    String topName = top.participant().name();
    String topUuid = top.participant().uuid().toString();
    return new Replacer<>(Map.ofEntries(
      entry(Token.NAME, LivingEntity::getName),
      entry(Token.UUID, e -> e.getUniqueId().toString()),
      entry(Token.WINNER_NAME, e -> topName),
      entry(Token.WINNER_UUID, e -> topUuid)
    ), data);
  }

  private static <T> Entry<String, Replacement<T>> entry(Token token, Replacement<T> replacement) {
    return Map.entry(token.value(), replacement);
  }
}
