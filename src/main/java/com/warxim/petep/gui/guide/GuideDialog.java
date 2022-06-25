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
package com.warxim.petep.gui.guide;

import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.dialog.SimpleInfoDialog;
import com.warxim.petep.util.WebUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Guide dialog contains list view of all guides and handles their rendering using WebView component.
 */
public final class GuideDialog extends SimpleInfoDialog {
    @FXML
    private ListView<Guide> guideList;
    @FXML
    private WebView webView;

    /**
     * Constructs guide dialog.
     * @throws IOException If the dialog template could not be loaded
     */
    public GuideDialog() throws IOException {
        super("/fxml/guide/GuideDialog.fxml", "Guide");

        var webEngine = webView.getEngine();

        // Initialize list of guides.
        guideList.setItems(FXCollections.observableList(GuiBundle.getInstance().getGuideManager().getList()));

        // Run loadGuide(title, html) when Guide gets selected.
        guideList.getSelectionModel().selectedItemProperty().addListener(this::hangleGuideChange);

        webEngine.getLoadWorker().stateProperty().addListener(this::stateListener);

        // Load HTML template.
        try {
            webEngine.load(getClass().getResource("/html/guide/Guide.html").toURI().toURL().toString());
        } catch (URISyntaxException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load guide dialog!", e);
            return;
        }

        // Enable JavaScript.
        webEngine.setJavaScriptEnabled(true);
    }

    /**
     * Runs loadGuide(title, html) when Guide gets selected.
     */
    private void hangleGuideChange(
            ObservableValue<? extends Guide> observable,
            Guide oldValue,
            Guide newValue) {
        var script = "loadGuide("
                + WebUtils.toJavaScriptParam(newValue.getTitle())
                + ", "
                + WebUtils.toJavaScriptParam(newValue.getHtml())
                + ")";
        webView.getEngine().executeScript(script);
    }

    /**
     * Registers guide utils to JavaScript.
     */
    private void stateListener(
            ObservableValue<? extends Worker.State> observable,
            Worker.State oldValue,
            Worker.State newValue) {
        if (newValue != State.SUCCEEDED) {
            return;
        }
        guideList.getSelectionModel().selectFirst();
        var window = (JSObject) webView.getEngine().executeScript("window");
        window.setMember("petep", new GuideJS());
    }
}
