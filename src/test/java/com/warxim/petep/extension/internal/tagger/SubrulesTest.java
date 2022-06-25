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
package com.warxim.petep.extension.internal.tagger;

import com.warxim.petep.common.Constant;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tagger.factory.internal.contains.ContainsData;
import com.warxim.petep.extension.internal.tagger.factory.internal.contains.ContainsSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.destination.DestinationData;
import com.warxim.petep.extension.internal.tagger.factory.internal.destination.DestinationSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.ends_with.EndsWithData;
import com.warxim.petep.extension.internal.tagger.factory.internal.ends_with.EndsWithSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.has_tag.HasTagData;
import com.warxim.petep.extension.internal.tagger.factory.internal.has_tag.HasTagSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.proxy.ProxyData;
import com.warxim.petep.extension.internal.tagger.factory.internal.proxy.ProxySubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.size.SizeData;
import com.warxim.petep.extension.internal.tagger.factory.internal.size.SizeSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.internal.starts_with.StartsWithData;
import com.warxim.petep.extension.internal.tagger.factory.internal.starts_with.StartsWithSubruleFactory;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.test.base.extension.proxy.TestPdu;
import com.warxim.petep.util.BytesUtils;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubrulesTest {
    @Test
    public void proxySubruleTest() {
        var factory = new ProxySubruleFactory(null);
        var data = new ProxyData("test_proxy");
        var subrule = factory.createSubrule(data);
        var proxy = mock(Proxy.class);
        var pdu = new TestPdu(proxy, null, null, new byte[0], 0);

        when(proxy.getCode()).thenReturn("test_proxy");
        assertThat(subrule.test(pdu)).isTrue();

        when(proxy.getCode()).thenReturn("other_proxy");
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void destinationSubruleTest() {
        var factory = new DestinationSubruleFactory();
        var data = new DestinationData(PduDestination.SERVER);
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, new byte[0], 0);

        pdu.setDestination(PduDestination.SERVER);
        assertThat(subrule.test(pdu)).isTrue();

        pdu.setDestination(PduDestination.CLIENT);
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void hasTagSubruleTest() {
        var factory = new HasTagSubruleFactory();
        var data = new HasTagData("test_tag");
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, new byte[0], 0);

        pdu.addTag("test_tag");
        assertThat(subrule.test(pdu)).isTrue();

        pdu.removeTag("test_tag");
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void sizeSubruleTest() {
        var factory = new SizeSubruleFactory();
        var data = new SizeData(16);
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        pdu.setBuffer(new byte[16], 16);
        assertThat(subrule.test(pdu)).isTrue();

        pdu.setBuffer(new byte[10], 10);
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void containsSubruleTest_anyOccurrence() {
        var factory = new ContainsSubruleFactory();
        var data = new ContainsData(BytesUtils.getBytes("test_data"), Constant.DEFAULT_CHARSET, -1);
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        byte[] buffer;
        buffer = BytesUtils.getBytes("This contains test_data!");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isTrue();

        buffer = BytesUtils.getBytes("This contains another_data!");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void containsSubruleTest_onFifthByte() {
        var factory = new ContainsSubruleFactory();
        var data = new ContainsData(BytesUtils.getBytes("test_data"), Constant.DEFAULT_CHARSET, 4);
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        byte[] buffer;
        buffer = BytesUtils.getBytes("1234test_data!");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isTrue();

        buffer = BytesUtils.getBytes("12345test_data!");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void startsWithSubruleTest() {
        var factory = new StartsWithSubruleFactory();
        var data = new StartsWithData(BytesUtils.getBytes("test_data"), Constant.DEFAULT_CHARSET);
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        byte[] buffer;
        buffer = BytesUtils.getBytes("test_data...");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isTrue();

        buffer = BytesUtils.getBytes("...test_data...");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isFalse();
    }

    @Test
    public void endsWithSubruleTest() {
        var factory = new EndsWithSubruleFactory();
        var data = new EndsWithData(BytesUtils.getBytes("test_data"), Constant.DEFAULT_CHARSET);
        var subrule = factory.createSubrule(data);
        var pdu = new TestPdu(null, null, null, null, 0);

        byte[] buffer;
        buffer = BytesUtils.getBytes("...test_data");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isTrue();

        buffer = BytesUtils.getBytes("...test_data...");
        pdu.setBuffer(buffer, buffer.length);
        assertThat(subrule.test(pdu)).isFalse();
    }
}
