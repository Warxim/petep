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
package com.warxim.petep.common;

import com.warxim.petep.extension.PetepAPI;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Static class containing important constants, which are used across the whole PETEP
 */
@PetepAPI
public final class Constant {
    // PETEP version
    public static final String VERSION = "2.3.0";

    // PETEP web
    public static final String WEB = "https://petep.warxim.com/";

    // Charset
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    public static final Charset FILE_CHARSET = StandardCharsets.UTF_8;

    // Time & date format
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    // Configuration files
    public static final String WIZARD_CONFIG_FILE = "petep.json";

    public static final String PROJECT_CONFIG_DIRECTORY = "conf";
    public static final String PROJECT_CONFIG_FILE = "project.json";
    public static final String EXTENSIONS_CONFIG_FILE = "extensions.json";
    public static final String PROXIES_CONFIG_FILE = "proxies.json";
    public static final String INTERCEPTORS_C2S_CONFIG_FILE = "interceptors-C2S.json";
    public static final String INTERCEPTORS_S2C_CONFIG_FILE = "interceptors-S2C.json";

    public static final String PROJECT_TEMPLATE_DIRECTORY = "project_template";
    public static final String PRESETS_DIRECTORY = "presets";

    // Internal extensions
    public static final List<String> INTERNAL_EXTENSIONS = List.of(
            "logger",
            "tcp",
            "udp",
            "http",
            "external_http_proxy",
            "connection_view",
            "tagger",
            "catcher",
            "modifier",
            "history",
            "scripter",
            "repeater",
            "deluder"
    );

    // Configuration items
    public static final String CONFIG_ITEM_FACTORY = "factory";
    public static final String CONFIG_ITEM_ENABLED = "enabled";
    public static final String CONFIG_ITEM_STORE = "store";
    public static final String CONFIG_ITEM_CONFIG = "config";
    public static final String CONFIG_ITEM_CODE = "code";
    public static final String CONFIG_ITEM_PATH = "path";
    public static final String CONFIG_ITEM_NAME = "name";
    public static final String CONFIG_ITEM_DESCRIPTION = "description";

    /**
     * No need for instance creation.
     */
    private Constant() {
    }
}
