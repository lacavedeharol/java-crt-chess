package com.lacavedeharol.chess.ai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

/**
 * Thin wrapper around a Stockfish process, speaking the UCI protocol over the
 * process's standard input/output.
 *
 * <p>
 * The binary is bundled on the classpath under {@code /stockfish/} and
 * extracted
 * to a temporary file on startup, because a binary inside a packaged JAR is not
 * a real file on disk and cannot be executed directly.
 * </p>
 */
final class StockfishEngine {

    /** Classpath folder containing the bundled binaries. */
    private static final String RESOURCE_DIR = "/stockfish/";

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Path extractedBinary;

    /**
     * Starts the engine: extracts the binary, launches it, and performs the UCI
     * handshake.
     *
     * @return {@code true} if the engine started and is ready
     */
    boolean start() {
        try {
            Path binary = extractBinary();
            process = new ProcessBuilder(binary.toAbsolutePath().toString())
                    .redirectErrorStream(false)
                    .start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

            send("uci");
            if (!waitFor("uciok"))
                return false;
            send("isready");
            return waitFor("readyok");
        } catch (IOException | UnsupportedOperationException e) {
            /*
             * IOException: binary missing/unreadable or process failed to launch.
             * UnsupportedOperationException: no binary bundled for this platform.
             */
            return false;
        }
    }

    /**
     * Locates the correct binary for the current OS on the classpath and copies
     * it to a temporary file marked executable.
     */
    private Path extractBinary() throws IOException {
        String resource = RESOURCE_DIR + binaryName();
        InputStream in = StockfishEngine.class.getResourceAsStream(resource);
        if (in == null)
            throw new IOException("Bundled Stockfish binary not found on classpath: " + resource);

        String suffix = isWindows() ? ".exe" : "";
        Path tmp = Files.createTempFile("stockfish-", suffix);
        try (in) {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        File f = tmp.toFile();
        f.setExecutable(true, true);
        f.deleteOnExit();
        extractedBinary = tmp;
        return tmp;
    }

    /**
     * Resolves the bundled binary filename for the current OS and CPU
     * architecture. Throws if no binary is bundled for the detected platform.
     *
     * <p>
     * To add a platform: bundle its binary under the resources folder and add a
     * matching branch here. The returned name must match the bundled file
     * EXACTLY (including any instruction-set suffix), or extraction will fail.
     * </p>
     */
    private static String binaryName() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

        boolean isX86_64 = arch.equals("amd64") || arch.equals("x86_64");
        if (!isX86_64)
            throw new UnsupportedOperationException(
                    "No bundled Stockfish binary for CPU architecture: " + arch
                            + " (only x86-64 is bundled)");

        if (os.contains("win"))
            return "stockfish-windows-x86-64-avx2.exe";
        if (os.contains("linux"))
            return "stockfish-ubuntu-x86-64-avx2";

        // macOS and any other OS: no binary bundled.
        throw new UnsupportedOperationException(
                "No bundled Stockfish binary for OS: " + os);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    /**
     * Sets the engine skill level (0 = weakest, 20 = full strength).
     *
     * @param level the skill level, clamped to 0..20
     */
    void setSkillLevel(int level) {
        int clamped = Math.max(0, Math.min(20, level));
        try {
            send("setoption name Skill Level value " + clamped);
        } catch (IOException ignored) {
            // Non-fatal: engine will just play at default strength.
        }
    }

    /**
     * Asks the engine for the best move in the given position.
     *
     * @param fen        the position in FEN
     * @param moveTimeMs how long the engine may think, in milliseconds
     * @return the best move in UCI coordinate notation (e.g. "e2e4", "e7e8q"),
     *         or {@code null} on error or if no move is available
     */
    String getBestMove(String fen, int moveTimeMs) {
        try {
            send("position fen " + fen);
            send("go movetime " + Math.max(1, moveTimeMs));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length < 2 || "(none)".equals(parts[1]))
                        return null;
                    return parts[1];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Stops the engine and releases resources. Safe to call more than once.
     */
    void stop() {
        try {
            if (writer != null)
                send("quit");
        } catch (IOException ignored) {
            // Falling through to destroy below.
        }
        if (process != null) {
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (extractedBinary != null) {
            try {
                Files.deleteIfExists(extractedBinary);
            } catch (IOException ignored) {
                // deleteOnExit will catch it as a fallback.
            }
        }
    }

    private void send(String command) throws IOException {
        writer.write(command);
        writer.write('\n');
        writer.flush();
    }

    /**
     * Reads lines until one contains the given token, or the stream ends.
     *
     * @return {@code true} if the token was seen
     */
    private boolean waitFor(String token) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(token))
                return true;
        }
        return false;
    }
}
