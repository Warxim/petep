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
package com.warxim.petep.extension.internal.test;

import java.io.IOException;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.proxy.worker.Proxy;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class TestPduMetadataPane extends PduMetadataPane {
  @FXML
  private TextField testInput;

  public TestPduMetadataPane() throws IOException {
    super("/fxml/extension/internal/test/TestPduMetadata.fxml");
  }

  @Override
  public PDU getPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    return new TestPdu(proxy, connection, destination, buffer, size, testInput.getText());
  }

  @Override
  public void setPdu(PDU pdu) {
    testInput.setText(((TestPdu) pdu).getTest());
  }

  @Override
  public void clear() {
    testInput.clear();
  }
}
