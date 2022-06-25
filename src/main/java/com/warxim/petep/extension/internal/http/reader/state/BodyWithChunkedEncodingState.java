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
package com.warxim.petep.extension.internal.http.reader.state;

import lombok.Getter;
import lombok.Setter;

/**
 * State for body with chunked encoding.
 * <p>Determines, whether we are reading LENGTH or CHUNK.</p>
 */
@Getter
@Setter
public final class BodyWithChunkedEncodingState implements InternalBodyState {
    private int chunkLength;
    private ChunkedBodyStep chunkStep;

    /**
     * Constructs body state for chunked encoding.
     */
    public BodyWithChunkedEncodingState() {
        this.chunkLength = -1;
        this.chunkStep = ChunkedBodyStep.LENGTH;
    }

    /**
     * Step in processing of chunked body.
     */
    public enum ChunkedBodyStep {
        /**
         * Processing length of chunk in chunked body.
         */
        LENGTH,
        /**
         * Processing chunk in chunked body.
         */
        CHUNK
    }
}
