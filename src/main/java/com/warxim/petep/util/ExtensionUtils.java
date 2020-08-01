/*
 * PEnetration TEsting Proxy (PETEP)
 * 
 * Copyright (C) 2020 Michal Válka
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.persistence.Storable;

/** Extension utils. */
public final class ExtensionUtils {
  private ExtensionUtils() {}

  /**
   * Determines store type using reflections.
   *
   * @returns Type or null if extension does not contain store.
   */
  public static Type getStoreType(Object object) {
    if (!(object instanceof Storable)) {
      return null;
    }

    Type[] genericInterfaces = object.getClass().getGenericInterfaces();

    for (Type genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType
          && ((ParameterizedType) genericInterface).getRawType() == Storable.class) {
        return ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
      }
    }

    return null;
  }

  /**
   * Determines configuration type using reflections.
   *
   * @returns Type or null if extension does not contain configuration.
   */
  public static Type getConfigType(Object object) {
    if (!(object instanceof Configurable)) {
      return null;
    }

    Type[] genericInterfaces = object.getClass().getGenericInterfaces();

    for (Type genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType
          && ((ParameterizedType) genericInterface).getRawType() == Configurable.class) {
        return ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
      }
    }

    return null;
  }

  /**
   * Determines configuration type using reflections.
   *
   * @returns Type or null if extension does not contain configuration.
   */
  public static Type getConfiguratorType(Object object) {
    if (!(object instanceof Configurator)) {
      return null;
    }

    Type[] genericInterfaces = object.getClass().getGenericInterfaces();

    for (Type genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType
          && ((ParameterizedType) genericInterface).getRawType() == Configurator.class) {
        return ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
      }
    }

    return null;
  }
}
