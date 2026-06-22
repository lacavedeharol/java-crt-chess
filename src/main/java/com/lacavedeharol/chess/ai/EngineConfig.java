package com.lacavedeharol.chess.ai;

import java.util.List;

/**
 * Describes how to locate, launch, and drive a specific UCI engine, so that one
 * {@link UciEngine} implementation can serve many engines (Stockfish, Leela,
 * Maia, etc.) by configuration rather than by subclassing.
 *
 * <p>
 * Use {@link #stockfish()} for the bundled Stockfish setup. Add factory methods
 * here for additional engines as they are introduced.
 * </p>
 *
 * @param resourceDir     classpath folder holding the binaries, e.g.
 *                        {@code /stockfish/}.
 * @param windowsBinary   filename of the Windows build (with {@code .exe}).
 * @param linuxBinary     filename of the Linux build.
 * @param launchArgs      extra command-line arguments passed after the binary
 *                        (e.g. {@code --weights=...} for lc0/Maia). May be
 *                        empty.
 * @param goCommand       the UCI "go" command used to request a move
 *                        (e.g. {@code "go movetime 1000"} for Stockfish,
 *                        {@code "go nodes 1"} for Maia).
 * @param skillOptionName name of the UCI option used to limit strength, or
 *                        {@code null} if the engine has none.
 */
record EngineConfig(
        String resourceDir,
        String windowsBinary,
        String linuxBinary,
        List<String> launchArgs,
        String goCommand,
        String skillOptionName) {

    /**
     * Configuration for the bundled Stockfish engine: binaries under
     * {@code /stockfish/}, fixed think time per move, and the "Skill Level"
     * option for strength limiting.
     *
     * @param moveTimeMs think time per move in milliseconds.
     * @return the Stockfish engine configuration.
     */
    static EngineConfig stockfish(int moveTimeMs) {
        return new EngineConfig(
                "/stockfish/",
                "stockfish-windows-x86-64-avx2.exe",
                "stockfish-ubuntu-x86-64-avx2",
                List.of(),
                "go movetime " + Math.max(1, moveTimeMs),
                "Skill Level");
    }
}
