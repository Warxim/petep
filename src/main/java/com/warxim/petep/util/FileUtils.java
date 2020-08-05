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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;
import java.util.stream.Stream;
import com.warxim.petep.Main;
import com.warxim.petep.extension.PetepAPI;

/** File utils. */
@PetepAPI
public final class FileUtils {
  private static final String APP_DIR = initApplicationDirectory();

  private FileUtils() {}

  /** Returns application directory. */
  public static String getApplicationDirectory() {
    return APP_DIR;
  }

  /** Returns application file. */
  public static File getApplicationFile(String path) {
    if (Paths.get(path).isAbsolute()) {
      // Absolute path
      return new File(path);
    }

    // Relative path
    return new File(FileUtils.getApplicationDirectory(), path);
  }

  /** Returns application file absolute path. */
  public static String getApplicationFileAbsolutePath(String path) {
    if (Paths.get(path).isAbsolute()) {
      // Absolute path
      return path;
    }

    // Relative path
    return new File(FileUtils.getApplicationDirectory(), path).getAbsolutePath();
  }

  public static String applicationRelativize(Path path) {
    try {
      return Paths.get(APP_DIR).relativize(path).toString();
    } catch (IllegalArgumentException e) {
      return path.toString();
    }
  }

  public static String applicationRelativize(String path) {
    return applicationRelativize(Paths.get(path));
  }

  public static String projectRelativize(Path path) {
    try {
      return Paths.get(getProjectDirectory()).relativize(path).toString();
    } catch (IllegalArgumentException e) {
      return path.toString();
    }
  }

  public static String projectRelativize(String path) {
    return projectRelativize(Paths.get(path));
  }

  /** Returns project path. */
  public static File getProjectFile(String path) {
    if (Paths.get(path).isAbsolute()) {
      // Absolute path
      return new File(path);
    }

    // Relative path
    return new File(FileUtils.getProjectDirectory(), path);
  }

  /** Returns absolute path of project file. */
  public static String getProjectFileAbsolutePath(String path) {
    if (Paths.get(path).isAbsolute()) {
      // Absolute path
      return path;
    }

    // Relative path
    return new File(FileUtils.getProjectDirectory(), path).getAbsolutePath();
  }

  /** Returns project directory. */
  public static String getProjectDirectory() {
    return System.getProperty("user.dir");
  }

  /** Sets working directory (used internally). */
  public static void setProjectDirectory(String directory) {
    System.setProperty("user.dir", directory);
  }

  /** Returns true if the specified directory is empty. */
  public static boolean isDirectoryEmpty(File directory) {
    return directory.list().length == 0;
  }

  /** Copies directory from source to destination. */
  public static void copyDirectory(String source, String destination) throws IOException {
    copyDirectory(Path.of(source), Path.of(destination));
  }

  /** Copies directory from source to destination. */
  public static void copyDirectory(Path source, Path destination) throws IOException {
    try (Stream<Path> stream = Files.walk(source)) {
      stream.forEach(src -> copy(src, destination.resolve(source.relativize(src))));
    }
  }

  /** Copies from source to destination and replace existing. */
  private static void copy(Path source, Path destination) {
    try {
      Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /** Initializes application directory. */
  private static String initApplicationDirectory() {
    try {
      Path path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

      if (path.toString().toLowerCase().endsWith(".jar")) {
        return path.getParent().getParent().toString();
      }

      return getProjectDirectory();
    } catch (URISyntaxException e) {
      Logger.getGlobal().severe(e.getMessage());
    }

    return "";
  }
}
