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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.model.victory.BattleVictory;
import me.moros.ares.util.StepParser;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BattleImpl implements Battle {
  private final Map<Participant, BattleData> parties;
  private BattleVictory condition = x -> null;
  private BattleRules rules;
  private Stage stage = Stage.CREATED;

  BattleImpl(Collection<Participant> parties) {
    this(parties, BattleScore.ZERO);
  }

  BattleImpl(Collection<Participant> parties, BattleScore startingScore) {
    this.parties = parties.stream().collect(Collectors.toConcurrentMap(Function.identity(), p -> BattleData.create(startingScore)));
  }

  @Override
  public Map<Participant, BattleScore> scores() {
    return parties.entrySet().stream().collect(Collectors.toConcurrentMap(Entry::getKey, e -> e.getValue().score()));
  }

  @Override
  public Stream<Participant> participants() {
    return parties.keySet().stream();
  }

  @Override
  public Entry<Participant, BattleScore> topEntry() {
    return parties.entrySet().stream().max(Entry.comparingByValue())
      .map(e -> Map.entry(e.getKey(), e.getValue().score())).orElseThrow();
  }

  @Override
  public boolean start(BattleManager manager, BattleRules rules) {
    if (stage != Stage.CREATED) {
      return false;
    }
    stage = Stage.STARTING;
    this.condition = rules.condition();
    this.rules = rules;
    manager.addBattle(this);
    runSteps(manager);
    return true;
  }

  @Override
  public void runSteps(BattleManager manager) {
    Collection<Participant> participants = List.copyOf(parties.keySet());
    StepParser.parseAndExecute(this.rules.steps(), participants);
    manager.gaia().ifPresent(g -> g.teleportParticipants(participants, rules.arena()));
  }

  @Override
  public Map<Participant, BattleData> complete(BattleManager manager) {
    if (stage != Stage.COMPLETED) {
      stage = Stage.COMPLETED;
      manager.clearBattle(this);
      StepParser.parseAndExecute(this.rules.cleanupSteps(), List.copyOf(parties.keySet()));
    }
    return Map.copyOf(parties);
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
  public void forEachEntry(BiConsumer<Participant, BattleData> consumer) {
    parties.forEach(consumer);
  }

  @Override
  public Iterator<Participant> iterator() {
    return Collections.unmodifiableSet(parties.keySet()).iterator();
  }
}
