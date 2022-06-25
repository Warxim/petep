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

import com.warxim.petep.Bundle;
import com.warxim.petep.gui.guide.Guide;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.module.Module;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.util.WebUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Diagram guide renders diagram of PETEP.
 */
public final class DiagramGuide extends Guide {
    @Override
    public String getTitle() {
        return "Diagram";
    }

    @Override
    public String getHtml() {
        // Load templates.
        var page = loadHtmlResource("/html/guide/internal/diagram/Diagram.html");
        var interceptorTemplate = loadHtmlResource("/html/guide/internal/diagram/DiagramInterceptor.html");

        // Generate interceptors.
        page = page.replace(
                "{{interceptorsC2S}}",
                generateInterceptors(
                        Bundle.getInstance().getInterceptorModuleContainerC2S().getList(),
                        interceptorTemplate,
                        loadHtmlResource("/html/guide/internal/diagram/DiagramInterceptorSeparatorRight.html")
                )
        );

        page = page.replace(
                "{{interceptorsS2C}}",
                generateInterceptors(
                        reverseList(Bundle.getInstance().getInterceptorModuleContainerS2C().getList()),
                        interceptorTemplate,
                        loadHtmlResource("/html/guide/internal/diagram/DiagramInterceptorSeparatorLeft.html")
                )
        );

        // Generate proxies.
        page = page.replace(
                "{{proxies}}",
                generateProxies(
                        Bundle.getInstance().getProxyModuleContainer().getList(),
                        loadHtmlResource("/html/guide/internal/diagram/DiagramProxy.html")
                )
        );

        return page;
    }

    /**
     * Generates interceptors HTML using specified template separated by specified separator.
     */
    private static String generateInterceptors(
            List<InterceptorModule> modules,
            String template,
            String separator) {
        var joiner = new StringJoiner(separator);

        for (var module : modules) {
            if (!module.isEnabled()) {
                continue;
            }

            var filledTemplate = preprocessTemplate(template, module);
            joiner.add(filledTemplate);
        }

        return joiner.toString();
    }

    /**
     * Generates proxies HTML using specified template.
     */
    private static String generateProxies(List<ProxyModule> modules, String template) {
        var builder = new StringBuilder();

        for (var module : modules) {
            if (!module.isEnabled()) {
                continue;
            }

            var filledTemplate = preprocessTemplate(template, module);
            builder.append(filledTemplate);
        }

        return builder.toString();
    }

    /**
     * Preprocesses template using given module (replaces code, name, description placeholders).
     */
    private static String preprocessTemplate(String template, Module<?> module) {
        var name = WebUtils.escapeHtml(module.getName());
        var code = WebUtils.escapeHtml(module.getCode());
        var description = WebUtils.escapeHtml(module.getDescription()).replace("\n", "<br>");
        return template
                .replace("{{name}}", name)
                .replace("{{code}}", code)
                .replace("{{description}}", description);
    }

    /**
     * Returns reversed list.
     */
    private static <E> List<E> reverseList(List<E> alist) {
        var reversedList = new ArrayList<E>(alist);

        Collections.reverse(reversedList);

        return reversedList;
    }
}
