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
package com.warxim.petep.extension.internal.logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;

/** Logger extension. */
public final class LoggerExtension extends Extension {
  private ExecutorService executor;

  /** Logger extension constructor. */
  public LoggerExtension(String path) {
    super(path);

    executor = null;

    Logger.getGlobal().info("Logger extension created.");
  }

  @Override
  public void init(ExtensionHelper helper) {
    helper.registerInterceptorModuleFactory(new LoggerInterceptorModuleFactory(this));

    helper.registerPetepListener(new PetepListener() {
      /** Creates workfer for each log file. */
      private void processInterceptors(
          List<Interceptor> interceptors,
          Map<String, LoggerWorker> workers) {
        for (Interceptor interceptor : interceptors) {
          if (interceptor instanceof LoggerInterceptor) {
            // Get or create a log worker
            LoggerWorker worker = workers.computeIfAbsent(
                ((LoggerInterceptorModule) interceptor.getModule()).getConfig().getPath(),
                LoggerWorker::new);

            ((LoggerInterceptor) interceptor).setWorker(worker);
          }
        }
      }

      @Override
      public void beforePrepare(PetepHelper helper) {
        Map<String, LoggerWorker> workers = new HashMap<>();

        // Process interceptors.

        processInterceptors(helper.getInterceptorsC2S(), workers);
        processInterceptors(helper.getInterceptorsS2C(), workers);

        if (workers.isEmpty()) {
          return;
        }

        // Start workers.
        executor = Executors.newCachedThreadPool();

        for (LoggerWorker worker : workers.values()) {
          executor.submit(worker);
        }
      }

      @Override
      public void afterStop(PetepHelper helper) {
        if (executor != null) {
          executor.shutdownNow();
          executor = null;
        }
      }
    });

    Logger.getGlobal().info("Logger extension registered.");
  }

  @Override
  public String getCode() {
    return "logger";
  }

  @Override
  public String getName() {
    return "Logger";
  }

  @Override
  public String getDescription() {
    return "Internal logger extension.";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public void initGui(GuiHelper helper) {
    helper.registerGuide(new LoggerGuide());
  }
}
