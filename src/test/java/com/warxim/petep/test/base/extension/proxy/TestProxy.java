package com.warxim.petep.test.base.extension.proxy;

import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;
import lombok.extern.java.Log;

/**
 * Test proxy
 */
@Log
public class TestProxy extends Proxy {
    private final TestConnectionManager connectionManager;

    /**
     * Constructs test proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     */
    public TestProxy(ProxyModule module, PetepHelper helper) {
        super(module, helper);
        connectionManager = new TestConnectionManager(helper);
        log.info("TestProxy created");
    }

    @Override
    public boolean prepare() {
        log.info("TestProxy prepared");
        return true;
    }

    @Override
    public boolean start() {
        log.info("TestProxy started");
        return true;
    }

    @Override
    public void stop() {
        log.info("TestProxy stopped");
    }

    @Override
    public boolean supports(PDU pdu) {
        if (pdu instanceof TestPdu) {
            return true;
        }
        return false;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public TestConnection createTestConnection() {
        return new TestConnection(connectionManager.nextCode(), this);
    }
}
