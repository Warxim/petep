package com.warxim.petep.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.StringJoiner;

import static com.warxim.petep.util.BytesUtils.getBytes;
import static org.assertj.core.api.Assertions.assertThat;

public class BytesUtilsTest {
    private static byte[] generateAllBytes() {
        var buffer = new byte[256];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte) i;
        }
        return buffer;
    }

    private static String generateAllHex() {
        var joiner = new StringJoiner(" ");
        for (int i = 0; i < 256; ++i) {
            joiner.add(String.format("%02X", i & 0xFF));
        }
        return joiner.toString();
    }

    @DataProvider(name = "conversionData")
    public Object[][] conversionDataProvider() {
        return new Object[][]{
                {generateAllBytes(), generateAllHex()},
                {getBytes("Test"), "54 65 73 74"},
                {getBytes(" X "), "20 58 20"},
                {getBytes("  "), "20 20"},
                {getBytes("A"), "41"},
                {getBytes(""), ""},
        };
    }

    @Test(dataProvider = "conversionData")
    public void bytesToStringTest(byte[] bytes, String string) {
        assertThat(BytesUtils.bytesToHexString(bytes)).isEqualTo(string);
    }

    @Test(dataProvider = "conversionData")
    public void stringToBytesTest(byte[] bytes, String string) {
        assertThat(BytesUtils.hexStringToBytes(string)).containsExactly(bytes);
    }

    @Test(dataProvider = "conversionData")
    public void bytesToStringTestWithSize(byte[] bytes, String string) {
        int skip = string.length() / 4;
        assertThat(BytesUtils.bytesToHexString(bytes, bytes.length - skip))
                .isEqualTo(string.substring(0, string.length() - 3 * skip));

        assertThat(BytesUtils.bytesToHexString(bytes, bytes.length)).isEqualTo(string);
    }

    @DataProvider(name = "findData")
    public Object[][] findDataProvider() {
        var testString = "Testing content 1234";
        var testStringLength = testString.length();
        var testStringBytes = getBytes(testString);

        var middleString = "con";
        var middleStringBytes = getBytes(middleString);
        var middleStringPosition = testString.indexOf(middleString);

        var startString = "Test";
        var startStringBytes = getBytes(startString);
        var startStringPosition = testString.indexOf(startString);

        var endString = "234";
        var endStringBytes = getBytes(endString);
        var endStringPosition = testString.indexOf(endString);

        return new Object[][]{
                // Start
                {testStringBytes, testStringLength, 0, startStringBytes, startStringPosition},
                {testStringBytes, testStringLength, 1, startStringBytes, -1},
                {testStringBytes, testStringLength, testStringLength, startStringBytes, -1},
                // Middle
                {testStringBytes, testStringLength, 0, middleStringBytes, middleStringPosition},
                {testStringBytes, testStringLength, middleStringPosition - 1, middleStringBytes, middleStringPosition},
                {testStringBytes, testStringLength, middleStringPosition, middleStringBytes, middleStringPosition},
                {testStringBytes, testStringLength, middleStringPosition + 1, middleStringBytes, -1},
                {testStringBytes, testStringLength, testStringLength, middleStringBytes, -1},
                // End
                {testStringBytes, testStringLength, 0, endStringBytes, endStringPosition},
                {testStringBytes, testStringLength, endStringPosition - 1, endStringBytes, endStringPosition},
                {testStringBytes, testStringLength, endStringPosition, endStringBytes, endStringPosition},
                {testStringBytes, testStringLength, endStringPosition + 1, endStringBytes, -1},
                {testStringBytes, testStringLength, testStringLength, endStringBytes, -1},
        };
    }

    @Test(dataProvider = "findData")
    public void findTest(byte[] buffer, int size, int offset, byte[] array, int expectedResult) {
        assertThat(BytesUtils.find(buffer, size, offset, array)).isEqualTo(expectedResult);
    }

    @DataProvider(name = "findNthData")
    public Object[][] findNthDataProvider() {
        var testString = "test string test for test is test";
        var testStringLength = testString.length();
        var testStringBytes = getBytes(testString);

        var string = "test";
        var stringBytes = getBytes(string);
        var stringFirstPosition = testString.indexOf(string);
        var stringSecondPosition = testString.indexOf(string, stringFirstPosition + 1);
        var stringThirdPosition = testString.indexOf(string, stringSecondPosition + 1);
        var stringFourthPosition = testString.indexOf(string, stringThirdPosition + 1);

        return new Object[][]{
                {testStringBytes, testStringLength, 0, stringBytes, 0, stringFirstPosition},
                {testStringBytes, testStringLength, 0, stringBytes, 1, stringSecondPosition},
                {testStringBytes, testStringLength, 0, stringBytes, 2, stringThirdPosition},
                {testStringBytes, testStringLength, 0, stringBytes, 3, stringFourthPosition},
                {testStringBytes, testStringLength, 0, stringBytes, 4, -1},
                {testStringBytes, testStringLength, 0, stringBytes, 100, -1},
                {testStringBytes, testStringLength, stringFirstPosition, stringBytes, 0, stringFirstPosition},
                {testStringBytes, testStringLength, stringSecondPosition, stringBytes, 0, stringSecondPosition},
                {testStringBytes, testStringLength, stringThirdPosition, stringBytes, 1, stringFourthPosition},
                {testStringBytes, testStringLength, stringFourthPosition, stringBytes, 1, -1},
                {testStringBytes, testStringLength, stringFourthPosition, stringBytes, 100, -1},
        };
    }

    @Test(dataProvider = "findNthData")
    public void findNthTest(byte[] buffer, int size, int offset, byte[] array, int n, int expectedResult) {
        assertThat(BytesUtils.findNth(buffer, size, offset, array, n)).isEqualTo(expectedResult);
    }

    @DataProvider(name = "containsData")
    public Object[][] containsDataProvider() {
        var testString = "Test string 123456789!";
        var testStringLength = testString.length();
        var testStringBytes = getBytes(testString);

        return new Object[][]{
                {testStringBytes, testStringLength, getBytes("string"), true},
                {testStringBytes, testStringLength, getBytes("T"), true},
                {testStringBytes, testStringLength, getBytes("!"), true},
                {testStringBytes, testStringLength, getBytes("r"), true},
                {testStringBytes, testStringLength, getBytes("1234"), true},
                {testStringBytes, testStringLength, getBytes("Test "), true},
                {testStringBytes, testStringLength, getBytes(testString), true},
                {testStringBytes, testStringLength, getBytes(""), true},
                {testStringBytes, testStringLength, getBytes("TEST"), false},
                {testStringBytes, testStringLength, getBytes("E"), false},
                {testStringBytes, testStringLength, getBytes(testString + "x"), false},
                {testStringBytes, testStringLength, getBytes("x" + testString), false},
        };
    }

    @Test(dataProvider = "containsData")
    public void containsTest(byte[] buffer, int size, byte[] what, boolean expectedResult) {
        assertThat(BytesUtils.contains(buffer, size, what)).isEqualTo(expectedResult);
    }

    @DataProvider(name = "containsAtData")
    public Object[][] containsAtDataProvider() {
        var testString = "Test string 123456789!";
        var testStringLength = testString.length();
        var testStringBytes = getBytes(testString);

        return new Object[][]{
                {testStringBytes, testStringLength, getBytes("Test"), 0, true},
                {testStringBytes, testStringLength, getBytes("Test"), 1, false},
                {testStringBytes, testStringLength, getBytes("Test"), 100, false},
                {testStringBytes, testStringLength, getBytes("string"), testString.indexOf("string"), true},
                {testStringBytes, testStringLength, getBytes("1234"), testString.indexOf("1234") + 1, false},
                {testStringBytes, testStringLength, getBytes("789!"), testString.indexOf("789!"), true},
                {testStringBytes, testStringLength, getBytes("789!"), testString.indexOf("789!") + 1, false},
                {testStringBytes, testStringLength, getBytes("!"), testString.indexOf("!"), true},
                {testStringBytes, testStringLength, getBytes("!"), testString.indexOf("!") + 1, false},
        };
    }

    @Test(dataProvider = "containsAtData")
    public void containsAtTest(byte[] buffer, int size, byte[] what, int position, boolean expectedResult) {
        assertThat(BytesUtils.containsAt(buffer, size, what, position)).isEqualTo(expectedResult);
    }

    @DataProvider(name = "startsWithData")
    public Object[][] startsWithDataProvider() {
        var testString = "Test string!";
        var testStringLength = testString.length();
        var testStringBytes = getBytes(testString);

        return new Object[][]{
                {testStringBytes, testStringLength, getBytes("T"), true},
                {testStringBytes, testStringLength, getBytes("Test"), true},
                {testStringBytes, testStringLength, getBytes(testString), true},
                {testStringBytes, testStringLength, getBytes("est"), false},
                {testStringBytes, testStringLength, getBytes(testString + "Longer string."), false},
        };
    }

    @Test(dataProvider = "startsWithData")
    public void startsWithTest(byte[] buffer, int size, byte[] what, boolean expectedResult) {
        assertThat(BytesUtils.startsWith(buffer, size, what)).isEqualTo(expectedResult);
    }

    @DataProvider(name = "endsWithData")
    public Object[][] endsWithDataProvider() {
        var testString = "Test string!";
        var testStringLength = testString.length();
        var testStringBytes = getBytes(testString);

        return new Object[][]{
                {testStringBytes, testStringLength, getBytes("ing!"), true},
                {testStringBytes, testStringLength, getBytes("!"), true},
                {testStringBytes, testStringLength, getBytes(testString), true},
                {testStringBytes, testStringLength, getBytes("ing"), false},
                {testStringBytes, testStringLength, getBytes(testString + "Longer string."), false},
        };
    }

    @Test(dataProvider = "endsWithData")
    public void endsWithTest(byte[] buffer, int size, byte[] what, boolean expectedResult) {
        assertThat(BytesUtils.endsWith(buffer, size, what)).isEqualTo(expectedResult);
    }
}
