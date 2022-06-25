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
package com.warxim.petep.extension.internal.tagger.factory.internal.proxy;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.proxy.module.ProxyModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;

/**
 * Configurator for configuring "proxy" subrule data.
 */
public final class ProxySubruleConfigurator extends TagSubruleConfigurator {
    @FXML
    private ComboBox<ProxyModule> proxyInput;

    /**
     * Constructs tag subrule configurator for Proxy subrule.
     * @param helper Extension helper for obtaining configured proxy modules
     * @throws IOException If the template could not be loaded
     */
    public ProxySubruleConfigurator(ExtensionHelper helper) throws IOException {
        super("/fxml/extension/internal/tagger/factory/ProxySubrule.fxml");

        proxyInput.setItems(FXCollections.observableList(helper.getProxyModules()));
    }

    @Override
    public TagSubruleData getConfig() {
        return new ProxyData(proxyInput.getSelectionModel().getSelectedItem().getCode());
    }

    @Override
    public void setConfig(TagSubruleData config) {
        ObservableList<ProxyModule> modules = proxyInput.getItems();
        for (int i = 0; i < modules.size(); ++i) {
            if (modules.get(i).getCode().equals((((ProxyData) config).getProxyCode()))) {
                proxyInput.getSelectionModel().select(i);
                return;
            }
        }
    }

    @Override
    public boolean isValid() {
        if (proxyInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Proxy required", "You have to select proxy.");
            return false;
        }

        return true;
    }
}
