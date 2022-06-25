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
package com.warxim.petep.extension.internal.modifier;

import com.google.gson.JsonParseException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManagerProvider;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupsGuiFactory;
import com.warxim.petep.extension.internal.modifier.config.ModifierConfig;
import com.warxim.petep.extension.internal.modifier.config.ModifyRuleConfig;
import com.warxim.petep.extension.internal.modifier.config.ModifyRuleGroupConfig;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactoryManager;
import com.warxim.petep.extension.internal.modifier.factory.internal.replace.ReplacerFactory;
import com.warxim.petep.extension.internal.modifier.gui.ModifierController;
import com.warxim.petep.extension.internal.modifier.intercept.ModifierInterceptorModuleFactory;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRuleGroupManager;
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
 * Modifier extension.
 * <p>Adds support for creating automatic modification rules.</p>
 */
public final class ModifierExtension extends Extension implements Storable<ModifierConfig>, RuleGroupManagerProvider<ModifyRule>, ModifierApi {
    private final ModifierFactoryManager moduleManager;
    private final ModifyRuleGroupManager groupManager;

    private ExtensionHelper extensionHelper;
    private ModifierConfig config;

    /**
     * Constructs modifier extension.
     * @param path Path to the extension
     */
    public ModifierExtension(String path) {
        super(path);

        moduleManager = new ModifierFactoryManager();
        groupManager = new ModifyRuleGroupManager();
    }

    @Override
    public void init(ExtensionHelper helper) {
        this.extensionHelper = helper;

        moduleManager.registerFactory(new ReplacerFactory());

        initGroupManager();

        helper.registerInterceptorModuleFactory(new ModifierInterceptorModuleFactory(this));
    }

    @Override
    public void initGui(GuiHelper helper) {
        try {
            helper.registerTab(
                    "Modifier",
                    RuleGroupsGuiFactory.createRoleGroupsNode(
                            new ModifierController(groupManager, this.extensionHelper, moduleManager)),
                    GuiConstant.MODIFIER_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load modifier tab!", e);
        }

        helper.registerGuide(new ModifierGuide());
    }

    @Override
    public String getCode() {
        return "modifier";
    }

    @Override
    public String getName() {
        return "Modifier";
    }

    @Override
    public String getDescription() {
        return "Process PDUs using different algorithms for replacing etc.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public ModifyRuleGroupManager getRuleGroupManager() {
        return groupManager;
    }

    @Override
    public ModifierConfig saveStore() {
        var groups = new LinkedList<ModifyRuleGroupConfig>();

        for (var group : groupManager.getList()) {
            var rules = new LinkedList<ModifyRuleConfig>();

            for (var rule : group.getRules()) {
                rules.add(new ModifyRuleConfig(
                        rule.getName(),
                        rule.getDescription(),
                        rule.isEnabled(),
                        rule.getTag(),
                        rule.getModifier().getFactory().getCode(),
                        rule.getModifier().getData()));
            }

            groups.add(new ModifyRuleGroupConfig(group.getCode(), group.getName(), rules));
        }

        return new ModifierConfig(groups);
    }

    @Override
    public void loadStore(ModifierConfig store) {
        config = store;
    }

    @Override
    public boolean registerModifierFactory(ModifierFactory module) {
        return moduleManager.registerFactory(module);
    }

    /**
     * Initializes group manager from configuration.
     */
    private void initGroupManager() {
        if (config == null) {
            return;
        }

        var gson = GsonUtils.getGson();

        // Process groups.
        for (var groupConfig : config.getGroups()) {
            var group = new RuleGroup<ModifyRule>(groupConfig.getCode(), groupConfig.getName());

            // Process rules.
            for (var ruleConfig : groupConfig.getRules()) {
                var maybeFactory = moduleManager.getFactory(ruleConfig.getFactoryCode());
                if (maybeFactory.isEmpty()) {
                    Logger.getGlobal()
                            .severe(() -> "Could not load module "
                                    + ruleConfig.getFactoryCode()
                                    + " - rule "
                                    + ruleConfig.getName()
                                    + " not loaded!");
                    continue;
                }
                var factory = maybeFactory.get();

                // Add rule.
                try {
                    ModifierData modifierData;
                    if (factory.getConfigType().isPresent()) {
                        modifierData = gson.fromJson(ruleConfig.getData(), factory.getConfigType().get());
                    } else {
                        modifierData = null;
                    }
                    var modifier = factory.createModifier(modifierData);
                    group.addRule(new ModifyRule(
                            ruleConfig.getName(),
                            ruleConfig.getDescription(),
                            ruleConfig.isEnabled(),
                            ruleConfig.getTag(),
                            modifier));
                } catch (JsonParseException e) {
                    Logger.getGlobal().log(
                            Level.SEVERE,
                            e,
                            () -> "Could not load rule " + ruleConfig.getName() + "!");
                }
            }

            groupManager.add(group);
        }

        config = null;
    }
}
