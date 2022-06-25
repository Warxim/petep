/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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

import com.google.gson.*;
import com.warxim.petep.extension.PetepAPI;

import java.nio.charset.Charset;

/**
 * Utils for using GSON library.
 */
@PetepAPI
public final class GsonUtils {
    /**
     * GSON instance for serializing/deserializing in PETEP.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Charset.class, createCharsetSerializer())
            .registerTypeAdapter(Charset.class, createCharsetDeserializer())
            .setPrettyPrinting()
            .create();

    private GsonUtils() {
    }

    /**
     * Obtains GSON instance.
     * @return GSON instance used throughout the whole application
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * Creates json serailizer for {@link Charset}.
     */
    private static JsonSerializer<Charset> createCharsetSerializer() {
        return (src, typeOfSrc, context) -> new JsonPrimitive(src.name());
    }

    /**
     * Creates json deserailizer for {@link Charset}.
     */
    private static JsonDeserializer<Charset> createCharsetDeserializer() {
        return (json, typeOfSrc, context) -> Charset.forName(json.getAsJsonPrimitive().getAsString());
    }
}
