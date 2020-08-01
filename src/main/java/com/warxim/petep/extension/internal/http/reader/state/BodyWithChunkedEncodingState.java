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

public final class BodyWithChunkedEncodingState extends InternalState {
  public enum ChunkedBodyStep {
    LENGTH, CHUNK
  }

  private int chunkLength;
  private ChunkedBodyStep step;

  public BodyWithChunkedEncodingState() {
    this.chunkLength = -1;
    this.step = ChunkedBodyStep.LENGTH;
  }

  public int getChunkLength() {
    return chunkLength;
  }

  public int decreaseChunkLength() {
    return --chunkLength;
  }

  public void setChunkLength(int chunkLength) {
    this.chunkLength = chunkLength;
  }

  public ChunkedBodyStep getChunkStep() {
    return step;
  }

  public void setChunkStep(ChunkedBodyStep step) {
    this.step = step;
  }
}
