package com.warxim.petep.test.base.extension.interceptor;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.util.PduUtils;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Test interceptor
 */
@Log
@Getter
public class TestInterceptor extends Interceptor {
    private int counter;

    /**
     * Constructs test interceptor.
     * @param id Identifier of interceptor (index of the interceptor)
     * @param module Parent module of the interceptor
     * @param helper Helper for accessing running instance of PETEP core
     */
    public TestInterceptor(int id, InterceptorModule module, PetepHelper helper) {
        super(id, module, helper);
        counter = 0;
        log.info("TestInterceptor created");
    }

    @Override
    public boolean prepare() {
        log.info("TestInterceptor prepared");
        return true;
    }

    @Override
    public boolean intercept(PDU pdu) {
        ++counter;

        log.info(String.format(
                "TestInterceptor intercepted PDU n.%d (%d bytes): %s",
                counter,
                pdu.getSize(),
                PduUtils.bufferToHexString(pdu)));
        return true;
    }

    @Override
    public void stop() {
        log.info("TestInterceptor stopped");
    }
}
