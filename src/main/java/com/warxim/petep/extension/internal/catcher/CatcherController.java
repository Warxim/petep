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
package com.warxim.petep.extension.internal.catcher;

import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.gui.control.pdueditor.PduEditor;
import com.warxim.petep.gui.control.pdueditor.PduEditorConfig;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.PetepHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Catcher controller.
 */
public final class CatcherController implements Initializable, PetepListener {
    /**
     * After how many milliseconds to recheck queue for new PDU.
     */
    private static final int QUEUE_CHECK_PERIOD_MS = 100;

    /**
     * Extension helper for PDU editor.
     */
    private final ExtensionHelper extensionHelper;
    /**
     * PDU queue.
     */
    private final PduQueue queue;
    /**
     * PETEP helper for currently active core.
     */
    private PetepHelper petepHelper;
    /**
     * Current PDU that is beeing catched.
     */
    private PDU pdu;
    /**
     * Timer for checking queue for new PDUs.
     */
    private Timer timer;

    /**
     * State of catcher (on, transition, off).
     */
    private CatcherState state;

    /*
     * GUI
     */
    @FXML
    private PduEditor pduEditor;
    @FXML
    private Label stateLabel;
    @FXML
    private Button forwardButton;
    @FXML
    private Button dropButton;
    @FXML
    private Button startStopButton;

    /**
     * Catcher controller constructor.
     * @param extensionHelper Extension helper
     */
    public CatcherController(ExtensionHelper extensionHelper) {
        this.extensionHelper = extensionHelper;
        extensionHelper.registerPetepListener(this);
        this.queue = new PduQueue();

        // Stopped by default.
        state = CatcherState.OFF;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDisableEditor(true);
        startStopButton.setDisable(true);
        pduEditor.init(
                extensionHelper,
                PduEditorConfig.builder().build()
        );
    }

    @Override
    public void beforeCorePrepare(PetepHelper helper) {
        Platform.runLater(() -> {
            petepHelper = helper;
            startStopButton.setDisable(false);
        });
    }

    @Override
    public void beforeCoreStop(PetepHelper helper) {
        Platform.runLater(() -> {
            stopCatcher();
            petepHelper = null;
            startStopButton.setDisable(true);
        });
    }

    /**
     * Adds PDU to catcher queue.
     * @param data PDU to be added to catch queue
     */
    public void catchPdu(PDU data) {
        queue.add(data);
    }

    /**
     * Obtains catcher state.
     * @return State of the catcher
     */
    public CatcherState getState() {
        return state;
    }

    @FXML
    private void onInterceptStartStopButtonClick(ActionEvent event) {
        if (state == CatcherState.ON) {
            stopCatcher();
        } else {
            startCatcher();
        }
    }

    @FXML
    private void onDropButtonClick(ActionEvent event) {
        // Reset GUI.
        setDisableEditor(true);

        pdu = null;

        // Check for new PDU.
        checkQueue();
    }

    @FXML
    private void onForwardButtonClick(ActionEvent event) {
        var maybePdu = pduEditor.validateAndGetPdu();
        if (maybePdu.isEmpty()) {
            return;
        }

        // Process PDU in PETEP.
        petepHelper.processPdu(maybePdu.get());

        // Reset GUI.
        setDisableEditor(true);

        pdu = null;

        // Check for new PDU.
        checkQueue();
    }

    /**
     * Starts catching.
     */
    private void startCatcher() {
        // Create timer and schedule queue checking.
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkQueue();
            }
        }, 0, QUEUE_CHECK_PERIOD_MS);

        // Update GUI.
        stateLabel.setText("ON");
        startStopButton.setText("STOP");

        // Set state to ON.
        state = CatcherState.ON;
    }

    /**
     * Stops catching.
     */
    private void stopCatcher() {
        // Update GUI.
        state = CatcherState.TRANSITION;
        stateLabel.setText("...");

        // Process current PDU in PETEP.
        if (pdu != null) {
            petepHelper.processPdu(pdu);
            pdu = null;
        }

        // Reset GUI.
        setDisableEditor(true);

        // Clear
        pduEditor.clear();

        // Process PDUs from queue in PETEP.
        Optional<PDU> maybePdu;
        while ((maybePdu = queue.poll()).isPresent()) {
            pdu = maybePdu.get();
            petepHelper.processPdu(pdu);
        }
        pdu = null;

        // Cancel timer.
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        // Update GUI.
        stateLabel.setText("OFF");
        startStopButton.setText("START");

        state = CatcherState.OFF;
    }

    /**
     * Disables editor.
     */
    private void setDisableEditor(boolean value) {
        forwardButton.setDisable(value);
        dropButton.setDisable(value);
        pduEditor.setDisable(value);
    }

    /**
     * Checks whether there are any PDUs to be processed by the user.
     */
    private void checkQueue() {
        Platform.runLater(() -> {
            if (pdu != null) {
                return;
            }

            var maybePdu = queue.poll();
            if (maybePdu.isEmpty()) {
                return; // No PDU in queue.
            }
            pdu = maybePdu.get();

            // PDU has no_catch tag and does not have catch tag.
            // (Let it be processed automatically in PETEP.)
            if (pdu.hasTag("no_catch") && !pdu.hasTag("catch")) {
                petepHelper.processPdu(pdu);
                pdu = null;
                checkQueue();
                return;
            }

            // Set PDU to editor.
            pduEditor.setPdu(pdu);

            // Enable editor.
            setDisableEditor(false);
        });
    }
}
