# Java CRT Chess

Java chess engine built in the spirit of early computer chess, featuring a retro aesthetic and a 3D perspective board.

## Features

- **3D Perspective View**: Play chess on a rendered 3D board with adjustable camera angles.
- **AI Opponent**: Challenge the computer with adjustable difficulty levels (Easy, Medium, Hard).
- **Game Modes**: Play as White (vs AI), Black (vs AI), or Local Multiplayer (Hotseat).
- **Move Assistance**: Visual highlights for legal moves and last moves.
- **Customizable Experience**: Toggle sound, view modes (2D/3D), timer settings (Blitz, Rapid, Classic), and more.
- **Undo/Redo**: Support for undoing moves (implicit in game state management).

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or higher.
- Maven 3.6 or higher.

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/lacavedeharol/java-crt-chess.git
    cd java-crt-chess
    ```

2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

### Running the Game

You can run the game directly from the command line using Maven:

```bash
mvn exec:java -Dexec.mainClass="com.lacavedeharol.chess.Main"
```

Or run the generated JAR file from the `target` directory:

```bash
java -jar target/java-crt-chess-1.0.0-SNAPSHOT.jar
```

## Controls

- **Mouse**: Click to select pieces and move them. Drag and drop is supported.
- **Context Menu**: Access settings, restart game, and toggle options via the on-screen menu.

## Technologies Used

- **Java Swing/AWT**: For window management and custom rendering.
- **Maven**: Dependency management and build automation.

## Author

- **lacavedeharol**
