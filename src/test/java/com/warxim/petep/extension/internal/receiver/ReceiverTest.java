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
package com.warxim.petep.extension.internal.receiver;

import com.warxim.petep.extension.receiver.Receiver;
import com.warxim.petep.extension.receiver.ReceiverManager;
import com.warxim.petep.test.base.MockTestBase;
import org.mockito.Mock;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReceiverTest extends MockTestBase {
    @Mock
    private Receiver receiverA;
    @Mock
    private Receiver receiverB;
    @Mock
    private Receiver receiverC;

    @Test
    public void receiverTest() {
        var manager = new ReceiverManager();
        when(receiverA.getCode()).thenReturn("receiver_1");
        when(receiverB.getCode()).thenReturn("receiver_2");
        when(receiverC.getCode()).thenReturn("receiver_3");
        when(receiverA.supports(String.class)).thenReturn(true);
        when(receiverB.supports(String.class)).thenReturn(true);
        when(receiverC.supports(Integer.class)).thenReturn(true);

        manager.registerReceiver(receiverA);
        manager.registerReceiver(receiverB);
        manager.registerReceiver(receiverC);

        manager.send("receiver_1", "Hello!");
        manager.send("receiver_2", "Hi!");
        manager.send("receiver_1", "Hey!");

        var verifierA = inOrder(receiverA);
        var verifierB = inOrder(receiverB);
        verifierA.verify(receiverA).receive("Hello!");
        verifierB.verify(receiverB).receive("Hi!");
        verifierA.verify(receiverA).receive("Hey!");

        assertThat(manager.getReceivers(String.class)).containsExactlyInAnyOrder(receiverA, receiverB);

        manager.unregisterReceiver(receiverA);
        manager.send("receiver_1", "Hello!");
        verifierA.verify(receiverA, never()).receive("Hello!");

        manager.send("receiver_2", "Hello!");
        verifierB.verify(receiverB).receive("Hello!");

        manager.send("receiver_2", 1234);
        verifierB.verify(receiverB, never()).receive(1234);

        assertThat(manager.getReceivers(String.class)).containsExactlyInAnyOrder(receiverB);
        assertThat(manager.getReceivers(Integer.class)).containsExactlyInAnyOrder(receiverC);
    }
}
