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
package com.warxim.petep.util;

import com.warxim.petep.util.GsonUtils;
import lombok.Builder;
import lombok.Value;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonUtilsTest {
    @Test
    public void gsonTest() {
        var gson = GsonUtils.getGson();
        var data = TestData.builder()
                .name("Test name")
                .number(1234)
                .charset(StandardCharsets.ISO_8859_1)
                .build();
        var serialized = gson.toJsonTree(data);
        var deserialized = gson.fromJson(serialized, TestData.class);
        assertThat(deserialized.getName()).isEqualTo("Test name");
        assertThat(deserialized.getNumber()).isEqualTo(1234);
        assertThat(deserialized.getCharset()).isEqualTo(StandardCharsets.ISO_8859_1);
    }

    @Value
    @Builder
    private static class TestData {
        String name;
        Integer number;
        Charset charset;
    }
}
