/*
 *   Copyright 2020 Moros <https://github.com/PrimordialMoros>
 *
 *    This file is part of Ares.
 *
 *   Ares is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Ares is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Ares.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.ares.game;

import me.moros.ares.model.Battle;
import me.moros.ares.model.Participant;
import me.moros.atlas.cf.checker.nullness.qual.NonNull;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BattleManager {
	private final Map<LivingEntity, Battle> activeBattles;

	public BattleManager() {
		activeBattles = new ConcurrentHashMap<>();
	}

	public boolean isInBattle(@NonNull LivingEntity entity) {
		return activeBattles.containsKey(entity);
	}

	public Optional<Battle> getBattle(@NonNull LivingEntity entity) {
		return Optional.of(activeBattles.get(entity));
	}

	public void addBattle(@NonNull Battle battle) {
		battle.getParticipants().stream().map(Participant::getMembers)
			.flatMap(Collection::stream).forEach(e -> activeBattles.put(e, battle));
	}

	public void clearBattle(@NonNull Battle battle) {
		battle.getParticipants().stream().map(Participant::getMembers)
			.flatMap(Collection::stream).forEach(activeBattles::remove);
	}

	public boolean removeFromBattle(@NonNull LivingEntity entity) {
		return activeBattles.remove(entity) != null;
	}
}
