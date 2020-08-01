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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.gui.RuleGroupsGuiFactory;
import com.warxim.petep.extension.internal.modifier.config.ModifierConfig;
import com.warxim.petep.extension.internal.modifier.config.ModifyRuleConfig;
import com.warxim.petep.extension.internal.modifier.config.ModifyRuleGroupConfig;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactoryManager;
import com.warxim.petep.extension.internal.modifier.factory.internal.replace.ReplacerFactory;
import com.warxim.petep.extension.internal.modifier.gui.ModifierController;
import com.warxim.petep.extension.internal.modifier.intercept.ModifierInterceptorModuleFactory;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRuleGroupManager;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.persistence.Storable;

public final class ModifierExtension extends Extension
    implements Storable<ModifierConfig>, ModifierApi {
  private final ModifierFactoryManager moduleManager;
  private final ModifyRuleGroupManager groupManager;

  private ExtensionHelper helper;
  private ModifierConfig config;

  public ModifierExtension(String path) {
    super(path);

    moduleManager = new ModifierFactoryManager();
    groupManager = new ModifyRuleGroupManager();
  }

  @Override
  public void init(ExtensionHelper helper) {
    this.helper = helper;

    moduleManager.registerFactory(new ReplacerFactory());

    initGroupManager();

    helper.registerInterceptorModuleFactory(new ModifierInterceptorModuleFactory(this));
  }

  @Override
  public void initGui(GuiHelper helper) {
    try {
      helper.registerTab("Modifier", RuleGroupsGuiFactory
          .createRoleGroupsNode(new ModifierController(groupManager, this.helper, moduleManager)));
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
    return "0.5";
  }

  public ModifyRuleGroupManager getRuleGroupManager() {
    return groupManager;
  }

  @Override
  public ModifierConfig saveStore() {
    List<ModifyRuleGroupConfig> groups = new ArrayList<>(groupManager.size());

    for (RuleGroup<ModifyRule> group : groupManager.getList()) {
      List<ModifyRuleConfig> rules = new ArrayList<>(group.size());

      for (ModifyRule rule : group.getRules()) {
        rules.add(new ModifyRuleConfig(rule.getName(), rule.getDescription(), rule.isEnabled(),
            rule.getTag(), rule.getModifier().getFactory().getCode(),
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

  private void initGroupManager() {
    Gson gson = new GsonBuilder().create();

    if (config != null) {
      // Process groups.
      for (ModifyRuleGroupConfig groupConfig : config.getGroups()) {
        RuleGroup<ModifyRule> group = new RuleGroup<>(groupConfig.getCode(), groupConfig.getName());

        // Process rules.
        for (ModifyRuleConfig ruleConfig : groupConfig.getRules()) {
          ModifierFactory module = moduleManager.getFactory(ruleConfig.getFactoryCode());

          // No module found.
          if (module == null) {
            Logger.getGlobal()
                .severe("Could not load module " + ruleConfig.getFactoryCode() + " - rule "
                    + ruleConfig.getName() + " not loaded!");
            continue;
          }

          // Add rule.
          try {
            group.addRule(new ModifyRule(ruleConfig.getName(), ruleConfig.getDescription(),
                ruleConfig.isEnabled(), ruleConfig.getTag(), module
                    .createModifier(gson.fromJson(ruleConfig.getData(), module.getConfigType()))));
          } catch (JsonParseException e) {
            Logger.getGlobal()
                .log(Level.SEVERE, "Could not load rule " + ruleConfig.getName() + "!", e);
          }
        }

        groupManager.add(group);
      }

      config = null;
    }
  }

  @Override
  public boolean registerModifierFactory(ModifierFactory module) {
    return moduleManager.registerFactory(module);
  }
}
