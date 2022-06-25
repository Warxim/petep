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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.server;

import com.warxim.petep.core.listener.ConnectionListener;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.exception.InvalidDataException;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.test.base.MockTestBase;
import com.warxim.petep.test.base.extension.proxy.*;
import lombok.extern.java.Log;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.warxim.petep.test.base.util.TestUtils.generateAllBytes;
import static com.warxim.petep.test.base.util.TestUtils.generateBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Log
public class EHTTPPLightHttpServerTest extends MockTestBase {
    @Mock
    private PetepHelper petepHelper;
    @Mock
    private ConnectionListener connectionListener;
    @Mock
    private List<Interceptor> interceptors;
    @Mock
    private Interceptor interceptor;

    @DataProvider(name = "data")
    public Object[][] dataProvider() {
        return new Object[][]{
                {"127.0.0.1", 8888, "my_proxy", "my_connection", 1, new byte[] {0x00, 0x02, 0x03}, StandardCharsets.UTF_8, Set.of("tag_1", "tag_2", "tag_3"), PduDestination.SERVER, null, null},
                {"my.local.proxy", 8080, "my-local-proxy", "my-connection", 13, generateAllBytes(), StandardCharsets.ISO_8859_1, Set.of("tag_1"), PduDestination.CLIENT, null, 15.0},
                {"123.124.125.126", 808, "MY_PROXY", "MY_CONNECTION", 13, generateBytes(16384), StandardCharsets.US_ASCII, Set.of("tag_1", "tag_3"), PduDestination.SERVER, null, Double.MAX_VALUE},
                {"1.2.3.4", 10, "M", "M", 13, new byte[] {}, StandardCharsets.UTF_16, Set.of(), PduDestination.SERVER, null, Double.MAX_VALUE},
        };
    }

    @Test(dataProvider = "data")
    public void lightHttpServerTestBasicFlow(
            String host,
            int port,
            String proxyCode,
            String connectionCode,
            int interceptorId,
            byte[] data,
            Charset charset,
            Set<String> tags,
            PduDestination destination,
            String testString,
            Double testNumber
    ) throws IOException, InvalidDataException {
        when(petepHelper.getConnectionListener()).thenReturn(connectionListener);
        when(petepHelper.getInterceptorsC2S()).thenReturn(interceptors);
        when(petepHelper.getInterceptorsS2C()).thenReturn(interceptors);
        when(interceptors.size()).thenReturn(interceptorId);
        when(interceptors.get(interceptorId - 1)).thenReturn(interceptor);

        var proxyModuleFactory = new TestProxyModuleFactory(null);
        var proxyModule = new TestProxyModule(proxyModuleFactory, proxyCode, proxyCode, "", true);
        var proxy = new TestProxy(proxyModule, petepHelper);
        var connection = new TestConnection(connectionCode, proxy);
        proxy.getConnectionManager().add(connection);

        when(petepHelper.getProxy(proxyCode)).thenReturn(Optional.of(proxy));

        var buffer = ByteBuffer.allocate(1024 + data.length);
        buffer.put(String.format(
                "POST /destination/%s/proxy/%s/connection/%s/interceptor/%d HTTP/1.0\r\n",
                destination.name().toLowerCase(),
                proxyCode,
                connectionCode,
                interceptorId
        ).getBytes());
        buffer.put(String.format("Host: %s:%d\r\n", host, port).getBytes());
        buffer.put(String.format("Content-Type: text/plain; charset=%s\r\n", charset.name()).getBytes());
        buffer.put(String.format("Content-Length: %d\r\n", data.length).getBytes());
        if (testString != null) {
            buffer.put(String.format("M-Test-String: %s\r\n", testString).getBytes());
        }
        if (testNumber != null) {
            buffer.put(String.format("M-Test-Number: %s\r\n", testNumber).getBytes());
        }
        if (!tags.isEmpty()) {
            buffer.put(String.format("T: %s\r\n", String.join(",", tags)).getBytes());
        }
        buffer.put("\r\n".getBytes());
        buffer.put(data);

        var input = new ByteArrayInputStream(buffer.array());
        var reader = new LightHttpPduReader(petepHelper);

        var pdu = (TestPdu) reader.read(input);

        assertThat(pdu.getProxy()).isEqualTo(proxy);
        assertThat(pdu.getTags()).containsExactlyInAnyOrderElementsOf(tags);
        assertThat(pdu.getLastInterceptor()).isEqualTo(interceptor);
        assertThat(pdu.getConnection()).isEqualTo(connection);
        assertThat(pdu.getDestination()).isEqualTo(destination);
        assertThat(pdu.getSize()).isEqualTo(data.length);
        assertThat(pdu.getBuffer()).isEqualTo(data);
        assertThat(pdu.getCharset()).isEqualTo(charset);
        assertThat(pdu.getTestNumber()).isEqualTo(testNumber);
        assertThat(pdu.getTestString()).isEqualTo(testString);
    }
}
