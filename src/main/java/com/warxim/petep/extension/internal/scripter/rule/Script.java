/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.scripter.rule;

import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.util.ScripterUtils;
import lombok.Getter;
import org.graalvm.polyglot.Context;

/**
 * Base class for script rules.
 */
@Getter
public abstract class Script extends Rule implements AutoCloseable {
    protected static final String HELPER_VARIABLE = "scripter";

    protected final String language;
    protected final ScriptInterceptorManager scriptInterceptorManager;
    protected Context context;

    /**
     * Constructs script.
     * @param name Name of the script
     * @param description Description of the script
     * @param enabled {@code true} if the script should be used
     * @param language Language of the script
     */
    protected Script(String name, String description, boolean enabled, String language) {
        super(name, description, enabled);
        this.language = language;
        if (enabled) {
            scriptInterceptorManager = new ScriptInterceptorManager();
        } else {
            scriptInterceptorManager = null;
        }
    }

    /**
     * Gets type of the script.
     * @return Type of script
     */
    public abstract ScriptType getType();

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && getContext() != null;
    }

    @Override
    public void close() {
        if (context != null) {
            context.close();
        }
    }

    /**
     * Initialized context (creates and binds helper, ...).
     * @param factory Script helper factory
     * @param script String representation of script (loaded script)
     * @param contextPath Context path for script execution
     */
    protected void initContext(ScriptHelperFactory factory, String script, String contextPath) {
        context = ScripterUtils.createContext();

        var helper = factory.createHelper(
                getName(),
                language,
                contextPath,
                context,
                scriptInterceptorManager
        );

        context.getBindings(language).putMember(
                HELPER_VARIABLE,
                helper
        );

        context.eval(language, script);
    }

    /**
     * Creates deep copy of the Script.
     * @param factory Factory for creation of script helpers
     * @return Deep copy of the Script
     */
    public abstract Script copy(ScriptHelperFactory factory);
}
