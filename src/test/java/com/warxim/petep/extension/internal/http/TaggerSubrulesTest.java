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
import com.warxim.petep.core.pdu.DefaultPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
import com.warxim.petep.extension.internal.http.tagger.hasheader.HasHeaderData;
import com.warxim.petep.extension.internal.http.tagger.hasheader.HasHeaderSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.headercontains.HeaderContainsData;
import com.warxim.petep.extension.internal.http.tagger.headercontains.HeaderContainsSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.ishttp.IsHttpSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.iswebsocket.IsWebSocketSubruleFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaggerSubrulesTest {
    @Test
    public void hasHeaderTest() {
        var factory = new HasHeaderSubruleFactory();
        var data = new HasHeaderData("Test-Header");
        var subrule = factory.createSubrule(data);
        var pdu = new HttpRequestPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);

        pdu.addHeader("Test-Header", "Test Value");
        assertThat(subrule.test(pdu)).isTrue();

        pdu.removeHeader("Test-Header");
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void headerContainsTest() {
        var factory = new HeaderContainsSubruleFactory();
        var data = new HeaderContainsData("Test-Header", "test");
        var subrule = factory.createSubrule(data);
        var pdu = new HttpRequestPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);

        pdu.addHeader("Test-Header", "Value test...");
        assertThat(subrule.test(pdu)).isTrue();

        pdu.addHeader("Test-Header", "Another value...");
        assertThat(subrule.test(pdu)).isFalse();

        pdu.removeHeader("Test-Header");
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void headerContainsTest_anyHeader() {
        var factory = new HeaderContainsSubruleFactory();
        var data = new HeaderContainsData("", "test");
        var subrule = factory.createSubrule(data);
        var pdu = new HttpRequestPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);

        pdu.addHeader("Test-Header", "Value test...");
        assertThat(subrule.test(pdu)).isTrue();

        pdu.addHeader("Test-Header", "Another value...");
        assertThat(subrule.test(pdu)).isFalse();

        pdu.removeHeader("Test-Header");
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void isHttpTest() {
        var factory = new IsHttpSubruleFactory();
        var subrule = factory.createSubrule(null);

        var httpRequest = new HttpRequestPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(httpRequest)).isTrue();

        var httpResponse = new HttpResponsePdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(httpResponse)).isTrue();

        var websocketPdu = new WebSocketPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(websocketPdu)).isFalse();

        var defaultPdu = new DefaultPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(defaultPdu)).isFalse();
    }

    @Test
    public void isWebsocketTest() {
        var factory = new IsWebSocketSubruleFactory();
        var subrule = factory.createSubrule(null);

        var httpRequest = new HttpRequestPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(httpRequest)).isFalse();

        var httpResponse = new HttpResponsePdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(httpResponse)).isFalse();

        var websocketPdu = new WebSocketPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(websocketPdu)).isTrue();

        var defaultPdu = new DefaultPdu(null, null, null, new byte[0], 0, Constant.DEFAULT_CHARSET);
        assertThat(subrule.test(defaultPdu)).isFalse();
    }

}
