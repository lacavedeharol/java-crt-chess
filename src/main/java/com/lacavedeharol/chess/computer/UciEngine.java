package com.lacavedeharol.chess.computer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper around a UCI engine process, speaking the UCI protocol over the
 * process's standard input/output. Generic across engines: the specifics
 * (binary name, launch args, go-command, strength option) come from an
 * {@link EngineConfig}.
 *
 * <p>
 * The binary is expected to already exist in the per-user data directory (see
 * {@link EngineCache}); it is launched directly from there. If it is missing,
 * {@link #start()} fails cleanly (returns {@code false}).
 * </p>
 */
final class UciEngine {

    private final EngineConfig config;

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * Creates an engine wrapper for the given configuration. Call {@link #start()}
     * to launch the process.
     *
     * @param config the engine configuration
     */
    UciEngine(EngineConfig config) {
        this.config = config;
    }

    /**
     * Starts the engine: launches the data-dir binary (with any configured
     * launch args) and performs the UCI handshake.
     *
     * @return {@code true} if the engine started and is ready; {@code false} if
     *         the binary is missing or the handshake failed.
     */
    boolean start() {
        Path binary = EngineCache.binaryPath(config.engineId(), config.binaryForThisOs());
        if (!Files.isRegularFile(binary))
            return false; // not installed; caller should not have offered this opponent

        try {
            // Ensure executable bit on Unix (no effect on Windows). Harmless if
            // already set; the installer should set it, but we defend here too.
            binary.toFile().setExecutable(true, true);

            List<String> command = new ArrayList<>();
            command.add(binary.toAbsolutePath().toString());
            command.addAll(config.launchArgs());

            process = new ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

            send("uci");
            if (!waitFor("uciok"))
                return false;
            send("isready");
            return waitFor("readyok");
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Sets the engine strength via its configured skill option, if it has one
     * (no-op for engines without a strength option).
     *
     * @param level the engine-specific strength value
     */
    void setStrength(int level) {
        if (config.skillOptionName() == null)
            return;
        try {
            send("setoption name " + config.skillOptionName() + " value " + level);
        } catch (IOException ignored) {
            // Non-fatal: engine plays at default strength.
        }
    }

    /**
     * Asks the engine for the best move in the given position.
     *
     * @param fen the position in FEN.
     * @return the best move in UCI coordinate notation (e.g. "e2e4", "e7e8q"),
     *         or {@code null} on error or if no move is available.
     */
    String getBestMove(String fen) {
        try {
            send("position fen " + fen);
            send(config.goCommand());
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
    }

    private void send(String command) throws IOException {
        writer.write(command);
        writer.write('\n');
        writer.flush();
    }

    /**
     * Reads lines until one contains the given token, or the stream ends.
     *
     * @return {@code true} if the token was seen.
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
