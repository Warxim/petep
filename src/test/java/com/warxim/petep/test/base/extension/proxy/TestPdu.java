package com.warxim.petep.test.base.extension.proxy;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.DefaultPdu;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.proxy.worker.Proxy;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Test PDU
 */
@Setter
@Getter
public class TestPdu extends DefaultPdu {
    private String testString;
    private Double testNumber;

    /**
     * Constructs test PDU with default charset.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     * @param tags Set of tags
     */
    public TestPdu(Proxy proxy,
                   Connection connection,
                   PduDestination destination,
                   byte[] buffer,
                   int size,
                   Set<String> tags) {
        super(proxy, connection, destination, buffer, size, tags);
    }

    /**
     * Constructs test PDU with default charset and empty tag set.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     */
    public TestPdu(Proxy proxy, Connection connection, PduDestination destination, byte[] buffer, int size) {
        super(proxy, connection, destination, buffer, size);
    }
}
