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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.moros.ares.model.battle.BattleRules;
import me.moros.ares.model.battle.BattleRules.BattleRulesBuilder;
import me.moros.ares.registry.Registries;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.WatchServiceListener;

public final class ConfigManager {
  public static final String RULES_SUFFIX = ".json";

  private final Logger logger;
  private final Path rulesDirectory;
  private final WatchServiceListener listener;
  private final ConfigurationReference<CommentedConfigurationNode> reference;
  private final Gson gson;
  private final Properties properties;

  public ConfigManager(Logger logger, String directory) {
    this.logger = logger;
    this.rulesDirectory = Path.of(directory, "rules");
    gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    Path path = Path.of(directory, "ares.conf");
    try {
      Files.createDirectories(rulesDirectory);
      listener = WatchServiceListener.create();
      reference = listener.listenToConfiguration(f -> HoconConfigurationLoader.builder().path(f).build(), path);
      properties = config().get(Properties.class);
      loadAllAsync().thenRun(() -> logger.info("Loaded " + Registries.RULES.size() + " battle rules"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (!fileExists("default")) {
      BattleRulesBuilder builder = BattleRules.builder("default");
      saveRules(builder);
      Registries.RULES.register(Objects.requireNonNull(builder.build()));
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

  public CommentedConfigurationNode config() {
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
    try (Stream<Path> stream = Files.walk(rulesDirectory, 1)) {
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
    JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
    BattleRulesBuilder builder = gson.fromJson(reader, BattleRulesBuilder.class);
    if (builder == null) {
      logger.warn("Invalid currency data: " + path);
    } else {
      return builder.build();
    }
    return null;
  }

  private CompletableFuture<Boolean> saveRules(BattleRulesBuilder rules) {
    return CompletableFuture.supplyAsync(() -> {
      Path path = rulesDirectory.resolve(rules.name() + RULES_SUFFIX);
      try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
        gson.toJson(rules, writer);
        return true;
      } catch (IOException e) {
        throw new CompletionException(e);
      }
    }).exceptionally(e -> {
      e.printStackTrace();
      return false;
    });
  }

  private boolean isValidFile(Path path) {
    return path.getFileName().toString().endsWith(RULES_SUFFIX);
  }

  private boolean fileExists(String name) {
    return Files.exists(rulesDirectory.resolve(name + RULES_SUFFIX));
  }
}
