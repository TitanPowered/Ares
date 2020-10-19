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

package me.moros.ares.command;

import me.moros.ares.Ares;
import me.moros.ares.model.Tournament;
import me.moros.ares.model.user.CommandUser;
import me.moros.atlas.acf.BukkitCommandCompletionContext;
import me.moros.atlas.acf.BukkitCommandExecutionContext;
import me.moros.atlas.acf.CommandCompletions;
import me.moros.atlas.acf.CommandContexts;
import me.moros.atlas.acf.InvalidCommandArgument;

public class Commands {
	public static void init() {
		registerCommandContexts();
		registerCommandCompletions();

		Ares.getCommandManager().getCommandReplacements().addReplacement("arescommand", "ares|tournament|tournaments");

		Ares.getCommandManager().registerCommand(new AresCommand());
	}

	private static void registerCommandCompletions() {
		CommandCompletions<BukkitCommandCompletionContext> commandCompletions = Ares.getCommandManager().getCommandCompletions();
		commandCompletions.registerAsyncCompletion("tournaments", c -> Ares.getManager().getTournamentNames());
	}

	private static void registerCommandContexts() {
		CommandContexts<BukkitCommandExecutionContext> commandContexts = Ares.getCommandManager().getCommandContexts();
		commandContexts.registerIssuerOnlyContext(CommandUser.class, c -> new CommandUser(c.getSender()));

		commandContexts.registerContext(Tournament.class, c -> {
			String name = c.popFirstArg().toLowerCase();
			return Ares.getManager().getTournament(name).orElseThrow(() -> new InvalidCommandArgument("Could not find tournament " + name));
		});
	}
}
