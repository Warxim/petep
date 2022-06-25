/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2020 Michal Válka
 *
 * This p am is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep.extension.internal.common.rulegroup.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

/**
 * GUI factory that creates rule groups node (node for controlling groups of rules).
 */
public class RuleGroupsGuiFactory {
    private RuleGroupsGuiFactory() {
    }

    /**
     * Creates rule groups component.
     * @param controller Controller for the component
     * @return Rule groups node
     * @throws IOException If the group controller template could not be loaded
     */
    public static Node createRoleGroupsNode(RuleGroupsController<?> controller) throws IOException {
        var fxmlLoader = new FXMLLoader(
                RuleGroupsGuiFactory.class
                        .getResource("/fxml/extension/internal/common/rulegroup/GroupsController.fxml"));
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }
}
