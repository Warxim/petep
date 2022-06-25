package com.warxim.petep.test.base.extension.proxy;

import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;
import lombok.extern.java.Log;

/**
 * Test proxy module
 */
@Log
public class TestProxyModule extends ProxyModule {
    /**
     * Constructs test proxy module.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     */
    public TestProxyModule(ProxyModuleFactory factory, String code, String name, String description, boolean enabled) {
        super(factory, code, name, description, enabled);
        log.info("TestProxyModule created");
    }

    @Override
    public Proxy createProxy(PetepHelper helper) {
        return new TestProxy(this, helper);
    }
}