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
package com.warxim.petep.helper;

import com.warxim.petep.common.ContextType;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.receiver.Receiver;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;

import java.util.List;
import java.util.Optional;

/**
 * Helper for extensions that allows extensions to use internal components, register module
 * factories and get information about the project.
 */
@PetepAPI
public interface ExtensionHelper {
    /*
     * PROJECT INFO
     */
    /**
     * Obtains project name.
     * @return Name of the opened project
     */
    String getProjectName();

    /**
     * Obtains project description.
     * @return Description of the opened project
     */
    String getProjectDescription();

    /*
     * APPLICATION INFO
     */
    /**
     * Obtains context type of current application instance (whether it is running in GUI or command line).
     * @return Context type (GUI / COMMAND_LINE)
     */
    ContextType getContextType();

    /**
     * CORE INFO
     */
    /**
     * Obtains PETEP core state.
     * @return PETEP core state
     */
    PetepState getPetepState();

    /**
     * Obtains PETEP helper for currently running core.
     * @return  PETEP helper if core is running;
     *          {@code Optional.empty()} otherwise
     */
    Optional<PetepHelper> getPetepHelper();

    /*
     * MODULES
     */
    /**
     * Registers proxy module factory for creating custom proxies.
     * @param factory Proxy module factory to be registered
     * @return {@code true} if the factory was successfully registered (code is available)
     */
    boolean registerProxyModuleFactory(ProxyModuleFactory factory);

    /**
     * Unregisters proxy module factory.
     * @param factory Proxy module factory to be unregistered
     * @return {@code true} if the factory was successfully unregistered (factory was registered before)
     */
    boolean unregisterProxyModuleFactory(ProxyModuleFactory factory);

    /**
     * Registers interceptor module factory for creating custom interceptors.
     * @param factory Interceptor module factory to be registered
     * @return {@code true} if the factory was successfully registered (code is available)
     */
    boolean registerInterceptorModuleFactory(InterceptorModuleFactory factory);

    /**
     * Unregisters interceptor module factory.
     * @param factory Interceptor module factory to be unregistered
     * @return {@code true} if the factory was successfully unregistered (factory was registered before)
     */
    boolean unregisterInterceptorModuleFactory(InterceptorModuleFactory factory);

    /*
     * LISTENERS
     */
    /**
     * Registers listener for PETEP core events (start, stop, ...).
     * <p>
     *     Listeners are persisted using weak reference, so the strong reference has to be saved somewhere.
     *     Otherwise the GC would get rid of it.
     * </p>
     * <p>
     *     Registered listeners are automatically freed after they are only weakly referenced,
     *     however, it is possible to unregister them using {@link ExtensionHelper#unregisterPetepListener}
     * </p>
     * @param listener Listener to be registered
     */
    void registerPetepListener(PetepListener listener);

    /**
     * Unregisters listener for PETEP core events.
     * @param listener Listener to be unregistered
     */
    void unregisterPetepListener(PetepListener listener);

    /*
     * EXTENSIONS
     */
    /**
     * Obtains list of loaded extensions.
     * @return List of extensions, which are loaded in the application
     */
    List<Extension> getExtensions();

    /**
     * Obtains extension by its code.
     * @param code Code of the extension to find
     * @return Extension of a given code;
     *         Empty optional if that extension is not loaded in the project
     */
    Optional<Extension> getExtension(String code);

    /*
     * RECEIVERS
     */
    /**
     * Registers receiver for receiving data from extensions.
     * <p>
     *     Receivers are persisted using weak reference, so the strong reference has to be saved somewhere.
     *     Otherwise the GC would get rid of it.
     * </p>
     * <p>
     *     Registered receivers are automatically freed after they are only weakly referenced,
     *     however, it is possible to unregister them using {@link ExtensionHelper#unregisterReceiver}
     * </p>
     * @param receiver Receiver to be registered
     * @return {@code true} if the receiver was successfully registered (code is available)
     */
    boolean registerReceiver(Receiver receiver);

    /**
     * Unregisters receiver.
     * @param receiver Receiver to be unregistered
     */
    void unregisterReceiver(Receiver receiver);

    /**
     * Sends data to receiver with specified code.
     * @param code Code of target receiver
     * @param data Data to be sent to the receiver
     */
    void sendToReceiver(String code, Object data);

    /**
     * Obtains list of all registered receivers.
     * @return List of all receivers
     */
    List<Receiver> getReceivers();

    /**
     * Obtains list of registered receivers that support given class.
     * @param clazz Class to check
     * @return List of receivers that support given class
     */
    List<Receiver> getReceivers(Class<?> clazz);

    /*
     * MODULES
     */
    /**
     * Obtains list of interceptor modules in direction C2S (Client -&gt; Server).
     * @return List of interceptor modules
     */
    List<InterceptorModule> getInterceptorModulesC2S();

    /**
     * Obtains list of interceptor modules in direction S2C (Client &lt;- Server).
     * @return List of interceptor modules
     */
    List<InterceptorModule> getInterceptorModulesS2C();

    /**
     * Obtains list of proxy modules.
     * @return List of proxy modules
     */
    List<ProxyModule> getProxyModules();

}
