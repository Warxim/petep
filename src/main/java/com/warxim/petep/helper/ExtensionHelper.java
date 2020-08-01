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

import java.util.List;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;

/**
 * Helper for extensions that allows extensions to use internal components, register module
 * factories and get information about the project.
 */
@PetepAPI
public interface ExtensionHelper {
  /*
   * PROJECT INFO
   */
  String getProjectName();

  String getProjectDescription();

  /*
   * APPLICATION INFO
   */
  /** Returns context type (GUI / COMMAND_LINE). */
  ContextType getContextType();

  /*
   * MODULES
   */
  boolean registerProxyModuleFactory(ProxyModuleFactory factory);

  boolean unregisterProxyModuleFactory(ProxyModuleFactory factory);

  boolean registerInterceptorModuleFactory(InterceptorModuleFactory factory);

  boolean unregisterInterceptorModuleFactory(InterceptorModuleFactory factory);

  /*
   * LISTENERS
   */
  void registerPetepListener(PetepListener listener);

  void unregisterPetepListener(PetepListener listener);

  /*
   * EXTENSIONS
   */
  /** Returns list of extensions loaded in the project. */
  List<Extension> getExtensions();

  /** Returns extension of a given code or null if that extension is not loaded in the project. */
  Extension getExtension(String code);

  /*
   * MODULES
   */
  /** Returns list of interceptor modules in direction C2S (Client -> Server). */
  List<InterceptorModule> getInterceptorModulesC2S();

  /** Returns list of interceptor modules in direction S2C (Client <- Server). */
  List<InterceptorModule> getInterceptorModulesS2C();

  /** Returns list of proxy modules. */
  List<ProxyModule> getProxyModules();
}
