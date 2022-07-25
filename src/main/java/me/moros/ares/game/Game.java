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

package me.moros.ares.game;

import me.moros.ares.Ares;
import me.moros.ares.registry.Registries;

public class Game {
  private final BattleManager battleManager;

  public Game(Ares plugin) {
    battleManager = new BattleManager(plugin);
    plugin.getServer().getScheduler().runTaskTimer(plugin, this::update, 1, 20);
  }

  private void update() {
    Registries.TOURNAMENTS.stream().filter(t -> !t.isOpen()).toList().forEach(t -> t.update(battleManager));
  }

  public BattleManager battleManager() {
    return battleManager;
  }
}
