package com.warxim.petep.extension.internal.history.service;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.listener.HistoryListener;
import com.warxim.petep.extension.internal.history.model.HistoricConnection;
import com.warxim.petep.extension.internal.history.model.HistoricInterceptor;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricProxy;
import com.warxim.petep.extension.internal.history.repository.CachedDatabaseHistoryRepository;
import com.warxim.petep.extension.internal.history.repository.DatabaseHistoryRepository;
import lombok.extern.java.Log;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Log
public class HistoryTestBase {
    protected static final String TEST_DIRECTORY = "./test_temp_directory";
    protected static final Path TEST_DIRECTORY_PATH = Path.of(TEST_DIRECTORY);

    private List<DefaultHistoryService> services = new ArrayList<>();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws IOException {
        log.info("Creating test directory: " + TEST_DIRECTORY);
        Files.createDirectories(TEST_DIRECTORY_PATH);
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() throws IOException {
        for (var service : services) {
            service.stop();
        }

        log.info("Deleting test directory: " + TEST_DIRECTORY);
        Files.walk(TEST_DIRECTORY_PATH)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    protected static String getTestFilePath(String path) {
        return TEST_DIRECTORY_PATH.resolve(path).toAbsolutePath().toString();
    }

    protected DefaultHistoryService createService(String databaseUrl) throws SQLException {
        var repo = new DatabaseHistoryRepository(databaseUrl);
        var service = new DefaultHistoryService(repo, new HistoryListener() {});
        services.add(service);
        return service;
    }

    protected DefaultHistoryService createServiceWithCache(String databaseUrl) throws SQLException, ExecutionException, InterruptedException {
        var repo = new CachedDatabaseHistoryRepository(databaseUrl);
        var service = new CachedHistoryService(repo, new HistoryListener() {});
        services.add(service);
        return service;
    }

    protected HistoricPdu.HistoricPduBuilder createHistoricPduBuilder() {
        return HistoricPdu.builder()
                .proxy(HistoricProxy.builder().code("proxy").name("Proxy").build())
                .connection(HistoricConnection.builder().code("c").name("Connection").build())
                .destination(PduDestination.SERVER)
                .interceptor(HistoricInterceptor.builder().code("history").name("History").build())
                .tags(Set.of("tag_1", "tag_2"))
                .size("TEST".length())
                .time(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .charset(StandardCharsets.UTF_8)
                .data("TEST".getBytes())
                .metadata(Map.of("key_1", "value_1", "key_2", "value_2"));
    }
}
