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
package com.warxim.petep.gui.controller;

import com.warxim.petep.Bundle;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.gui.controller.log.LogController;
import com.warxim.petep.gui.controller.settings.SettingsController;
import com.warxim.petep.gui.dialog.AboutDialog;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.ProjectInfoDialog;
import com.warxim.petep.gui.guide.GuideDialog;
import com.warxim.petep.helper.DefaultGuiHelper;
import com.warxim.petep.util.GuiUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application controller.
 */
public final class ApplicationController implements Initializable {
    /**
     * Main application tabs.
     */
    @FXML
    private TabPane tabs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize tabs.
        initTabs();

        // Setup GUI bundle.
        GuiBundle.getInstance().setApplicationController(this);

        // Initialize extensions GUI.
        Bundle.getInstance()
                .getExtensionManager()
                .initGui(new DefaultGuiHelper(GuiBundle.getInstance()));
    }

    /**
     * Registers new tab to main tabs.
     * @param title Title of the tab
     * @param node Node to be added as a child into the tab
     */
    public void registerTab(String title, Node node) {
        GuiUtils.addTabToTabPane(tabs, title, node);
    }

    /**
     * Registers new tab to main tabs.
     * @param title Title of the tab
     * @param node Node to be added as a child into the tab
     * @param order Order of the tab (where should the tab be placed)
     */
    public void registerTab(String title, Node node, int order) {
        GuiUtils.addTabToTabPane(tabs, title, node, order);
    }

    /**
     * Registers new tab to main tabs.
     * @param node Child node of the tab that should be removed
     */
    public void unregisterTab(Node node) {
        GuiUtils.removeTabFromTabPane(tabs, node);
    }

    /**
     * Shows application about dialog.
     */
    @FXML
    private void showAbout(ActionEvent e) {
        AboutDialog.show();
    }

    /**
     * Shows application guide dialog.
     */
    @FXML
    private void showGuide(ActionEvent event) {
        try {
            new GuideDialog().show();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not open guide dialog", e);
        }
    }

    /**
     * Shows project info dialog.
     */
    @FXML
    private void onProjectInfoMenuClick(ActionEvent event) {
        try {
            new ProjectInfoDialog(
                    Bundle.getInstance().getProject(),
                    Bundle.getInstance().getExtensionManager().getList()
            ).show();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not open project info dialog", e);
        }
    }

    /**
     * Exits application dialog.
     */
    @FXML
    private void onExitMenuClick(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Saves project dialog.
     */
    @FXML
    private void onSaveMenuClick(ActionEvent event) {
        try {
            Bundle.getInstance().save();

            Dialogs.createInfoDialog("Project saved", "Project has been saved!");
        } catch (ConfigurationException e) {
            Dialogs.createExceptionDialog("Save fault", "Project could not be saved!", e);
            Logger.getGlobal().log(Level.SEVERE, "Exception during save", e);
        }
    }

    /**
     * Initializes main tabs.
     */
    private void initTabs() {
        registerLogTab();
        registerSettingsTab();
    }

    /**
     * Loads and registers settings tab.
     */
    private void registerSettingsTab() {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tab/Settings.fxml"));
            fxmlLoader.setController(new SettingsController());
            registerTab("Settings", fxmlLoader.load(), GuiConstant.SETTINGS_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not add settings tab", e);
        }
    }

    /**
     * Loads and registers log tab.
     */
    private void registerLogTab() {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tab/Log.fxml"));
            fxmlLoader.setController(new LogController());
            registerTab("Log", fxmlLoader.load(), GuiConstant.LOG_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not add log tab", e);
        }
    }
}
