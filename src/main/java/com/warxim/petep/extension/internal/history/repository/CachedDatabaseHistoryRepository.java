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
package com.warxim.petep.extension.internal.history.repository;

import com.warxim.petep.extension.internal.history.util.Pair;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Cached database history repository
 * <p>Caches most used queries in memory, so that the use of History is much faster.</p>
 */
public class CachedDatabaseHistoryRepository extends DatabaseHistoryRepository {
    private EntityCache<String, Long> tagByCodeCache;
    private EntityCache<String, Long> metadataByCodeCache;
    private EntityCache<String, Long> charsetByCodeCache;
    private EntityCache<Pair<String, String>, Long> proxyByCodeAndNameCache;
    private EntityCache<Pair<String, String>, Long> interceptorByCodeAndNameCache;

    /**
     * Constructs database history repository with database at given URL.
     * @param url Path to database file
     * @throws SQLException If anything fails during history database processing
     */
    public CachedDatabaseHistoryRepository(String url) throws SQLException {
        super(url);
        tagByCodeCache = new EntityCache<>();
        metadataByCodeCache = new EntityCache<>();
        charsetByCodeCache = new EntityCache<>();
        proxyByCodeAndNameCache = new EntityCache<>();
        interceptorByCodeAndNameCache = new EntityCache<>();
    }

    @Override
    public Optional<Long> getTagIdByCode(String code) {
        return Optional.ofNullable(
                tagByCodeCache.computeIfAbsent(
                        code,
                        this::getTagIdByCodeValue));
    }

    @Override
    public Optional<Long> getMetadataIdByCode(String code) {
        return Optional.ofNullable(
                metadataByCodeCache.computeIfAbsent(
                        code,
                        this::getMetadataIdByCodeValue));
    }

    @Override
    public Optional<Long> getCharsetId(Charset charset) {
        return Optional.ofNullable(
                charsetByCodeCache.computeIfAbsent(charset.name(),
                        code -> super.getCharsetId(charset).orElse(null)));
    }

    @Override
    public Optional<Long> getProxyIdByCodeAndName(String code, String name) {
        return Optional.ofNullable(
                proxyByCodeAndNameCache.computeIfAbsent(
                        new Pair<>(code, name),
                        this::getProxyIdByCodeAndNameValue));
    }

    @Override
    public Optional<Long> getInterceptorIdByCodeAndName(String code, String name) {
        return Optional.ofNullable(
                interceptorByCodeAndNameCache.computeIfAbsent(
                        new Pair<>(code, name),
                        this::getInterceptorIdByCodeAndNameValue));
    }

    @Override
    public void deleteUnusedRecords() {
        super.deleteUnusedRecords();
        tagByCodeCache.clear();
        metadataByCodeCache.clear();
        proxyByCodeAndNameCache.clear();
        interceptorByCodeAndNameCache.clear();
    }

    /**
     * Returns tag ID by code or null if it does not exist.
     */
    private Long getTagIdByCodeValue(String code) {
        return super.getTagIdByCode(code).orElse(null);
    }

    /**
     * Returns metadata ID by code or null if it does not exist.
     */
    private Long getMetadataIdByCodeValue(String code) {
        return super.getMetadataIdByCode(code).orElse(null);
    }

    /**
     * Returns proxy ID by code or null if it does not exist.
     */
    private Long getProxyIdByCodeAndNameValue(Pair<String, String> codeNamePair) {
        return super.getProxyIdByCodeAndName(codeNamePair.getLeft(), codeNamePair.getRight()).orElse(null);
    }

    /**
     * Returns interceptor ID by code or null if it does not exist.
     */
    private Long getInterceptorIdByCodeAndNameValue(Pair<String, String> codeNamePair) {
        return super.getInterceptorIdByCodeAndName(codeNamePair.getLeft(), codeNamePair.getRight()).orElse(null);
    }
}
