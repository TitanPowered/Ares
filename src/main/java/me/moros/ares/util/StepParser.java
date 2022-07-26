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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StepParser {
  private static final Pattern PLACEHOLDERS = Pattern.compile("<(name|uuid)>");

  private StepParser() {
  }

  public static void parseAndExecute(Collection<String> steps, Collection<Participant> participants) {
    List<LivingEntity> entities = participants.stream().flatMap(Participant::members).toList();
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
        case "msg" -> msg(Audience.audience(participants), replace(participants.iterator(), value));
        case "broadcast" -> msg(Bukkit.getServer(), replace(participants.iterator(), value));
        case "cmd" -> entities.forEach(e -> dynamicCommand(e, value, false));
        case "console" -> entities.forEach(e -> dynamicCommand(e, value, true));
        case "global" -> command(value);
        case "teleport" -> {
          Location loc = location(value.split("\\s+"));
          if (loc != null) {
            entities.forEach(e -> teleport(e, loc.clone()));
          }
        }
      }
    }
  }

  public static String replace(Iterator<Participant> iterator, String value) {
    Matcher matcher = PLACEHOLDERS.matcher(value);
    int length = value.length();
    int lastIndex = 0;
    StringBuilder output = new StringBuilder();
    while (matcher.find()) {
      if (!iterator.hasNext()) {
        break;
      }
      String token = matcher.group();
      output.append(value, lastIndex, matcher.start()).append(replace(iterator.next(), token));
      lastIndex = matcher.end();
    }
    if (lastIndex < length) {
      output.append(value, lastIndex, length);
    }
    return output.toString();
  }

  private static String replace(Participant p, String token) {
    return token.equalsIgnoreCase("<uuid>") ? p.uuid().toString() : p.name();
  }

  private static String replace(LivingEntity e, String token) {
    return token.equalsIgnoreCase("<uuid>") ? e.getUniqueId().toString() : e.getName();
  }

  private static void msg(Audience audience, String msg) {
    Component component = MiniMessage.miniMessage().deserialize(msg);
    audience.sendMessage(component);
  }

  private static void dynamicCommand(LivingEntity entity, String cmd, boolean console) {
    CommandSender sender = console ? Bukkit.getConsoleSender() : entity;
    Bukkit.dispatchCommand(sender, PLACEHOLDERS.matcher(cmd).replaceAll(r -> replace(entity, r.group())));
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

    float[] dir = new float[] {0, 0};
    for (int i = 3; i < args.length; i++) {
      try {
        dir[i - 3] = Float.parseFloat(args[i]);
      } catch (Exception ignore) {
      }
    }
    return new Location(null, coords[0], coords[1], coords[2], dir[0], dir[1]);
  }
}
