package com.lacavedeharol.chess.computer;

import java.util.List;
import java.util.Locale;

/**
 * Describes how to locate, launch, and drive a specific UCI engine, so that one
 * {@link UciEngine} implementation can serve many engines (Stockfish, Leela,
 * Maia, etc.) by configuration rather than by subclassing.
 *
 * <p>
 * Engine binaries are expected to already be present in the per-user data
 * directory (placed there by the installer or the user); the game does not
 * download them. {@link EngineCache} resolves their location and presence.
 * </p>
 *
 * @param engineId        namespacing id for the data dir, e.g.
 *                        {@code "stockfish-18"}.
 * @param windowsBinary   filename of the Windows build (with {@code .exe}).
 * @param linuxBinary     filename of the Linux build.
 * @param launchArgs      extra command-line arguments passed after the binary
 *                        (e.g. {@code --weights=...} for lc0/Maia). May be
 *                        empty.
 * @param goCommand       the UCI "go" command used to request a move
 *                        (e.g. {@code "go movetime 1000"}; {@code "go nodes 1"}
 *                        for Maia).
 * @param skillOptionName name of the UCI option used to limit strength, or
 *                        {@code null} if the engine has none.
 */
record EngineConfig(
        String engineId,
        String windowsBinary,
        String linuxBinary,
        List<String> launchArgs,
        String goCommand,
        String skillOptionName) {

    /** The binary filename for the current OS. */
    String binaryForThisOs() {
        return isWindows() ? windowsBinary : linuxBinary;
    }

    /** True if this engine's binary is present in the data directory. */
    boolean isAvailable() {
        return EngineCache.isPresent(engineId, binaryForThisOs());
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    /**
     * Configuration for the Stockfish engine. The binary is expected at
     * {@code <dataDir>/java-crt-chess/engines/stockfish-18/<binary>}.
     *
     * @param moveTimeMs think time per move in milliseconds.
     * @return the Stockfish engine configuration.
     */
    static EngineConfig stockfish(int moveTimeMs) {
        return new EngineConfig(
                "stockfish-18",
                "stockfish-windows-x86-64-avx2.exe",
                "stockfish-ubuntu-x86-64-avx2",
                List.of(),
                "go movetime " + Math.max(1, moveTimeMs),
                "Skill Level");
    }
}
