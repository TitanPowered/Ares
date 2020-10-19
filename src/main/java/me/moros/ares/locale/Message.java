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

package me.moros.ares.locale;

import me.moros.ares.model.user.CommandUser;
import me.moros.atlas.kyori.adventure.text.Component;
import me.moros.atlas.kyori.adventure.text.ComponentLike;

import static me.moros.atlas.kyori.adventure.text.Component.text;
import static me.moros.atlas.kyori.adventure.text.format.NamedTextColor.*;

public interface Message {
	Component PREFIX = text("[", DARK_GRAY)
		.append(text("Ares", GOLD))
		.append(text("] ", DARK_GRAY));

	Args0 HELP_HEADER = () -> brand(text("List of commands:", GOLD));
	Args0 TOURNAMENT_LIST_HEADER = () -> text("List of active tournaments:", GOLD);
	Args0 TOURNAMENT_LIST_EMPTY = () -> text("There are no active tournaments at the moment!", YELLOW);

	Args1<Component> TOURNAMENT_ALREADY_JOINED = tournament -> text("You have already joined tournament {tournament}", YELLOW)
		.replaceFirstText("{tournament}", tournament);
	Args1<Component> TOURNAMENT_JOIN_SUCCESS = tournament -> text("You have successfully joined tournament {tournament}", GREEN)
		.replaceFirstText("{tournament}", tournament);
	Args1<Component> TOURNAMENT_JOIN_FAIL = tournament -> text("You cannot join tournament {tournament}", RED)
		.replaceFirstText("{tournament}", tournament);

	static Component brand(ComponentLike message) {
		return PREFIX.asComponent().append(message);
	}

	interface Args0 {
		Component build();

		default void send(CommandUser user) {
			user.sendMessage(build());
		}
	}

	interface Args1<A0> {
		Component build(A0 arg0);

		default void send(CommandUser user, A0 arg0) {
			user.sendMessage(build(arg0));
		}
	}

	interface Args2<A0, A1> {
		Component build(A0 arg0, A1 arg1);

		default void send(CommandUser user, A0 arg0, A1 arg1) {
			user.sendMessage(build(arg0, arg1));
		}
	}
}
