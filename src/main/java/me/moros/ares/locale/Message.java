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

package me.moros.ares.locale;

import java.util.Locale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Message {
  Locale DEFAULT_LOCALE = Locale.ENGLISH;

  Component PREFIX = text("[", DARK_GRAY)
    .append(text("Ares", GOLD))
    .append(text("] ", DARK_GRAY));

  Args0 CONFIRM_REQUIRED = () -> translatable("ares.command.confirm.required", YELLOW);
  Args0 CONFIRM_NO_PENDING = () -> translatable("ares.command.confirm.no-pending", RED);

  Args0 RELOAD = () -> translatable("ares.command.reload", GREEN);

  Args0 TOURNAMENT_LIST_HEADER = () -> translatable("ares.command.tournament.list.header", GOLD);
  Args0 TOURNAMENT_LIST_EMPTY = () -> translatable("ares.command.tournament.list.empty", YELLOW);

  Args1<Component> TOURNAMENT_ALREADY_JOINED = tournament -> translatable("ares.command.tournament.join.rejoin", YELLOW)
    .args(tournament);
  Args1<Component> TOURNAMENT_JOIN_SUCCESS = tournament -> translatable("ares.command.tournament.join.success", GREEN)
    .args(tournament);
  Args1<Component> TOURNAMENT_JOIN_FAIL = tournament -> translatable("ares.command.tournament.join.fail", RED)
    .args(tournament);

  Args2<String, Component> TOURNAMENT_ADD_SUCCESS = (name, tournament) -> translatable("ares.command.tournament.add.success", GREEN)
    .args(text(name), tournament);
  Args2<String, Component> TOURNAMENT_ADD_FAIL = (name, tournament) -> translatable("ares.command.tournament.add.fail", RED)
    .args(text(name), tournament);

  Args2<String, Component> TOURNAMENT_REMOVE_SUCCESS = (name, tournament) -> translatable("ares.command.tournament.remove.success", GREEN)
    .args(text(name), tournament);
  Args2<String, Component> TOURNAMENT_REMOVE_FAIL = (name, tournament) -> translatable("ares.command.tournament.remove.fail", RED)
    .args(text(name), tournament);

  Args1<String> TOURNAMENT_CREATE_INVALID_NAME = name -> translatable("ares.command.tournament.create.invalid", RED)
    .args(text(name));
  Args1<String> TOURNAMENT_CREATE_SUCCESS = name -> translatable("ares.command.tournament.create.success", GREEN)
    .args(text(name));
  Args1<String> TOURNAMENT_CREATE_FAIL = name -> translatable("ares.command.tournament.create.fail", RED)
    .args(text(name));
  Args1<Component> TOURNAMENT_START_SUCCESS = tournament -> translatable("ares.command.tournament.start.success", GREEN)
    .args(tournament);
  Args1<Integer> TOURNAMENT_START_ODD = size -> translatable("ares.command.tournament.start.warning", YELLOW)
    .args(text(size));
  Args1<Component> TOURNAMENT_START_FAIL = tournament -> translatable("ares.command.tournament.start.fail", RED)
    .args(tournament);
  Args1<Component> TOURNAMENT_CLOSED = tournament -> translatable("ares.command.tournament.closed", YELLOW)
    .args(tournament);
  Args1<Component> TOURNAMENT_NOT_STARTED = tournament -> translatable("ares.command.tournament.not-started", RED)
    .args(tournament);
  Args1<Component> TOURNAMENT_PROGRESSING = tournament -> translatable("ares.command.tournament.progressing", YELLOW)
    .args(tournament);
  Args1<Component> TOURNAMENT_CANCEL = tournament -> translatable("ares.command.tournament.cancel", YELLOW)
    .args(tournament);
  Args1<Component> TOURNAMENT_AUTO_ON = tournament -> translatable("ares.command.tournament.auto.on", GREEN)
    .args(tournament);
  Args1<Component> TOURNAMENT_AUTO_OFF = tournament -> translatable("ares.command.tournament.auto.off", GREEN)
    .args(tournament);

  Args3<Component, Integer, String> TOURNAMENT_RESULT = (tournament, position, score) -> translatable("ares.command.tournament.result", AQUA)
    .args(tournament, text(position, DARK_AQUA), text(score, GOLD));

  Args0 BATTLE_ERROR = () -> translatable("ares.command.duel.battle.error", RED);
  Args0 SELF_BATTLE = () -> translatable("ares.command.duel.battle.self", YELLOW);
  Args0 SELF_IN_BATTLE = () -> translatable("ares.command.duel.in-battle.self", YELLOW);
  Args1<String> OTHER_IN_BATTLE = name -> translatable("ares.command.duel.in-battle.other", YELLOW)
    .args(text(name, GOLD));
  Args1<String> LEAVE_BATTLE_SUCCESS = name -> translatable("ares.command.duel.leave.success", YELLOW)
    .args(text(name));
  Args0 LEAVE_BATTLE_FAIL = () -> translatable("ares.command.duel.leave.fail", YELLOW);

  Args2<String, String> VERSION_COMMAND_HOVER = (author, link) -> translatable("ares.command.version.hover", DARK_AQUA)
    .args(text(author, GREEN), text(link, GREEN));

  static Component brand(ComponentLike message) {
    return PREFIX.asComponent().append(message);
  }

  interface Args0 {
    Component build();

    default void send(Audience user) {
      user.sendMessage(build());
    }
  }

  interface Args1<A0> {
    Component build(A0 arg0);

    default void send(Audience user, A0 arg0) {
      user.sendMessage(build(arg0));
    }
  }

  interface Args2<A0, A1> {
    Component build(A0 arg0, A1 arg1);

    default void send(Audience user, A0 arg0, A1 arg1) {
      user.sendMessage(build(arg0, arg1));
    }
  }

  interface Args3<A0, A1, A2> {
    Component build(A0 arg0, A1 arg1, A2 arg2);

    default void send(Audience user, A0 arg0, A1 arg1, A2 arg2) {
      user.sendMessage(build(arg0, arg1, arg2));
    }
  }
}
