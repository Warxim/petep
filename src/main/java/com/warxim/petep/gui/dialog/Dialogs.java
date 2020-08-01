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

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;

/** Dialog helper. */
@PetepAPI
public final class Dialogs {
  private static Image defaultIcon;

  private Dialogs() {}

  public static void setDefaultIcon(Image icon) {
    defaultIcon = icon;
  }

  public static Image getDefaultIcon() {
    return defaultIcon;
  }

  /** Creates simple error dialog. */
  public static void createErrorDialog(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(title);
    alert.setContentText(message);
    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);
    alert.showAndWait();
  }

  /** Creates simple success dialog. */
  public static void createInfoDialog(String title, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(title);
    alert.setContentText(message);
    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);
    alert.showAndWait();
  }

  /** Creates simple exception dialog. */
  public static void createExceptionDialog(String title, String message, Exception exception) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(title);
    alert.setContentText(message);

    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    String exceptionText = sw.toString();

    TextArea textArea = new TextArea(exceptionText);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    Label label = new Label("The exception stacktrace was:");

    GridPane expContent = new GridPane();
    expContent.setMaxWidth(Double.MAX_VALUE);
    expContent.add(label, 0, 0);
    expContent.add(textArea, 0, 1);

    alert.getDialogPane().setExpandableContent(expContent);

    alert.showAndWait();
  }

  /** Creates simple yes / no dialog. */
  public static boolean createYesOrNoDialog(String title, String message) {
    Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO,
        ButtonType.CANCEL);
    alert.setTitle(title);
    alert.setHeaderText(title);
    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

    alert.showAndWait();

    return alert.getResult() == ButtonType.YES;
  }

  /** Creates simple text input dialog. */
  public static String createTextInputDialog(String title, String message) {
    return createTextInputDialog(title, message, "");
  }

  /** Creates simple text input dialog. */
  public static String createTextInputDialog(String title, String message, String value) {
    TextInputDialog dialog = new TextInputDialog(value);
    dialog.setTitle(title);
    dialog.setHeaderText(title);
    dialog.setContentText(message);

    ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
      return result.get();
    }

    return null;
  }

  /** Creates dialog for text pair input. */
  public static Pair<String, String> createTextPairDialog(
      String title,
      String firstLabel,
      String secondLabel) {
    return createTextPairDialog(title, firstLabel, secondLabel, "", "");
  }

  /** Creates dialog for text pair input with default values. */
  public static Pair<String, String> createTextPairDialog(
      String title,
      String firstLabel,
      String secondLabel,
      String firstValue,
      String secondValue) {
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(title);

    ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    Label keyLabel = new Label(firstLabel);
    TextField keyInput = new TextField(firstValue);
    keyInput.setMaxWidth(Double.MAX_VALUE);

    Label valueLabel = new Label(secondLabel);
    TextField valueInput = new TextField(secondValue);
    valueInput.setMaxWidth(Double.MAX_VALUE);

    GridPane grid = new GridPane();
    grid.setMaxWidth(Double.MAX_VALUE);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    grid.add(keyLabel, 0, 0);
    grid.add(keyInput, 1, 0);
    GridPane.setHgrow(keyInput, Priority.ALWAYS);

    grid.add(valueLabel, 0, 1);
    grid.add(valueInput, 1, 1);
    GridPane.setHgrow(valueInput, Priority.ALWAYS);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter((ButtonType buttonType) -> {
      if (buttonType == ButtonType.OK) {
        return new Pair<>(keyInput.getText(), valueInput.getText());
      }

      return null;
    });

    Optional<Pair<String, String>> result = dialog.showAndWait();
    if (result.isPresent()) {
      return result.get();
    }

    return null;
  }


  /** Creates simple new version dialog. */
  public static void createNewVersionDialog(String newVersion) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("New version available");
    alert.setHeaderText("New version available");
    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

    // Current Version
    Label currentVersionLabel = new Label("Current Version: ");
    currentVersionLabel.getStyleClass().add("input-label");
    Label currentVersionValueLabel = new Label(Constant.VERSION);

    // New Version
    Label newVersionLabel = new Label("New Version: ");
    newVersionLabel.getStyleClass().add("input-label");
    Label newVersionValueLabel = new Label(newVersion);

    // Link
    Label linkLabel = new Label("Link: ");
    linkLabel.getStyleClass().add("input-label");
    Hyperlink link = new Hyperlink(Constant.WEB);
    link.setOnAction((ActionEvent event) -> {
      try {
        Desktop.getDesktop().browse(new URI(Constant.WEB));
      } catch (IOException | URISyntaxException e) {
        Logger.getGlobal().log(Level.SEVERE, "Could not open PETEP link.");
      }
    });

    // Grid
    GridPane grid = new GridPane();
    grid.setMaxWidth(Double.MAX_VALUE);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    // Current version
    grid.add(currentVersionLabel, 0, 0);
    grid.add(currentVersionValueLabel, 1, 0);

    // New version
    grid.add(newVersionLabel, 0, 1);
    grid.add(newVersionValueLabel, 1, 1);

    // Link
    grid.add(linkLabel, 0, 2);
    grid.add(link, 1, 2);

    alert.getDialogPane().setContent(grid);

    alert.showAndWait();
  }
}
