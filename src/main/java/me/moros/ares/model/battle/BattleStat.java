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

package me.moros.ares.model.battle;

import java.util.Collection;
import java.util.List;

public interface BattleStat {
  String key();

  double defaultValue();

  enum Keys implements BattleStat {
    DAMAGE("damage", 0),
    HEALTH_REGENERATED("healed", 0),
    ACCURACY("accuracy", 0),
    KILLS("kills", 0),
    DEATHS("deaths", 0);

    private final String key;
    private final double defaultValue;

    Keys(String id, double defaultValue) {
      this.key = "ares.stat." + id;
      this.defaultValue = defaultValue;
    }

    @Override
    public String key() {
      return key;
    }

    @Override
    public double defaultValue() {
      return defaultValue;
    }

    public static final Collection<BattleStat> VALUES = List.of(values());
  }
}
