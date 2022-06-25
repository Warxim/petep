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
package com.warxim.petep.extension.internal.scripter;

import com.google.gson.Gson;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManager;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManagerProvider;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupsGuiFactory;
import com.warxim.petep.extension.internal.scripter.config.*;
import com.warxim.petep.extension.internal.scripter.gui.ScripterController;
import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.intercept.ScripterInterceptorModuleFactory;
import com.warxim.petep.extension.internal.scripter.rule.*;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.GsonUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scripter extension
 * <p>
 *     Adds support for creating simple scripts for processing PDUs.
 * </p>
 */
public class ScripterExtension extends Extension implements Storable<ScripterConfig>, RuleGroupManagerProvider<Script>  {
    private final ScriptGroupManager groupManager;
    private ExtensionHelper extensionHelper;
    private ScripterConfig config;
    private ScriptHelperFactory scriptHelperFactory;

    /**
     * Constructs Scripter extension
     * @param path Path to the extension
     */
    public ScripterExtension(String path) {
        super(path);
        groupManager = new ScriptGroupManager();
    }

    @Override
    public void init(ExtensionHelper helper) {
        helper.registerInterceptorModuleFactory(new ScripterInterceptorModuleFactory(this));
        this.extensionHelper = helper;
        scriptHelperFactory = new ScriptHelperFactory(helper);
        initGroupManager();
    }

    @Override
    public void initGui(GuiHelper helper) {
        try {
            var controller = new ScripterController(groupManager, extensionHelper, scriptHelperFactory);
            helper.registerTab(
                    "Scripter",
                    RuleGroupsGuiFactory.createRoleGroupsNode(controller),
                    GuiConstant.SCRIPTER_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load modifier tab!", e);
        }

        helper.registerGuide(new ScripterGuide());
    }

    @Override
    public String getCode() {
        return "scripter";
    }

    @Override
    public String getName() {
        return "Scripter";
    }

    @Override
    public String getDescription() {
        return "Scripter allows user to create scripts in various languages to intercept PDUs.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public RuleGroupManager<RuleGroup<Script>> getRuleGroupManager() {
        return groupManager;
    }

    @Override
    public ScripterConfig saveStore() {
        var groups = new LinkedList<ScriptGroupConfig>();

        for (var group : groupManager.getList()) {
            var scripts = new LinkedList<ScriptConfig>();

            for (var script : group.getRules()) {
                var data = getScriptData(script);
                scripts.add(new ScriptConfig(
                        script.getName(),
                        script.getDescription(),
                        script.isEnabled(),
                        script.getLanguage(),
                        script.getType(),
                        data));
            }

            groups.add(new ScriptGroupConfig(group.getCode(), group.getName(), scripts));
        }

        return new ScripterConfig(groups);
    }

    @Override
    public void loadStore(ScripterConfig config) {
        this.config = config;
    }

    /**
     * Initializes script group manager from configuration.
     */
    private void initGroupManager() {
        var gson = GsonUtils.getGson();

        if (config != null) {
            for (var groupConfig : config.getGroups()) {
                var group = new RuleGroup<Script>(groupConfig.getCode(), groupConfig.getName());

                for (var scriptConfig : groupConfig.getRules()) {
                    try {
                        group.addRule(createScript(scriptConfig, gson));
                    } catch (RuntimeException e) {
                        Logger.getGlobal().log(Level.SEVERE, "Could not load script!", e);
                    }
                }

                groupManager.add(group);
            }

            config = null;
        }
    }

    /**
     * Creates script data from given script for storing it in configuration.
     */
    private ScriptData getScriptData(Script script) {
        if (script.getType() == ScriptType.STRING) {
            return new StringScriptData(((StringScript) script).getScript());
        }
        return new FileScriptData(((FileScript) script).getPath());
    }

    /**
     * Creates script from given configuration.
     */
    private Script createScript(ScriptConfig config, Gson gson) {
        if (config.getType() == ScriptType.STRING) {
            var data = gson.fromJson(config.getData(), StringScriptData.class);
            return new StringScript(
                    config.getName(),
                    config.getDescription(),
                    config.isEnabled(),
                    config.getLanguage(),
                    scriptHelperFactory,
                    data.getString());
        }

        var data = gson.fromJson(config.getData(), FileScriptData.class);
        return new FileScript(
                config.getName(),
                config.getDescription(),
                config.isEnabled(),
                config.getLanguage(),
                scriptHelperFactory,
                data.getPath());
    }
}
