package com.warxim.petep.test.base.extension.proxy;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.serizalization.ProxySerializer;
import com.warxim.petep.proxy.worker.Proxy;
import lombok.extern.java.Log;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Test proxy module factory
 */
@Log
public class TestProxyModuleFactory extends ProxyModuleFactory implements ProxySerializer, ProxyDeserializer {
    /**
     * Constructs test proxy module factory.
     * @param extension Extension that owns this factory
     */
    public TestProxyModuleFactory(Extension extension) {
        super(extension);
        log.info("TestProxyModuleFactory created");
    }

    @Override
    public String getName() {
        return "Test Proxy";
    }

    @Override
    public String getCode() {
        return "test-proxy";
    }

    @Override
    public ProxyModule createModule(String code, String name, String description, boolean enabled) {
        return new TestProxyModule(this, code, name, description, enabled);
    }

    @Override
    public Optional<PduMetadataPane> createPduMetadataPane() {
        return Optional.empty();
    }

    @Override
    public ProxySerializer getSerializer() {
        return this;
    }

    @Override
    public ProxyDeserializer getDeserializer() {
        return this;
    }

    @Override
    public Optional<PDU> deserializePdu(Proxy proxy,
                              Connection connection,
                              PduDestination destination,
                              byte[] buffer,
                              int size,
                              Charset charset,
                              Set<String> tags,
                              Map<String, String> serializedMetadata) {
        var pdu = new TestPdu(proxy, connection, destination, buffer, size, tags);
        var testString = serializedMetadata.get("Test-String");
        if (testString != null) {
            pdu.setTestString(testString);
        }
        var testNumber = serializedMetadata.get("Test-Number");
        if (testNumber != null) {
            pdu.setTestNumber(Double.valueOf(testNumber));
        }
        pdu.setCharset(charset);
        return Optional.of(pdu);
    }

    @Override
    public Map<String, String> serializePduMetadata(PDU pdu) {
        var metadata = new HashMap<String, String>();
        var testPdu = (TestPdu) pdu;
        if (testPdu.getTestString() != null) {
            metadata.put("Test-String", testPdu.getTestString());
        }
        if (testPdu.getTestNumber() != null) {
            metadata.put("Test-Number", String.valueOf(testPdu.getTestNumber()));
        }
        return metadata;
    }
}
