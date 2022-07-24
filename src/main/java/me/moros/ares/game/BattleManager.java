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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.moros.ares.model.Battle;
import me.moros.ares.model.Participant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BattleManager {
  private final Map<UUID, Battle> activeBattles;

  public BattleManager() {
    activeBattles = new ConcurrentHashMap<>();
  }

  public boolean inBattle(LivingEntity entity) {
    return inBattle(entity.getUniqueId());
  }

  public boolean inBattle(UUID uuid) {
    return activeBattles.containsKey(uuid);
  }

  public @Nullable Battle battle(LivingEntity entity) {
    return battle(entity.getUniqueId());
  }

  public @Nullable Battle battle(UUID uuid) {
    return activeBattles.get(uuid);
  }

  public void addBattle(Battle battle) {
    battle.participants().flatMap(Participant::members).map(Entity::getUniqueId)
      .forEach(uuid -> activeBattles.put(uuid, battle));
  }

  public void clearBattle(Battle battle) {
    battle.participants().flatMap(Participant::members).map(Entity::getUniqueId).forEach(activeBattles::remove);
  }

  public boolean removeFromBattle(UUID uuid) {
    return activeBattles.remove(uuid) != null;
  }
}
