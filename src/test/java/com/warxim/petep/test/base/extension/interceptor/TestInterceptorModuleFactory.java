package com.warxim.petep.test.base.extension.interceptor;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import lombok.extern.java.Log;

/**
 * Test interceptor module factory
 */
@Log
public class TestInterceptorModuleFactory extends InterceptorModuleFactory {
    /**
     * Constructs test interceptor module factory.
     * @param extension Extension that owns this factory
     */
    public TestInterceptorModuleFactory(Extension extension) {
        super(extension);
        log.info("TestInterceptorModuleFactory created");
    }

    @Override
    public String getName() {
        return "Test Interceptor";
    }

    @Override
    public String getCode() {
        return "test-interceptor";
    }

    @Override
    public InterceptorModule createModule(String code, String name, String description, boolean enabled) {
        return new TestInterceptorModule(this, code, name, description, enabled);
    }
}
