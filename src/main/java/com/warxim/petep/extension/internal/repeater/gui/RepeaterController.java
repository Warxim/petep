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
package com.warxim.petep.extension.internal.repeater.gui;

import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.extension.internal.repeater.config.RepeaterConfig;
import com.warxim.petep.extension.internal.repeater.config.RepeaterTabConfig;
import com.warxim.petep.extension.internal.repeater.gui.dialog.CreateTabDialog;
import com.warxim.petep.extension.internal.repeater.gui.tab.RepeaterTabController;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.PetepHelper;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repeater controller for managing all repeater tabs.
 * <p>
 *     Contains tabs, which are automatically unloaded after constant period of time,
 *     so that it does not use so much memory and processing power.
 * </p>
 */
public class RepeaterController implements Initializable, PetepListener {
    /**
     * After how many seconds should the tab automatically unload.
     */
    private static final int AUTO_UNLOAD_DELAY_MS = 5 * 60 * 1000;

    @FXML
    private TabPane tabs;
    @FXML
    private Button createButton;

    private final Map<Tab, RepeaterTabController> tabControllers;
    private final HistoryApi historyApi;
    private final AtomicInteger nextTabNumber;
    private final ExtensionHelper extensionHelper;
    private PetepHelper petepHelper;

    /**
     * Constructs repeater controller.
     * @param extensionHelper Extension helper
     * @param historyApi API for working with historic database
     */
    public RepeaterController(ExtensionHelper extensionHelper, HistoryApi historyApi) {
        this.extensionHelper = extensionHelper;
        this.historyApi = historyApi;
        petepHelper = null;
        nextTabNumber = new AtomicInteger(0);
        tabControllers = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabs.tabDragPolicyProperty().setValue(TabPane.TabDragPolicy.REORDER);
        tabs.getSelectionModel().selectedItemProperty().addListener(this::onTabChange);
        createButton.setDisable(true);
    }

    /**
     * Initializes the controller (adds tabs to it)
     * @param config Config containing tab configuration
     */
    public void init(RepeaterConfig config) {
        var tabsConfig = config.getTabs();
        for (var tabConfig : tabsConfig) {
            createTab(
                    tabConfig.getTitle(),
                    tabConfig.getSerializedPdu(),
                    tabConfig.getHistoryFilter());
        }
    }

    /**
     * Saves repeater state to configuration. (Saves all tabs.)
     * @return Repeater configuration
     */
    public RepeaterConfig save() {
        var tabsConfig = new LinkedList<RepeaterTabConfig>();
        for (var tab : tabs.getTabs()) {
            var tabController = tabControllers.get(tab);
            var maybeSerializedPdu = tabController.getSerializedPdu();
            if (maybeSerializedPdu.isEmpty()) {
                continue;
            }
            var tabConfig = new RepeaterTabConfig(
                    tab.getText(),
                    maybeSerializedPdu.get(),
                    tabController.getHistoryFilter()
            );
            tabsConfig.add(tabConfig);
        }
        return new RepeaterConfig(tabsConfig);
    }

    /**
     * Creates repeater tab with the specified serialized PDU.
     * @param serializedPdu Serialized PDU to be displayed in the tab
     */
    public void createTab(SerializedPdu serializedPdu) {
        Platform.runLater(
                () -> createTab(
                        String.valueOf(nextTabNumber.incrementAndGet()),
                        serializedPdu,
                        HistoryFilter.all()));
    }

    @Override
    public void afterCoreStart(PetepHelper helper) {
        createButton.setDisable(false);
        petepHelper = helper;
        reloadTabs();
    }

    @Override
    public void beforeCoreStop(PetepHelper helper) {
        createButton.setDisable(true);
        reloadTabs();
        petepHelper = null;
    }

    /**
     * Destroys the controller by unloading all tabs.
     */
    public void destroy() {
        tabControllers.values().stream()
                .filter(RepeaterTabController::isLoaded)
                .forEach(RepeaterTabController::unload);
    }

