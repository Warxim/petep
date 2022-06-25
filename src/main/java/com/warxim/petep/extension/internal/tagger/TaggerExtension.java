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
package com.warxim.petep.extension.internal.tagger;

import com.google.gson.JsonParseException;
import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManagerProvider;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupsGuiFactory;
import com.warxim.petep.extension.internal.tagger.config.TagRuleConfig;
import com.warxim.petep.extension.internal.tagger.config.TagRuleGroupConfig;
import com.warxim.petep.extension.internal.tagger.config.TagSubruleConfig;
import com.warxim.petep.extension.internal.tagger.config.TaggerConfig;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.factory.internal.contains.ContainsSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.destination.DestinationSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.ends_with.EndsWithSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.has_tag.HasTagSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.proxy.ProxySubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.size.SizeSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.starts_with.StartsWithSubruleFactory;
import com.warxim.petep.extension.internal.tagger.gui.TaggerController;
import com.warxim.petep.extension.internal.tagger.intercept.TagInterceptorModuleFactory;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.extension.internal.tagger.rule.TagRuleGroupManager;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.GsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tagger extension.
 */
public final class TaggerExtension
        extends Extension
        implements Storable<TaggerConfig>, RuleGroupManagerProvider<TagRule>, TaggerApi {
    private final TagRuleGroupManager groupManager;
    private final TagSubruleFactoryManager factoryManager;
    private ExtensionHelper extensionHelper;
    private TaggerConfig config;

    /**
     * Tagger extension constructor.
     * @param path Path to the extension
     */
    public TaggerExtension(String path) {
        super(path);

        factoryManager = new TagSubruleFactoryManager();
        groupManager = new TagRuleGroupManager();
    }

    @Override
    public void init(ExtensionHelper helper) {
        this.extensionHelper = helper;

        registerInternalModules();

        initGroupManager();

        helper.registerInterceptorModuleFactory(new TagInterceptorModuleFactory(this));
    }

    @Override
    public void initGui(GuiHelper helper) {
        try {
            var controller = new TaggerController(groupManager, this.extensionHelper, factoryManager);
            helper.registerTab(
                    "Tagger",
                    RuleGroupsGuiFactory.createRoleGroupsNode(controller),
                    GuiConstant.TAGGER_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load replacer tab!", e);
        }

        helper.registerGuide(new TaggerGuide());
    }

    @Override
    public String getCode() {
        return "tagger";
    }

    @Override
    public String getName() {
        return "Tagger extension";
    }

    @Override
    public String getDescription() {
        return "Simple tagger extension.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public TaggerConfig saveStore() {
        var groups = new LinkedList<TagRuleGroupConfig>();

        for (var group : groupManager.getList()) {
            var rules = new LinkedList<TagRuleConfig>();

            for (var rule : group.getRules()) {
                var subrules = new LinkedList<TagSubruleConfig>();

                for (var subrule : rule.getSubrules()) {
                    subrules.add(new TagSubruleConfig(subrule.getFactory().getCode(), subrule.getData()));
                }

                rules.add(new TagRuleConfig(
                        rule.getName(),
                        rule.getDescription(),
                        rule.isEnabled(),
                        rule.getTag(),
                        subrules,
                        rule.getExpressionString()));
            }

            groups.add(new TagRuleGroupConfig(group.getCode(), group.getName(), rules));
        }

        return new TaggerConfig(groups);
    }

    @Override
    public void loadStore(TaggerConfig store) {
        config = store;
    }

    @Override
    public TagRuleGroupManager getRuleGroupManager() {
        return groupManager;
    }

    @Override
    public boolean registerSubruleFactory(TagSubruleFactory factory) {
        return factoryManager.registerFactory(factory);
    }


    /**
     * Registers internal tag subrule modules.
     */
    private void registerInternalModules() {
        registerSubruleFactory(new ContainsSubruleFactory());
        registerSubruleFactory(new StartsWithSubruleFactory());
        registerSubruleFactory(new EndsWithSubruleFactory());
        registerSubruleFactory(new HasTagSubruleFactory());
        registerSubruleFactory(new SizeSubruleFactory());
        registerSubruleFactory(new DestinationSubruleFactory());
        registerSubruleFactory(new ProxySubruleFactory(extensionHelper));
    }

    /**
     * Initializes group manager from configuration.
     */
    private void initGroupManager() {
        if (config == null) {
            return;
        }

        // Process groups.
        for (var groupConfig : config.getGroups()) {
            var group = new RuleGroup<TagRule>(groupConfig.getCode(), groupConfig.getName());

            // Process rules.
            for (var ruleConfig : groupConfig.getRules()) {
                var maybeRule = loadTagRule(ruleConfig);
                if (maybeRule.isEmpty()) {
                    continue;
                }
                group.addRule(maybeRule.get());
            }

            groupManager.add(group);
        }

        config = null;
    }

    /**
     * Loads tag rule from tag rule config.
     */
    private Optional<TagRule> loadTagRule(TagRuleConfig ruleConfig) {
        var maybeTagSubrules = loadTagSubrules(ruleConfig);

        // Skip rule.
        if (maybeTagSubrules.isEmpty()) {
            Logger.getGlobal().severe(() -> String.format("Ignoring rule '%s', because there an error occured!", ruleConfig.getName()));
            return Optional.empty();
        }
        var subrules = maybeTagSubrules.get();

        // Add rule.
        try {
            return Optional.of(new TagRule(
                    ruleConfig.getName(),
                    ruleConfig.getDescription(),
                    ruleConfig.isEnabled(),
                    ruleConfig.getTag(),
                    subrules,
                    ruleConfig.getExpressionString()));
        } catch (InvalidExpressionException e) {
            Logger.getGlobal().log(Level.SEVERE, e, () -> String.format("Could not load rule '%s'!", ruleConfig.getName()));
        }
        return Optional.empty();
    }

    /**
     * Loads tag subrules from tag rule config.
     */
    private Optional<List<TagSubrule>> loadTagSubrules(TagRuleConfig ruleConfig) {
        var gson = GsonUtils.getGson();
        var subrules = new ArrayList<TagSubrule>();

        // Process subrules.
        for (var subruleConfig : ruleConfig.getSubrules()) {
            var maybeFactory = factoryManager.getFactory(subruleConfig.getFactoryCode());
            if (maybeFactory.isEmpty()) {
                Logger.getGlobal().severe(() -> String.format("Could not load factory '%s'!", subruleConfig.getFactoryCode()));
                return Optional.empty();
            }
            var factory = maybeFactory.get();

            // Add subrule.
            try {
                TagSubruleData data;
                if (factory.getConfigType().isPresent()) {
                    data = gson.fromJson(subruleConfig.getData(), factory.getConfigType().get());
                } else {
                    data = null;
                }
                subrules.add(factory.createSubrule(data));
            } catch (JsonParseException e) {
                Logger.getGlobal().log(Level.SEVERE, e, () -> String.format("Could not load rule '%s'!", ruleConfig.getName()));
                return Optional.empty();
            }
        }

        return Optional.of(subrules);
    }
}
