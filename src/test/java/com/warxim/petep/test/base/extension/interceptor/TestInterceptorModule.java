package com.warxim.petep.test.base.extension.interceptor;

import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.worker.Interceptor;

/**
 * Test interceptor module
 */
public class TestInterceptorModule extends InterceptorModule {
    /**
     * Constructs test interceptor module.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     */
    public TestInterceptorModule(InterceptorModuleFactory factory,
                                 String code,
                                 String name,
                                 String description,
                                 boolean enabled) {
        super(factory, code, name, description, enabled);
    }

    @Override
    public Interceptor createInterceptor(int id, PetepHelper helper) {
        return new TestInterceptor(id, this, helper);
    }
}
