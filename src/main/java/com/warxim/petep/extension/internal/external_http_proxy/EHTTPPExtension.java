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
package com.warxim.petep.extension.internal.external_http_proxy;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.persistence.Storable;
import javafx.fxml.FXMLLoader;

/** External HTTP proxy extension. */
public final class EHTTPPExtension extends Extension implements Storable<EHTTPPConfig> {
  private EHTTPPManager manager;
  private EHTTPPConfig config;

  /** External HTTP proxy extension constructor. */
  public EHTTPPExtension(String path) {
    super(path);
    manager = null;
    Logger.getGlobal().info("HTTP Proxy extension loaded.");
  }

  @Override
  public void init(ExtensionHelper helper) {
    Logger.getGlobal().info("HTTP Proxy extension registered.");

    if (config == null) {
      // Use defaults.
      config = new EHTTPPConfig("127.0.0.1", 8181, "127.0.0.1", 8080);
    }

    helper.registerPetepListener(new PetepListener() {
      /** Creates HTTP client for each HTTPP interceptor. */
      private void processInterceptors(List<Interceptor> interceptors) {
        for (Interceptor interceptor : interceptors) {
          if (interceptor instanceof EHTTPPInterceptor) {
            if (manager == null) {
              manager = new EHTTPPManager(config);
            }

            PduQueue queue = new PduQueue();

            ((EHTTPPInterceptor) interceptor).setQueue(queue);

            // Create HTTPP client for interceptor queue and set the following interceptor as
            // a target.
            manager.createClient(queue, interceptor.getId() + 1);
          }
        }
      }

      @Override
      public void beforePrepare(PetepHelper helper) {
        processInterceptors(helper.getInterceptorsC2S());
        processInterceptors(helper.getInterceptorsS2C());

        if (manager != null) {
          manager.start(helper);
        }
      }

      @Override
      public void beforeStop(PetepHelper helper) {
        if (manager != null) {
          manager.stop();
          manager = null;
        }
      }
    });

    helper.registerInterceptorModuleFactory(new EHTTPPInterceptorModuleFactory(this));
  }

  @Override
  public void initGui(GuiHelper helper) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(
          getClass().getResource("/fxml/extension/internal/ehttp/HTTPPSettings.fxml"));
      fxmlLoader.setController(new EHTTPPSettingsController(config));
      helper.registerSettingsTab("HTTP Proxy", fxmlLoader.load());
      Logger.getGlobal().info("HTTP Proxy extension GUI registered.");
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "HTTP Proxy tab could not be created.", e);
    }

    helper.registerGuide(new EHTTPPGuide());
  }

  @Override
  public String getCode() {
    return "external_http_proxy";
  }

  @Override
  public String getName() {
    return "External HTTP Proxy";
  }

  @Override
  public String getDescription() {
    return "Internal extension for external HTTP proxy usage.";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public EHTTPPConfig saveStore() {
    return config;
  }

  @Override
  public void loadStore(EHTTPPConfig store) {
    this.config = store;
  }
}
