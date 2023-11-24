package com.warxim.petep.test.base.extension.proxy;

import com.warxim.petep.core.connection.ConnectionBase;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.proxy.worker.Proxy;
import lombok.extern.java.Log;

/**
 * Test connection
 */
@Log
public class TestConnection extends ConnectionBase {
    /**
     * Constructs test connection.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     */
    public TestConnection(String code, Proxy proxy) {
        super(code, proxy);
        log.info("TestConnection created");
    }

    @Override
    public boolean start() {
        log.info("TestConnection started");
        return true;
    }

    @Override
    public void stop() {
        log.info("TestConnection stopped");
    }

    public void receiveFromServer(byte[] data) {
        process(new TestPdu(proxy, this, PduDestination.CLIENT, data, data.length));
    }

    public void receiveFromClient(byte[] data) {
        process(new TestPdu(proxy, this, PduDestination.SERVER, data, data.length));
    }

    public TestPdu getOutgoingPduC2S() {
        return (TestPdu) queueC2S.poll().orElse(null);
    }

    public TestPdu getOutgoingPduS2C() {
        return (TestPdu) queueS2C.poll().orElse(null);
    }
}
