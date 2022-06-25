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
package com.warxim.petep.extension.internal.repeater.gui.tab;

import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.internal.common.gui.ReloadableController;
import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.history.gui.view.HistoryView;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.gui.control.pducontrol.PduControl;
import com.warxim.petep.gui.control.pducontrol.PduControlButton;
import com.warxim.petep.gui.control.pducontrol.PduControlConfig;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for Repeater tab.
 * <p>Controls two parts:</p>
 * <ul>
 *     <li>PDU view/editor</li>
 *     <li>History view</li>
 * </ul>
 * <p>Repeater tab controller is reloadable, so it is possible to have multiple tabs, without having all fully loaded.</p>
 */
public class RepeaterTabController extends ReloadableController implements PetepListener {
    private final ExtensionHelper extensionHelper;
    private final HistoryApi historyApi;

    private SerializedPdu serializedPdu;
    private HistoryView historyView;
    private HistoryFilter historyFilter;

    @FXML
    private AnchorPane pduPane;
    @FXML
    private AnchorPane historyPane;

    /**
     * Constructs repeater tab controller.
     * @param serializedPdu Serialized PDU to show in the tab
     * @param historyFilter Filter for history in the tab
     * @param historyApi API for accessing the historic database
     * @param extensionHelper Extension helper
     */
    public RepeaterTabController(
            SerializedPdu serializedPdu,
            HistoryFilter historyFilter,
            HistoryApi historyApi,
            ExtensionHelper extensionHelper) {
        this.serializedPdu = serializedPdu;
        this.historyFilter = historyFilter;
        this.historyApi = historyApi;
        this.extensionHelper = extensionHelper;
    }

    /**
     * Obtains serialized PDU from underlying repeater control.
     * @return Serialized PDU
     */
    public Optional<SerializedPdu> getSerializedPdu() {
        var children = pduPane.getChildren();
        if (!children.isEmpty()) {
            var child = (PduControl) children.get(0);
            var maybeSerializedPdu = child.getSerializedPdu();
            if (maybeSerializedPdu.isPresent()) {
                serializedPdu = maybeSerializedPdu.get();
            }
        }
        return Optional.ofNullable(serializedPdu);
    }

    /**
     * Obtains currently used history filter.
     * @return History filter
     */
    public HistoryFilter getHistoryFilter() {
        if (historyView != null) {
            historyFilter = historyView.getFilter();
        }
        return historyFilter;
    }

    @Override
    protected void handleLoad() {
        loadHistory();
        loadPduControl();
    }

    @Override
    protected void handleUnload() {
        if (!pduPane.getChildren().isEmpty()) {
            var child = (PduControl) pduPane.getChildren().get(0);
            var maybeSerializedPdu = child.getSerializedPdu();
            if (maybeSerializedPdu.isPresent()) {
                serializedPdu = maybeSerializedPdu.get();
            }
            unloadPduControl();
        }
        unloadHistory();
    }

    /**
     * Loads control for PDUs.
     */
    private void loadPduControl() {
        if (!pduPane.getChildren().isEmpty()) {
            // If PDU control is loaded, leave right away
            return;
        }

        try {
            // Create PDU control
            var pduControl = new PduControl();
            pduControl.init(
                    extensionHelper,
                    PduControlConfig.builder()
                            .editorButtons(List.of(
                                    new PduControlButton("Send", this::onSendClick)
                            ))
                            .build()
            );
            // Set PDU to the control
            pduControl.setSerializedPdu(serializedPdu);
            // Show the control in PDU pane
            setChild(pduPane, pduControl);
        } catch (IOException e) {
            Dialogs.createExceptionDialog(
                    "Could not load PDU control",
                    "Repeater could not load PDU control template!",
                    e
            );
            Logger.getGlobal().log(Level.SEVERE, "Could not load PDU Control!", e);
        }
    }

    /**
     * Unloads PDU control.
     */
    private void unloadPduControl() {
        pduPane.getChildren().clear();
    }

    /**
     * Loads history view.
     */
    private void loadHistory() {
        if (historyApi == null) {
            return;
        }

        if (historyView != null) {
            return;
        }

        historyView = historyApi.createView(historyFilter);
        setChild(historyPane, historyView.getNode());
    }

    /**
     * Unloads history view.
     */
    private void unloadHistory() {
        if (historyApi == null) {
            return;
        }
        if (historyPane.getChildren().isEmpty()) {
            return;
        }
        historyFilter = historyView.getFilter();
        historyPane.getChildren().clear();
        historyView.destroy();
        historyView = null;
    }

    /**
     * Sets node as a child to pane and make it fill it by setting anchors.
     */
    private void setChild(Pane parent, Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        parent.getChildren().setAll(node);
    }

    /**
     * Handles send button click.
     */
    private void onSendClick(ActionEvent event) {
        var child = (PduControl) pduPane.getChildren().get(0);
        var maybePdu = child.validateAndGetPdu();
        if (maybePdu.isEmpty()) {
            return;
        }
        var pdu = maybePdu.get();
        pdu.getProxy().getHelper().processPdu(pdu);
    }
}
