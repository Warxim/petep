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
package com.warxim.petep.gui.control.pducontrol;

import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.control.pdueditor.PduEditor;
import com.warxim.petep.gui.control.SerializedPduView;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.util.PduUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component for displaying PduEditor / SerializedPduView automatically.
 * <p>
 *     After this control is initialized using {@link #init(ExtensionHelper, PduControlConfig)},
 *     displays given PDU/SerializedPdu in the view/editor automatically.
 * </p>
 * <p>
 *     The functionality is dependent on the state of core:
 * </p>
 * <ul>
 *     <li>Active core - displays PDUs in {@link PduEditor} if possible, or {@link SerializedPduView} if PDUs need fixing,</li>
 *     <li>Inactive core - displays PDUs in {@link SerializedPduView},</li>
 * </ul>
 */
@PetepAPI
public class PduControl extends AnchorPane implements PetepListener {
    private static final String FIX_VIEW_USER_DATA = "fix-view";

    private ExtensionHelper extensionHelper;
    private PduControlConfig config;
    private SerializedPdu serializedPdu;
    private PetepHelper petepHelper;

    /**
     * Box for displaying action buttons.
     */
    @FXML
    private HBox buttonBox;
    /**
     * Pane for component (view/editor).
     */
    @FXML
    private AnchorPane componentPane;

    /**
     * Constructs PDU component.
     * <p>For full use, you have to initialize it using init method.</p>
     * @throws IOException If the template could not be loaded
     */
    public PduControl() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/PduComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();
    }

    /**
     * Initializes the PDU control.
     * <p>Can be called only once!</p>
     * @param extensionHelper Extension helper
     * @param config Configuration describing how the control should work
     */
    public void init(ExtensionHelper extensionHelper, PduControlConfig config) {
        if (this.extensionHelper != null) {
            throw new IllegalStateException("Cannot initialize PduComponent twice!");
        }
        this.extensionHelper = extensionHelper;
        this.config = config;
        this.extensionHelper.registerPetepListener(this);

        // Determine correct initialization method based on core state
        var state = this.extensionHelper.getPetepState();
        if (state == PetepState.STARTED) {
            // Core is started, try to use PETEP helper for loading
            var maybeHelper = this.extensionHelper.getPetepHelper();
            if (maybeHelper.isPresent()) {
                setPetepHelper(maybeHelper.get());
                return;
            }
        }
        unsetPetepHelper();
    }

    /**
     * Checks whether the control contains valid PDU.
     * @return  {@code true} if PETEP core is running and the PDU is valid;
     *          {@code false} otherwise
     */
    public boolean isValid() {
        return getIfEditor(editor -> Optional.of(editor.isValid())).orElse(false);
    }

    /**
     * Obtains PDU from the control.
     * @return  Currently displayed PDU;
     *         {@code Optional.empty()} in case that the PDU could not be obtained (inactive core, corrupted PDU, ...)
     */
    public Optional<PDU> getPdu() {
        return getIfEditor(editor -> {
            var maybePdu = editor.getPdu();
            if (maybePdu.isEmpty()) {
                return Optional.empty();
            }
            var pdu = maybePdu.get();
            serializedPdu = PduUtils.serializePdu(pdu);
            return Optional.of(pdu);
        });
    }

    /**
     * Obtains PDU from the control if valid.
     * @return  Currently displayed PDU if valid;
     *         {@code Optional.empty()} in case that the PDU could not be obtained (inactive core, invalid PDU, ...)
     */
    public Optional<PDU> validateAndGetPdu() {
        return getIfEditor(editor -> {
            if (!editor.isValid()) {
                return Optional.empty();
            }

            var maybePdu = editor.getPdu();
            if (maybePdu.isEmpty()) {
                return Optional.empty();
            }
            var pdu = maybePdu.get();
            serializedPdu = PduUtils.serializePdu(pdu);
            return Optional.of(pdu);
        });
    }

    /**
     * Obtains serialized PDU from the control.
     * @return Currently displayed serialized PDU
     */
    public Optional<SerializedPdu> getSerializedPdu() {
        return getFromEditorOrView(
                editor -> {
                    var maybePdu = editor.getPdu();
                    if (maybePdu.isEmpty()) {
                        return Optional.empty();
                    }
                    var pdu = maybePdu.get();
                    serializedPdu = PduUtils.serializePdu(pdu).copy();
                    return Optional.ofNullable(serializedPdu);
                },
                view -> {
                    var maybeSerializablePdu = view.getSerializedPdu();
                    if (maybeSerializablePdu.isPresent()) {
                        serializedPdu = maybeSerializablePdu.get().copy();
                    }
                    return maybeSerializablePdu;
                }
        );
    }

    /**
     * Sets PDU to the control.
     * <p>Shows the PDU using {@link PduEditor} or {@link SerializedPduView}.</p>
     * @param pdu PDU to be displayed
     */
    public void setPdu(PDU pdu) {
        if (pdu == null) {
            this.serializedPdu = null;
            return;
        }

        this.serializedPdu = PduUtils.serializePdu(pdu);
        if (petepHelper == null) {
            showView(false);
            ifView(view -> view.setSerializedPdu(serializedPdu.copy()));
            return;
        }

        // Load PDU editor with the created PDU
        showEditor();
        ifEditor(editor -> editor.setPdu(pdu));
    }

    /**
     * Sets serialized PDU to the control.
     * <p>Shows the serialized PDU using {@link PduEditor} or {@link SerializedPduView}.</p>
     * @param serializedPdu Serialized PDU to be displayed
     */
    public void setSerializedPdu(SerializedPdu serializedPdu) {
        if (serializedPdu == null) {
            this.serializedPdu = null;
            return;
        }

        this.serializedPdu = serializedPdu.copy();
        if (petepHelper == null) {
            showView(false);
            ifView(view -> view.setSerializedPdu(serializedPdu));
            return;
        }

        var maybePdu = PduUtils.deserializePdu(serializedPdu, petepHelper);
        if (maybePdu.isEmpty()) {
            showView(true);
            ifView(view -> view.setSerializedPdu(serializedPdu));
            return;
        }

        // Load PDU editor with the created PDU
        var pdu = maybePdu.get();
        showEditor();
        ifEditor(editor -> editor.setPdu(pdu));
    }

    /**
     * Clears the control.
     */
    public void clear() {
        processUsingEditorOrView(
                PduEditor::clear,
                SerializedPduView::clear
        );
    }

    @Override
    public void afterCoreStart(PetepHelper helper) {
        Platform.runLater(() -> setPetepHelper(helper));
    }

    @Override
    public void beforeCoreStop(PetepHelper helper) {
        Platform.runLater(this::unsetPetepHelper);
    }

    /**
     * Sets PETEP helper.
     * <p>Loads editor (if possible) for currently displayed PDU.</p>
     */
    private void setPetepHelper(PetepHelper helper) {
        // Store PETEP helper
        petepHelper = helper;

        // Get view (this will also store it to serializedPdu)
        getSerializedPdu();

        // Set the PDU (since there is PETEP helper, it will automatically load editor if possible)
        setSerializedPdu(serializedPdu);
    }

    /**
     * Unsets PETEP helper.
     * <p>Loads view for currently displayed PDU.</p>
     */
    private void unsetPetepHelper() {
        // Get view (this will also store it to serializedPdu)
        getSerializedPdu();

        // Remove stored PETEP helper
        petepHelper = null;

        // Set the PDU (since there is not PETEP helper, it will automatically load view)
        setSerializedPdu(serializedPdu);
    }

    /**
     * Shows editor if it is not already shown.
     * <p>Initializes the editor if it is not yet initialized.</p>
     */
    private void showEditor() {
        if (isEditor()) {
            if (petepHelper != null) {
                // Initialize the editor if needed
                ifEditor(editor -> {
                    if (!editor.isLoaded()) {
                        editor.load(petepHelper);
                    }
                });
            }
            return;
        }
        try {
            // Load editor
            var pduEditor = new PduEditor();
            pduEditor.init(
                    extensionHelper,
                    config.getEditorConfig().toBuilder()
                            .automaticLifecycle(false)
                            .build()
            );

            setComponent(pduEditor);
            setButtons(config.getEditorButtons());

            // Initialize the editor if needed
            if (petepHelper != null) {
                pduEditor.load(petepHelper);
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load PDU editor!", e);
            clearNodes();
        }
    }

    /**
     * Shows view if it is not already shown.
     * <p>Optionally adds FIX button.</p>
     */
    private void showView(boolean fixView) {
        if (isView()) {
            // Add/remove fix button if needed
            ifView(view -> {
                var object = view.getUserData();
                var isFixView = FIX_VIEW_USER_DATA.equals(object);
                if (isFixView && !fixView) {
                    setButtons(config.getViewButtons());
                    view.setUserData(null);
                } else if (!isFixView && fixView) {
                    setButtons(withFixViewButton(config.getViewButtons()));
                    view.setUserData(FIX_VIEW_USER_DATA);
                }
            });
            return;
        }
        // Create and display view
        try {
            var serializedPduView = new SerializedPduView();

            setComponent(serializedPduView);

            if (fixView) {
                serializedPduView.setUserData(FIX_VIEW_USER_DATA);
                setButtons(withFixViewButton(config.getViewButtons()));
            } else {
                setButtons(config.getViewButtons());
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load serialized PDU view!", e);
            clearNodes();
        }
    }

    /**
     * Creates new list of control buttons with FIX button.
     */
    private List<PduControlButton> withFixViewButton(List<PduControlButton> buttons) {
        var newButtons = new ArrayList<PduControlButton>(buttons.size() + 1);
        newButtons.addAll(buttons);
        newButtons.add(new PduControlButton("Fix PDU", this::onFixButtonClick));
        return newButtons;
    }

    /**
     * Show dialog for fixing broken PDU. (Let user choose proxy.)
     */
    private void onFixButtonClick(ActionEvent event) {
        // Only allow this action if there is PETEP helper available.
        if (petepHelper == null) {
            return;
        }

        // Let user choose proxy
        var maybeProxy = Dialogs.createChoiceDialog("Choose proxy substitute", "Proxy", petepHelper.getProxies());
        if (maybeProxy.isEmpty()) {
            return;
        }

        // Duplicate serialized PDU and set new proxy value
        var newSerializedPdu = serializedPdu.copy();
        newSerializedPdu.setProxy(maybeProxy.get().getModule().getCode());

        // Try to deserialize the PDU
        var maybePdu = PduUtils.deserializePdu(newSerializedPdu, petepHelper);
        if (maybePdu.isEmpty()) {
            return;
        }
        var pdu = maybePdu.get();

        // Store new serialized PDU and load editor with PDU
        serializedPdu = newSerializedPdu;
        showEditor();
        ifEditor(editor -> editor.setPdu(pdu));
    }

    /**
     * Sets node as a child to pane and make it fill it by setting anchors.
     */
    private void setComponent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        componentPane.getChildren().setAll(node);
    }

    /**
     * Sets buttons to the button box.
     */
    private void setButtons(List<PduControlButton> buttons) {
        var fxButtons = new ArrayList<Button>(buttons.size());
        buttons.forEach(button -> {
            var fxButton = new Button(button.getText());
            fxButton.setOnAction(button.getAction());
            fxButtons.add(fxButton);
        });
        buttonBox.getChildren().setAll(fxButtons);
    }

    /**
     * Clears all children nodes.
     */
    private void clearNodes() {
        componentPane.getChildren().clear();
        buttonBox.getChildren().clear();
    }

    /**
     * Returns the current active component.
     */
    private Optional<Node> getActiveComponent() {
        return componentPane.getChildren().stream().findAny();
    }

    /**
     * Returns {@code true} if the current active component is PDU editor.
     */
    private boolean isEditor() {
        return componentPane.getChildren().stream().anyMatch(PduEditor.class::isInstance);
    }

    /**
     * Returns {@code true} if the current active component is PDU view.
     */
    private boolean isView() {
        return componentPane.getChildren().stream().anyMatch(SerializedPduView.class::isInstance);
    }

    /**
     * Processes given action using specified component if it is active.
     */
    private <T> void ifInstanceOf(Consumer<T> action, Class<T> clazz) {
        var maybeComponent = getActiveComponent();
        if (maybeComponent.isEmpty()) {
            return;
        }
        var component = maybeComponent.get();

        if (!clazz.isInstance(component)) {
            return;
        }
        action.accept(clazz.cast(component));
    }

    /**
     * Processes given action using editor if it is active.
     */
    private void ifEditor(Consumer<PduEditor> action) {
        ifInstanceOf(action, PduEditor.class);
    }

    /**
     * Processes given action using view if it is active.
     */
    private void ifView(Consumer<SerializedPduView> action) {
        ifInstanceOf(action, SerializedPduView.class);
    }

    /**
     * Obtains value from specified component if it is active.
     */
    private <R, T> Optional<R> getIfInstanceOf(Function<T, Optional<R>> action, Class<T> clazz) {
        var maybeComponent = getActiveComponent();
        if (maybeComponent.isEmpty()) {
            return Optional.empty();
        }
        var component = maybeComponent.get();

        if (!clazz.isInstance(component)) {
            return Optional.empty();
        }
        return action.apply(clazz.cast(component));
    }

    /**
     * Obtains value using editor if it is active.
     */
    private <R> Optional<R> getIfEditor(Function<PduEditor, Optional<R>> action) {
        return getIfInstanceOf(action, PduEditor.class);
    }

    /**
     * Obtains value using view if it is active.
     */
    private <R> Optional<R> getIfView(Function<SerializedPduView, Optional<R>> action) {
        return getIfInstanceOf(action, SerializedPduView.class);
    }

    /**
     * Obtains value using editor or view (depending on what is currently active).
     */
    private <R> Optional<R> getFromEditorOrView(
            Function<PduEditor, Optional<R>> editorAction,
            Function<SerializedPduView, Optional<R>> viewAction
    ) {
        var result = getIfEditor(editorAction);
        if (result.isPresent()) {
            return result;
        }
        return getIfView(viewAction);
    }

    /**
     * Handles given action using editor or view (depending on what is currently active).
     */
    private void processUsingEditorOrView(
            Consumer<PduEditor> editorAction,
            Consumer<SerializedPduView> viewAction
    ) {
        ifEditor(editorAction);
        ifView(viewAction);
    }

}
