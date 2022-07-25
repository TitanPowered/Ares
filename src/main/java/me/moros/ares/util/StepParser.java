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
import java.util.regex.Pattern;

import me.moros.ares.model.participant.Participant;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public final class StepParser {
  private static final Pattern ENTITY = Pattern.compile("\\{entity}");

  private StepParser() {
  }

  public static void parseAndExecute(Collection<String> steps, Collection<Participant> participants) {
    Collection<LivingEntity> entities = participants.stream().flatMap(Participant::members).toList();
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
        case "msg" -> participants.forEach(p -> msg(p, value));
        case "broadcast" -> broadcast(value);
        case "cmd" -> entities.forEach(e -> command(e, value));
        case "console" -> entities.forEach(e -> command(ENTITY.matcher(value).replaceAll(e.getName())));
        case "global" -> command(value);
      }
    }
  }

  private static void broadcast(String msg) {
    msg(Bukkit.getServer(), msg);
  }

  private static void msg(Audience audience, String msg) {
    Component component = MiniMessage.miniMessage().deserialize(msg);
    audience.sendMessage(component);
  }

  private static void command(String cmd) {
    command(Bukkit.getConsoleSender(), cmd);
  }

  private static void command(CommandSender sender, String cmd) {
    Bukkit.dispatchCommand(sender, cmd);
  }
}
