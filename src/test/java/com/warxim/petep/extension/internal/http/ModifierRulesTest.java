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
package com.warxim.petep.extension.internal.http;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.internal.http.modifier.addheader.AddHeaderData;
import com.warxim.petep.extension.internal.http.modifier.addheader.AddHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.modifier.removeheader.RemoveHeaderData;
import com.warxim.petep.extension.internal.http.modifier.removeheader.RemoveHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.modifier.replaceheader.ReplaceHeaderData;
import com.warxim.petep.extension.internal.http.modifier.replaceheader.ReplaceHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ModifierRulesTest {
    @Test
    public void addHeaderTest() {
        var factory = new AddHeaderModifierFactory();
        var data = new AddHeaderData("Test-Header", "Test value");
        var rule = factory.createModifier(data);
        var pdu = new HttpRequestPdu(null, null, null, null, 0, Constant.DEFAULT_CHARSET);

        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isEqualTo("Test value");

        pdu.addHeader("Test-Header", "Other value");
        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isEqualTo("Test value");
    }

    @Test
    public void removeHeaderTest() {
        var factory = new RemoveHeaderModifierFactory();
        var data = new RemoveHeaderData("Test-Header");
        var rule = factory.createModifier(data);
        var pdu = new HttpRequestPdu(null, null, null, null, 0, Constant.DEFAULT_CHARSET);

        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isNull();

        pdu.addHeader("Test-Header", "Other value");
        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isNull();
    }

    @Test
    public void replaceHeaderTest() {
        var factory = new ReplaceHeaderModifierFactory();
        var data = new ReplaceHeaderData("Test-Header", "test", "new_value");
        var rule = factory.createModifier(data);
        var pdu = new HttpRequestPdu(null, null, null, null, 0, Constant.DEFAULT_CHARSET);

        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isNull();

        pdu.addHeader("Test-Header", "Simple header test.");
        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isEqualTo("Simple header new_value.");
    }

    @Test
    public void replaceHeaderTest_anyHeader() {
        var factory = new ReplaceHeaderModifierFactory();
        var data = new ReplaceHeaderData("", "test", "new_value");
        var rule = factory.createModifier(data);
        var pdu = new HttpRequestPdu(null, null, null, null, 0, Constant.DEFAULT_CHARSET);

        pdu.setHeaders(Map.of(
                "Test-Header", "Simple header test.",
                "Other-Header", "data=test; sid=1234",
                "Another-Header", "data=test; test=1234"
        ));
        rule.process(pdu);
        assertThat(pdu.getHeader("Test-Header")).isEqualTo("Simple header new_value.");
        assertThat(pdu.getHeader("Other-Header")).isEqualTo("data=new_value; sid=1234");
        assertThat(pdu.getHeader("Another-Header")).isEqualTo("data=new_value; new_value=1234");
    }
}
