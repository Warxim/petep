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
package com.warxim.petep.extension.internal.tagger.factory.internal.destination;

import java.io.IOException;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public final class DestinationSubruleConfigurator extends TagSubruleConfigurator {
  @FXML
  private ComboBox<PduDestination> destinationInput;

  public DestinationSubruleConfigurator() throws IOException {
    super("/fxml/extension/internal/tagger/factory/DestinationSubrule.fxml");

    destinationInput.setConverter(new StringConverter<PduDestination>() {
      @Override
      public String toString(PduDestination destination) {
        if (destination == null) {
          return "";
        }

        return destination == PduDestination.CLIENT ? "To client" : "To server";
      }

      @Override
      public PduDestination fromString(String str) {
        return null;
      }
    });

    destinationInput
        .setItems(FXCollections.observableArrayList(PduDestination.CLIENT, PduDestination.SERVER));
  }

  @Override
  public TagSubruleData getConfig() {
    return new DestinationData(destinationInput.getSelectionModel().getSelectedItem());
  }

  @Override
  public void setConfig(TagSubruleData config) {
    destinationInput.getSelectionModel().select(((DestinationData) config).getDestination());
  }

  @Override
  public boolean isValid() {
    if (destinationInput.getSelectionModel().getSelectedItem() == null) {
      Dialogs.createErrorDialog("Destination required", "You have to select destination.");
      return false;
    }

    return true;
  }
}
