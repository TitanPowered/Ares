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

import me.moros.ares.command.Commands;
import me.moros.ares.game.Game;
import me.moros.atlas.kyori.adventure.platform.bukkit.BukkitAudiences;
import me.moros.ares.locale.TranslationManager;
import me.moros.storage.logging.Logger;
import me.moros.storage.logging.Slf4jLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

public class Ares extends JavaPlugin {
	private static Ares plugin;

	private Logger logger;

	private TranslationManager translationManager;
	private BukkitAudiences audiences;
	private Game game;

	private String author;
	private String version;

	@Override
	public void onEnable() {
		plugin = this;
		logger = new Slf4jLogger(LoggerFactory.getLogger(getClass().getSimpleName()));
		author = getDescription().getAuthors().get(0);
		version = getDescription().getVersion();

		translationManager = new TranslationManager(getConfigFolder());
		audiences = BukkitAudiences.create(this);
		game = new Game(this);

		new Commands(this);
	}

	@Override
	public void onDisable() {
	}

	public static TranslationManager getTranslationManager() {
		return plugin.translationManager;
	}

	public static BukkitAudiences getAudiences() {
		return plugin.audiences;
	}

	public static Game getGame() {
		return plugin.game;
	}

	public static String getAuthor() {
		return plugin.author;
	}

	public static String getVersion() {
		return plugin.version;
	}

	public static Logger getLog() {
		return plugin.logger;
	}

	public static String getConfigFolder() {
		return plugin.getDataFolder().toString();
	}
}
