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

package me.moros.ares.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.victory.BattleVictory;
import org.checkerframework.checker.nullness.qual.Nullable;

// TODO link to specific arena
public class BattleImpl implements Battle {
  private final Map<Participant, BattleScore> parties;
  private BattleVictory condition = x -> null;
  private Stage stage = Stage.CREATED;

  BattleImpl(Collection<Participant> parties) {
    this(parties, BattleScore.ZERO);
  }

  BattleImpl(Collection<Participant> parties, BattleScore startingScore) {
    this.parties = parties.stream().collect(Collectors.toConcurrentMap(Function.identity(), p -> startingScore));
  }

  @Override
  public Map<Participant, BattleScore> scores() {
    return Map.copyOf(parties);
  }

  @Override
  public Stream<Participant> participants() {
    return parties.keySet().stream();
  }

  @Override
  public boolean setScore(Participant participant, UnaryOperator<BattleScore> function) {
    return parties.computeIfPresent(participant, (p, b) -> function.apply(b)) != null;
  }

  @Override
  public Entry<Participant, BattleScore> topEntry() {
    return parties.entrySet().stream().max(Entry.comparingByValue()).orElseThrow();
  }

  @Override
  public boolean start(BattleManager manager, BattleVictory condition) {
    if (stage != Stage.CREATED) {
      return false;
    }
    stage = Stage.STARTING;
    this.condition = condition;
    manager.addBattle(this);
    // TODO add preparation steps
    return true;
  }

  @Override
  public Map<Participant, BattleScore> complete(BattleManager manager) {
    // TODO cleanup after battle
    stage = Stage.COMPLETED;
    manager.clearBattle(this);
    return scores();
  }


  @Override
  public Stage stage() {
    return stage;
  }

  @Override
  public @Nullable Participant testVictory() {
    return condition.apply(this);
  }

  @Override
  public Iterator<Participant> iterator() {
    return Collections.unmodifiableSet(parties.keySet()).iterator();
  }
}
