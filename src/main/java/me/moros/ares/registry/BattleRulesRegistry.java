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

package me.moros.ares.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.ares.model.battle.BattleRules;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BattleRulesRegistry implements Registry<BattleRules> {
  private final Map<String, BattleRules> registry;

  BattleRulesRegistry() {
    registry = new ConcurrentHashMap<>();
  }

  public boolean register(BattleRules rules) {
    if (!contains(rules)) {
      registry.put(rules.name(), rules);
      return true;
    }
    return false;
  }

  public boolean registerDefault(BattleRules rules) {
    if (!contains(rules)) {
      registry.put(rules.name(), rules);
      return true;
    }
    return false;
  }

  public boolean contains(BattleRules rules) {
    return registry.containsKey(rules.name());
  }

  public @Nullable BattleRules get(@Nullable String id) {
    return (id == null || id.isEmpty()) ? null : registry.get(id.toLowerCase(Locale.ROOT));
  }

  public int size() {
    return registry.size();
  }

  public Collection<String> keys() {
    return Set.copyOf(registry.keySet());
  }

  public Stream<BattleRules> stream() {
    return registry.values().stream();
  }

  @Override
  public Iterator<BattleRules> iterator() {
    return Collections.unmodifiableCollection(registry.values()).iterator();
  }
}
