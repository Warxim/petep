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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.gui.control.PduEditor;
import com.warxim.petep.helper.PetepHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Catcher controller. */
public final class CatcherController implements Initializable {
  private static final int QUEUE_CHECK_PERIOD_MS = 500;

  private final PetepHelper petepHelper;

  /** Current PDU. */
  private PDU pdu;

  /** PDU queue. */
  private final PduQueue queue;

  /** Timer for checking queue for new PDUs. */
  private Timer timer;

  /** State of catcher (on, transition, off). */
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

  /** Catcher controller constructor. */
  public CatcherController(PetepHelper petepHelper) {
    this.petepHelper = petepHelper;
    this.queue = new PduQueue();

    // Stopped by default.
    state = CatcherState.OFF;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    pduEditor.init(petepHelper);

    setDisableEditor(true);
  }

  public void stop() {
    if (timer != null) {
      timer.cancel();
    }
  }

  private void setDisableEditor(boolean value) {
    forwardButton.setDisable(value);
    dropButton.setDisable(value);
    pduEditor.setDisable(value);
  }

  private void checkQueue() {
    Platform.runLater(() -> {
      if (pdu == null) {
        pdu = queue.poll();

        // No PDU in queue.
        if (pdu == null) {
          return;
        }

        // PDU has no_catch tag and does not have catch tag. (Let it be processed
        // automatically in PETEP.)
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
      }
    });
  }

  private void disable() {
    Platform.runLater(() -> {
      setDisableEditor(true);

      pdu = null;
    });
  }

  @FXML
  private void onInterceptStartStopButtonClick(ActionEvent event) {
    if (state == CatcherState.ON) {
      // Stop catcher.
      // Update GUI.
      state = CatcherState.TRANSITION;
      stateLabel.setText("...");

      // Process current PDU in PETEP.
      if (pdu != null) {
        petepHelper.processPdu(pdu);
      }

      // Reset GUI.
      disable();

      // Clear
      pduEditor.clear();

      // Process PDUs from queue in PETEP.
      while ((pdu = queue.poll()) != null) {
        petepHelper.processPdu(pdu);
      }

      // Cancel timer.
      if (timer != null) {
        timer.cancel();
        timer = null;
      }

      // Update GUI.
      stateLabel.setText("OFF");
      startStopButton.setText("START");

      state = CatcherState.OFF;
    } else {
      // Start catcher.
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
  }

  @FXML
  private void onDropButtonClick(ActionEvent event) {
    disable();
  }

  @FXML
  private void onForwardButtonClick(ActionEvent event) {
    // Process PDU in PETEP.
    petepHelper.processPdu(pduEditor.getPdu());

    // Reset GUI.
    disable();

    // Check for new PDU.
    checkQueue();
  }

  /** Adds PDU to catcher queue. */
  public void catchPdu(PDU data) {
    queue.add(data);
  }

  /** Returns catcher state. */
  public CatcherState getState() {
    return state;
  }
}
