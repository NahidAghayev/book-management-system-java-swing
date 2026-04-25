package com.bookmanagementsystem.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {
    private static final Path PROJECT_ROOT = locateProjectRoot();
    private static final Path DATA_DIR = PROJECT_ROOT.resolve("data");

    private AppPaths() {
    }

    public static Path dataFile(String fileName) {
        return DATA_DIR.resolve(fileName);
    }

    private static Path locateProjectRoot() {
        Path current = Paths.get("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.isDirectory(current.resolve("data"))
                    || Files.isDirectory(current.resolve("src"))
                    || Files.exists(current.resolve("README.md"))) {
                return current;
            }
            current = current.getParent();
        }
        return Paths.get("").toAbsolutePath().normalize();
    }
}
