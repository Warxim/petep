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
package com.warxim.petep.extension.internal.http.modifier.removeheader;

import com.warxim.petep.extension.internal.modifier.factory.Modifier;
import com.warxim.petep.extension.internal.modifier.factory.ModifierConfigurator;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Factory for creating Remove Header modifiers.
 */
public final class RemoveHeaderModifierFactory extends ModifierFactory {
    @Override
    public String getCode() {
        return "http_remove_header";
    }

    @Override
    public String getName() {
        return "Remove HTTP header";
    }

    @Override
    public Modifier createModifier(ModifierData data) {
        return new RemoveHeaderModifier(this, data);
    }

    @Override
    public Optional<Type> getConfigType() {
        return Optional.of(RemoveHeaderData.class);
    }

    @Override
    public Optional<ModifierConfigurator> createConfigPane() throws IOException {
        return Optional.of(new RemoveHeaderModifierConfigurator());
    }
}
