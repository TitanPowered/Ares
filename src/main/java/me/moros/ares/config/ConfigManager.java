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

package me.moros.ares.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.battle.BattleRules.BattleRulesBuilder;
import me.moros.ares.registry.Registries;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.WatchServiceListener;

public final class ConfigManager {
  public static final String SUFFIX = ".ares";

  private final Logger logger;
  private final String directory;
  private final WatchServiceListener listener;
  private final ConfigurationReference<BasicConfigurationNode> reference;
  private final Properties properties;

  public ConfigManager(Logger logger, String directory) {
    this.logger = logger;
    this.directory = directory;
    Path path = Path.of(this.directory, "ares.conf");
    try {
      listener = WatchServiceListener.create();
      reference = listener.listenToConfiguration(f -> GsonConfigurationLoader.builder().path(f).build(), path);
      properties = config().get(Properties.class);
      BattleRulesBuilder builder = config().node("default").get(BattleRulesBuilder.class);
      Registries.RULES.register(Objects.requireNonNull(builder == null ? null : builder.build()));
      loadAllAsync();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void save() {
    try {
      logger.info("Saving ares config");
      reference.save();
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
    }
  }

  public void close() {
    try {
      reference.close();
      listener.close();
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
    }
  }

  public BasicConfigurationNode config() {
    return reference.node();
  }

  public Properties properties() {
    return properties;
  }

  public CompletableFuture<Void> loadAllAsync() {
    return CompletableFuture.runAsync(this::loadAll).exceptionally(t -> {
      logger.error(t.getMessage(), t);
      return null;
    });
  }

  private void loadAll() {
    IOException firstError = null;
    int errorCount = 0;
    Collection<Path> paths = new ArrayList<>();
    try (Stream<Path> stream = Files.walk(Path.of(directory, "types"), 1)) {
      stream.filter(this::isValidFile).forEach(paths::add);
    } catch (IOException e) {
      firstError = e;
      errorCount++;
    }
    for (Path p : paths) {
      try {
        BattleRules rules = loadRules(p);
        if (rules != null) {
          Registries.RULES.register(rules);
        }
      } catch (IOException e) {
        if (firstError == null) {
          firstError = e;
        }
        errorCount++;
      }
    }
    if (firstError != null) {
      if (errorCount == 1) {
        throw new RuntimeException(firstError);
      } else if (errorCount > 1) {
        throw new RuntimeException(String.format("IOException (and %d more)", errorCount - 1), firstError);
      }
    }
  }

  private @Nullable BattleRules loadRules(Path path) throws IOException {
    GsonConfigurationLoader loader = GsonConfigurationLoader.builder().path(path).build();
    BattleRulesBuilder builder = loader.load().get(BattleRulesBuilder.class);
    return builder == null ? null : builder.build();
  }

  private boolean isValidFile(Path path) {
    return path.getFileName().toString().endsWith(SUFFIX);
  }

  private boolean fileExists(String name) {
    Path file = Path.of(directory, name + SUFFIX);
    return Files.exists(file);
  }
}
