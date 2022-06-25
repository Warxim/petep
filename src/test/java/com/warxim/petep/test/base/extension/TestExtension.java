package com.warxim.petep.test.base.extension;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import lombok.extern.java.Log;

/**
 * Test extension
 */
@Log
public class TestExtension extends Extension {
    /**
     * Constructs test extension.
     * @param path Path to the extension
     */
    public TestExtension(String path) {
        super(path);
        log.info("Created TestExtension (path: " + path + ")");
    }

    @Override
    public void init(ExtensionHelper helper) {
        log.info("Initialized TestExtension");
    }

    @Override
    public void initGui(GuiHelper helper) {
        log.info("Initialized TestExtension GUI");
    }

    @Override
    public String getCode() {
        return "test";
    }

    @Override
    public String getName() {
        return "Test Extension";
    }

    @Override
    public String getDescription() {
        return "Simple test extension.";
    }

    @Override
    public String getVersion() {
        return "beta";
    }
}
