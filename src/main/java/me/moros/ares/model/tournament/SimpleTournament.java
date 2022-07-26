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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.ares.game.BattleManager;
import me.moros.ares.locale.Message;
import me.moros.ares.model.battle.Battle;
import me.moros.ares.model.battle.Battle.Stage;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.battle.BattleScore;
import me.moros.ares.model.participant.Participant;
import me.moros.ares.registry.Registries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;

public class SimpleTournament implements Tournament {
  private final String name;
  private final BattleManager manager;
  private final Map<Participant, BattleScore> scoreMap;
  private final Deque<Round> rounds;
  private final long delay;

  private Battle currentBattle;
  private BattleRules rules;
  private long nextBattleTime;
  private boolean open = true;

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
  public boolean isOpen() {
    return open;
  }

  @Override
  public boolean start(BattleRules rules) {
    if (open && size() > 1) {
      open = false;
      this.rules = rules;
      generateRound();
      nextBattle();
      return true;
    }
    return false;
  }

  @Override
  public void update() {
    if (System.currentTimeMillis() < nextBattleTime) {
      return;
    }
    if (currentBattle != null) {
      switch (currentBattle.stage()) {
        case CREATED -> startBattle();
        case COMPLETED -> nextBattle();
      }
    }
  }

  private void startBattle() {
    currentBattle.start(manager, rules);
    if (currentBattle.stage() == Stage.COMPLETED) {
      addWinnerScore(currentBattle);
    } else {
      currentBattle.onComplete(this::addWinnerScore);
    }
  }

  private void addWinnerScore(Battle battle) {
    Participant winner = battle.testVictory();
    if (winner != null) {
      scoreMap.computeIfPresent(winner, (p, s) -> s.increment());
    }
  }

  private void nextBattle() {
    Round round = rounds.peekLast();
    if (round != null && round.iterator().hasNext()) {
      currentBattle = round.iterator().next();
    } else {
      generateRound();
    }
    nextBattleTime = System.currentTimeMillis() + delay;
  }

  private void generateRound() {
    Round lastRound = rounds.peekLast();
    List<Participant> randomized = new ArrayList<>(scoreMap.keySet());
    Collections.shuffle(randomized);
    Round nextRound = lastRound == null ? new Round(randomized, rules.teamAmount()) : lastRound.nextRound(rules.teamAmount());
    if (nextRound != null) {
      rounds.addLast(nextRound);
    } else {
      finish();
    }
  }

  @Override
  public boolean finish(boolean sendFeedback) {
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
  public Collection<Component> details() {
    Collection<Component> components = new ArrayList<>();
    components.add(displayName());
    int roundCounter = 0;
    for (Round round : rounds) {
      ++roundCounter;
      components.add(Component.text("Round " + roundCounter, NamedTextColor.GOLD));
      for (Battle battle : round.battles) {
        BattleScore top = battle.topEntry().getValue();
        boolean bold = top.compareTo(BattleScore.ZERO) > 0;
        Collection<Component> participants = new ArrayList<>();
        battle.forEachEntry((p, d) -> {
          Style style = Style.style().color(battle.stage().color())
            .decoration(TextDecoration.BOLD, bold && d.score().compareTo(top) >= 0).build();
          participants.add(text(p.name(), style));
        });
        components.add(join(JoinConfiguration.separator(text(" vs ")), participants));
      }
      components.add(Component.newline());
    }
    return components;
  }

  @Override
  public int size() {
    return scoreMap.size();
  }

  private static final class Round implements Iterable<Battle> {
    private final Collection<Battle> battles;
    private final Iterator<Battle> iterator;

    private Round(Collection<Participant> input, int participantsPerBattle) {
      int size = input.size();
      List<Participant> randomized = new ArrayList<>(input);
      battles = new ArrayList<>();
      for (int i = 0; i < size; i += participantsPerBattle) {
        int end = Math.min(size, i + participantsPerBattle);
        battles.add(Battle.createBattle(randomized.subList(i, end)));
      }
      iterator = battles.iterator();
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

    @Override
    public Iterator<Battle> iterator() {
      return iterator;
    }
  }
}
