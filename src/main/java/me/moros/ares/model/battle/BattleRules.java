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

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import me.moros.ares.model.participant.Participant;
import me.moros.ares.model.victory.BattleVictory;
import me.moros.ares.model.victory.ScoreVictory;
import me.moros.ares.model.victory.TimedVictory;
import me.moros.ares.util.TextUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

public final class BattleRules {
  private final String name;
  private final int teamSize;
  private final int teamAmount;
  private final int scoreToWin;
  private final long duration;
  private final long preparationTime;
  private final String arena;
  private final List<String> steps;
  private final List<String> cleanupSteps;

  private BattleRules(String name, BattleRulesBuilder builder) {
    this.name = name;
    this.teamSize = builder.teamSize;
    this.teamAmount = builder.teamAmount;
    this.scoreToWin = builder.scoreToWin;
    this.duration = builder.duration;
    this.preparationTime = builder.preparationTime;
    this.arena = builder.arena;
    this.steps = List.copyOf(builder.steps);
    this.cleanupSteps = List.copyOf(builder.cleanupSteps);
  }

  public String name() {
    return name;
  }

  public int teamSize() {
    return teamSize;
  }

  public int teamAmount() {
    return teamAmount;
  }

  public int scoreToWin() {
    return scoreToWin;
  }

  public long duration() {
    return duration;
  }

  public long preparationTime() {
    return preparationTime;
  }

  public String arena() {
    return arena;
  }

  public List<String> steps() {
    return steps;
  }

  public List<String> cleanupSteps() {
    return cleanupSteps;
  }

  public BattleVictory condition() {
    BattleVictory scoreCondition = ScoreVictory.of(scoreToWin);
    BattleVictory timeCondition = TimedVictory.of(duration > 0 ? (duration + preparationTime) : 0);
    return b -> {
      Participant p1 = scoreCondition.apply(b);
      return p1 != null ? p1 : timeCondition.apply(b);
    };
  }

  public static BattleRulesBuilder builder(String name) {
    return new BattleRulesBuilder(Objects.requireNonNull(name));
  }

  @ConfigSerializable
  public static final class BattleRulesBuilder {
    private String name = "default";
    private int teamSize = 1;
    private int teamAmount = 2;
    private int scoreToWin = 2;
    private long duration = 0;
    private long preparationTime = 3000;
    private String arena = "pvp";
    private List<String> steps = List.of("broadcast:<hover:show_text:\"<name>: <score><newline><name>: <score>\"><aqua>Match starting between <name> and <name>!</aqua></hover>");
    private List<String> cleanupSteps = List.of("broadcast:<hover:show_text:\"<name>: <score><newline><name>: <score>\"><bold><winner_name> won this duel!</aqua></hover>");

    private BattleRulesBuilder(String name) {
      this.name = name;
    }

    public String name() {
      return name;
    }

    public BattleRulesBuilder teamSize(int teamSize) {
      this.teamSize = Math.max(1, teamSize);
      return this;
    }

    public BattleRulesBuilder teamAmount(int teamAmount) {
      this.teamAmount = Math.max(2, teamAmount);
      return this;
    }

    public BattleRulesBuilder scoreToWin(int scoreToWin) {
      this.scoreToWin = Math.max(0, scoreToWin);
      return this;
    }

    public BattleRulesBuilder duration(long duration) {
      this.duration = Math.max(0, duration);
      return this;
    }

    public BattleRulesBuilder preparationTime(long preparationTime) {
      this.preparationTime = Math.max(0, preparationTime);
      return this;
    }

    public BattleRulesBuilder arena(String arena) {
      this.arena = Objects.requireNonNull(arena);
      return this;
    }

    public BattleRulesBuilder steps(List<String> steps) {
      this.steps = List.copyOf(steps);
      return this;
    }

    public BattleRulesBuilder cleanupSteps(List<String> cleanupSteps) {
      this.cleanupSteps = List.copyOf(cleanupSteps);
      return this;
    }

    public boolean isValid() {
      if (name == null || arena == null || steps == null || cleanupSteps == null) {
        return false;
      }
      if (teamSize < 1 || teamAmount < 2 || preparationTime < 0) {
        return false;
      }
      return scoreToWin > 0 || duration > 0;
    }

    public @Nullable BattleRules build() {
      if (isValid()) {
        String validatedName = TextUtil.sanitizeInput(name).toLowerCase(Locale.ROOT);
        if (!validatedName.isEmpty()) {
          return new BattleRules(validatedName, this);
        }
      }
      return null;
    }
  }
}
