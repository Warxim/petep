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
package com.warxim.petep.extension.internal.history.gui.view.filter;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.model.HistoricConnection;
import com.warxim.petep.extension.internal.history.model.HistoricInterceptor;
import com.warxim.petep.extension.internal.history.model.HistoricProxy;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.extension.internal.history.service.HistoryService;
import com.warxim.petep.gui.control.byteseditor.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Dialog for filtering historic PDUs
 */
public class HistoryFilterDialog extends SimpleInputDialog<HistoryFilter> {
    @FXML
    private ComboBox<HistoricProxyWrapper> proxyInput;
    @FXML
    private ComboBox<HistoricInterceptorWrapper> interceptorInput;
    @FXML
    private ComboBox<HistoricConnectionWrapper> connectionInput;
    @FXML
    private ComboBox<PduDestinationWrapper> destinationInput;
    @FXML
    private TextField tagsInput;
    @FXML
    private TextField fromSizeInput;
    @FXML
    private TextField toSizeInput;
    @FXML
    private BytesEditor dataInput;
    @FXML
    private ToggleGroup dataFilterTypeInput;
    @FXML
    private RadioButton containsDataInput;
    @FXML
    private RadioButton startsWithDataInput;
    @FXML
    private RadioButton endsWithDataInput;
    @FXML
    private CheckBox negativeSearchDataInput;

    private final HistoryFilter filter;
    private final HistoryService service;

