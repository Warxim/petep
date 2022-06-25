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
package com.warxim.petep.extension.internal.modifier;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.internal.modifier.factory.internal.replace.ReplacerData;
import com.warxim.petep.extension.internal.modifier.factory.internal.replace.ReplacerFactory;
import com.warxim.petep.test.base.extension.proxy.TestPdu;
import com.warxim.petep.util.BytesUtils;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RulesTest {
    @Test
    public void replacerTest_anyOccurrence() {
        var factory = new ReplacerFactory();
        var data = new ReplacerData(
                -1,
                BytesUtils.getBytes("test"),
                Constant.DEFAULT_CHARSET,
                BytesUtils.getBytes("new_data"),
                Constant.DEFAULT_CHARSET);
        var rule = factory.createModifier(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        byte[] buffer;
        byte[] expected;
        buffer = BytesUtils.getBytes("...test");
        expected = BytesUtils.getBytes("...new_data");
        pdu.setBuffer(buffer, buffer.length);
        rule.process(pdu);
        assertThat(pdu.getBuffer()).startsWith(expected);
        assertThat(pdu.getSize()).isEqualTo(expected.length);

        buffer = BytesUtils.getBytes("...1234...");
        expected = BytesUtils.getBytes("...1234...");
        pdu.setBuffer(buffer, buffer.length);
        rule.process(pdu);
        assertThat(pdu.getBuffer()).startsWith(expected);
        assertThat(pdu.getSize()).isEqualTo(expected.length);

        buffer = BytesUtils.getBytes("...test - test...");
        expected = BytesUtils.getBytes("...new_data - new_data...");
        pdu.setBuffer(buffer, buffer.length);
        rule.process(pdu);
        assertThat(pdu.getBuffer()).startsWith(expected);
        assertThat(pdu.getSize()).isEqualTo(expected.length);
    }

    @Test
    public void replacerTest_secondOccurrence() {
        var factory = new ReplacerFactory();
        var data = new ReplacerData(
                1,
                BytesUtils.getBytes("test"),
                Constant.DEFAULT_CHARSET,
                BytesUtils.getBytes("new_data"),
                Constant.DEFAULT_CHARSET);
        var rule = factory.createModifier(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        byte[] buffer;
        byte[] expected;
        buffer = BytesUtils.getBytes("...test");
        expected = BytesUtils.getBytes("...test");
        pdu.setBuffer(buffer, buffer.length);
        rule.process(pdu);
        assertThat(pdu.getBuffer()).startsWith(expected);
        assertThat(pdu.getSize()).isEqualTo(expected.length);

        buffer = BytesUtils.getBytes("...test - test - test...");
        expected = BytesUtils.getBytes("...test - new_data - test...");
        pdu.setBuffer(buffer, buffer.length);
        rule.process(pdu);
        assertThat(pdu.getBuffer()).startsWith(expected);
        assertThat(pdu.getSize()).isEqualTo(expected.length);
    }
}
