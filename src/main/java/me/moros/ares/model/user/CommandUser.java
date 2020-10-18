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

package me.moros.ares.model.user;

import me.moros.ares.Ares;
import me.moros.atlas.checker.checker.nullness.qual.NonNull;
import me.moros.atlas.kyori.adventure.audience.Audience;
import me.moros.atlas.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;

public class CommandUser implements ForwardingAudience.Single {
	private final CommandSender sender;

	public CommandUser(@NonNull CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public @NonNull Audience audience() {
		return Ares.getAudiences().sender(sender);
	}
}
