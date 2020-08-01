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

import java.util.Collections;
import java.util.List;
import com.warxim.petep.Bundle;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;

/** Default extension helper implementation. */
public final class DefaultExtensionHelper implements ExtensionHelper {
  private final Bundle bundle;

  /** Constructor of default extension helper. */
  public DefaultExtensionHelper(Bundle bundle) {
    this.bundle = bundle;
  }

  /*
   * PROJECT INFO
   */
  @Override
  public String getProjectName() {
    return bundle.getProject().getName();
  }

  @Override
  public String getProjectDescription() {
    return bundle.getProject().getDescription();
  }

  /*
   * APPLICATION INFO
   */
  @Override
  public ContextType getContextType() {
    return bundle.getContextType();
  }

  /*
   * MODULES
   */
  @Override
  public boolean registerProxyModuleFactory(ProxyModuleFactory factory) {
    return bundle.getProxyModuleFactoryManager().registerModuleFactory(factory);
  }

  @Override
  public boolean unregisterProxyModuleFactory(ProxyModuleFactory factory) {
    return bundle.getProxyModuleFactoryManager().unregisterModuleFactory(factory);
  }

  @Override
  public boolean registerInterceptorModuleFactory(InterceptorModuleFactory factory) {
    return bundle.getInterceptorModuleFactoryManager().registerModuleFactory(factory);
  }

  @Override
  public boolean unregisterInterceptorModuleFactory(InterceptorModuleFactory factory) {
    return bundle.getInterceptorModuleFactoryManager().unregisterModuleFactory(factory);
  }

  /*
   * LISTENERS
   */
  @Override
  public void registerPetepListener(PetepListener listener) {
    bundle.getPetepListenerManager().registerListener(listener);
  }

  @Override
  public void unregisterPetepListener(PetepListener listener) {
    bundle.getPetepListenerManager().unregisterListener(listener);
  }

  /*
   * EXTENSIONS
   */
  @Override
  public List<Extension> getExtensions() {
    return bundle.getExtensionManager().getList();
  }

  /*
   * Modules
   */
  @Override
  public List<InterceptorModule> getInterceptorModulesC2S() {
    return Collections.unmodifiableList(bundle.getInterceptorModuleContainerC2S().getList());
  }

  @Override
  public List<InterceptorModule> getInterceptorModulesS2C() {
    return Collections.unmodifiableList(bundle.getInterceptorModuleContainerS2C().getList());
  }

  @Override
  public List<ProxyModule> getProxyModules() {
    return Collections.unmodifiableList(bundle.getProxyModuleContainer().getList());
  }

  @Override
  public Extension getExtension(String code) {
    return bundle.getExtensionManager().getExtension(code);
  }
}
