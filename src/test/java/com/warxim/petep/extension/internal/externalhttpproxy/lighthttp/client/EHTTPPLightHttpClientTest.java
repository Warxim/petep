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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.client;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpConstant;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpUtils;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.test.base.MockTestBase;
import com.warxim.petep.test.base.extension.proxy.*;
import lombok.Value;
import lombok.extern.java.Log;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.warxim.petep.test.base.util.TestUtils.generateAllBytes;
import static com.warxim.petep.test.base.util.TestUtils.generateBytes;
import static org.assertj.core.api.Assertions.assertThat;

@Log
public class EHTTPPLightHttpClientTest extends MockTestBase {
    @Mock
    private PetepHelper petepHelper;

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
    public void lightHttpClientTestBasicFlow(
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
    ) throws IOException {
        var proxyModuleFactory = new TestProxyModuleFactory(null);
        var proxyModule = new TestProxyModule(proxyModuleFactory, proxyCode, proxyCode, "", true);
        var proxy = new TestProxy(proxyModule, petepHelper);
        var connection = new TestConnection(connectionCode, proxy);

        var pdu = new TestPdu(proxy, connection, destination, data, data.length, tags);
        pdu.setTestString(testString);
        pdu.setTestNumber(testNumber);
        pdu.setCharset(charset);

        var writer = new LightHttpPduWriter(interceptorId, host, port);
        var output = new ByteArrayOutputStream();

        writer.write(pdu, output);

        var entity = readEntity(new ByteArrayInputStream(output.toByteArray()));

        // First line
        var expectedFirstLine = String.format(
                "POST http://%s:%d/destination/%s/proxy/%s/connection/%s/interceptor/%d HTTP/1.0",
                host,
                port,
                destination.name().toLowerCase(),
                proxyCode,
                connectionCode,
                interceptorId
        );
        assertThat(entity.getFirstLine()).isEqualTo(expectedFirstLine);

        // Headers
        var headers = entity.getHeaders();
        assertThat(headers)
                .containsEntry("Content-Length", String.valueOf(pdu.getSize()))
                .containsEntry("Content-Type", String.format("text/plain; charset=%s", pdu.getCharset().name()));
        if (!tags.isEmpty()) {
            var tagsFromHeader = headers.get("T").split(",");
            assertThat(tagsFromHeader).containsExactlyInAnyOrderElementsOf(pdu.getTags());
        }
        assertThat(headers.get("M-Test-String")).isEqualTo(testString);
        if (testNumber != null) {
            assertThat(headers.get("M-Test-Number")).isEqualTo(String.valueOf(testNumber));
        }

        // Body
        assertThat(entity.getBody()).hasSize(pdu.getSize());
        assertThat(entity.getBody()).isEqualTo(pdu.getBuffer());

        log.info(output.toString());
    }

    private SimpleHttpEntity readEntity(InputStream in) throws IOException {
        // First line
        var firstLine = readStringUntilNewLine(in);

        // Headers
        var headers = new HashMap<String, String>();
        var headerLine = readStringUntilNewLine(in);
        do {
            var headerSeparator = headerLine.indexOf(':');
            headers.put(
                    headerLine.substring(0, headerSeparator),
                    headerLine.substring(headerSeparator + 2)
            );
            headerLine = readStringUntilNewLine(in);
        } while (!headerLine.isBlank());

        var body = in.readAllBytes();

        return new SimpleHttpEntity(firstLine, headers, body);
    }

    private String readStringUntilNewLine(InputStream in) throws IOException {
        var builder = new StringBuilder();
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.CR);
        in.skip(1);
        return builder.toString();
    }

    @Value
    private static class SimpleHttpEntity {
        String firstLine;
        Map<String, String> headers;
        byte[] body;
    }
}
