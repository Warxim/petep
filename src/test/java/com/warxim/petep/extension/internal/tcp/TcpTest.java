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
package com.warxim.petep.extension.internal.tcp;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.internal.tcp.proxy.TcpProxyModule;
import com.warxim.petep.extension.internal.tcp.proxy.TcpProxyModuleFactory;
import com.warxim.petep.test.base.extension.TestPetepHelper;
import com.warxim.petep.test.proxy.common.Message;
import com.warxim.petep.extension.internal.tcp.echo.TestTcpClient;
import com.warxim.petep.extension.internal.tcp.echo.TestTcpServer;
import lombok.extern.java.Log;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.IntStream;

import static com.warxim.petep.test.proxy.common.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;

@Log
public class TcpTest {
    private TcpConfig config;

    @BeforeClass(alwaysRun = true)
    public void initialize() {
        config = TcpConfig.builder()
                .proxyIP("127.0.0.1")
                .proxyPort(20001)
                .targetIP("127.0.0.1")
                .targetPort(20000)
                .charset(Constant.DEFAULT_CHARSET)
                .bufferSize(BUFFER_SIZE)
                .build();
    }

    @Test
    public void basicFlowTest() throws InterruptedException {
        var server = new TestTcpServer("127.0.0.1", 20000, PARALLEL_CONNECTION_COUNT);

        var extension = new TcpExtension("tcp");
        var factory = new TcpProxyModuleFactory(extension);
        var module = new TcpProxyModule(factory, "tcp", "TCP", "", true);
        module.loadConfig(config);
        var testPetepHelper = new TestPetepHelper(
                List.of(module),
                List.of(),
                List.of()
        );

        testPetepHelper.start();

        server.start();

        var errorCounter = new AtomicInteger(0);
        var successCounter = new AtomicInteger(0);

        // Run multiple test threads (echo clients connecting to echo server)
        var executor = Executors.newFixedThreadPool(PARALLEL_CONNECTION_COUNT);
        IntStream.range(0, PARALLEL_CONNECTION_COUNT)
                .forEach((id) -> executor.execute(
                        () -> processSendAndReceiveTest(id, errorCounter, successCounter)
                ));

        // Shutdown test threads
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Assert that no errors were present and success count is equal to connection count
        assertThat(errorCounter.get()).isZero();
        assertThat(successCounter.get()).isEqualTo(PARALLEL_CONNECTION_COUNT);

        server.interrupt();

        testPetepHelper.stop();

        // Assert that all connections were removed from connection manager
        int connectionCount = testPetepHelper.getPetep().getProxyManager().get("tcp").getConnectionManager().getList().size();
        assertThat(connectionCount).isZero();
    }

    private static void processSendAndReceiveTest(int id, AtomicInteger errorCounter, AtomicInteger successCounter) {
        try {
            var client = new TestTcpClient("127.0.0.1", 20001);
            client.start();

            processSendAndReceiveTest(client, MESSAGE_1, errorCounter);
            processSendAndReceiveTest(client, MESSAGE_2, errorCounter);
            processSendAndReceiveTest(client, MESSAGE_3, errorCounter);
            processSendAndReceiveTest(client, MESSAGE_4, errorCounter);
            processSendAndReceiveTest(client, MESSAGE_5, errorCounter);

            successCounter.incrementAndGet();

            client.close();
        } catch (SocketException e) {
            log.log(Level.SEVERE, "Socket exception occurred during test.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "IO exception occurred during test.");
            throw new RuntimeException(e);
        }
    }

    private static void processSendAndReceiveTest(TestTcpClient client, Message message, AtomicInteger errorCounter) {
        client.send(message);
        var receivedData = client.receive();
        if (!receivedData.equals(message)) {
            errorCounter.incrementAndGet();
        }
    }
}
