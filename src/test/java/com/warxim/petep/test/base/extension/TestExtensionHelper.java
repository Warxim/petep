package com.warxim.petep.test.base.extension;

import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.test.base.extension.interceptor.TestInterceptorModuleFactory;
import com.warxim.petep.test.base.extension.proxy.TestProxyModuleFactory;
import lombok.Getter;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test extension helper
 */
@Getter
public class TestExtensionHelper {
    public static final String DEFAULT_EXTENSION_PATH = "local-test-ext";

    private final TestExtension extension;
    private final TestProxyModuleFactory proxyModuleFactory;
    private final TestInterceptorModuleFactory interceptorModuleFactory;
    private final Random rand;

    /**
     * Constructs test extension helper
     */
    public TestExtensionHelper() {
        extension = new TestExtension(DEFAULT_EXTENSION_PATH);
        proxyModuleFactory = new TestProxyModuleFactory(extension);
        interceptorModuleFactory = new TestInterceptorModuleFactory(extension);
        rand = new Random();
    }

    /**
     * Creates test proxy module.
     */
    public ProxyModule createProxyModule(String code, String name, String description, boolean enabled) {
        return proxyModuleFactory.createModule(
                code,
                name,
                description,
                enabled);
    }

    /**
     * Creates test proxy module with default generated parameters.
     */
    public ProxyModule createProxyModule() {
        int id = generateId();
        return proxyModuleFactory.createModule(
                "proxy-" + id,
                "Proxy " + id,
                "Simple test proxy " + id,
                true);
    }

    /**
     * Creates specified number of randomly generated test proxy modules.
     */
    public List<ProxyModule> createProxyModules(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> createProxyModule())
                .collect(Collectors.toList());
    }

    /**
     * Creates test interceptor module.
     */
    public InterceptorModule createInterceptorModule(String code, String name, String description, boolean enabled) {
        return interceptorModuleFactory.createModule(
                code,
                name,
                description,
                enabled);
    }

    /**
     * Creates test interceptor module with default generated parameters.
     */
    public InterceptorModule createInterceptorModule() {
        int id = generateId();
        return interceptorModuleFactory.createModule(
                "interceptor-" + id,
                "Interceptor " + id,
                "Simple test interceptor " + id,
                true);
    }

    /**
     * Creates specified number of randomly generated test interceptor modules.
     */
    public List<InterceptorModule> createInterceptorModules(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> createInterceptorModule())
                .collect(Collectors.toList());
    }

    private int generateId() {
        return rand.nextInt(Integer.MAX_VALUE);
    }

}
