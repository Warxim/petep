package com.warxim.petep.util;

import com.warxim.petep.test.base.extension.proxy.TestPdu;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PduUtilsTest {
    private static byte[] getBytes(String string) {
        return string.getBytes(StandardCharsets.ISO_8859_1);
    }

    @DataProvider(name = "tags")
    public Object[][] tagsProvider() {
        return new Object[][]{
                {"", Set.of()},
                {"tag_1", Set.of("tag_1")},
                {"other_tag,tag-two", new HashSet<>(Set.of("tag-two", "other_tag"))},
                {"other_tag,tag-two,tag_1", new HashSet<>(Set.of("tag_1", "tag-two", "other_tag"))},
        };
    }

    @Test(dataProvider = "tags")
    public void tagsToStringTest(String serialized, Set<String> tags) {
        assertThat(PduUtils.tagsToString(tags)).isEqualTo(serialized);
    }

    @Test(dataProvider = "tags")
    public void stringToTagsTest(String serialized, Set<String> tags) {
        assertThat(PduUtils.stringToTags(serialized)).containsExactlyInAnyOrderElementsOf(tags);
    }

    @DataProvider(name = "replaceData")
    public Object[][] replaceDataProvider() {
        return new Object[][]{
                {getBytes("Test string!"), getBytes("Test"), getBytes("XYZ"), getBytes("XYZ string!")},
                {getBytes("Test string!"), getBytes("Test"), getBytes("XYZ123"), getBytes("XYZ123 string!")},
                {getBytes("Test string!"), getBytes("Test"), getBytes(""), getBytes(" string!")},
                {getBytes("Test string!"), getBytes("Test"), getBytes("X"), getBytes("X string!")},
                {getBytes("Test string!"), getBytes("ing!"), getBytes("X"), getBytes("Test strX")},
                {getBytes("Test string!"), getBytes("ing!"), getBytes("XYZ"), getBytes("Test strXYZ")},
                {getBytes("Test string!"), getBytes("ing!"), getBytes("XYZ123"), getBytes("Test strXYZ123")},
                {getBytes("Test string Test!"), getBytes("Test"), getBytes("XYZ"), getBytes("XYZ string XYZ!")},
                {getBytes("Test string Test! Test"), getBytes("Test"), getBytes("XYZ"), getBytes("XYZ string XYZ! XYZ")},
                {getBytes("Test string!"), getBytes("XYZ"), getBytes("NOPE"), getBytes("Test string!")},
        };
    }

    @Test(dataProvider = "replaceData")
    public void replaceTest(byte[] data, byte[] what, byte[] with, byte[] expected) {
        var pdu = new TestPdu(null, null, null, data, data.length);
        PduUtils.replace(pdu, what, with);
        assertThat(pdu.getSize()).isEqualTo(expected.length);
        assertThat(pdu.getBuffer()).startsWith(expected);
    }

    @DataProvider(name = "replaceDataWithOccurrence")
    public Object[][] replaceDataWithOccurrenceProvider() {
        return new Object[][]{
                {getBytes("Test string!"), getBytes("Test"), getBytes("XYZ"), 0, getBytes("XYZ string!")},
                {getBytes("Test string!"), getBytes("Test"), getBytes("XYZ123"), 0, getBytes("XYZ123 string!")},
                {getBytes("Test string!"), getBytes("Test"), getBytes(""), 0, getBytes(" string!")},
                {getBytes("Test string!"), getBytes("Test"), getBytes("X"), 0, getBytes("X string!")},
                {getBytes("Test string!"), getBytes("ing!"), getBytes("X"), 0, getBytes("Test strX")},
                {getBytes("Test string!"), getBytes("ing!"), getBytes("XYZ"), 0, getBytes("Test strXYZ")},
                {getBytes("Test string!"), getBytes("ing!"), getBytes("XYZ123"), 0, getBytes("Test strXYZ123")},
                {getBytes("Test string for test purpose - test!"), getBytes("test"), getBytes("XYZ"), 0, getBytes("Test string for XYZ purpose - test!")},
                {getBytes("Test string for test purpose - test!"), getBytes("test"), getBytes("XYZ"), 1, getBytes("Test string for test purpose - XYZ!")},
                {getBytes("Test string for test purpose - test!"), getBytes("test"), getBytes("XYZ123"), 0, getBytes("Test string for XYZ123 purpose - test!")},
                {getBytes("Test string for test purpose - test!"), getBytes("test"), getBytes("XYZ123"), 1, getBytes("Test string for test purpose - XYZ123!")},
                {getBytes("Test string for test purpose - test!"), getBytes("test"), getBytes("XYZ"), 2, getBytes("Test string for test purpose - test!")},
                {getBytes("Test string for test purpose - test!"), getBytes("test"), getBytes(""), 1, getBytes("Test string for test purpose - !")},
                {getBytes("Test string for test purpose - test!"), getBytes("!"), getBytes("X"), 0, getBytes("Test string for test purpose - testX")},
                {getBytes("Test string for test purpose - test!"), getBytes("!"), getBytes("XYZ"), 0, getBytes("Test string for test purpose - testXYZ")},
        };
    }

    @Test(dataProvider = "replaceDataWithOccurrence")
    public void replaceTestWithOccurrence(byte[] data, byte[] what, byte[] with, int occurrence, byte[] expected) {
        var pdu = new TestPdu(null, null, null, data, data.length);
        PduUtils.replace(pdu, what, with, occurrence);
        assertThat(pdu.getSize()).isEqualTo(expected.length);
        assertThat(pdu.getBuffer()).startsWith(expected);
    }
}
