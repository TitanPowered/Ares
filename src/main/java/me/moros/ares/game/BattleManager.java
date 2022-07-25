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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.moros.ares.GaiaHook;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.Battle.Stage;
import me.moros.ares.model.participant.Participant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BattleManager {
  private final Map<UUID, Battle> activeBattles;
  private final GaiaHook gaiaHook;

  public BattleManager() {
    activeBattles = new ConcurrentHashMap<>();
    Plugin plugin = Bukkit.getPluginManager().getPlugin("Gaia");
    gaiaHook = plugin == null ? null : new GaiaHook(plugin);
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
    Battle battle = activeBattles.get(uuid);
    if (battle != null && battle.stage() == Stage.ONGOING) {
      Participant winner = battle.testVictory();
      if (winner != null) {
        battle.complete(this);
        return null;
      }
    }
    return battle;
  }

  public void addBattle(Battle battle) {
    if (battle.stage() != Stage.COMPLETED) {
      battle.participants().flatMap(Participant::members).map(Entity::getUniqueId)
        .forEach(uuid -> activeBattles.put(uuid, battle));
    }
  }

  public void clearBattle(Battle battle) {
    battle.participants().flatMap(Participant::members).map(Entity::getUniqueId).forEach(activeBattles::remove);
  }

  public Optional<GaiaHook> gaia() {
    return Optional.ofNullable(gaiaHook);
  }
}
