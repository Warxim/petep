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
package com.warxim.petep.extension.internal.tagger.gui;

import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupController;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupsController;
import com.warxim.petep.extension.internal.common.rulegroup.intercept.RuleInterceptorModule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.intercept.TagInterceptorModule;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.extension.internal.tagger.rule.TagRuleGroupManager;
import com.warxim.petep.helper.ExtensionHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.util.Pair;

import java.io.IOException;

/**
 * Tagger controller.
 */
public final class TaggerController extends RuleGroupsController<TagRule> {
    private final TagSubruleFactoryManager factoryManager;

    @FXML
    private TabPane tabs;

    /**
     * Constructs tagger controller.
     * @param groupManager Manager for working with groups of rules
     * @param extensionHelper Helper for extensions (for accessing configured modules]
     * @param factoryManager Manager of tagger subrule factories
     */
    public TaggerController(
            TagRuleGroupManager groupManager,
            ExtensionHelper extensionHelper,
            TagSubruleFactoryManager factoryManager) {
        super("Tagger", groupManager, extensionHelper);
        this.factoryManager = factoryManager;
    }

    @Override
    protected Class<? extends RuleInterceptorModule<TagRule>> getInterceptorModuleClass() {
        return TagInterceptorModule.class;
    }

    @Override
    protected Pair<Node, RuleGroupController<TagRule>> createGroupTabNode(RuleGroup<TagRule> group) throws IOException {
        var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/extension/internal/tagger/TagRuleGroup.fxml"));
        var controller = new TagGroupController(group, factoryManager);
        fxmlLoader.setController(controller);
        return new Pair<>(fxmlLoader.load(), controller);
    }
}
