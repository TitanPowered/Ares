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

package me.moros.ares;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.moros.ares.model.participant.CachedParticipants;
import me.moros.gaia.GaiaPlugin;
import me.moros.gaia.api.Arena;
import me.moros.gaia.api.ArenaPoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public class GaiaHook {
  private final GaiaPlugin gaia;

  public GaiaHook(Plugin plugin) {
    gaia = (GaiaPlugin) plugin;
  }

  public CompletableFuture<Void> teleportParticipants(CachedParticipants cache, String arenaName) {
    Arena arena = gaia.arenaManager().arena(arenaName).orElse(null);
    World world = arena == null ? null : Bukkit.getWorld(arena.worldUID());
    if (arena != null && world != null) {
      List<ArenaPoint> points = arena.points();
      if (points.size() > 1) {
        Iterator<ArenaPoint> it = arena.points().iterator();
        Collection<CompletableFuture<Boolean>> futures = new ArrayList<>();
        Iterator<LivingEntity> entityIt = cache.entityIterator();
        while (entityIt.hasNext()) {
          it = it.hasNext() ? it : points.iterator();
          Location loc = fromPoint(world, it.next());
          futures.add(entityIt.next().teleportAsync(loc));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
      }
    }
    return CompletableFuture.completedFuture(null);
  }

  private Location fromPoint(World world, ArenaPoint point) {
    return new Location(world, point.v().getX(), point.v().getY(), point.v().getZ(), point.yaw(), point.pitch());
  }
}
