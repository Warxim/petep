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

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.model.*;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;

/**
 * History repository
 */
public interface HistoryRepository extends AutoCloseable {
    /**
     * Gets PDU by identifier
     * @param id PDU identifier
     * @return Historic PDU
     */
    Optional<HistoricPdu> getPdu(long id);

    /**
     * Deletes PDUs by identifiers
     * @param ids PDU identifiers
     * @return List of identifiers of PDUs, which have been removed.
     */
    List<Long> deletePdus(Collection<Long> ids);

    /**
     * Deletes all PDUs in repository
     */
    void deleteAllPdus();

    /**
     * Gets all PDUs from repository
     * @return List of PDUs
     */
    List<HistoricPdu> getPdus();

    /**
     * Gets all PDU views from repository
     * @return List of PDU views
     */
    List<HistoricPduView> getPduViews();

    /**
     * Gets PDUs from repository by filter
     * @param filter Filter for finding specific PDUs
     * @return List of PDUs matching the filter
     */
    List<HistoricPdu> getPdusByFilter(HistoryFilter filter);

    /**
     * Gets PDU views from repository by filter
     * @param filter Filter for finding specific PDU views
     * @return List of PDU views matching the filter
     */
    List<HistoricPduView> getPduViewsByFilter(HistoryFilter filter);

    /**
     * Gets PDU identifiers from repository by filter
     * @param filter Filter for finding specific PDU identifiers
     * @return List of PDU identifiers matching the filter
     */
    Set<Long> getPduIdsByFilter(HistoryFilter filter);

    /**
     * Gets all proxies from repository
     * @return List of proxies
     */
    List<HistoricProxy> getProxies();

    /**
     * Gets all interceptors from repository
     * @return List of interceptors
     */
    List<HistoricInterceptor> getInterceptors();

    /**
     * Gets all connections from repository
     * @return List of connections
     */
    List<HistoricConnection> getConnections();

    /**
     * Gets all tags from repository
     * @return List of tags
     */
    Set<String> getTags();

    /**
     * Gets all metadata for given PDU identifier
     * @param id PDU identifier
     * @return Map of metadata (key, value) for given PDU identifier
     */
    Map<String, String> getPduMetadata(long id);

    /**
     * Gets all tags for given PDU identifier
     * @param id PDU identifier
     * @return Set of tags for given PDU identifier
     */
    Set<String> getPduTags(long id);

    /**
     * Gets proxy identifier by given code and name (or creates it if it does not exist)
     * @param code Code to find
     * @param name Name to find
     * @return Proxy identifier
     */
    Optional<Long> getProxyIdByCodeAndName(String code, String name);

    /**
     * Gets connection identifier by given code and name (or creates it if it does not exist)
     * @param code Code to find
     * @param name Name to find
     * @return Connection identifier
     */
    Optional<Long> getConnectionIdByCodeAndName(String code, String name);

    /**
     * Gets interceptor identifier by given code and name (or creates it if it does not exist)
     * @param code Code to find
     * @param name Name to find
     * @return Interceptor identifier
     */
    Optional<Long> getInterceptorIdByCodeAndName(String code, String name);

    /**
     * Gets charset identifier by given charset (or creates it if it does not exist)
     * @param charset Charset to find
     * @return Charset identifier
     */
    Optional<Long> getCharsetId(Charset charset);

    /**
     * Gets tag identifier by given code (or creates it if it does not exist)
     * @param code Code to find
     * @return Tag identifier
     */
    Optional<Long> getTagIdByCode(String code);

    /**
     * Gets metadata identifier by given code (or creates it if it does not exist)
     * @param code Code to find
     * @return Metadata identifier
     */
    Optional<Long> getMetadataIdByCode(String code);

    /**
     * Gets identifiers of all tags by given codes (or creates it if it does not exist)
     * @param codes Collection of codes to find
     * @return Collection of found tag identifiers
     */
    Collection<Long> getTagIdsByCodes(Collection<String> codes);

    /**
     * Creates PDU from given parameters and stores it into repository.
     * @param proxyId Proxy identifier
     * @param connectionId Connection identifier
     * @param interceptorId Interceptor identifier
     * @param charsetId Charset identifier
     * @param destination Destination of the PDU
     * @param size Size of the data
     * @param time Time of the PDU creation
     * @param data Data byte array
     * @return Identifier of created PDU
     */
    Optional<Long> createPdu(
            Long proxyId,
            Long connectionId,
            Long interceptorId,
            Long charsetId,
            PduDestination destination,
            int size,
            Instant time,
            byte[] data);

    /**
     * Adds tags with provided identifiers to PDU
     * @param pduId Identifier of PDU
     * @param tagIds Collection of tag identifiers, which will be added to the PDU
     * @return {@code true} if the tags have been added successfully
     */
    boolean addPduTags(Long pduId, Collection<Long> tagIds);

    /**
     * Adds metadata with provided identifiers to PDU
     * @param pduId Identifier of PDU
     * @param metadata Map of metadata, which will be added to the PDU
     * @return {@code true} if the metadata have been added successfully
     */
    boolean addPduMetadata(Long pduId, Map<String, String> metadata);

    /**
     * Deletes all unused records (cleans the history)
     */
    void deleteUnusedRecords();
}