    /**
     * Shows dialog for creating new repeater tab and adds the tab to the repeater.
     */
    @FXML
    private void onCreateButtonClick(ActionEvent event) {
        try {
            var dialog = new CreateTabDialog(extensionHelper, petepHelper);
            var result = dialog.showAndWait();

            if (result.isEmpty()) {
                return;
            }

            var tabConfig = result.get();
            createTab(tabConfig.getTitle(), tabConfig.getSerializedPdu(), tabConfig.getHistoryFilter());
        } catch (IOException e) {
            Dialogs.createExceptionDialog("Repeater error", "Could not load repeater dialog!", e);
            Logger.getGlobal().log(Level.SEVERE, "Could not load repeater dialog!", e);
        }
    }

    /**
     * Reloads all loaded tabs.
     */
    private void reloadTabs() {
        tabControllers.values().stream()
                .filter(RepeaterTabController::isLoaded)
                .forEach(RepeaterTabController::reload);
    }

    /**
     * Creates new tab for given serialized PDU and adds it to the repeater tabs.
     */
    private void createTab(String title, SerializedPdu serializedPdu, HistoryFilter historyFilter) {
        try {
            var fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/extension/internal/repeater/RepeaterTab.fxml"));
            var controller = new RepeaterTabController(
                    serializedPdu,
                    historyFilter,
                    historyApi,
                    extensionHelper);
            fxmlLoader.setController(controller);
            var node = (Node) fxmlLoader.load();
            var tab = new Tab(title, node);
            initTabContextMenu(tab);
            tabControllers.put(tab, controller);
            tab.setOnClosed(this::onTabClose);
            tabs.getTabs().add(tab);
        } catch (IOException e) {
            Dialogs.createExceptionDialog("Repeater tab error", "Could not load repeater!", e);
            Logger.getGlobal().log(Level.SEVERE, "Could not load repeater!", e);
        }
    }

    /**
     * Initializes tab context menu for renaming/duplicating tabs.
     */
    private void initTabContextMenu(Tab tab) {
        var renameTabItem = new MenuItem("Rename");
        renameTabItem.setOnAction(event -> onRenameTabClick(tab));

        var duplicateTabItem = new MenuItem("Duplicate");
        duplicateTabItem.setOnAction(event -> onDuplicateTabClick(tab));

        var menu = new ContextMenu();
        menu.getItems().addAll(renameTabItem, duplicateTabItem);
        tab.setContextMenu(menu);
    }

    /**
     * Lets user rename the tab.
     */
    private void onRenameTabClick(Tab tab) {
        var title = Dialogs.createTextInputDialog("Change tab name", "New tab name:", tab.getText());
        if (title.isPresent()) {
            tab.setText(title.get());
        }
    }

    /**
     * Duplicates tab.
     */
    private void onDuplicateTabClick(Tab tab) {
        var controller = tabControllers.get(tab);
        var filter = controller.getHistoryFilter();
        var maybeSerializedPdu = controller.getSerializedPdu();
        if (maybeSerializedPdu.isPresent()) {
            createTab(tab.getText() + " copy", maybeSerializedPdu.get(), filter);
        }
    }

    /**
     * Handles tab close event (unloads controller and removes it).
     */
    private void onTabClose(Event event) {
        var controller = tabControllers.get(event.getTarget());
        if (controller != null) {
            controller.unload();
        }
        tabControllers.remove(event.getTarget());
    }

    /**
     * Handles tab change event (loads/unloads controllers).
     */
    private void onTabChange(ObservableValue<? extends Tab> value, Tab previous, Tab current) {
        if (previous != null) {
            var previousController = tabControllers.get(previous);
            previousController.unload(AUTO_UNLOAD_DELAY_MS);
        }

        if (current != null) {
            var currentController = tabControllers.get(current);
            currentController.load();
        }
    }
}
