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
package com.warxim.petep.gui.dialog;

import java.io.IOException;
import com.warxim.petep.gui.GuiBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

/** Simple dialog class for creation of custom dialogs. */
public abstract class SimpleInputDialog<T> extends Dialog<T> {
  /** Creates simple input dialog from specified template. */
  public SimpleInputDialog(String template, String title, String okText) throws IOException {
    setTitle(title);
    setResizable(true);

    // Load template.
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(template));
    fxmlLoader.setController(this);
    ScrollPane root = new ScrollPane(fxmlLoader.load());

    root.setFitToHeight(true);
    root.setFitToWidth(true);

    DialogPane dialogPane = getDialogPane();
    dialogPane.getStylesheets().add("/css/Main.css");
    dialogPane.getStyleClass().add("simple-dialog");
    dialogPane.setContent(root);

    // Set icon.
    ((Stage) dialogPane.getScene().getWindow()).getIcons()
        .add(GuiBundle.getInstance().getPetepIcon());

    // Create buttons.
    ButtonType saveButtonType = new ButtonType(okText, ButtonData.OK_DONE);
    dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, saveButtonType);

    // Create bindings.
    dialogPane.lookupButton(saveButtonType)
        .addEventFilter(ActionEvent.ACTION, this::onOkButtonClick);

    setResultConverter(this::convertResult);
  }

  /** Returns result of obtainResult if user has not clicked on cancel. Returns null otherwise. */
  private T convertResult(ButtonType buttonType) {
    if (buttonType == ButtonType.CANCEL) {
      return null;
    }
    return this.obtainResult();
  }

  /** Handles save button click. */
  protected void onOkButtonClick(ActionEvent ae) {
    if (isValid()) {
      return;
    }

    ae.consume();
  }

  /** Returns result. */
  protected abstract T obtainResult();

  /** Checks validity of inputs. */
  protected abstract boolean isValid();
}
