package com.lacavedeharol.chess.computer.uci;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Resolves where engine binaries live in the per-user data directory and checks
 * whether they are present.
 *
 * <p>
 * The game does <em>not</em> download engines. They are placed into the data
 * directory out-of-band (by the installer's optional components, or manually by
 * the user). This class only answers "where would engine X be?" and "is it
 * actually there?", so the UI can offer only the opponents that are installed.
 * </p>
 *
 * <p>
 * Layout:
 * </p>
 * 
 * <pre>
 *   &lt;dataDir&gt;/java-crt-chess/engines/&lt;engineId&gt;/&lt;binaryFileName&gt;
 * </pre>
 *
 * <p>
 * Platform data directories:
 * </p>
 * <ul>
 * <li>Windows: {@code %LOCALAPPDATA%} (machine-local, not roaming)</li>
 * <li>macOS: {@code ~/Library/Application Support}</li>
 * <li>Linux: {@code $XDG_DATA_HOME}, else {@code ~/.local/share}</li>
 * </ul>
 */
final class EngineCache {

    private static final String APP_DIR = "java-crt-chess";
    private static final String ENGINES_SUBDIR = "engines";

    private EngineCache() {
    }

    /**
     * Resolves the on-disk path an engine's binary should live at (whether or
     * not it currently exists).
     *
     * @param engineId       namespacing id, e.g. {@code "stockfish-18"}
     * @param binaryFileName the binary's filename for this platform.
     * @return the absolute path the binary should live at.
     */
    static Path binaryPath(String engineId, String binaryFileName) {
        return dataDir().resolve(APP_DIR).resolve(ENGINES_SUBDIR).resolve(engineId).resolve(binaryFileName);
    }

    /**
     * Returns true if the engine's binary exists in the data directory.
     *
     * @param engineId       the engine id.
     * @param binaryFileName the binary filename.
     * @return true if the file is present and is a regular file.
     */
    static boolean isPresent(String engineId, String binaryFileName) {
        return Files.isRegularFile(binaryPath(engineId, binaryFileName));
    }

    /**
     * Resolves the platform's user data directory.
     */
    private static Path dataDir() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String home = System.getProperty("user.home", ".");

        if (os.contains("win")) {
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null && !localAppData.isBlank())
                return Path.of(localAppData);
            return Path.of(home, "AppData", "Local");
        }

        if (os.contains("mac") || os.contains("darwin"))
            return Path.of(home, "Library", "Application Support");

        String xdg = System.getenv("XDG_DATA_HOME");
        if (xdg != null && !xdg.isBlank())
            return Path.of(xdg);
        return Path.of(home, ".local", "share");
    }
}
