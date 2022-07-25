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

package me.moros.ares;

import me.moros.ares.command.CommandManager;
import me.moros.ares.config.ConfigManager;
import me.moros.ares.game.Game;
import me.moros.ares.listener.BendingListener;
import me.moros.ares.listener.ParticipantListener;
import me.moros.ares.listener.ProjectKorraListener;
import me.moros.ares.locale.TranslationManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class Ares extends JavaPlugin {
  private String author;
  private String version;

  private Logger logger;
  private ConfigManager configManager;
  private TranslationManager translationManager;
  private Game game;

  @Override
  public void onLoad() {
    logger = getSLF4JLogger();
    author = getDescription().getAuthors().get(0);
    version = getDescription().getVersion();

    String dir = getDataFolder().toString();
    configManager = new ConfigManager(logger, dir);
    translationManager = new TranslationManager(logger, dir);
  }

  @Override
  public void onEnable() {
    game = new Game(this);
    try {
      new CommandManager(this, game);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      getServer().getPluginManager().disablePlugin(this);
    }
    getServer().getPluginManager().registerEvents(new ParticipantListener(game), this);
    configManager.save();
    registerHooks();
  }

  @Override
  public void onDisable() {
    configManager.close();
  }

  private void registerHooks() {
    if (getServer().getPluginManager().isPluginEnabled("Bending")) {
      getServer().getPluginManager().registerEvents(new BendingListener(game), this);
    } else if (getServer().getPluginManager().isPluginEnabled("ProjectKorra")) {
      getServer().getPluginManager().registerEvents(new ProjectKorraListener(game), this);
    }
  }

  public String author() {
    return author;
  }

  public String version() {
    return version;
  }

  public Logger logger() {
    return logger;
  }

  public ConfigManager configManager() {
    return configManager;
  }

  public TranslationManager translationManager() {
    return translationManager;
  }
}
