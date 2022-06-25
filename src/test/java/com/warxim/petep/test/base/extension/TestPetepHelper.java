package com.warxim.petep.test.base.extension;

import com.warxim.petep.core.PETEP;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.PetepListenerManager;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.module.InterceptorModuleContainer;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.module.ProxyModuleContainer;
import com.warxim.petep.test.base.util.TestUtils;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;

/**
 * Test PETEP helper
 */
@Log
@Getter
public class TestPetepHelper {
    protected static final int PETEP_STATE_RETRY_COUNT = 5;
    protected static final int PETEP_STATE_RETRY_INTERVAL = 100;

    protected final ProxyModuleContainer proxyModuleContainer;
    protected final InterceptorModuleContainer interceptorModuleContainerC2S;
    protected final InterceptorModuleContainer interceptorModuleContainerS2C;
    protected final PetepListenerManager petepListenerManager;

    protected PETEP petep;

    /**
     * Constructs test PETEP helper.
     * @param proxyModules List of proxy modules to use
     * @param interceptorModulesC2S List of interceptor modules to use in direction C2S
     * @param interceptorModulesS2C List of interceptor modules to use in direction S2C
     */
    public TestPetepHelper(List<ProxyModule> proxyModules,
                           List<InterceptorModule> interceptorModulesC2S,
                           List<InterceptorModule> interceptorModulesS2C) {
        proxyModuleContainer = new ProxyModuleContainer(proxyModules);
        interceptorModuleContainerC2S = new InterceptorModuleContainer(interceptorModulesC2S);
        interceptorModuleContainerS2C = new InterceptorModuleContainer(interceptorModulesS2C);

        petepListenerManager = new PetepListenerManager();

        petep = new PETEP(
                proxyModuleContainer,
                interceptorModuleContainerC2S,
                interceptorModuleContainerS2C,
                petepListenerManager);
    }

    /**
     * Starts PETEP core.
     */
    public void start() {
        petep.start();
        waitForState(PetepState.STARTED);
    }

    /**
     * Stops PETEP core.
     */
    public void stop() {
        petep.stop();
        waitForState(PetepState.STOPPED);
    }

    private void waitForState(PetepState state) {
        for (int retryCount = PETEP_STATE_RETRY_COUNT; retryCount > 0; --retryCount) {
            if (petep.getState().equals(state)) {
                break;
            }

            TestUtils.sleep(PETEP_STATE_RETRY_INTERVAL);
        }
    }
}