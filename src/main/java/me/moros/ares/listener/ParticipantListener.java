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

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import me.moros.ares.game.Game;
import me.moros.ares.model.Battle;
import me.moros.ares.model.Battle.Stage;
import me.moros.ares.model.BattleScore;
import me.moros.ares.model.Participant;
import me.moros.ares.registry.Registries;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ParticipantListener implements Listener {
  private final Game game;

  public ParticipantListener(Game game) {
    this.game = game;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Registries.PARTICIPANTS.register(Participant.of(event.getPlayer()));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLeave(PlayerQuitEvent event) {
    Registries.PARTICIPANTS.invalidate(event.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onEntityMove(EntityMoveEvent event) {
    if (event.hasChangedBlock() && cancelMovement(event.getEntity())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.hasChangedBlock() && cancelMovement(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerJump(PlayerJumpEvent event) {
    if (cancelMovement(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  private boolean cancelMovement(LivingEntity entity) {
    Battle battle = game.battleManager().battle(entity);
    return battle != null && battle.stage() == Stage.STARTING;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();
    Battle battle = game.battleManager().battle(player);
    if (updateBattle(battle)) {
      for (Participant participant : battle) {
        if (!participant.contains(player)) {
          battle.setScore(participant, BattleScore::increment);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    Battle battle = game.battleManager().battle(player);
    if (updateBattle(battle) && battle.stage() == Stage.ONGOING) {
      // TODO respawn player back in the arena
    }
  }

  private boolean updateBattle(@Nullable Battle battle) {
    if (battle != null && battle.stage() == Stage.ONGOING) {
      Participant winner = battle.testVictory();
      if (winner != null) {
        battle.complete(game.battleManager());
      }
      return true;
    }
    return false;
  }
}
