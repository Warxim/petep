/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2020 Michal Válka
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep.util;

import com.warxim.petep.Main;
import com.warxim.petep.extension.PetepAPI;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.logging.Logger;

/**
 * File utils.
 */
@PetepAPI
public final class FileUtils {
    /**
     * Application directory path
     */
    private static final String APP_DIR = initApplicationDirectory();

    private FileUtils() {
    }

    /**
     * Obtains application directory.
     * @return Current application directory
     */
    public static String getApplicationDirectory() {
        return APP_DIR;
    }

    /**
     * Obtains application file.
     * @param path Path relative to application directory
     * @return File at given path, which is relative to application directory
     */
    public static File getApplicationFile(String path) {
        return new File(getApplicationFileAbsolutePath(path));
    }

    /**
     * Obtains application file absolute path
     * @param path Path relative to application directory
     * @return Absolute path of application file
     */
    public static String getApplicationFileAbsolutePath(String path) {
        return getFileAbsolutePath(getApplicationDirectory(), path);
    }

    /**
     * Relativizes given path to application directory path
     * @param path Absolute path to relativize
     * @return Path relative to application directory
     */
    public static String applicationRelativize(Path path) {
        return relativize(Path.of(getApplicationDirectory()), path);
    }

    /**
     * Relativizes given path to application directory path
     * @param path Absolute path to relativize
     * @return Path relative to application directory
     */
    public static String applicationRelativize(String path) {
        return applicationRelativize(Paths.get(path));
    }

    /**
     * Relativizes given path to working directory path
     * @param path Absolute path to relativize
     * @return Path relative to working directory
     */
    public static String workingDirectoryRelativize(Path path) {
        return relativize(Path.of(getWorkingDirectory()), path);
    }

    /**
     * Relativizes given path to working directory path
     * @param path Absolute path to relativize
     * @return Path relative to working directory
     */
    public static String workingDirectoryRelativize(String path) {
        return workingDirectoryRelativize(Path.of(path));
    }

    /**
     * Relativizes given path to project directory path
     * @param path Absolute path to relativize
     * @return Path relative to project directory
     */
    public static String projectRelativize(Path path) {
        return relativize(Path.of(getProjectDirectory()), path);
    }

    /**
     * Relativizes given path to project directory path
     * @param path Absolute path to relativize
     * @return Path relative to project directory
     */
    public static String projectRelativize(String path) {
        return projectRelativize(Paths.get(path));
    }

    /**
     * Relativizes given path to root directory path
     * @param path Absolute path to relativize
     * @return Path relative to root directory
     */
    public static String relativize(Path rootDirectoryPath, Path path) {
        try {
            // Resolve path
            path = rootDirectoryPath.resolve(path).normalize();

            // Relativize the path
            return rootDirectoryPath.relativize(path).toString();
        } catch (IllegalArgumentException e) {
            return path.toString();
        }
    }

    /**
     * Obtains project file.
     * @param path Path relative to project directory
     * @return File at given path, which is relative to project directory
     */
    public static File getProjectFile(String path) {
        return new File(getProjectFileAbsolutePath(path));
    }

    /**
     * Obtains project file absolute path
     * @param path Path relative to project directory
     * @return Absolute path of project file
     */
    public static String getProjectFileAbsolutePath(String path) {
        return getFileAbsolutePath(getProjectDirectory(), path);
    }

    /**
     * Obtains working directory file absolute path
     * @param path Path relative to working directory
     * @return Absolute path of working directory file
     */
    public static String getWorkingDirectoryFileAbsolutePath(String path) {
        return getFileAbsolutePath(getWorkingDirectory(), path);
    }

    /**
     * Obtains absolute path by concatenating root and path
     * @param root Path to root
     * @param path Relative path
     * @return Absolute path of project file
     */
    public static String getFileAbsolutePath(String root, String path) {
        if (Paths.get(path).isAbsolute()) {
            // Absolute path
            return path;
        }

        // Relative path
        return Paths.get(root).resolve(path).normalize().toString();
    }

    /**
     * Obtains project directory.
     * @return Current project director
     */
    public static String getProjectDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * Obtains working directory.
     * @return Current working directory
     */
    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * Sets working directory (used internally).
     * @param directory Directory to be set
     */
    public static void setProjectDirectory(String directory) {
        System.setProperty("user.dir", directory);
    }

    /**
     * Checks whether the specified directory is empty.
     * @param directory Directory to check for emptiness
     * @return {@code true} if the directory is empty
     */
    public static boolean isDirectoryEmpty(File directory) {
        return directory.list().length == 0;
    }

    /**
     * Copies directory from source to destination.
     * @param source Source path
     * @param destination Destination path
     * @throws IOException If the copy failed
     */
    public static void copyDirectory(String source, String destination) throws IOException {
        copyDirectory(Path.of(source), Path.of(destination));
    }

    /**
     * Copies directory from source to destination.
     * @param source Source path
     * @param destination Destination path
     * @throws IOException If the copy failed
     */
    public static void copyDirectory(Path source, Path destination) throws IOException {
        try (var stream = Files.walk(source)) {
            var destFile = destination.toFile();
            if (!destFile.exists() && !destFile.mkdirs()) {
                throw new IOException(String.format("Could not create directory '%s'", destFile.getAbsolutePath()));
            }
            stream.forEach(src -> copy(src, destination.resolve(source.relativize(src))));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Deletes directories recursively
     * @param directory Directory to delete (including content)
     * @return {@code true} if the deletion was successful
     */
    public static boolean deleteDirectories(String directory) {
        try (var stream = Files.walk(Path.of(directory))) {
            return stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .allMatch(File::delete);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Copies from source to destination and replace existing.
     * @param source Source path
     * @param destination Destination path
     */
    private static void copy(Path source, Path destination) {
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * Initializes application directory.
     * @return Path to application directory
     */
    private static String initApplicationDirectory() {
        try {
            var path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            // PETEP run from lib/petep.jar
            if (path.toString().toLowerCase().endsWith(".jar")) {
                return path.getParent().getParent().toString();
            }

            // Fallback for working directory in IDE
            return Paths.get(".").toAbsolutePath().normalize().toString();
        } catch (URISyntaxException e) {
            Logger.getGlobal().severe(e.getMessage());
        }

        return "";
    }
}
