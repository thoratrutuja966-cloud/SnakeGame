# Snake Arena — Java Mini Project

A beginner-friendly Snake Game built using **Java Swing**. It uses custom Java graphics for the neon jungle theme and includes optional image assets.

## Features
- Neon-jungle start menu
- Instructions screen
- Snake movement using arrow keys
- Food and score system (+10 points per apple)
- Pause and resume (P or Space)
- Sound toggle (system beep)
- Game-over screen and replay
- Best score during the current app session
- No external Java libraries required

## Requirements
- JDK 8 or later
- VS Code (optional) with the Java Extension Pack

## Folder contents
- `SnakeGame.java` — complete Java source code
- `assets/apple.png` — food image
- `assets/snake-banner.png` — menu banner
- `assets/jungle-background.png` — optional background artwork

The program also draws the main game graphics itself, so it remains playable if the image assets are missing.

## Run in VS Code
1. Extract the ZIP file.
2. Open the extracted `SnakeArena_Java_Project` folder in VS Code.
3. Open **Terminal → New Terminal**.
4. Make sure the terminal is in the folder that contains `SnakeGame.java`.
5. Compile:

   ```powershell
   javac SnakeGame.java
   ```

6. Run:

   ```powershell
   java SnakeGame
   ```

## Controls
- Arrow keys: move
- `P` or `Space`: pause/resume
- Click **SOUND: ON/OFF** on the menu to toggle sound
- Click **MENU** during a game to return to the menu

## Troubleshooting
- If `javac` is not recognized, install a JDK and configure `JAVA_HOME`/PATH.
- If `file not found: SnakeGame.java` appears, use `cd` to move into the extracted project folder.
- Keep the `assets` folder next to `SnakeGame.java` for the optional image files.
