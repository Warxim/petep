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
package com.warxim.petep.core.listener;

import java.util.ArrayList;
import java.util.List;
import com.warxim.petep.helper.PetepHelper;

/** Listener manager that allows modules to register their own listener. */
public final class PetepListenerManager implements PetepListener {
  private final List<PetepListener> listeners;

  public PetepListenerManager() {
    listeners = new ArrayList<>();
  }

  public void registerListener(PetepListener listener) {
    listeners.add(listener);
  }

  public void unregisterListener(PetepListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void beforePrepare(PetepHelper helper) {
    listeners.parallelStream().forEach(listener -> listener.beforePrepare(helper));
  }

  @Override
  public void afterPrepare(PetepHelper helper) {
    listeners.parallelStream().forEach(listener -> listener.afterPrepare(helper));
  }

  @Override
  public void beforeStart(PetepHelper helper) {
    listeners.parallelStream().forEach(listener -> listener.beforeStart(helper));
  }

  @Override
  public void afterStart(PetepHelper helper) {
    listeners.parallelStream().forEach(listener -> listener.afterStart(helper));
  }

  @Override
  public void beforeStop(PetepHelper helper) {
    listeners.parallelStream().forEach(listener -> listener.beforeStop(helper));
  }

  @Override
  public void afterStop(PetepHelper helper) {
    listeners.parallelStream().forEach(listener -> listener.afterStop(helper));
  }
}
