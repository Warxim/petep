package com.warxim.petep.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WebUtilsTest {
    @DataProvider(name = "paramData")
    public Object[][] paramDataProvider() {
        return new Object[][]{
                {"test", "\"test\""},
                {"test\"1234", "\"test\\\"1234\""},
                {"test\n1234", "\"test\\n1234\""},
                {"test\r1234", "\"test\\r1234\""},
                {"test\t1234", "\"test\\t1234\""},
                {"test\\1234", "\"test\\\\1234\""},
        };
    }

    @Test(dataProvider = "paramData")
    public void toJavaScriptParamTest(String input, String expected) {
        assertThat(WebUtils.toJavaScriptParam(input)).isEqualTo(expected);
    }

    @DataProvider(name = "textData")
    public Object[][] textDataProvider() {
        return new Object[][]{
                {"test", "test"},
                {"<>\"&", "&#60;&#62;&#34;&#38;"},
                {"test<b>1234</b>", "test&#60;b&#62;1234&#60;/b&#62;"},
                {"test<<b>1234</b>>", "test&#60;&#60;b&#62;1234&#60;/b&#62;&#62;"},
        };
    }

    @Test(dataProvider = "textData")
    public void escapeHtmlTest(String input, String expected) {
        assertThat(WebUtils.escapeHtml(input)).isEqualTo(expected);
    }
}