    /**
     * Constructs history filter dialog
     * @param filter Filter to edit
     * @param service History service to obtain available entities
     * @throws IOException If the template could not be loaded
     */
    public HistoryFilterDialog(HistoryFilter filter, HistoryService service) throws IOException {
        super("/fxml/extension/internal/history/HistoryFilterDialog.fxml", "History Filter", "Save");
        this.filter = filter;
        this.service = service;
        service.getProxies().thenAccept(this::initProxies);
        service.getConnections().thenAccept(this::initConnections);
        service.getInterceptors().thenAccept(this::initInterceptors);
        initPduDestinations();
        initDataRadios();
        resetTagsFilter();
        resetFromSizeFilter();
        resetToSizeFilter();
        resetDataFilter();

        var dialogPane = getDialogPane();
        var resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.LEFT);
        var clearButtonType = new ButtonType("Clear", ButtonBar.ButtonData.LEFT);
        dialogPane.getButtonTypes().add(0, resetButtonType);
        dialogPane.getButtonTypes().add(0, clearButtonType);
        dialogPane.lookupButton(clearButtonType).addEventFilter(ActionEvent.ACTION, this::clear);
        dialogPane.lookupButton(resetButtonType).addEventFilter(ActionEvent.ACTION, this::reset);
    }

    @FXML
    private void onTagsChooseClick(ActionEvent event) {
        try {
            var dialog = new HistoryFilterTagsDialog(service, getTags());
            var data = dialog.showAndWait();

            if (data.isEmpty()) {
                return;
            }

            var joiner = new StringJoiner(", ");
            data.get().forEach(joiner::add);
            tagsInput.setText(joiner.toString());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of history filter tags dialog", e);
        }
    }

    @Override
    protected HistoryFilter obtainResult() {
        var builder = HistoryFilter.builder();

        var proxy = proxyInput.getSelectionModel().getSelectedItem().getProxy();
        if (proxy != null) {
            builder.proxyId(proxy.getId());
        }

        var connection = connectionInput.getSelectionModel().getSelectedItem().getConnection();
        if (connection != null) {
            builder.connectionId(connection.getId());
        }

        var interceptor = interceptorInput.getSelectionModel().getSelectedItem().getInterceptor();
        if (interceptor != null) {
            builder.interceptorId(interceptor.getId());
        }

        var destination = destinationInput.getSelectionModel().getSelectedItem().getDestination();
        if (destination != null) {
            builder.destination(destination);
        }

        var fromSize = Integer.parseInt(fromSizeInput.getText());
        if (fromSize >= 0) {
            builder.fromSize(fromSize);
        }

        var toSize = Integer.parseInt(toSizeInput.getText());
        if (toSize >= 0) {
            builder.toSize(toSize);
        }

        var tags = getTags();
        if (!tags.isEmpty()) {
            builder.tags(tags);
        }

        var data = dataInput.getBytes();
        if (data.length > 0) {
            builder.data(data);
            builder.dataFilterNegative(negativeSearchDataInput.isSelected());
            builder.dataCharset(dataInput.getCharset());
            builder.dataFilterType((HistoryFilter.DataFilterType) dataFilterTypeInput.getSelectedToggle().getUserData());
        }

        return builder.build();
    }

    @Override
    protected boolean isValid() {
        if (proxyInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Proxy required", "You have to select proxy filter.");
            return false;
        }

        if (connectionInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Connection required", "You have to select connection filter.");
            return false;
        }

        if (interceptorInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Interceptor required", "You have to select interceptor filter.");
            return false;
        }

        if (destinationInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Destination required", "You have to select destination filter.");
            return false;
        }

        if (!isInteger(fromSizeInput.getText()) || Integer.parseInt(fromSizeInput.getText()) < -1) {
            Dialogs.createErrorDialog("From size required", "From size has to be integer greater or equal to -1.");
            return false;
        }

        if (!isInteger(toSizeInput.getText()) || Integer.parseInt(toSizeInput.getText()) < -1) {
            Dialogs.createErrorDialog("To size required", "To size has to be integer greater or equal to -1.");
            return false;
        }

        return true;
    }

    /**
     * Parses tags from tagsInput and returns them in Set.
     */
    private Set<String> getTags() {
        return Arrays.stream(tagsInput.getText()
                .trim()
                .split(","))
                .filter(tag -> !tag.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    /**
     * Clears the dialog (sets default values).
     */
    private void clear(ActionEvent event) {
        proxyInput.getSelectionModel().selectFirst();
        interceptorInput.getSelectionModel().selectFirst();
        connectionInput.getSelectionModel().selectFirst();
        destinationInput.getSelectionModel().selectFirst();
        tagsInput.clear();
        fromSizeInput.setText("-1");
        toSizeInput.setText("-1");
        dataInput.clear();
        containsDataInput.setSelected(true);
        negativeSearchDataInput.setSelected(false);
        event.consume();
    }

    /**
     * Resets the dialog (sets original values from filter).
     */
    private void reset(ActionEvent event) {
        resetProxyFilter();
        resetInterceptorFilter();
        resetConnectionFilter();
        resetDestinationFilter();
        resetTagsFilter();
        resetFromSizeFilter();
        resetToSizeFilter();
        resetDataFilter();
        event.consume();
    }

    /**
     * Initializes proxy input.
     */
    private void initProxies(Collection<HistoricProxy> proxies) {
        var items = new ArrayList<HistoricProxyWrapper>();

        items.add(new HistoricProxyWrapper(null));

        proxies.stream()
                .map(HistoricProxyWrapper::new)
                .forEach(items::add);

        Platform.runLater(() -> {
            proxyInput.setItems(FXCollections.observableList(items));
            resetProxyFilter();
        });
    }

    /**
     * Initializes interceptor input.
     */
    private void initInterceptors(Collection<HistoricInterceptor> interceptors) {
        var items = new ArrayList<HistoricInterceptorWrapper>();

        items.add(new HistoricInterceptorWrapper(null));

        interceptors.stream()
                .map(HistoricInterceptorWrapper::new)
                .forEach(items::add);

        Platform.runLater(() -> {
            interceptorInput.setItems(FXCollections.observableList(items));
            resetInterceptorFilter();
        });
    }

    /**
     * Initializes connection input.
     */
    private void initConnections(Collection<HistoricConnection> connections) {
        var items = new ArrayList<HistoricConnectionWrapper>();

        items.add(new HistoricConnectionWrapper(null));

        connections.stream()
                .map(HistoricConnectionWrapper::new)
                .forEach(items::add);

        Platform.runLater(() -> {
            connectionInput.setItems(FXCollections.observableList(items));
            resetConnectionFilter();
        });
    }

    /**
     * Initializes destination input.
     */
    private void initPduDestinations() {
        destinationInput.setItems(FXCollections.observableArrayList(
                new PduDestinationWrapper(null),
                new PduDestinationWrapper(PduDestination.SERVER),
                new PduDestinationWrapper(PduDestination.CLIENT)
        ));
        resetDestinationFilter();
    }

    /**
     * Initializes data radios (sets userData for determining DataFilterType).
     */
    private void initDataRadios() {
        containsDataInput.setUserData(HistoryFilter.DataFilterType.CONTAINS);
        startsWithDataInput.setUserData(HistoryFilter.DataFilterType.STARTS_WITH);
        endsWithDataInput.setUserData(HistoryFilter.DataFilterType.ENDS_WITH);
    }

    /**
     * Resets proxy filter (uses original filter.proxy).
     */
    private void resetProxyFilter() {
        if (filter.getProxyId() != null) {
            var selectedWrapper = proxyInput.getItems().stream()
                    .filter(wrapper -> wrapper.getProxy() != null && wrapper.getProxy().getId().equals(filter.getProxyId()))
                    .findAny();

            if (selectedWrapper.isPresent()) {
                proxyInput.getSelectionModel().select(selectedWrapper.get());
                return;
            }
        }

        proxyInput.getSelectionModel().selectFirst();
    }

    /**
     * Resets interceptor filter (uses original filter.interceptor).
     */
    private void resetInterceptorFilter() {
        if (filter.getInterceptorId() != null) {
            var selectedWrapper = interceptorInput.getItems().stream()
                    .filter(wrapper -> wrapper.getInterceptor() != null && wrapper.getInterceptor().getId().equals(filter.getInterceptorId()))
                    .findAny();

            if (selectedWrapper.isPresent()) {
                interceptorInput.getSelectionModel().select(selectedWrapper.get());
                return;
            }
        }

        interceptorInput.getSelectionModel().selectFirst();
    }

    /**
     * Resets connection filter (uses original filter.connection).
     */
    private void resetConnectionFilter() {
        if (filter.getConnectionId() != null) {
            var selectedWrapper = connectionInput.getItems().stream()
                    .filter(wrapper -> wrapper.getConnection() != null && wrapper.getConnection().getId().equals(filter.getConnectionId()))
                    .findAny();

            if (selectedWrapper.isPresent()) {
                connectionInput.getSelectionModel().select(selectedWrapper.get());
                return;
            }
        }

        connectionInput.getSelectionModel().selectFirst();
    }

    /**
     * Resets destination filter (uses original filter.destination).
     */
    private void resetDestinationFilter() {
        if (filter.getDestination() != null) {
            var selectedWrapper = destinationInput.getItems().stream()
                    .filter(wrapper -> wrapper.getDestination() != null && wrapper.getDestination() == filter.getDestination())
                    .findAny();

            if (selectedWrapper.isPresent()) {
                destinationInput.getSelectionModel().select(selectedWrapper.get());
                return;
            }
        }

        destinationInput.getSelectionModel().selectFirst();
    }

    /**
     * Resets tags filter (uses original filter.tags).
     */
    private void resetTagsFilter() {
        if (filter.getTags() != null) {
            var joiner = new StringJoiner(", ");
            filter.getTags().forEach(joiner::add);
            tagsInput.setText(joiner.toString());
        }
    }

    /**
     * Resets fromSize filter (uses original filter.fromSize).
     */
    private void resetFromSizeFilter() {
        if (filter.getFromSize() != null) {
            fromSizeInput.setText(String.valueOf(filter.getFromSize()));
        } else {
            fromSizeInput.setText("-1");
        }
    }

    /**
     * Resets toSize filter (uses original filter.toSize).
     */
    private void resetToSizeFilter() {
        if (filter.getToSize() != null) {
            toSizeInput.setText(String.valueOf(filter.getToSize()));
        } else {
            toSizeInput.setText("-1");
        }
    }

    /**
     * Reset data filter (uses original filter.data).
     */
    private void resetDataFilter() {
        if (filter.getData() == null) {
            containsDataInput.setSelected(true);
            negativeSearchDataInput.setSelected(false);
            dataInput.clear();
            return;
        }

        dataInput.setData(filter.getData(), filter.getDataCharset());
        if (filter.getDataFilterType() == HistoryFilter.DataFilterType.STARTS_WITH) {
            startsWithDataInput.setSelected(true);
        } else if (filter.getDataFilterType() == HistoryFilter.DataFilterType.ENDS_WITH) {
            endsWithDataInput.setSelected(true);
        } else {
            containsDataInput.setSelected(true);
        }
        negativeSearchDataInput.setSelected(filter.isDataFilterNegative());
    }

    /**
     * Returns true if the value is a valid integer.
     */
    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
