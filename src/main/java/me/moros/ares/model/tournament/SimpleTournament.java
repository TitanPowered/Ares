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
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.Battle.Stage;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.battle.BattleScore;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.registry.Registries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleTournament implements Tournament {
  private final String name;
  private final Map<Participant, BattleScore> scoreMap;
  private final Deque<Round> rounds;
  private final long delay;

  private Battle currentBattle;
  private BattleRules rules;
  private long nextBattleTime;
  private boolean open = true;

  public SimpleTournament(String name, long delay) {
    this.name = name;
    this.delay = delay;
    scoreMap = new ConcurrentHashMap<>();
    rounds = new ArrayDeque<>();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Component displayName() {
    return Component.text(name, NamedTextColor.AQUA);
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public boolean start(BattleRules rules) {
    if (open && size() > 1) {
      open = false;
      this.rules = rules;
      generateRound();
      return true;
    }
    return false;
  }

  @Override
  public void update(BattleManager manager) {
    long time = System.currentTimeMillis();
    if (time < nextBattleTime) {
      return;
    }
    Round lastRound = rounds.peekLast();
    if (lastRound == null) {
      return;
    }
    nextBattle(lastRound);
    if (currentBattle != null) {
      switch (currentBattle.stage()) {
        case CREATED -> manager.addBattle(currentBattle);
        case ONGOING -> {
          Participant winner = currentBattle.testVictory();
          if (winner == null) {
            return;
          }
          currentBattle.complete(manager);
          nextBattleTime = time + delay;
        }
        case COMPLETED -> nextBattle(lastRound);
      }
    }
  }

  private void nextBattle(@Nullable Round current) {
    if (current == null) {
      finish();
      return;
    }
    if (current.iterator().hasNext()) {
      currentBattle = current.iterator().next();
    } else {
      generateRound();
      nextBattle(rounds.peekLast());
    }
  }

  private void generateRound() {
    Round lastRound = rounds.peekLast();
    Round nextRound = lastRound == null ? new Round(scoreMap.keySet(), rules.teamAmount()) : lastRound.nextRound(rules.teamAmount());
    if (nextRound != null) {
      rounds.addLast(nextRound);
    }
  }

  @Override
  public boolean finish() {
    recordStats();
    Registries.TOURNAMENTS.invalidate(this);
    return true;
  }

  private void recordStats() {
    // TODO add stat recording
  }

  @Override
  public boolean addParticipant(Participant participant) {
    return open && scoreMap.putIfAbsent(participant, BattleScore.ZERO) == null;
  }

  @Override
  public boolean removeParticipant(Participant participant) {
    return open && scoreMap.remove(participant) != null;
  }

  @Override
  public boolean hasParticipant(Participant participant) {
    return scoreMap.containsKey(participant);
  }

  @Override
  public Stream<Participant> participants() {
    return scoreMap.keySet().stream();
  }

  @Override
  public boolean addBattle(Battle battle) {
    Round lastRound = rounds.peekLast();
    if (lastRound != null) {
      return lastRound.battles.add(battle);
    }
    return false;
  }

  @Override
  public Stream<Battle> currentBattles() {
    return rounds.stream().flatMap(Round::stream);
  }

  @Override
  public int size() {
    return scoreMap.size();
  }

  private static final class Round implements Iterable<Battle> {
    private final Collection<Battle> battles;
    private final int size;

    private final Iterator<Battle> iterator;

    private Round(Collection<Participant> input, int participantsPerBattle) {
      size = input.size();
      List<Participant> randomized = new ArrayList<>(input);
      Collections.shuffle(randomized);
      battles = new ArrayList<>();
      for (int i = 0; i < size; i += participantsPerBattle) {
        int end = Math.min(size, i + participantsPerBattle);
        battles.add(Battle.createBattle(randomized.subList(i, end)).orElseThrow());
      }
      iterator = resetIterator();
    }

    public int size() {
      return size;
    }

    public Stream<Battle> stream() {
      return battles.stream();
    }

    public @Nullable Round nextRound(int participantsPerBattle) {
      if (stream().allMatch(b -> b.stage() == Stage.COMPLETED)) {
        Collection<Participant> input = stream().map(b -> b.topEntry().getKey()).toList();
        if (input.size() > 1) {
          return new Round(input, participantsPerBattle);
        }
      }
      return null;
    }

    private Iterator<Battle> resetIterator() {
      return Collections.unmodifiableCollection(battles).iterator();
    }

    @Override
    public Iterator<Battle> iterator() {
      return iterator;
    }
  }
}
