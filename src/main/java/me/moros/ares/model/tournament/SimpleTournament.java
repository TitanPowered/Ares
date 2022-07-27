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

package me.moros.ares.model.tournament;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.locale.Message;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.battle.BattleScore;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.registry.Registries;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

// TODO tournament auto pilot separate
public class SimpleTournament implements Tournament {
  private final String name;
  private final BattleManager manager;
  private final Map<Participant, BattleScore> scoreMap;
  private final Deque<Round> rounds;
  private final long delay;

  private Status status = Status.OPEN;
  private Battle currentBattle;
  private BattleRules rules;

  private long nextBattleTime = 0;
  private boolean auto = false;

  public SimpleTournament(String name, long delay, BattleManager manager) {
    this.name = name;
    this.delay = delay;
    this.manager = manager;
    scoreMap = new ConcurrentHashMap<>();
    rounds = new ArrayDeque<>();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Status status() {
    return status;
  }

  @Override
  public boolean auto() {
    return auto;
  }

  @Override
  public boolean auto(boolean value) {
    auto = value;
    nextBattleTime = 0;
    return auto;
  }

  @Override
  public boolean start(BattleRules rules) {
    if (status == Status.OPEN && size() > 1) {
      status = Status.CLOSED;
      this.rules = rules;
      generateRound();
      return true;
    }
    return false;
  }

  @Override
  public boolean update() {
    if (status == Status.CLOSED && currentBattle != null && System.currentTimeMillis() > nextBattleTime) {
      return currentBattle.start(manager, rules, this::onComplete);
    }
    return false;
  }

  private void onComplete(Battle battle) {
    Participant winner = battle.testVictory();
    if (winner != null) {
      scoreMap.computeIfPresent(winner, (p, s) -> s.increment());
    }
    nextBattle();
  }

  private void nextBattle() {
    Round round = rounds.peekLast();
    if (round != null && round.hasNext()) {
      currentBattle = round.next();
    } else {
      generateRound();
    }
    if (auto) {
      nextBattleTime = System.currentTimeMillis() + delay;
    }
  }

  private void generateRound() {
    Round lastRound = rounds.peekLast();
    if (lastRound == null) {
      List<Participant> randomized = new ArrayList<>(scoreMap.keySet());
      Collections.shuffle(randomized);
      lastRound = Round.of(randomized, rules.teamAmount());
    } else {
      lastRound = lastRound.nextRound();
    }
    if (lastRound != null) {
      rounds.addLast(lastRound);
      nextBattle();
    } else {
      finish(true);
    }
  }

  @Override
  public boolean finish(boolean sendFeedback) {
    if (status == Status.COMPLETED) {
      return false;
    }
    if (currentBattle != null) {
      currentBattle.complete(manager);
    }
    recordStats();
    Registries.TOURNAMENTS.invalidate(this);
    if (sendFeedback) {
      var results = scoreMap.entrySet().stream()
        .sorted(Entry.comparingByValue(BattleScore.COMPARATOR.reversed())).toList();
      int position = 0;
      for (var entry : results) {
        Participant participant = entry.getKey();
        int place = ++position;
        String score = entry.getValue().toString();
        Message.TOURNAMENT_RESULT.send(participant, displayName(), place, score);
        Component text = Component.text(entry.getKey().name() + " ranked " + place + " with a score of " + score);
        Bukkit.getConsoleSender().sendMessage(text);
      }
    }
    status = Status.COMPLETED;
    return true;
  }

  private void recordStats() {
    // TODO add stat recording
  }

  @Override
  public boolean add(Participant participant) {
    return canRegister() && scoreMap.putIfAbsent(participant, BattleScore.ZERO) == null;
  }

  @Override
  public boolean remove(Participant participant) {
    return canRegister() && scoreMap.remove(participant) != null;
  }

  @Override
  public boolean contains(Participant participant) {
    return scoreMap.containsKey(participant);
  }

  @Override
  public Stream<Participant> participants() {
    return scoreMap.keySet().stream();
  }

  @Override
  public int size() {
    return scoreMap.size();
  }

  @Override
  public Iterator<Round> iterator() {
    return Collections.unmodifiableCollection(rounds).iterator();
  }
}
