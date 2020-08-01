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
package com.warxim.petep.core.pdu;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;

/** PDU (Protocol Data Unit) is core unit for data processed in PETEP. */
@PetepAPI
public abstract class PDU {
  /** Parent proxy. */
  protected Proxy proxy;

  /** Parent connection. */
  protected Connection connection;

  /** PDU destination. */
  protected PduDestination destination;

  /** Interceptor in which was the PDU processed last time. */
  protected Interceptor lastInterceptor;

  /** PDU tags. */
  protected Set<String> tags;

  /** PDU constructor */
  public PDU(Proxy proxy, Connection connection, PduDestination destination, Set<String> tags) {
    this.proxy = proxy;
    this.destination = destination;
    this.connection = connection;
    this.tags = tags;
  }

  /*
   * TAGS
   */
  /** Does the PDU contain specified tag? */
  public boolean hasTag(String tag) {
    return tags.contains(tag);
  }

  /** Adds tag to the PDU. */
  public void addTag(String tag) {
    tags.add(tag);
  }

  /** Removes tag from the PDU. */
  public void removeTag(String tag) {
    tags.remove(tag);
  }

  /** Removes tag from the PDU. */
  public void addTags(Collection<String> tags) {
    this.tags.addAll(tags);
  }

  /*
   * GETTERS
   */
  /** Returns set of tags. */
  public Set<String> getTags() {
    return tags;
  }

  /** Returns parent proxy. */
  public Proxy getProxy() {
    return proxy;
  }

  /** Returns PDU destination. */
  public PduDestination getDestination() {
    return destination;
  }

  /** Returns connection. */
  public Connection getConnection() {
    return connection;
  }

  /** Returns interceptor. */
  public Interceptor getLastInterceptor() {
    return lastInterceptor;
  }

  /*
   * SETTERS
   */
  /** Sets parent proxy. */
  public void setProxy(Proxy proxy) {
    this.proxy = proxy;
  }

  /** Sets PDU destination. */
  public void setDestination(PduDestination destination) {
    this.destination = destination;
  }

  /** Sets connection. */
  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  /** Sets interceptor. */
  public void setLastInterceptor(Interceptor interceptor) {
    this.lastInterceptor = interceptor;
  }

  /*
   * ABSTRACT METHODS
   */
  /** Returns buffer. */
  public abstract byte[] getBuffer();

  /** Returns size of data in the buffer. */
  public abstract int getSize();

  /** Returns the buffer and size of data in the buffer. */
  public abstract void setBuffer(byte[] buffer, int size);

  /** Resizes the buffer. */
  public abstract void resize(int size);

  /** Returns the charset of the PDU. */
  public abstract Charset getCharset();

  /** Sets the charset of the PDU. */
  public abstract void setCharset(Charset charset);

  /** Creates deep copy of the PDU. */
  public abstract PDU copy();
}
