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

package me.moros.ares.model;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import me.moros.ares.game.BattleManager;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

// TODO link to specific arena
public class Battle {
  private final Map<Participant, BattleScore> parties;
  private ScoreEntry top;

  private boolean started = false;

  private Battle(Collection<Participant> parties) {
    this(parties, BattleScore.ZERO);
  }

  private Battle(Collection<Participant> parties, BattleScore startingScore) {
    this.parties = parties.stream().collect(Collectors.toConcurrentMap(Function.identity(), p -> startingScore));
  }

  public Map<Participant, BattleScore> scores() {
    return Map.copyOf(parties);
  }

  public Collection<Participant> participants() {
    return Set.copyOf(parties.keySet());
  }

  public boolean setScore(Participant participant, BattleScore score) {
    BattleScore prev = parties.get(participant);
    if (prev == null || prev.getScore() >= score.getScore()) return false;
    if (score.getScore() > top.getBattleScore().getScore()) top = new ScoreEntry(participant, score);
    parties.put(participant, score);
    return true;
  }

  public @Nullable ScoreEntry topEntry() {
    return top;
  }

  public boolean start(BattleManager manager) {
    if (started) {
      return false;
    }
    manager.addBattle(this);
    // TODO add preparation steps
    return started = true;
  }

  public Map<Participant, BattleScore> complete(BattleManager manager) {
    // TODO cleanup after battle
    manager.clearBattle(this);
    return scores();
  }

  public static Optional<Battle> createBattle(Collection<Participant> parties) {
    if (parties.stream().allMatch(Participant::isValid)) {
      Collection<LivingEntity> col = parties.stream().flatMap(Participant::members).toList();
      Set<LivingEntity> unique = Set.copyOf(col);
      if (col.size() == unique.size()) {
        return Optional.of(new Battle(parties));
      }
    }
    return Optional.empty();
  }
}
