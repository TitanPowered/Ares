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

import cloud.commandframework.permission.CommandPermission;
import cloud.commandframework.permission.Permission;

public final class CommandPermissions {
  private CommandPermissions() {
  }

  public static final CommandPermission HELP = create("help");
  public static final CommandPermission VERSION = create("version");
  public static final CommandPermission LIST = create("list");
  public static final CommandPermission DUEL = create("duel");
  public static final CommandPermission LEAVE = create("leave");
  public static final CommandPermission JOIN = create("join");
  public static final CommandPermission MANAGE = create("manage");
  public static final CommandPermission RELOAD = create("reload");
  public static final CommandPermission DEBUG = create("debug");


  private static Permission create(String node) {
    return Permission.of("ares.command." + node);
  }
}
