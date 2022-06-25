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
package com.warxim.petep.extension.internal.repeater.gui;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.receiver.Receiver;
import com.warxim.petep.util.PduUtils;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Receiver for receiving PDUs/serialized PDUs and adding them to repeater tabs.
 */
@Setter
@NoArgsConstructor
public class RepeaterReceiver implements Receiver {
    private RepeaterController controller;

    @Override
    public String getName() {
        return "Repeater";
    }

    @Override
    public String getCode() {
        return "repeater";
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PDU.class)
                || clazz.equals(SerializedPdu.class)
                || clazz.isInstance(PDU.class)
                || clazz.isInstance(SerializedPdu.class);
    }

    @Override
    public void receive(Object data) {
        if (controller == null) {
            return;
        }

        if (data instanceof PDU) {
            controller.createTab(PduUtils.serializePdu((PDU) data));
            return;
        }

        controller.createTab((SerializedPdu) data);
    }
}
