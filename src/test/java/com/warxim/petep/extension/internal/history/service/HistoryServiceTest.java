package com.warxim.petep.extension.internal.history.service;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.model.HistoricConnection;
import com.warxim.petep.extension.internal.history.model.HistoricProxy;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryServiceTest extends HistoryTestBase {
    private static final String DATABASE_URL = "jdbc:sqlite:" + getTestFilePath("history_test.db");

    @Test
    public void basicSaveAndGetTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);
        processSaveAndGetTest(service);
        service.stop();
    }

    @Test
    public void cachedSaveAndGetTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);
        processSaveAndGetTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = {"basicSaveAndGetTest", "cachedSaveAndGetTest"})
    public void basicGetPdusTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);
        processGetPdusTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = {"basicSaveAndGetTest", "cachedSaveAndGetTest"})
    public void cachedGetPdusTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);
        processGetPdusTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = "cachedGetPdusTest")
    public void basicDeletePduTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);
        processDeletePduTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = "cachedGetPdusTest")
    public void cachedDeletePduTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);
        processDeletePduTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = "cachedDeletePduTest")
    public void basicDeletePdusTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);
        processDeletePdusTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = "cachedDeletePduTest")
    public void cachedDeletePdusTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);
        processDeletePdusTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = "cachedDeletePdusTest")
    public void basicClearHistoryTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);
        processClearHistoryTest(service);
        service.stop();
    }

    @Test(dependsOnMethods = "cachedDeletePdusTest")
    public void cachedClearHistoryTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);
        processClearHistoryTest(service);
        service.stop();
    }

    protected void processClearHistoryTest(HistoryService service) throws ExecutionException, InterruptedException {
        var pdu = createHistoricPduBuilder()
                .tags(Set.of("tag_to_delete"))
                .metadata(Map.of("metadata_to_delete", "test value"))
                .build();
        pdu.getProxy().setName("proxy_to_delete");
        pdu.getInterceptor().setName("interceptor_to_delete");
        pdu.getConnection().setName("connection_to_delete");

        var id = service.savePdu(pdu).get().get();

        service.clearHistory().get();

        var pduOptional = service.getPdu(id).get();
        assertThat(pduOptional).isEmpty();

        var pdus = service.getPdus().get();
        assertThat(pdus).isEmpty();

        var pduViews = service.getPduViews().get();
        assertThat(pduViews).isEmpty();

        var tags = service.getTags().get();
        assertThat(tags).isEmpty();

        var proxies = service.getProxies().get();
        assertThat(proxies).isEmpty();

        var interceptors = service.getInterceptors().get();
        assertThat(interceptors).isEmpty();

        var connections = service.getConnections().get();
        assertThat(connections).isEmpty();
    }

    protected void processDeletePduTest(HistoryService service) throws ExecutionException, InterruptedException {
        var pdu = createHistoricPduBuilder()
                .tags(Set.of("tag_to_delete"))
                .metadata(Map.of("metadata_to_delete", "test value"))
                .build();
        pdu.getProxy().setName("proxy_to_delete");
        pdu.getInterceptor().setName("interceptor_to_delete");
        pdu.getConnection().setName("connection_to_delete");

        var id = service.savePdu(pdu).get().get();

        var deleted = service.deletePdu(id).get();
        assertThat(deleted).isTrue();

        var pduOptional = service.getPdu(id).get();
        assertThat(pduOptional).isEmpty();

        var pdus = service.getPdus().get();
        assertThat(pdus).noneMatch(p -> id.equals(p.getId()));

        var pduViews = service.getPduViews().get();
        assertThat(pduViews).noneMatch(p -> id.equals(p.getId()));

        var tags = service.getTags().get();
        assertThat(tags).isNotEmpty();
        assertThat(tags).doesNotContain("tag_to_delete");

        var proxies = service.getProxies().get();
        assertThat(proxies).noneMatch(proxy -> proxy.getName().equals("proxy_to_delete"));

        var interceptors = service.getInterceptors().get();
        assertThat(interceptors).noneMatch(interceptor -> interceptor.getName().equals("interceptor_to_delete"));

        var connections = service.getConnections().get();
        assertThat(connections).noneMatch(connection -> connection.getName().equals("connection_to_delete"));
    }

    protected void processDeletePdusTest(HistoryService service) throws ExecutionException, InterruptedException {
        var pdu = createHistoricPduBuilder()
                .tags(Set.of("tag_to_delete"))
                .metadata(Map.of("metadata_to_delete", "test value"))
                .build();
        pdu.getProxy().setName("proxy_to_delete");
        pdu.getInterceptor().setName("interceptor_to_delete");
        pdu.getConnection().setName("connection_to_delete");

        var id1 = service.savePdu(pdu).get().get();
        var id2 = service.savePdu(pdu).get().get();

        assertThat(service.deletePdus(List.of(id1, id2)).get())
                .containsExactly(id1, id2);

        assertThat(service.getPdu(id1).get()).isEmpty();
        assertThat(service.getPdu(id2).get()).isEmpty();

        var pdus = service.getPdus().get();
        assertThat(pdus).noneMatch(p -> id1.equals(p.getId()));
        assertThat(pdus).noneMatch(p -> id2.equals(p.getId()));

        var pduViews = service.getPduViews().get();
        assertThat(pduViews).noneMatch(p -> id1.equals(p.getId()));
        assertThat(pduViews).noneMatch(p -> id2.equals(p.getId()));

        var tags = service.getTags().get();
        assertThat(tags).isNotEmpty();
        assertThat(tags).doesNotContain("tag_to_delete");

        var proxies = service.getProxies().get();
        assertThat(proxies).noneMatch(proxy -> proxy.getName().equals("proxy_to_delete"));

        var interceptors = service.getInterceptors().get();
        assertThat(interceptors).noneMatch(interceptor -> interceptor.getName().equals("interceptor_to_delete"));

        var connections = service.getConnections().get();
        assertThat(connections).noneMatch(connection -> connection.getName().equals("connection_to_delete"));
    }

    protected void processGetPdusTest(HistoryService service) throws ExecutionException, InterruptedException {
        var pdus = service.getPdus().get();
        assertThat(pdus).hasSize(20);

        var pduViews = service.getPduViews().get();
        assertThat(pduViews).hasSize(20);
    }

    protected void processSaveAndGetTest(HistoryService service) throws ExecutionException, InterruptedException {
        var buffer = ("This is a simple content with UTF-8 characters: Čááu!").getBytes();
        var random = new Random();
        for (int i = 0; i < 10; ++i) {
            var connectionId = random.nextInt();
            var proxyId = random.nextInt(10);
            var destination = (random.nextInt() % 2 == 0) ? PduDestination.CLIENT : PduDestination.SERVER;
            var pdu = createHistoricPduBuilder()
                    .proxy(HistoricProxy.builder().code("proxy_" + proxyId).name("Proxy " + proxyId).build())
                    .connection(HistoricConnection.builder().code("c_" + connectionId).name("Connection " + connectionId).build())
                    .destination(destination)
                    .size(buffer.length)
                    .data(buffer)
                    .build();

            var id = service.savePdu(pdu).get().get();
            var obtainedPdu = service.getPdu(id).get().get();

            assertThat(obtainedPdu)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(pdu);
            assertThat(obtainedPdu.getId()).isNotNull();
        }
    }

}
