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

import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.persistence.Storable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Extension utils.
 */
public final class ExtensionUtils {
    private ExtensionUtils() {
    }

    /**
     * Determines store type using reflections from object that implements {@link Storable} interface.
     * @param object Object in which to find type of storable data
     * @return Type or empty optional if extension does not contain store.
     */
    public static Optional<Type> getStoreType(Object object) {
        if (!(object instanceof Storable)) {
            return Optional.empty();
        }

        return getGenericTypeArgument(object.getClass(), Storable.class);
    }

    /**
     * Determines configuration type using reflections from object that implements {@link Configurable} interface.
     * @param object Object in which to find type of config data
     * @return Type or empty optional if extension does not contain configuration.
     */
    public static Optional<Type> getConfigType(Object object) {
        if (!(object instanceof Configurable)) {
            return Optional.empty();
        }

        return getGenericTypeArgument(object.getClass(), Configurable.class);
    }

    /**
     * Determines configuration type using reflections from object that implements {@link Configurator} interface.
     * @param object Object in which to find type of config data
     * @return Type or empty optional if extension does not contain configuration.
     */
    public static Optional<Type> getConfiguratorType(Object object) {
        if (!(object instanceof Configurator)) {
            return Optional.empty();
        }

        return getGenericTypeArgument(object.getClass(), Configurator.class);
    }

    /**
     * Finds type of generic argument in interface (type).
     */
    private static Optional<Type> getGenericTypeArgument(Class<?> clazz, Type type) {
        var genericInterfaces = clazz.getGenericInterfaces();

        for (var genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType
                    && ((ParameterizedType) genericInterface).getRawType() == type) {
                return Optional.of(((ParameterizedType) genericInterface).getActualTypeArguments()[0]);
            }
        }

        var superClass = clazz.getSuperclass();
        if (superClass != null) {
            return getGenericTypeArgument(superClass, type);
        }

        return Optional.empty();
    }
}
