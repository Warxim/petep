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
package com.warxim.petep.extension.internal.scripter.gui;

import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupController;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupsController;
import com.warxim.petep.extension.internal.common.rulegroup.intercept.RuleInterceptorModule;
import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.intercept.ScripterInterceptorModule;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.extension.internal.scripter.rule.ScriptGroupManager;
import com.warxim.petep.helper.ExtensionHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Pair;

import java.io.IOException;

/**
 * Controller for managing scripter groups.
 */
public class ScripterController extends RuleGroupsController<Script> {
    private ScriptHelperFactory scriptHelperFactory;

    /**
     * Constructs scripter controller.
     * @param groupManager Manager for working with groups of rules
     * @param extensionHelper Helper for extensions (for accessing configured modules]
     * @param scriptHelperFactory Manager of modifier scripter helpers
     */
    public ScripterController(ScriptGroupManager groupManager,
                              ExtensionHelper extensionHelper,
                              ScriptHelperFactory scriptHelperFactory) {
        super("Scripter", groupManager, extensionHelper);
        this.scriptHelperFactory = scriptHelperFactory;
    }

    @Override
    protected Pair<Node, RuleGroupController<Script>> createGroupTabNode(RuleGroup<Script> group) throws IOException {
        var fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/extension/internal/scripter/ScriptGroup.fxml")
        );

        var controller = new ScriptGroupController(group, scriptHelperFactory);
        fxmlLoader.setController(controller);

        return new Pair<>(fxmlLoader.load(), controller);
    }

    @Override
    protected Class<? extends RuleInterceptorModule<Script>> getInterceptorModuleClass() {
        return ScripterInterceptorModule.class;
    }
}
