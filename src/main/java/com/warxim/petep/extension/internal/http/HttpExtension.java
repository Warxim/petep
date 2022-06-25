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
package com.warxim.petep.extension.internal.http;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.http.modifier.addheader.AddHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.modifier.removeheader.RemoveHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.modifier.replaceheader.ReplaceHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.proxy.HttpProxyModuleFactory;
import com.warxim.petep.extension.internal.http.tagger.hasheader.HasHeaderSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.headercontains.HeaderContainsSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.ishttp.IsHttpSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.iswebsocket.IsWebSocketSubruleFactory;
import com.warxim.petep.extension.internal.modifier.ModifierApi;
import com.warxim.petep.extension.internal.tagger.TaggerApi;
import com.warxim.petep.helper.ExtensionHelper;

import java.util.logging.Logger;

/**
 * Extension for basic HTTP/WebSockets support.
 */
public final class HttpExtension extends Extension {
    /**
     * HTTP extension constructor.
     * @param path Path to the extension
     */
    public HttpExtension(String path) {
        super(path);

        Logger.getGlobal().info("HTTP extension loaded.");
    }

    /**
     * Initializes HTTP extension (registers HTTP proxy module).
     */
    @Override
    public void init(ExtensionHelper helper) {
        helper.registerProxyModuleFactory(new HttpProxyModuleFactory(this));

        Logger.getGlobal().info("HTTP extension registered.");
    }

    @Override
    public String getCode() {
        return "http";
    }

    @Override
    public String getName() {
        return "HTTP extension";
    }

    @Override
    public String getDescription() {
        return "HTTP extension adds HTTP proxy to PETEP.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void beforeInit(ExtensionHelper helper) {
        // Register tagger factories
        var maybeTagger = helper.getExtension("tagger");
        if (maybeTagger.isPresent()) {
            registerTaggerFactories((TaggerApi) maybeTagger.get());
        }

        // Register modifier factories
        var maybeModifier = helper.getExtension("modifier");
        if (maybeModifier.isPresent()) {
            registerModifierFactories((ModifierApi) maybeModifier.get());
        }
    }

    /**
     * Registers HTTP tagger factories to Tagger extension.
     */
    private void registerTaggerFactories(TaggerApi api) {
        if (!api.registerSubruleFactory(new IsWebSocketSubruleFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register IsWebsocket tag subrule.");
        }

        if (!api.registerSubruleFactory(new IsHttpSubruleFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register IsHTTP tag subrule.");
        }

        if (!api.registerSubruleFactory(new HasHeaderSubruleFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register HasHeader tag subrule.");
        }

        if (!api.registerSubruleFactory(new HeaderContainsSubruleFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register HeaderContains tag subrule.");
        }
    }

    /**
     * Registers HTTP modifier factories to Modifier extension.
     */
    private void registerModifierFactories(ModifierApi api) {
        if (!api.registerModifierFactory(new RemoveHeaderModifierFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register RemoveHeader modifier.");
        }

        if (!api.registerModifierFactory(new AddHeaderModifierFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register AddHeader modifier.");
        }

        if (!api.registerModifierFactory(new ReplaceHeaderModifierFactory())) {
            Logger.getGlobal().info("HTTP Extension could not register ReplaceHeader modifier.");
        }

    }
}
