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

package me.moros.ares.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class Replacer<T> {
  private final Map<String, Replacement<T>> replacements;
  private final List<T> data;
  private final Pattern pattern;

  public Replacer(Map<String, Replacement<T>> replacements, Collection<T> data) {
    this.replacements = replacements;
    this.data = List.copyOf(data);
    pattern = Pattern.compile(String.join("|", replacements.keySet()));
  }

  private @Nullable Replacement<T> get(String value) {
    return replacements.get(value);
  }

  public String apply(T input, String value) {
    return pattern.matcher(value).replaceAll(r -> replaceToken(input, r.group()));
  }

  private String replaceToken(T input, String token) {
    Replacement<T> replacement = get(token);
    return replacement == null ? token : replacement.apply(input);
  }

  public String replaceAll(String value) {
    Matcher matcher = pattern.matcher(value);
    int length = value.length();
    int lastIndex = 0;
    StringBuilder output = new StringBuilder();
    Map<String, Integer> indexes = new HashMap<>();
    while (matcher.find()) {
      String token = matcher.group();
      Replacement<T> replacement = get(token);
      if (replacement != null) {
        int index = indexes.compute(token, this::increment);
        output.append(value, lastIndex, matcher.start()).append(replacement.apply(data.get(index % data.size())));
        lastIndex = matcher.end();
      }
    }
    if (lastIndex < length) {
      output.append(value, lastIndex, length);
    }
    return output.toString();
  }

  private Integer increment(String key, @Nullable Integer value) {
    return value == null ? 0 : value + 1;
  }
}
