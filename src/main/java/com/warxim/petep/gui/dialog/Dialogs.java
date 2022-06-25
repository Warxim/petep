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

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.GuiBundle;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dialog helper.
 */
@PetepAPI
public final class Dialogs {
    private static Image defaultIcon;

    private Dialogs() {
    }

    /**
     * Gets default icon of PETEP.
     * @return JavaFX image object
     */
    public static Image getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * Sets default icon of PETEP.
     * @param icon Icon to be used through the whole PETEP.
     */
    public static void setDefaultIcon(Image icon) {
        defaultIcon = icon;
    }

    /**
     * Creates simple error dialog (with error icon).
     * @param title Dialog title
     * @param message Dialog content message
     */
    public static void createErrorDialog(String title, String message) {
        var alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);
        alert.showAndWait();
    }

    /**
     * Creates simple success dialog (with info icon).
     * @param title Dialog title
     * @param message Dialog content message
     */
    public static void createInfoDialog(String title, String message) {
        var alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);
        alert.showAndWait();
    }

    /**
     * Creates simple exception dialog.
     * @param title Dialog title
     * @param message Dialog content message
     * @param exception Exception to be displayed in textarea.
     */
    public static void createExceptionDialog(String title, String message, Exception exception) {
        var alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        var textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        var label = new Label("The exception stacktrace was:");

        var expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Creates simple yes / no / cancel dialog.
     * @param title Dialog title
     * @param message Dialog content message
     * @return  {@code Optional.of(true)} if yes has been clicked;<br>
     *          {@code Optional.of(false)} if no has been clicked;<br>
     *          {@code Optional.empty()} otherwise
     */
    public static Optional<Boolean> createYesOrNoOrCancelDialog(String title, String message) {
        var alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(title);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        alert.showAndWait();

        var result = alert.getResult();
        if (result == ButtonType.YES) {
            return Optional.of(Boolean.TRUE);
        } else if (result == ButtonType.NO) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    /**
     * Creates simple yes / no dialog.
     * @param title Dialog title
     * @param message Dialog content message
     * @return  {@code true} if yes has been clicked;<br>
     *          {@code false} if no has been clicked
     */
    public static boolean createYesOrNoDialog(String title, String message) {
        var alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(title);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }

    /**
     * Creates simple text input dialog. (Empty default value.)
     * @param title Dialog title
     * @param message Dialog content message
     * @return Entered value. (Empty optional if the dialog has been closed/canceled.]
     */
    public static Optional<String> createTextInputDialog(String title, String message) {
        return createTextInputDialog(title, message, "");
    }

    /**
     * Creates simple text input dialog.
     * @param title Dialog title
     * @param message Dialog content message
     * @param value Default input value
     * @return Entered value. (Empty optional if the dialog has been closed/canceled.)
     */
    public static Optional<String> createTextInputDialog(String title, String message, String value) {
        TextInputDialog dialog = new TextInputDialog(value);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setContentText(message);

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        return dialog.showAndWait();
    }

    /**
     * Creates simple choice dialog.
     * @param title Dialog title
     * @param message Dialog content message
     * @param choices Choices to display in dialog
     * @param value Default selected choice
     * @return Chosen value. (Empty optional if the dialog has been closed/canceled.)
     */
    public static <T> Optional<T> createChoiceDialog(String title, String message, List<T> choices, T value) {
        var dialog = new ChoiceDialog<>(value, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setContentText(message);

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        return dialog.showAndWait();
    }

    /**
     * Creates simple choice dialog.
     * @param title Dialog title
     * @param message Dialog content message
     * @param choices Choices to display in dialog
     * @return Chosen value. (Empty optional if the dialog has been closed/canceled.)
     */
    public static <T> Optional<T> createChoiceDialog(String title, String message, List<T> choices) {
        return createChoiceDialog(title, message, choices, null);
    }

    /**
     * Creates dialog for text pair input.
     * <p>Lets user enter two string values.</p>
     * @param title Dialog title
     * @param firstLabel Label of first input
     * @param secondLabel Label of second input
     * @return Entered values. (Empty optional if the dialog has been closed/canceled.)
     */
    public static Optional<Pair<String, String>> createTextPairDialog(
            String title,
            String firstLabel,
            String secondLabel) {
        return createTextPairDialog(title, firstLabel, secondLabel, "", "");
    }

    /**
     * Creates dialog for text pair input with default values.
     * <p>Lets user enter two string values.</p>
     * @param title Dialog title
     * @param firstLabel Label of first input
     * @param secondLabel Label of second input
     * @param firstValue Default value for first input
     * @param secondValue Default value for second input
     * @return Entered values. (Empty optional if the dialog has been closed/canceled.)
     */
    public static Optional<Pair<String, String>> createTextPairDialog(
            String title,
            String firstLabel,
            String secondLabel,
            String firstValue,
            String secondValue) {
        var dialog = new Dialog<Pair<String, String>>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        var keyLabel = new Label(firstLabel);
        var keyInput = new TextField(firstValue);
        keyInput.setMaxWidth(Double.MAX_VALUE);

        var valueLabel = new Label(secondLabel);
        var valueInput = new TextField(secondValue);
        valueInput.setMaxWidth(Double.MAX_VALUE);

        var grid = new GridPane();
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

        return dialog.showAndWait();
    }


    /**
     * Creates simple new version dialog.
     * <p>Shows dialog with information about new available version.</p>
     * @param newVersion New version
     */
    public static void createNewVersionDialog(String newVersion) {
        var alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("New version available");
        alert.setHeaderText("New version available");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultIcon);

        // Current Version
        var currentVersionLabel = new Label("Current Version: ");
        currentVersionLabel.getStyleClass().add("input-label");
        var currentVersionValueLabel = new Label(Constant.VERSION);

        // New Version
        var newVersionLabel = new Label("New Version: ");
        newVersionLabel.getStyleClass().add("input-label");
        var newVersionValueLabel = new Label(newVersion);

        // Link
        var linkLabel = new Label("Link: ");
        linkLabel.getStyleClass().add("input-label");
        var link = new Hyperlink(Constant.WEB);
        link.setOnAction(Dialogs::onWebClick);

        // Grid
        var grid = new GridPane();
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

    /**
     * Opens PETEP website.
     */
    private static void onWebClick(ActionEvent event) {
        try {
            GuiBundle.getInstance().getHostServices().showDocument(Constant.WEB);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not open PETEP link.");
        }
    }
}
