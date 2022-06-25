/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2020 Michal Válka
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep;

import com.warxim.petep.bootstrap.CommandLineArguments;
import com.warxim.petep.common.Constant;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.configuration.*;
import com.warxim.petep.core.PetepManager;
import com.warxim.petep.core.listener.PetepListenerManager;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.extension.ExtensionManager;
import com.warxim.petep.extension.receiver.ReceiverManager;
import com.warxim.petep.helper.DefaultExtensionHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactoryManager;
import com.warxim.petep.interceptor.module.InterceptorModuleContainer;
import com.warxim.petep.project.Project;
import com.warxim.petep.proxy.factory.ProxyFactoryModuleManager;
import com.warxim.petep.proxy.module.ProxyModuleContainer;
import com.warxim.petep.util.FileUtils;
import lombok.Getter;

import java.io.File;

/**
 * Singleton for PETEP assets.
 */
@Getter
public final class Bundle {
    /**
     * Singleton instance.
     */
    private static volatile Bundle instance;

    /*
     * DATA
     */
    private String projectPath;
    private Project project;
    private ContextType contextType;

    /*
     * MANAGERS
     */
    private ExtensionManager extensionManager;
    private ProxyFactoryModuleManager proxyModuleFactoryManager;
    private InterceptorModuleFactoryManager interceptorModuleFactoryManager;
    private PetepListenerManager petepListenerManager;
    private PetepManager petepManager;
    private ReceiverManager receiverManager;

    private Bundle() {
    }

    /**
     * Creates instance of Bundle or returns existing instance if it exists.
     * @return Bundle instance
     */
    public static Bundle getInstance() {
        if (instance == null) {
            synchronized(Bundle.class) {
                if (instance == null) {
                    instance = new Bundle();
                }
            }
        }

        return instance;
    }

    /**
     * Loads project and managers.
     * @param arguments Command line arguments
     * @throws ConfigurationException if the bundle could not be initialized because of configuration error
     */
    public void load(CommandLineArguments arguments) throws ConfigurationException {
        contextType = arguments.getContextType();
        projectPath = arguments.getProjectPath();

        var configDirectory = FileUtils.getProjectFileAbsolutePath(Constant.PROJECT_CONFIG_DIRECTORY) + File.separator;

        project = ProjectLoader.load(configDirectory + Constant.PROJECT_CONFIG_FILE);

        receiverManager = new ReceiverManager();
        // Create managers.
        extensionManager = new ExtensionManager(ExtensionsLoader.load(configDirectory + Constant.EXTENSIONS_CONFIG_FILE));
        proxyModuleFactoryManager = new ProxyFactoryModuleManager();
        interceptorModuleFactoryManager = new InterceptorModuleFactoryManager();
        petepListenerManager = new PetepListenerManager();

        // Init extensions using default extension helper.
        extensionManager.init(new DefaultExtensionHelper(this));

        // Load proxies.
        var proxyModuleContainer = new ProxyModuleContainer(ModulesLoader.load(
                configDirectory + Constant.PROXIES_CONFIG_FILE,
                proxyModuleFactoryManager));

        // Load interceptor module managers.
        var interceptorModuleContainerC2S = new InterceptorModuleContainer(ModulesLoader.load(
                configDirectory + Constant.INTERCEPTORS_C2S_CONFIG_FILE,
                interceptorModuleFactoryManager));

        var interceptorModuleContainerS2C = new InterceptorModuleContainer(ModulesLoader.load(
                configDirectory + Constant.INTERCEPTORS_S2C_CONFIG_FILE,
                interceptorModuleFactoryManager));

        // Create PETEP manager.
        petepManager = new PetepManager(
                proxyModuleContainer,
                interceptorModuleContainerC2S,
                interceptorModuleContainerS2C,
                petepListenerManager);
    }

    /**
     * Saves project, extensions and configuration.
     * @throws ConfigurationException if the bundle could not be saved because of configuration error
     */
    public void save() throws ConfigurationException {
        var configDirectory =
                FileUtils.getProjectFileAbsolutePath(Constant.PROJECT_CONFIG_DIRECTORY) + File.separator;

        ProjectSaver.save(
                configDirectory + Constant.PROJECT_CONFIG_FILE,
                project);

        ExtensionsSaver.save(
                configDirectory + Constant.EXTENSIONS_CONFIG_FILE,
                extensionManager.getList());

        ModulesSaver.save(
                configDirectory + Constant.PROXIES_CONFIG_FILE,
                petepManager.getProxyModuleContainer().getList());

        ModulesSaver.save(
                configDirectory + Constant.INTERCEPTORS_C2S_CONFIG_FILE,
                petepManager.getInterceptorModuleContainerC2S().getList());

        ModulesSaver.save(
                configDirectory + Constant.INTERCEPTORS_S2C_CONFIG_FILE,
                petepManager.getInterceptorModuleContainerS2C().getList());
    }

    /**
     * Destroys the bundle.
     */
    public void destroy() {
        if (extensionManager != null) {
            extensionManager.destroy();
        }
    }

    /*
     * GETTERS
     */
    /**
     * Obtains proxy module container for storing proxy modules.
     * @return Proxy module container
     */
    public ProxyModuleContainer getProxyModuleContainer() {
        return petepManager.getProxyModuleContainer();
    }

    /**
     * Returns interceptor module container in direction C2S. (Client -&gt; Server)
     * @return Interceptor module container for direction C2S
     */
    public InterceptorModuleContainer getInterceptorModuleContainerC2S() {
        return petepManager.getInterceptorModuleContainerC2S();
    }

    /**
     * Returns interceptor module container in direction S2C. (Client &lt;- Server)
     * @return Interceptor module container for direction S2C
     */
    public InterceptorModuleContainer getInterceptorModuleContainerS2C() {
        return petepManager.getInterceptorModuleContainerS2C();
    }

    /**
     * Obtains receiver manager for handling receiver instances.
     * @return Receiver manager
     */
    public ReceiverManager getReceiverManager() {
        return receiverManager;
    }
}
