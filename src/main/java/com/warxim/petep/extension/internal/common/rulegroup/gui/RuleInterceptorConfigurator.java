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
package com.warxim.petep.extension.internal.common.rulegroup.gui;

import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManager;
import com.warxim.petep.extension.internal.common.rulegroup.config.RuleInterceptorConfig;
import com.warxim.petep.gui.common.DisplayFunctionStringConverter;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;

/**
 * Rule interceptor configurator.
 * @param <R> Rule type for this interceptor configurator
 */
public final class RuleInterceptorConfigurator<R extends Rule> extends ConfigPane<RuleInterceptorConfig> {
    /**
     * Rule manager that handles all rule groups.
     */
    private final RuleGroupManager<RuleGroup<R>> manager;

    @FXML
    private ComboBox<RuleGroup<R>> groupInput;

    /**
     * Rule interceptor configurator constructor.
     * @param manager Manager that manages rule groups (contains all rule groups)
     * @throws IOException If the dialog template could not be loaded
     */
    public RuleInterceptorConfigurator(RuleGroupManager<RuleGroup<R>> manager) throws IOException {
        super("/fxml/extension/internal/common/rulegroup/RuleInterceptorConfigurator.fxml");
        this.manager = manager;

        groupInput.getItems().setAll(manager.getList());
        groupInput.setConverter(new DisplayFunctionStringConverter<>(RuleGroup::getName));
        groupInput.getSelectionModel().selectLast();
    }

    @Override
    public RuleInterceptorConfig getConfig() {
        return new RuleInterceptorConfig(groupInput.getSelectionModel().getSelectedItem().getCode());
    }

    @Override
    public void setConfig(RuleInterceptorConfig config) {
        var maybeRuleGroup = manager.get(config.getRuleGroupCode());
        if (maybeRuleGroup.isEmpty()) {
            return;
        }
        groupInput.getSelectionModel().select(maybeRuleGroup.get());
    }

    @Override
    public boolean isValid() {
        if (groupInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Replace group required", "You have to select replace group.");
            return false;
        }

        return true;
    }
}
