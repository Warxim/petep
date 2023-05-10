/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
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
package com.warxim.petep.extension.internal.tcp.proxy.starttls;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpConnection;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * STARTTLS TCP connection.
 * <p>Implementation of TcpConnection that supports STARTTLS.</p>
 */
public final class StarttlsTcpConnection extends TcpConnection {

    /**
     * Tag used to mark PDU for STARTTLS signalization
     */
    private static final String START_TLS_TAG = "starttls";

    /**
     * Timeout for reading from input stream of socket between the proxy and the server
     * <p>Used before transition to SSL/TLS, so that read does not block indefinitely.</p>
     */
    private static final int READ_TIMEOUT_MS = 100;

    /**
     * State of connection between the client and the proxy
     */
    private volatile StarttlsC2PState c2pState = StarttlsC2PState.PLAIN_TEXT_MODE;

    /**
     * State of connection between the proxy and the server
     */
    private volatile StarttlsP2SState p2sState = StarttlsP2SState.PLAIN_TEXT_MODE;

    /**
     * STARTTLS TCP connection constructor.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     * @param socket Client socket for the connection
     */
    public StarttlsTcpConnection(String code, Proxy proxy, Socket socket) {
        super(code, proxy, socket);
    }

    @Override
    protected void readFromServer() {
        // Size of the data read
        int size;
        int bufferSize = getConfig().getBufferSize();
        var charset = getConfig().getCharset();
        byte[] buffer = new byte[bufferSize];

        try {
            // Set timeout, because we will be interrupted by STARTTLS transition, after that it will continue without timeout
            p2sSocket.setSoTimeout(READ_TIMEOUT_MS);

            // Read until end is reached
            while (!closing.get()) {
                try {
                    // Read bytes to buffer and process it in PETEP.
                    while ((size = p2sSocket.getInputStream().read(buffer)) != -1) {
                        // Create PDU from buffer
                        var pdu = new TcpPdu(proxy, this, PduDestination.CLIENT, buffer, size, charset);

                        // Process PDU in PETEP.
                        process(pdu);

                        // Create new buffer.
                        buffer = new byte[bufferSize];
                    }
                    return;
                } catch (SocketTimeoutException timeoutException) {
                    // Busy wait before we reach SSL/TLS mode
                    if (p2sState == StarttlsP2SState.TRANSITION_STEP_1) {
                        // Let the other thread know we can continue with transition to encrypted communication
                        // since we are not reading from plain-text input stream anymore
                        p2sState = StarttlsP2SState.TRANSITION_STEP_2;

                        // There will be no need for busy-wait anymore
                        p2sSocket.setSoTimeout(0);

                        // Wait for transition to complete
                        waitForP2SState(StarttlsP2SState.ENCRYPTION_MODE);
                    }
                }
            }
        } catch (IOException e) {
            // Closed
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    @Override
    protected void readFromClient() {
        // Size of data
        int size;
        int bufferSize = getConfig().getBufferSize();
        var charset = getConfig().getCharset();

        // Buffer
        byte[] buffer = new byte[bufferSize];

        try {
            // Read bytes to buffer and process it in PETEP.
            while ((size = c2pSocket.getInputStream().read(buffer)) != -1) {
                // Process transition to encryption mode if needed (skips current data)
                if (transitionC2PToEncryptionModeIfNeeded(buffer, size)) {
                    continue;
                }

                // Create PDU from buffer
                var pdu = new TcpPdu(proxy, this, PduDestination.SERVER, buffer, size, charset);

                // Process PDU in PETEP.
                process(pdu);

                // Create new buffer.
                buffer = new byte[bufferSize];
            }
        } catch (IOException e) {
            // Closed
        }
    }

    @Override
    protected void writeToServer() {
        // PDU
        PDU pdu;

        try {
            // Read bytes to buffer and send it to out stream.
            while ((pdu = queueC2S.take()) != null) {
                // Process transition to encryption mode if needed (skips current data)
                if (transitionP2SToEncryptionModeIfNeeded(pdu)) {
                    continue;
                }

                // Write PDU to the server
                p2sSocket.getOutputStream().write(pdu.getBuffer(), 0, pdu.getSize());
            }
        } catch (IOException e) {
            // Closed socket
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void writeToClient() {
        // PDU
        PDU pdu;

        try {
            // Read bytes to buffer and send it to out stream.
            while ((pdu = queueS2C.take()) != null) {
                var out = c2pSocket.getOutputStream();
                out.write(pdu.getBuffer(), 0, pdu.getSize());
            }
        } catch (IOException e) {
            // Closed socket
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Transitions client to encryption mode
     * @return {@code true} if transition happened and the data should be skipped
     */
    private boolean transitionC2PToEncryptionModeIfNeeded(byte[] buffer, int size) throws IOException {
        if (c2pState != StarttlsC2PState.PLAIN_TEXT_MODE || size < 2 || buffer[0] != 0x16 || buffer[1] != 0x03) {
            return false;
        }

        // Notify proxy-server part of the connection that we are starting transition to encryption mode
        process(createStarttlsPdu());

        // Obtain data that we read that contain the encryption
        var consumedBytes = Arrays.copyOfRange(buffer, 0, size);

        // Wrap the socket
        c2pSocket = ((StarttlsTcpProxy) proxy).getStarttlsSocketWrapper().wrapC2PSocket(c2pSocket, consumedBytes);

        // Set state to encryption mode
        c2pState = StarttlsC2PState.ENCRYPTION_MODE;

        return true;
    }

    /**
     * Creates PDU for STARTTLS signalization
     */
    private TcpPdu createStarttlsPdu() {
        return new TcpPdu(proxy, this, PduDestination.SERVER, new byte[0], 0, getConfig().getCharset(), Set.of(START_TLS_TAG));
    }

    /**
     * Transitions socket from plain-text mode to encryption mode
     * @return {@code true} if transition happened and the data should be skipped
     */
    private boolean transitionP2SToEncryptionModeIfNeeded(PDU pdu) throws InterruptedException, IOException {
        // Transition is only allowed when we are currently in plain-text mode and the PDU has starttls tag (empty PDU)
        if (p2sState != StarttlsP2SState.PLAIN_TEXT_MODE || pdu.getSize() != 0 || !pdu.hasTag(START_TLS_TAG)) {
            return false;
        }

        // Notify worker that read from the server that we want to transition to encrypted mode
        p2sState = StarttlsP2SState.TRANSITION_STEP_1;

        // Wait for the worker to acknowledge it
        waitForP2SState(StarttlsP2SState.TRANSITION_STEP_2);

        // Transition to encrypted communication
        p2sSocket = ((StarttlsTcpProxy) proxy).getStarttlsSocketWrapper()
                .wrapP2SSocket(p2sSocket);

        // Set state to encryption mode
        p2sState = StarttlsP2SState.ENCRYPTION_MODE;

        return true;
    }

    /**
     * Busy-waits for correct socket state between proxy and server
     */
    private void waitForP2SState(StarttlsP2SState requiredState) throws InterruptedException {
        while (p2sState != requiredState && !closing.get()) {
            busyWait();
        }
    }

    /**
     * Hacky busy wait for plain-text and transition modes
     */
    private void busyWait() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(250);
    }
}
