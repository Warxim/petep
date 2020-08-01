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

import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.guide.Guide;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;

/** Default GUI helper implementation. */
public final class DefaultGuiHelper implements GuiHelper {
  private final GuiBundle bundle;

  /** Constructor of default GUI helper. */
  public DefaultGuiHelper(GuiBundle bundle) {
    this.bundle = bundle;
  }

  @Override
  public void registerTab(String title, Node node) {
    Platform.runLater(() -> bundle.getApplicationController().registerTab(title, node));
  }

  @Override
  public void registerSettingsTab(String title, Node node) {
    Platform.runLater(() -> bundle.getSettingsController().registerTab(title, node));
  }

  @Override
  public void unregisterTab(Node node) {
    Platform.runLater(() -> bundle.getApplicationController().unregisterTab(node));
  }

  @Override
  public void unregisterSettingsTab(Node node) {
    Platform.runLater(() -> bundle.getSettingsController().unregisterTab(node));
  }

  @Override
  public void registerGuide(Guide guide) {
    bundle.getGuideManager().add(guide);
  }

  @Override
  public void unregisterGuide(Guide guide) {
    bundle.getGuideManager().remove(guide);
  }

  @Override
  public Image getPetepIcon() {
    return GuiBundle.getInstance().getPetepIcon();
  }
}
