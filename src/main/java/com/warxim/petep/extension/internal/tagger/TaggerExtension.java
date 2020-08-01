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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.gui.RuleGroupsGuiFactory;
import com.warxim.petep.extension.internal.tagger.config.TagRuleConfig;
import com.warxim.petep.extension.internal.tagger.config.TagRuleGroupConfig;
import com.warxim.petep.extension.internal.tagger.config.TagSubruleConfig;
import com.warxim.petep.extension.internal.tagger.config.TaggerConfig;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
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
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.persistence.Storable;

/** Tagger extension. */
public final class TaggerExtension extends Extension
    implements Storable<TaggerConfig>, TaggerApi {
  private ExtensionHelper helper;

  private final TagRuleGroupManager groupManager;
  private final TagSubruleFactoryManager factoryManager;

  private TaggerConfig config;

  /** Tagger extension constructor. */
  public TaggerExtension(String path) {
    super(path);

    factoryManager = new TagSubruleFactoryManager();
    groupManager = new TagRuleGroupManager();
  }

  @Override
  public void init(ExtensionHelper helper) {
    this.helper = helper;

    registerInternalModules();

    initGroupManager();

    helper.registerInterceptorModuleFactory(new TagInterceptorModuleFactory(this));
  }

  @Override
  public void initGui(GuiHelper helper) {
    try {
      helper.registerTab("Tagger", RuleGroupsGuiFactory
          .createRoleGroupsNode(new TaggerController(groupManager, this.helper, factoryManager)));
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
    return "0.9";
  }

  @Override
  public TaggerConfig saveStore() {
    List<TagRuleGroupConfig> groups = new ArrayList<>(groupManager.size());

    for (RuleGroup<TagRule> group : groupManager.getList()) {
      List<TagRuleConfig> rules = new ArrayList<>(group.size());

      for (TagRule rule : group.getRules()) {
        List<TagSubruleConfig> subrules = new ArrayList<>(rule.getSubrules().size());

        for (TagSubrule subrule : rule.getSubrules()) {
          subrules.add(new TagSubruleConfig(subrule.getFactory().getCode(), subrule.getData()));
        }

        rules.add(new TagRuleConfig(rule.getName(), rule.getDescription(), rule.isEnabled(),
            rule.getTag(), subrules, rule.getExpressionString()));
      }
      groups.add(new TagRuleGroupConfig(group.getCode(), group.getName(), rules));
    }

    return new TaggerConfig(groups);
  }

  @Override
  public void loadStore(TaggerConfig store) {
    config = store;
  }

  private void registerInternalModules() {
    registerSubruleFactory(new ContainsSubruleFactory());
    registerSubruleFactory(new StartsWithSubruleFactory());
    registerSubruleFactory(new EndsWithSubruleFactory());
    registerSubruleFactory(new HasTagSubruleFactory());
    registerSubruleFactory(new SizeSubruleFactory());
    registerSubruleFactory(new DestinationSubruleFactory());
    registerSubruleFactory(new ProxySubruleFactory(helper));
  }

  private void initGroupManager() {
    Gson gson = new GsonBuilder().create();

    if (config != null) {
      // Process groups.
      for (TagRuleGroupConfig groupConfig : config.getGroups()) {
        RuleGroup<TagRule> group = new RuleGroup<>(groupConfig.getCode(), groupConfig.getName());

        // Process rules.
        for (TagRuleConfig ruleConfig : groupConfig.getRules()) {
          List<TagSubrule> subrules = new ArrayList<>();

          boolean isError = false;

          // Process subrules.
          for (TagSubruleConfig subruleConfig : ruleConfig.getSubrules()) {
            TagSubruleFactory factory = factoryManager.getFactory(subruleConfig.getFactoryCode());

            // No factory found.
            if (factory == null) {
              Logger.getGlobal()
                  .severe("Could not load factory " + subruleConfig.getFactoryCode() + "!");

              isError = true;

              break;
            }

            // Add subrule.
            try {
              subrules.add(factory
                  .createSubrule(gson.fromJson(subruleConfig.getData(), factory.getConfigType())));
            } catch (JsonParseException e) {
              Logger.getGlobal()
                  .log(Level.SEVERE, "Could not load rule " + ruleConfig.getName() + "!", e);

              isError = true;

              break;
            }
          }

          // Skip rule.
          if (isError) {
            Logger.getGlobal()
                .severe(
                    "Ignoring rule " + ruleConfig.getName() + ", because there an error occured!");
            continue;
          }

          // Add rule.
          try {
            group.addRule(new TagRule(ruleConfig.getName(), ruleConfig.getDescription(),
                ruleConfig.isEnabled(), ruleConfig.getTag(), subrules,
                ruleConfig.getExpressionString()));
          } catch (InvalidExpressionException e) {
            Logger.getGlobal()
                .log(Level.SEVERE, "Could not load rule " + ruleConfig.getName() + "!", e);
          }
        }

        groupManager.add(group);
      }

      config = null;
    }
  }

  public TagRuleGroupManager getRuleGroupManager() {
    return groupManager;
  }

  @Override
  public boolean registerSubruleFactory(TagSubruleFactory factory) {
    return factoryManager.registerFactory(factory);
  }
}
