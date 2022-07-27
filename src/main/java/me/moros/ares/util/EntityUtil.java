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

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public final class EntityUtil {
  private EntityUtil() {
  }

  public static void heal(LivingEntity entity) {
    AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    if (attribute == null) {
      return;
    }
    double maxHealth = attribute.getValue();
    double amount = maxHealth - entity.getHealth();
    EntityRegainHealthEvent event = new EntityRegainHealthEvent(entity, amount, RegainReason.CUSTOM);
    if (!event.callEvent()) {
      return;
    }
    double newAmount = Math.min(maxHealth, entity.getHealth() + event.getAmount());
    entity.setHealth(newAmount);
    if (entity instanceof Player player) {
      player.setFoodLevel(20);
    }
    entity.setFireTicks(0);
    entity.setFreezeTicks(0);
    entity.getActivePotionEffects().forEach(p -> entity.removePotionEffect(p.getType()));
  }
}
