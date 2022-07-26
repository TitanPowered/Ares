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
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.Battle.Stage;
import me.moros.ares.model.battle.BattleScore;
import me.moros.ares.model.battle.BattleStat.Keys;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.registry.Registries;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    return checkStage(game.battleManager().battle(entity), Stage.STARTING);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDeath(EntityDeathEvent event) {
    LivingEntity entity = event.getEntity();
    Battle battle = game.battleManager().battle(entity);
    LivingEntity killer = killer(entity);
    if (checkStage(battle, Stage.ONGOING)) {
      battle.forEachEntry((p, d) -> {
        if (!p.contains(entity)) {
          d.score(BattleScore::increment);
        } else {
          d.value(Keys.DEATHS, v -> v + 1);
        }
        if (killer != null && p.contains(killer)) {
          d.value(Keys.KILLS, v -> v + 1);
        }
      });
      event.setCancelled(true);
      Component deathMessage;
      if (killer != null) {
        deathMessage = killer.name().append(Component.text(" has killed "))
          .append(entity.name()).append(Component.text("."));
      } else {
        deathMessage = entity.name().append(Component.text(" has died."));
      }
      entity.getServer().sendMessage(deathMessage);
      if (battle.testVictory() != null) {
        battle.complete(game.battleManager());
      } else {
        battle.runSteps(game.battleManager());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityDeathMonitor(EntityDeathEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      Registries.PARTICIPANTS.invalidate(event.getEntity().getUniqueId());
    }
  }

  private @Nullable LivingEntity killer(LivingEntity entity) {
    Player killer = entity.getKiller();
    if (killer != null) {
      return killer;
    }
    if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent cause) {
      if (cause.getDamager() instanceof LivingEntity livingEntity) {
        return livingEntity;
      }
    }
    return null;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof LivingEntity source && event.getEntity() instanceof LivingEntity target) {
      Battle battle = game.battleManager().battle(source);
      Battle battle2 = game.battleManager().battle(target);
      if (battle != null && battle.equals(battle2) && checkStage(battle, Stage.ONGOING)) {
        battle.forEachEntry((p, d) -> {
          if (p.contains(source)) {
            d.value(Keys.DAMAGE, v -> v + event.getFinalDamage());
          }
        });
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityRegainHealth(EntityRegainHealthEvent event) {
    if (event.getEntity() instanceof LivingEntity entity) {
      Battle battle = game.battleManager().battle(entity);
      if (checkStage(battle, Stage.ONGOING)) {
        battle.forEachEntry((p, d) -> {
          if (p.contains(entity)) {
            d.value(Keys.HEALTH_REGENERATED, v -> v + event.getAmount());
          }
        });
      }
    }
  }

  private boolean checkStage(@Nullable Battle battle, Stage stage) {
    return battle != null && battle.stage() == stage;
  }
}
