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
package com.warxim.petep.extension.internal.tcp;

/** SSL certificate configuration. */
public final class SslCertificateConfig {
  private final String keyStore;
  private final String keyStoreType;
  private final String keyStorePassword;
  private final String keyPassword;

  /** SSL certificate configuration constructor. */
  public SslCertificateConfig(
      String keyStore,
      String keyStoreType,
      String keyStorePassword,
      String keyPassword) {
    this.keyStore = keyStore;
    this.keyStoreType = keyStoreType;
    this.keyStorePassword = keyStorePassword;
    this.keyPassword = keyPassword;
  }

  public String getKeyStore() {
    return keyStore;
  }

  public String getKeyStoreType() {
    return keyStoreType;
  }

  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  public String getKeyPassword() {
    return keyPassword;
  }
}
