# Java CRT Chess

Java chess application built in the spirit of early computer chess, featuring a retro aesthetic and a 3D perspective board.

## Download & Install

Get the latest Windows installer from the [Releases page](https://github.com/lacavedeharol/java-crt-chess/releases/latest) and run it:

```bash
java-crt-chess-1.1.0-windows-setup.exe
```

The installer is **self-contained**, it bundles a Java runtime, so **you do not need Java installed** to play. It installs per-user (no administrator prompt) and optionally includes third-party chess engines (see [Opponents](#opponents)).

During setup you can choose:

- **Main application** - the game and its bundled runtime (always installed).
- **Third-party chess engines** - Stockfish 18 and Berserk 14 (optional).

A desktop shortcut is offered during installation.

## Features

- **3D Perspective View**: Play chess on a 3D rendered board with adjustable camera angles.
- **Multiple Opponents**: Built-in AIs, external UCI engines, or local two-player (see [Opponents](#opponents)).
- **Game Modes**: Play as White, Black, or Local Multiplayer (Hotseat).
- **Move Assistance**: Visual highlights for legal moves.
- **Customizable Experience**: Toggle sound, view modes (2D/3D), timer settings (Blitz, Rapid, Classic), and more.

## Opponents

The opponent selector lets you choose who you play against:

| Opponent | Type | Notes |
| -------- | ---- | ----- |
| **CRT EASY** | Built-in AI | Lightweight built-in engine, gentler play |
| **CRT HARD** | Built-in AI | Built-in engine at full strength |
| **Stockfish** | UCI engine | Bundled (optional component), very strong |
| **Berserk** | UCI engine | Bundled (optional component), very strong |
| **Local** | Two players | Hotseat - two humans on one machine, no AI |

The Stockfish and Berserk options require the **Third-party chess engines** component to be selected during installation. If you skipped it, the built-in opponents and Local options are still fully available.

## Building from Source

The installer is the recommended way to play. The steps below are for anyone who want to build from source.

### Prerequisites

- Java Development Kit (JDK) **25** or higher.
- Maven 3.6 or higher.

### Build

1. Clone the repository:

    ```bash
    git clone https://github.com/lacavedeharol/java-crt-chess.git
    cd java-crt-chess
    ```

2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

### Running from source

Run directly via Maven:

```bash
mvn exec:java -Dexec.mainClass="com.lacavedeharol.chess.Main"
```

Or run the generated JAR from the `target` directory:

```bash
java -jar target/java-crt-chess-1.1.0.jar
```

> **Note:** Building from source does not include the bundled UCI engines. To use the Stockfish or Berserk opponents in a source build, place their executables in an `engines/` folder next to the JAR (or use the released installer).

## Controls

- **Mouse**: Click to select pieces and move them. Drag and drop is supported.
- **Context Menu**: Access settings, restart game, and toggle options via the on-screen menu.

## Technologies Used

- **Java Swing/AWT**: For window management and custom rendering.
- **Maven**: Dependency management and build automation.
- **UCI**: Communication with external chess engines (Stockfish, Berserk).

## License

This application is released under the **MIT License** - see [`LICENSE`](LICENSE).

The optional bundled engines are licensed separately under the **GNU General Public License v3.0**:

- **Stockfish 18** - GPL-3.0
- **Berserk 14** - GPL-3.0

Their full license texts and corresponding source offers are included with the installer (in the `engines/` folder) and summarized in [`THIRD-PARTY-LICENSES.txt`](THIRD-PARTY-LICENSES.txt).

## Author

- **lacavedeharol**
