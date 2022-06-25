package com.warxim.petep.extension.internal.history.service;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.model.HistoricConnection;
import com.warxim.petep.extension.internal.history.model.HistoricInterceptor;
import com.warxim.petep.extension.internal.history.model.HistoricProxy;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryServiceFilterTest extends HistoryTestBase {
    private static final String DATABASE_URL = "jdbc:sqlite:" + getTestFilePath("history_filter_test.db");
    private static final int PDU_COUNT = 16;

    private Collection<HistoricProxy> proxies;
    private Collection<HistoricInterceptor> interceptors;
    private Collection<HistoricConnection> connections;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        service.savePdu(createHistoricPduBuilder().proxy(HistoricProxy.builder().name("Proxy 1").code("proxy_1").build()).destination(PduDestination.CLIENT).build());
        service.savePdu(createHistoricPduBuilder().proxy(HistoricProxy.builder().name("Proxy 1").code("proxy_1").build()).destination(PduDestination.SERVER).build());
        service.savePdu(createHistoricPduBuilder().proxy(HistoricProxy.builder().name("Proxy 4").code("proxy_4").build()).destination(PduDestination.CLIENT).build());
        service.savePdu(createHistoricPduBuilder().proxy(HistoricProxy.builder().name("Proxy 4").code("proxy_4").build()).destination(PduDestination.SERVER).build());
        service.savePdu(createHistoricPduBuilder().proxy(HistoricProxy.builder().name("Proxy 4").code("proxy_4").build()).destination(PduDestination.SERVER).build());
        service.savePdu(createHistoricPduBuilder().size(20).data("01234567890123456789".getBytes()).build());
        service.savePdu(createHistoricPduBuilder().size(20).data("01234567890123456789".getBytes()).build());
        service.savePdu(createHistoricPduBuilder().size(1).data(new byte[] {11}).build());
        service.savePdu(createHistoricPduBuilder().size(2).build());
        service.savePdu(createHistoricPduBuilder().tags(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3")).data("12_".getBytes()).build());
        service.savePdu(createHistoricPduBuilder().tags(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3")).data(("1\u00002_").getBytes()).build());
        service.savePdu(createHistoricPduBuilder().tags(Set.of("tag_to_find_1", "tag_to_find_2")).data("test_012_test".getBytes()).build());
        service.savePdu(createHistoricPduBuilder().connection(HistoricConnection.builder().code("test_connection").name("TestConn").build()).data("_01".getBytes()).build());
        service.savePdu(createHistoricPduBuilder().connection(HistoricConnection.builder().code("test_connection").name("TestConn").build()).data("_01xx".getBytes()).build());
        service.savePdu(createHistoricPduBuilder().interceptor(HistoricInterceptor.builder().code("test_interceptor").name("TestInter").build()).build());
        service.savePdu(createHistoricPduBuilder()
                .proxy(HistoricProxy.builder().name("Proxy 2").code("proxy_2").build())
                .interceptor(HistoricInterceptor.builder().code("complex_test_interceptor").name("TestInter").build())
                .connection(HistoricConnection.builder().code("complex_test_connection").name("TestConn").build())
                .size(20).data("01234567890123456789".getBytes())
                .tags(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3", "tag_to_find_4"))
                .build());

        proxies = service.getProxies().get();
        interceptors = service.getInterceptors().get();
        connections = service.getConnections().get();

        service.stop();
    }

    @Test
    public void filterDestinationTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDestinationTest(service);

        service.stop();
    }

    @Test
    public void filterProxyTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterProxyTest(service);

        service.stop();
    }

    @Test
    public void filterInterceptorTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterInterceptorTest(service);

        service.stop();
    }

    @Test
    public void filterConnectionTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterConnectionTest(service);

        service.stop();
    }

    @Test
    public void filterFromTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterFromTest(service);

        service.stop();
    }

    @Test
    public void filterToTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterToTest(service);

        service.stop();
    }

    @Test
    public void filterTagsTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterTagsTest(service);

        service.stop();
    }

    @Test
    public void filterDataContainsTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDataContainsTest(service);

        service.stop();
    }
    @Test
    public void filterDataNotContainsTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDataNotContainsTest(service);

        service.stop();
    }

    @Test
    public void filterDataStartsWithTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDataStartsWithTest(service);

        service.stop();
    }

    @Test
    public void filterDataNotStartsWithTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDataNotStartsWithTest(service);

        service.stop();
    }

    @Test
    public void filterDataEndsWithTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDataEndsWithTest(service);

        service.stop();
    }

    @Test
    public void filterDataNotEndsWithTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterDataNotEndsWithTest(service);

        service.stop();
    }

    @Test
    public void filterComplexTest() throws SQLException, ExecutionException, InterruptedException {
        var service = createService(DATABASE_URL);

        processFilterComplexTest(service);

        service.stop();
    }

    @Test
    public void filterDestinationTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDestinationTest(service);

        service.stop();
    }

    @Test
    public void filterProxyTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterProxyTest(service);

        service.stop();
    }

    @Test
    public void filterInterceptorTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterInterceptorTest(service);

        service.stop();
    }

    @Test
    public void filterConnectionTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterConnectionTest(service);

        service.stop();
    }

    @Test
    public void filterFromTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterFromTest(service);

        service.stop();
    }

    @Test
    public void filterToTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterToTest(service);

        service.stop();
    }

    @Test
    public void filterTagsTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterTagsTest(service);

        service.stop();
    }

    @Test
    public void filterDataContainsTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDataContainsTest(service);

        service.stop();
    }

    @Test
    public void filterDataNotContainsTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDataNotContainsTest(service);

        service.stop();
    }

    @Test
    public void filterDataStartsWithTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDataStartsWithTest(service);

        service.stop();
    }

    @Test
    public void filterDataNotStartsWithTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDataNotStartsWithTest(service);

        service.stop();
    }

    @Test
    public void filterDataEndsWithTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDataEndsWithTest(service);

        service.stop();
    }

    @Test
    public void filterDataNotEndsWithTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterDataNotEndsWithTest(service);

        service.stop();
    }

    @Test
    public void filterComplexTestWithCache() throws SQLException, ExecutionException, InterruptedException {
        var service = createServiceWithCache(DATABASE_URL);

        processFilterComplexTest(service);

        service.stop();
    }

    private void processFilterDestinationTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .destination(PduDestination.CLIENT)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(2);
        assertThat(pdus).allMatch(pdu -> pdu.getDestination().equals(PduDestination.CLIENT));

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(2);
        assertThat(pduViews).allMatch(pdu -> pdu.getDestination().equals(PduDestination.CLIENT));
    }

    private void processFilterProxyTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var proxyId = proxies.stream().filter(proxy -> proxy.getCode().equals("proxy_4")).findAny().get().getId();
        var filter = HistoryFilter.builder()
                .proxyId(proxyId)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(3);
        assertThat(pdus).allMatch(pdu -> pdu.getProxy().getId().equals(proxyId));

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(3);
        assertThat(pduViews).allMatch(pdu -> pdu.getProxyId() == proxyId);
    }

    private void processFilterInterceptorTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var interceptorId = interceptors.stream().filter(interceptor -> interceptor.getCode().equals("test_interceptor")).findAny().get().getId();
        var filter = HistoryFilter.builder()
                .interceptorId(interceptorId)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(1);
        assertThat(pdus).allMatch(pdu -> pdu.getInterceptor().getId().equals(interceptorId));

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(1);
        assertThat(pduViews).allMatch(pdu -> pdu.getInterceptorId() == interceptorId);
    }

    private void processFilterConnectionTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var connectionId = connections.stream().filter(connection -> connection.getCode().equals("test_connection")).findAny().get().getId();
        var filter = HistoryFilter.builder()
                .connectionId(connectionId)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(2);
        assertThat(pdus).allMatch(pdu -> pdu.getConnection().getId().equals(connectionId));

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(2);
        assertThat(pduViews).allMatch(pdu -> pdu.getConnectionId() == connectionId);
    }

    private void processFilterFromTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .fromSize(10)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(3);
        assertThat(pdus).allMatch(pdu -> pdu.getSize() > 10);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(3);
        assertThat(pduViews).allMatch(pdu -> pdu.getSize() > 10);
    }

    private void processFilterToTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .toSize(2)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(2);
        assertThat(pdus).allMatch(pdu -> pdu.getSize() <= 2);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(2);
        assertThat(pduViews).allMatch(pdu -> pdu.getSize() <= 2);
    }

    private void processFilterTagsTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .tags(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3"))
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(3);
        assertThat(pdus).allMatch(pdu -> pdu.getTags().containsAll(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3")));

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(3);
        assertThat(pduViews).allMatch(pdu -> pdu.getTags().containsAll(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3")));
    }

    private void processFilterDataContainsTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .data("7890123".getBytes())
                .dataFilterType(HistoryFilter.DataFilterType.CONTAINS)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(3);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(3);
    }

    private void processFilterDataNotContainsTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .data("7890123".getBytes())
                .dataFilterNegative(true)
                .dataFilterType(HistoryFilter.DataFilterType.CONTAINS)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(PDU_COUNT - 3);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(PDU_COUNT - 3);
    }

    private void processFilterDataStartsWithTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .data("_0".getBytes())
                .dataFilterType(HistoryFilter.DataFilterType.STARTS_WITH)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(2);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(2);
    }

    private void processFilterDataNotStartsWithTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .data("_0".getBytes())
                .dataFilterNegative(true)
                .dataFilterType(HistoryFilter.DataFilterType.STARTS_WITH)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(PDU_COUNT - 2);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(PDU_COUNT - 2);
    }

    private void processFilterDataEndsWithTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .data("2_".getBytes())
                .dataFilterType(HistoryFilter.DataFilterType.ENDS_WITH)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(2);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(2);
    }

    private void processFilterDataNotEndsWithTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .data("2_".getBytes())
                .dataFilterNegative(true)
                .dataFilterType(HistoryFilter.DataFilterType.ENDS_WITH)
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(PDU_COUNT - 2);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(PDU_COUNT - 2);
    }

    private void processFilterComplexTest(DefaultHistoryService service) throws ExecutionException, InterruptedException {
        var filter = HistoryFilter.builder()
                .destination(PduDestination.SERVER)
                .fromSize(2)
                .toSize(20)
                .proxyId(proxies.stream().filter(proxy -> proxy.getCode().equals("proxy_2")).findAny().get().getId())
                .interceptorId(interceptors.stream().filter(interceptor -> interceptor.getCode().equals("complex_test_interceptor")).findAny().get().getId())
                .connectionId(connections.stream().filter(connection -> connection.getCode().equals("complex_test_connection")).findAny().get().getId())
                .tags(Set.of("tag_to_find_1", "tag_to_find_2", "tag_to_find_3", "tag_to_find_4"))
                .build();

        var pdus = service.getPdusByFilter(filter).get();
        assertThat(pdus).hasSize(1);

        var pduViews = service.getPduViewsByFilter(filter).get();
        assertThat(pduViews).hasSize(1);
    }

}
