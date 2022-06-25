package com.warxim.petep.util;

import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.persistence.Storable;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionUtilsTest {
    @Test
    public void getStoreTypeTest() {
        var object = new StorableTest();
        var type = ExtensionUtils.getStoreType(object).get();
        assertThat(type).isEqualTo(TestStore.class);
    }

    @Test
    public void getConfigType() {
        var object = new ConfigurableTest();
        var type = ExtensionUtils.getConfigType(object).get();
        assertThat(type).isEqualTo(TestConfig.class);
    }

    @Test
    public void getConfiguratorType() {
        var object = new ConfiguratorTest();
        var type = ExtensionUtils.getConfiguratorType(object).get();
        assertThat(type).isEqualTo(TestConfig.class);
    }

    private static class TestStore {
        private String data;
    }

    private static class StorableTest implements Storable<TestStore> {
        @Override
        public TestStore saveStore() {
            return new TestStore();
        }

        @Override
        public void loadStore(TestStore store) {
        }
    }

    private static class TestConfig {
        private String data;
    }

    private static class ConfigurableTest implements Configurable<TestConfig> {
        @Override
        public TestConfig saveConfig() {
            return new TestConfig();
        }

        @Override
        public void loadConfig(TestConfig config) {

        }
    }

    private static class TestConfigPane extends ConfigPane<TestConfig> {
        public TestConfigPane(String template) throws IOException {
            super(template);
        }

        @Override
        public TestConfig getConfig() {
            return new TestConfig();
        }

        @Override
        public void setConfig(TestConfig config) {

        }

        @Override
        public boolean isValid() {
            return false;
        }
    }

    private static class ConfiguratorTest implements Configurator<TestConfig> {
        @Override
        public ConfigPane<TestConfig> createConfigPane() throws IOException {
            return new TestConfigPane("");
        }
    }
}
