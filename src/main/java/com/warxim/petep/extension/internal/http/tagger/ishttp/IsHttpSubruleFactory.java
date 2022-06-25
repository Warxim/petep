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
package com.warxim.petep.extension.internal.http.tagger.ishttp;

import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Subrule factory for creating "IsHttp" tag subrule.
 */
public final class IsHttpSubruleFactory extends TagSubruleFactory {
    @Override
    public String getCode() {
        return "is_http";
    }

    @Override
    public String getName() {
        return "Is HTTP?";
    }

    @Override
    public TagSubrule createSubrule(TagSubruleData data) {
        return new IsHttpSubrule(this, data);
    }

    @Override
    public Optional<TagSubruleConfigurator> createConfigPane() {
        return Optional.empty();
    }

    @Override
    public Optional<Type> getConfigType() {
        return Optional.empty();
    }
}
