package com.warxim.petep.extension.internal.scripter;

import com.warxim.petep.core.pdu.DefaultPdu;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.extension.internal.scripter.rule.ScriptGroupManager;
import com.warxim.petep.extension.internal.scripter.rule.StringScript;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class ScripterTest {
    @Test
    public void scriptTest() throws URISyntaxException, IOException {
        var manager = new ScriptGroupManager();
        var group = new RuleGroup<Script>("group_1", "Group 1");
        manager.add(group);

        var factory = new ScriptHelperFactory(null);
        var path = getClass().getClassLoader().getResource("scripter/script.js").toURI();
        var script = new StringScript(
                "Test",
                "Test script",
                true,
                "js",
                factory,
                Files.readString(Paths.get(path), StandardCharsets.UTF_8));
        group.addRule(script);

        var data = "Hello world!".getBytes();
        var pdu = new DefaultPdu(
                null,
                null,
                PduDestination.SERVER,
                data,
                data.length
        );
        script.getScriptInterceptorManager().intercept(pdu, null);

        assertThat(new String(pdu.getBuffer(), 0, pdu.getSize(), pdu.getCharset()))
                .isEqualTo("Hi PETEP!");

        manager.close();
    }
}
