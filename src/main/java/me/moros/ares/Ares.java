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

package me.moros.ares;

import me.moros.atlas.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Ares extends JavaPlugin {
	private static Ares plugin;

	private TournamentManager manager;
	private BukkitAudiences audiences;
	private Logger log;

	@Override
	public void onEnable() {
		plugin = this;
		audiences = BukkitAudiences.create(this);
		log = getLogger();

		manager = new TournamentManager();
	}

	@Override
	public void onDisable() {
	}

	public static TournamentManager getManager() {
		return plugin.manager;
	}

	public static BukkitAudiences getAudiences() {
		return plugin.audiences;
	}

	public static Logger getLog() {
		return plugin.log;
	}
}
