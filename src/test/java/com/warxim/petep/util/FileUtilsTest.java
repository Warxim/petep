package com.warxim.petep.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FileUtilsTest {
    private static String unifyPath(String path) {
        return path.replace('\\', '/');
    }

    @DataProvider(name = "paths")
    public Object[][] pathsProvider() {
        var winDirectory = "D:/Projects/Test";
        var unixDirectory = "/data/projects/test";

        return new Object[][]{
                {winDirectory, "C:/Test/Project", "C:/Test/Project", true},
                {winDirectory, "D:/Test/Project", "D:/Test/Project", true},
                {winDirectory, "ProjectOne", "D:/Projects/Test/ProjectOne", true},
                {winDirectory, "../ProjectOne", "D:/Projects/ProjectOne", true},
                {winDirectory, "../../ProjectOne", "D:/ProjectOne", true},
                {winDirectory, "../../../../../ProjectOne", "D:/ProjectOne", true},
                {unixDirectory, "ProjectOne", "/data/projects/test/ProjectOne", false},
                {unixDirectory, "../ProjectOne", "/data/projects/ProjectOne", false},
                {unixDirectory, "../../../../../../ProjectOne", "/ProjectOne", false},
        };
    }

    @Test(dataProvider = "paths")
    public void getApplicationFileAbsolutePathTest(String directory, String path, String expectedResult, boolean windowsOnly) {
        if (windowsOnly && !isWindows()) {
            return;
        }
        try (var mocked = mockStatic(FileUtils.class)) {
            mocked.when(FileUtils::getApplicationDirectory).thenReturn(directory);
            mocked.when(() -> FileUtils.getApplicationFileAbsolutePath(anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.getFileAbsolutePath(anyString(), anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.getApplicationFile(anyString())).thenCallRealMethod();

            assertThat(unifyPath(FileUtils.getApplicationFileAbsolutePath(path)))
                    .isEqualTo(unifyPath(expectedResult));

            assertThat(unifyPath(FileUtils.getApplicationFile(path).getAbsolutePath()))
                    .endsWith(unifyPath(expectedResult));
        }
    }

    @Test(dataProvider = "paths")
    public void getProjectFileAbsolutePathTest(String directory, String path, String expectedResult, boolean windowsOnly) {
        if (windowsOnly && !isWindows()) {
            return;
        }
        try (var mocked = mockStatic(FileUtils.class)) {
            mocked.when(FileUtils::getProjectDirectory).thenReturn(directory);
            mocked.when(() -> FileUtils.getProjectFileAbsolutePath(anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.getFileAbsolutePath(anyString(), anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.getProjectFile(anyString())).thenCallRealMethod();

            assertThat(unifyPath(FileUtils.getProjectFileAbsolutePath(path)))
                    .isEqualTo(unifyPath(expectedResult));

            assertThat(unifyPath(FileUtils.getProjectFile(path).getAbsolutePath()))
                    .endsWith(unifyPath(expectedResult));
        }
    }

    @Test(dataProvider = "paths")
    public void getWorkingDirectoryFileAbsolutePathTest(String directory, String path, String expectedResult, boolean windowsOnly) {
        if (windowsOnly && !isWindows()) {
            return;
        }
        try (var mocked = mockStatic(FileUtils.class)) {
            mocked.when(FileUtils::getWorkingDirectory).thenReturn(directory);
            mocked.when(() -> FileUtils.getWorkingDirectoryFileAbsolutePath(anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.getFileAbsolutePath(anyString(), anyString())).thenCallRealMethod();

            assertThat(unifyPath(FileUtils.getWorkingDirectoryFileAbsolutePath(path)))
                    .isEqualTo(unifyPath(expectedResult));
        }
    }

    @DataProvider(name = "pathsToRelativize")
    public Object[][] pathsToRelativizeProvider() {
        var winDirectory = "D:/Projects/Test";
        var unixDirectory = "/data/projects/test";

        return new Object[][]{
                {winDirectory, "D:/Projects/Test/xyz", "xyz", true},
                {winDirectory, winDirectory, "", true},
                {winDirectory, "D:/Projects/Other", "../Other", true},
                {winDirectory, "../../other", "../../other", true},
                {unixDirectory, unixDirectory, "", false},
                {unixDirectory, "/data/projects/test/xyz", "xyz", false},
                {unixDirectory, "/data/projects/other", "../other", false},
                {unixDirectory, "../other", "../other", false},
        };
    }

    @Test(dataProvider = "pathsToRelativize")
    public void applicationRelativizeTest(String directory, String path, String expectedResult, boolean windowsOnly) {
        if (windowsOnly && !isWindows()) {
            return;
        }
        try (var mocked = mockStatic(FileUtils.class)) {
            mocked.when(FileUtils::getApplicationDirectory).thenReturn(directory);
            mocked.when(() -> FileUtils.applicationRelativize(anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.applicationRelativize(any(Path.class))).thenCallRealMethod();
            mocked.when(() -> FileUtils.relativize(any(Path.class), any(Path.class))).thenCallRealMethod();

            assertThat(unifyPath(FileUtils.applicationRelativize(path)))
                    .isEqualTo(unifyPath(expectedResult));

            assertThat(unifyPath(FileUtils.applicationRelativize(Path.of(path))))
                    .isEqualTo(unifyPath(expectedResult));
        }
    }

    @Test(dataProvider = "pathsToRelativize")
    public void projectRelativizeTest(String directory, String path, String expectedResult, boolean windowsOnly) {
        if (windowsOnly && !isWindows()) {
            return;
        }
        try (var mocked = mockStatic(FileUtils.class)) {
            mocked.when(FileUtils::getProjectDirectory).thenReturn(directory);
            mocked.when(() -> FileUtils.projectRelativize(anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.projectRelativize(any(Path.class))).thenCallRealMethod();
            mocked.when(() -> FileUtils.relativize(any(Path.class), any(Path.class))).thenCallRealMethod();

            assertThat(unifyPath(FileUtils.projectRelativize(path)))
                    .isEqualTo(unifyPath(expectedResult));

            assertThat(unifyPath(FileUtils.projectRelativize(Path.of(path))))
                    .isEqualTo(unifyPath(expectedResult));
        }
    }

    @Test(dataProvider = "pathsToRelativize")
    public void workingDirectoryRelativizeTest(String directory, String path, String expectedResult, boolean windowsOnly) {
        if (windowsOnly && !isWindows()) {
            return;
        }
        try (var mocked = mockStatic(FileUtils.class)) {
            mocked.when(FileUtils::getWorkingDirectory).thenReturn(directory);
            mocked.when(() -> FileUtils.workingDirectoryRelativize(anyString())).thenCallRealMethod();
            mocked.when(() -> FileUtils.workingDirectoryRelativize(any(Path.class))).thenCallRealMethod();
            mocked.when(() -> FileUtils.relativize(any(Path.class), any(Path.class))).thenCallRealMethod();

            assertThat(unifyPath(FileUtils.workingDirectoryRelativize(path)))
                    .isEqualTo(unifyPath(expectedResult));

            assertThat(unifyPath(FileUtils.workingDirectoryRelativize(Path.of(path))))
                    .isEqualTo(unifyPath(expectedResult));
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
