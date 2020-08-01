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
package com.warxim.petep.gui.guide.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import com.warxim.petep.Bundle;
import com.warxim.petep.gui.guide.Guide;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.util.WebUtils;

/** Diagram guide renders diagram of PETEP. */
public final class DiagramGuide extends Guide {
  @Override
  public String getTitle() {
    return "Diagram";
  }

  @Override
  public String getHtml() {
    // Load templates.
    String page = loadHtmlResource("/html/guide/internal/diagram/Diagram.html");
    String interceptorTemplate =
        loadHtmlResource("/html/guide/internal/diagram/DiagramInterceptor.html");

    // Generate interceptors.
    page = page.replace("{{interceptorsC2S}}", generateInterceptors(
        Bundle.getInstance().getInterceptorModuleContainerC2S().getList(), interceptorTemplate,
        loadHtmlResource("/html/guide/internal/diagram/DiagramInterceptorSeparatorRight.html")));

    page = page.replace("{{interceptorsS2C}}",
        generateInterceptors(
            reverseList(Bundle.getInstance().getInterceptorModuleContainerS2C().getList()),
            interceptorTemplate,
            loadHtmlResource("/html/guide/internal/diagram/DiagramInterceptorSeparatorLeft.html")));

    // Generate proxies.
    page = page.replace("{{proxies}}",
        generateProxies(Bundle.getInstance().getProxyModuleContainer().getList(),
            loadHtmlResource("/html/guide/internal/diagram/DiagramProxy.html")));

    return page;
  }

  /** Generates interceptors HTML using specified template separated by specified separator. */
  private static String generateInterceptors(
      List<InterceptorModule> modules,
      String template,
      String separator) {
    StringJoiner joiner = new StringJoiner(separator);

    for (InterceptorModule module : modules) {
      if (!module.isEnabled()) {
        continue;
      }

      joiner.add(template.replace("{{name}}", WebUtils.escapeHtml(module.getName()))
          .replace("{{code}}", WebUtils.escapeHtml(module.getCode()))
          .replace("{{description}}", WebUtils.escapeHtml(module.getDescription())));
    }

    return joiner.toString();
  }

  /** Generates proxies HTML using specified template. */
  private static String generateProxies(List<ProxyModule> modules, String template) {
    StringBuilder builder = new StringBuilder();

    for (ProxyModule module : modules) {
      if (!module.isEnabled()) {
        continue;
      }

      builder.append(template.replace("{{name}}", WebUtils.escapeHtml(module.getName()))
          .replace("{{code}}", WebUtils.escapeHtml(module.getCode()))
          .replace("{{description}}",
              WebUtils.escapeHtml(module.getDescription()).replace("\n", "<br>")));
    }

    return builder.toString();
  }

  /** Returns reversed list. */
  private static <E> List<E> reverseList(List<E> alist) {
    ArrayList<E> rlist = new ArrayList<>(alist);

    Collections.reverse(rlist);

    return rlist;
  }
}
