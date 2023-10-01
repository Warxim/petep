package com.warxim.petep.test.base.util;

import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Test utilities
 */
@Log
public final class TestUtils {
    private TestUtils() {
    }

    /**
     * Sleeps for specified amount of milliseconds.
     * @throws AssertionError If sleep was interrupted
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.severe("Sleep interrupted...");
            Thread.currentThread().interrupt();
            throw new AssertionError("Test interrupted!");
        }
    }

    /**
     * Tries to obtain data using provided supplier until it is not null
     * @param supplier Supplier providing data or null if the data are not ready yet
     * @param retryCount How many times to try to obtain the value
     * @param retryInterval After how many milliseconds to retry
     * @throws AssertionError If the waiting was interrupted
     */
    public static <T> T getWithRetries(Supplier<T> supplier, int retryCount, int retryInterval) {
        for (; retryCount > 0; --retryCount) {
            T value = supplier.get();
            if (value != null) {
                return value;
            }
            sleep(retryInterval);
        }
        throw new AssertionError("Could not get value!");
    }

    /**
     * Tries to obtain specified number of items using provided supplier until it is not null
     * @param count How many items to obtain
     * @param supplier Supplier providing data or null if the data are not ready yet
     * @param retryCount How many times to try to obtain the value
     * @param retryInterval After how many milliseconds to retry
     * @throws AssertionError If the waiting was interrupted
     */
    public static <T> List<T> getMultipleWithRetries(
            int count,
            Supplier<T> supplier,
            int retryCount,
            int retryInterval) {
        var values = new ArrayList<T>();
        for (int i = 0; i < count; ++i) {
            T value = getWithRetries(supplier, retryCount, retryInterval);
            values.add(value);
        }
        return values;
    }

    /**
     * Generates byte array with all bytes (256 values).
     * @return Byte array
     */
    public static byte[] generateAllBytes() {
        var bytes = new byte[256];
        for (int i = 0; i < 256; ++i) {
            bytes[i] = (byte) i;
        }
        return bytes;
    }

    /**
     * Generates byte array of specified length.
     * @return Byte array of specified length
     */
    public static byte[] generateBytes(int length) {
        var bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = (byte) i;
        }
        return bytes;
    }
}
