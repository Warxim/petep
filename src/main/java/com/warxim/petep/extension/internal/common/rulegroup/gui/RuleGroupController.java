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
package com.warxim.petep.extension.internal.common.rulegroup.gui;

import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;

/**
 * Rule group controller.
 * @param <R> Rule type for this controller
 */
public abstract class RuleGroupController<R extends Rule> {
    /**
     * Rule group that is being controller by this controller.
     */
    protected final RuleGroup<R> group;

    /**
     * Rule group controller constructor.
     * @param group Group of rules to show in the controller
     */
    protected RuleGroupController(RuleGroup<R> group) {
        this.group = group;
    }

    /**
     * Get rule group that is controller by this controller.
     * @return Rule group
     */
    public final RuleGroup<R> getRuleGroup() {
        return group;
    }
}
