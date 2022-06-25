package com.warxim.petep.test.base.extension.proxy;

import com.warxim.petep.core.connection.StringBasedConnectionManager;
import com.warxim.petep.helper.PetepHelper;

import java.util.UUID;

/**
 * Test connection manager
 */
public class TestConnectionManager extends StringBasedConnectionManager {
    /**
     * Constructs connection manager based on string codes.
     * @param helper PETEP helper for currently running core
     */
    public TestConnectionManager(PetepHelper helper) {
        super(helper);
    }

    /**
     * Generates random code for connection in form of UUID.
     * @return Random UUID as string
     */
    public String nextCode() {
        return UUID.randomUUID().toString();
    }
}
