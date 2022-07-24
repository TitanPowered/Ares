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

package me.moros.ares.listener;

import me.moros.ares.game.Game;
import me.moros.bending.event.ActionLimitEvent;
import me.moros.bending.event.BendingDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BendingListener implements Listener {
  private final Game game;

  public BendingListener(Game game) {
    this.game = game;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBendingDamage(BendingDamageEvent event) {
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBendingRestriction(ActionLimitEvent event) {
  }
}
