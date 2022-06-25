package com.warxim.petep.test.integration;

import com.warxim.petep.test.base.extension.TestExtensionHelper;
import com.warxim.petep.test.base.extension.TestPetepHelper;
import com.warxim.petep.test.base.extension.interceptor.TestInterceptor;
import com.warxim.petep.test.base.extension.proxy.TestProxy;
import com.warxim.petep.test.base.util.TestUtils;
import lombok.extern.java.Log;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log
public class CoreIntegrationTest {
    protected final TestExtensionHelper testExtensionHelper;

    public CoreIntegrationTest() {
        testExtensionHelper = new TestExtensionHelper();
    }

    @DataProvider(name = "testPetepHelper")
    public Object[][] testPetepHelperProvider() {
        return new Object[][]{
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(1),
                                testExtensionHelper.createInterceptorModules(1),
                                testExtensionHelper.createInterceptorModules(1)
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(1),
                                List.of(),
                                List.of()
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(1),
                                List.of(),
                                testExtensionHelper.createInterceptorModules(2)
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(1),
                                testExtensionHelper.createInterceptorModules(2),
                                List.of()
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(3),
                                testExtensionHelper.createInterceptorModules(2),
                                testExtensionHelper.createInterceptorModules(1)
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(3),
                                testExtensionHelper.createInterceptorModules(1),
                                testExtensionHelper.createInterceptorModules(2)
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(2),
                                testExtensionHelper.createInterceptorModules(3),
                                testExtensionHelper.createInterceptorModules(1)
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(2),
                                testExtensionHelper.createInterceptorModules(3),
                                testExtensionHelper.createInterceptorModules(3)
                        )
                },
                {
                        new TestPetepHelper(
                                testExtensionHelper.createProxyModules(2),
                                testExtensionHelper.createInterceptorModules(1),
                                testExtensionHelper.createInterceptorModules(3)
                        )
                },
        };
    }

    @Test(dataProvider = "testPetepHelper")
    public void basicFlowTest(TestPetepHelper testPetepHelper) {
        // Prepare
        log.info("Preparing for test...");
        var petep = testPetepHelper.getPetep();
        testPetepHelper.start();

        // Perform
        log.info("Running test...");
        var proxy = (TestProxy) petep.getProxyManager().getList().get(0);
        var connection = proxy.createTestConnection();

        var dataFromClient = List.of(
                "Test message 1 from client!".getBytes(),
                "".getBytes(),
                "Test message 2 from client!".getBytes(),
                "Test message 3 from client!".getBytes()
        );

        var dataFromServer = List.of(
                "Test message 1 from server!".getBytes(),
                "Test message 2 from server!".getBytes(),
                "Test message 3 from server!".getBytes(),
                "Test message 4 from server!".getBytes(),
                "".getBytes(),
                "Test message 5 from server!".getBytes()
        );

        dataFromClient.forEach(connection::receiveFromClient);
        dataFromServer.forEach(connection::receiveFromServer);

        // Check outgoing PDUs C2S
        var outgoingC2S = TestUtils.getMultipleWithRetries(dataFromClient.size(), connection::getOutgoingPduC2S, 10, 100);
        for (int i = 0; i < outgoingC2S.size(); ++i) {
            assertThat(outgoingC2S.get(i).getBuffer())
                    .containsExactly(dataFromClient.get(i));
        }

        // Check outgoing PDUs S2C
        var outgoingS2C = TestUtils.getMultipleWithRetries(dataFromServer.size(), connection::getOutgoingPduS2C, 10, 100);
        for (int i = 0; i < outgoingS2C.size(); ++i) {
            assertThat(outgoingS2C.get(i).getBuffer())
                    .containsExactly(dataFromServer.get(i));
        }

        // Check that C2S interceptors intercepted all C2S PDUs
        assertThat(petep.getInterceptorManagerC2S().getList())
                .allSatisfy(
                        interceptor -> assertThat(((TestInterceptor) interceptor).getCounter()).isEqualTo(dataFromClient.size())
                );

        // Check that S2C interceptors intercepted all S2C PDUs
        assertThat(petep.getInterceptorManagerS2C().getList())
                .allSatisfy(
                        interceptor -> assertThat(((TestInterceptor) interceptor).getCounter()).isEqualTo(dataFromServer.size())
                );

        // Cleanup
        log.info("Cleaning up after test...");
        testPetepHelper.stop();
    }

}
