package com.warxim.petep.util;

import com.warxim.petep.util.GuiUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class GuiUtilsTest {
    private static String generateMessage() {
        char[] buffer = new char[256];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (char) i;
        }
        return String.valueOf(buffer);
    }

    @DataProvider(name = "data")
    public Object[][] dataProvider() {
        return new Object[][]{
                {format("Test %c message.", (char) 0), "Test \u2400 message."},
                {format("Test %c message.", (char) 8), "Test \u2408 message."},
                {format("Test %c message.", (char) 11), "Test \u240B message."},
                {format("Test %c message.", (char) 31), "Test \u241F message."},
        };
    }

    @Test(dataProvider = "data")
    public void formatTextTest(String input, String expected) {
        assertThat(GuiUtils.formatText(input)).isEqualTo(expected);
    }

    @Test(dataProvider = "data")
    public void unformatTextTest(String expected, String formatted) {
        assertThat(GuiUtils.unformatText(formatted)).isEqualTo(expected);
    }

    @Test
    public void formatAndUnformatTest() {
        var original = generateMessage();
        var formatted = GuiUtils.formatText(original);
        assertThat(GuiUtils.unformatText(formatted)).isEqualTo(original);
    }
}
