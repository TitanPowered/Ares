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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.ValueReference;
import me.moros.ares.model.participant.CachedParticipants;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.model.victory.BattleVictory;
import me.moros.ares.util.EntityUtil;
import me.moros.ares.util.StepParser;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BattleImpl implements Battle {
  private final Collection<BattleData> data;
  private final CachedParticipants cache;
  private final ValueReference<BattleData> topReference;

  private BattleVictory condition = x -> null;
  private BattleRules rules;
  private Stage stage = Stage.CREATED;
  private Consumer<Battle> consumer;

  BattleImpl(Set<Participant> parties) {
    this(parties, BattleScore.ZERO);
  }

  BattleImpl(Set<Participant> parties, BattleScore startingScore) {
    this.cache = new CachedParticipants(parties);
    topReference = new ValueReference<>();
    data = cache.participants().stream().map(p -> new BattleData(p, startingScore, this::onScoreChange)).toList();
    topReference.set(data.iterator().next());
  }

  private void onScoreChange(BattleData data) {
    BattleData previous = topReference.get();
    if (previous == null || data.compareTo(previous) > 0) {
      topReference.set(data);
    }
  }

  @Override
  public CachedParticipants cache() {
    return cache;
  }

  @Override
  public BattleData topEntry() {
    return topReference.get();
  }

  @Override
  public boolean start(BattleManager manager, BattleRules rules, @Nullable Consumer<Battle> consumer) {
    if (stage == Stage.CREATED) {
      this.condition = rules.condition();
      this.rules = rules;
      this.consumer = consumer;
      manager.addBattle(this);
      runSteps(manager);
      if (rules.duration() > 0) {
        long delay = 20 + (rules.duration() + rules.preparationTime()) / 50L;
        manager.async(() -> complete(manager), delay);
      }
      return true;
    }
    return false;
  }

  @Override
  public CompletableFuture<Void> runSteps(BattleManager manager) {
    stage = Stage.STARTING;
    return manager.gaia().map(g -> g.teleportParticipants(cache, rules.arena()))
      .orElseGet(() -> CompletableFuture.completedFuture(null))
      .thenRun(() -> runPreparation(manager));
  }

  private void runPreparation(BattleManager manager) {
    StepParser.parseAndExecute(rules.steps(), cache);
    cache.entities().forEach(EntityUtil::heal);
    if (rules.preparationTime() > 0) {
      long delay = rules.preparationTime() / 50L;
      manager.async(this::mainStage, delay);
    } else {
      stage = Stage.ONGOING;
    }
  }

  private void mainStage() {
    if (stage == Stage.CREATED || stage == Stage.STARTING) {
      stage = Stage.ONGOING;
    }
  }

  @Override
  public Collection<BattleData> complete(BattleManager manager) {
    if (stage != Stage.COMPLETED) {
      stage = Stage.COMPLETED;
      manager.clearBattle(this);
      if (this.rules != null) {
        StepParser.parseAndExecute(this.rules.cleanupSteps(), cache);
      }
      if (consumer != null) {
        consumer.accept(this);
      }
    }
    return data;
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
  public Iterator<BattleData> iterator() {
    return data.iterator();
  }
}
