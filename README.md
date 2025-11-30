# Tetris Game - COMP2042 Coursework

---

## GitHub Repository
**Link:** (https://github.com/Zeniel06/CW2025)

---

## Compilation Instructions

### Option 1: Command Line (Maven Wrapper)

#### Prerequisites
- **JDK 23** which supports Java 23 (`java --version` should report 23.x)
- **Maven** has already been bundled together in the wrapper so no separate installation is required
- **Internet access** is required for Maven to test the dependencies on the first build

#### Setup Steps

1. **Open the terminal at the project root**

2. **Verify the toolchain**

   **Windows (Command Prompt/PowerShell):**
   ```cmd
   mvnw.cmd --version
   ```

   **macOS:**
   ```bash
   ./mvnw --version
   ```
   - The output should have listed Java version 23 and Apache Maven wrapper

3. **Compile the project**

   **Windows (Command Prompt/PowerShell):**
   ```cmd
   mvnw.cmd clean package
   ```

   **macOS:**
   ```bash
   ./mvnw clean package
   ```
   - Maven will resolve the dependencies and compile the JavaFX resources

4. **Run the application directly using Maven**

   **Windows (Command Prompt/PowerShell):**
   ```cmd
   mvnw.cmd clean javafx:run
   ```

   **macOS:**
   ```bash
   ./mvnw clean javafx:run
   ```
   - The JavaFX Maven plugin will launch `com.comp2042.ui.Main` and build the required JavaFX modules automatically

---

### Option 2: Using IntelliJ IDEA

1. **Open the project in IntelliJ IDE**
   - Make sure that IntelliJ is configured with JDK 23

2. **Maven imports the dependencies**
   - IntelliJ automatically detects the `pom.xml`

3. **Run the JavaFX from the built-in Maven tool window**
   - Open the Maven tool window towards the left side of the screen
   - Click on Plugins → javafx
   - Double-click javafx:run

---

##  Implemented Features

###  Core Gameplay Features

#### **1. Core Gameplay Loop**
- GameController has a standard 10 by 20 visible playfield with left and right movement and brick rotation that are mapped to the keyboard controls, ensuring smooth gameplay.

#### **2. Brick Rotation and Randomization**
- Every new brick that spawns comes from RandomBrickGenerator and BrickRotator preserves each orientation. ViewData then feeds the current brick and next 4 upcoming bricks to the preview panels.

#### **3. Ghost Blocks and Hard-Drop**
- `calculateGhostPosition` creates a live outline of where the brick is going to land
- Pressing **SPACE** snaps the brick into place and locks it
- Distance-based score is also taken into account before the next brick spawns

#### **4. Hold Mechanic (SHIFT)**
- Pressing **SHIFT** triggers `holdCurrentBrick` which only allows one swap per drop cycle
- When the hold slot is empty the current brick is stored and replaced with a fresh random brick
- Otherwise the held and active bricks swap immediately
- Updates the hold panel so that the stored brick is always visible

#### **5. Score and Level System**
- The Score class tracks the ongoing score, lines, and levels to the HUD
- Raises the fall speed of the bricks as the level increases which is based on the number of lines cleared

#### **6. Dynamic Video Background**
- Supports video backgrounds that loop continuously throughout the game
- Video plays at 0.5x speed (half speed) for a smooth, ambient effect
- Automatically scales with window resizing to fill the entire screen
- Video audio is muted to prevent interference with background music
- Falls back gracefully to dark background if no video file is present
- Place `background_video.mp4` in `src/main/resources/` to enable

#### **7. Background Music**
- Background music plays continuously during gameplay
- Automatically pauses when the game is paused
- Stops when returning to main menu or on game over
- Volume set to 15% for non-intrusive ambient audio

#### **8. Resizable Window with Dynamic Scaling**
- Window can be freely resized by dragging edges or corners
- All game elements (board, bricks, UI panels, menus, video background) scale proportionally in real-time
- Maintains layout positions - elements stay in their original spots while scaling
- Minimum window size of 500x540 ensures playability with proper margins
- Initial window size set to 650x740 for comfortable gameplay
- Background video fills entire window and scales accordingly
- Game board centered with equal 20px margins on all sides (top, bottom, left, right)
- NEXT panel positioned on left side, HOLD panel on right side (modern Tetris layout)

#### **9. UI Redesign**
- GuiController draws the playfield gridlines as well as the hold panel, next panel and score panel updating them respectively after every move
- Modern Tetris layout with NEXT panel on the left side showing 4 upcoming bricks in a single preview panel
- HOLD and STATS panels positioned on the right side of the game board
- Centered game board with equal spacing on all edges for a balanced, professional appearance
- NotificationPanel also animates bonuses whenever multiple lines are cleared

#### **10. Main Menu & Pause Flow**
- On startup the game boots into MainMenuPanel
- Pressing **Start Game** causes GameController to initialise the board and begin a fresh session immediately
- Pressing **Escape** opens the pause menu with resume and back to main menu options
- GameOverPanel triggers when the danger line has been breached, displaying a centered overlay with "GAME OVER" text, final score, and restart instructions
- The game board dims and active bricks are hidden when game over occurs, providing clear visual feedback
- Players can press **"N"** to restart immediately

---

##  Controls

| Key | Action |
|-----|--------|
| **←** | Move left |
| **→** | Move right |
| **↓** | Move down (soft drop) |
| **↑** | Rotate piece |
| **SPACE** | Hard drop (instant drop) |
| **SHIFT** | Hold current piece |
| **ESCAPE** | Pause/Resume |
| **"N" Key** | Reset/Restart Game |

---

##  New Java Classes Added

### 1. MainMenuPanel
**Path:** `src/main/java/com/comp2042/ui/MainMenuPanel.java`
- Introduces a dedicated JavaFX BorderPane for the game's startup screen
- Displays the branded "Tetris" header with a centralised "Start Game" button
- Gives players a polished entry point

### 2. PauseMenuPanel
**Path:** `src/main/java/com/comp2042/ui/PauseMenuPanel.java`
- Adds a pop-up pause menu when the escape key is pressed
- Uses JavaFX BorderPane that dims the background
- Shows "Resume" and "Main Menu" buttons

---

##  Modified Java Classes

### 1. GameController
**Location:** `src/main/java/com/comp2042/controller/GameController.java`
- The game board initialization is delayed until the user presses the "Start Game" button from the main menu panel
- Builds the UI for the score, level, and line bindings that creates the enhanced HUD
- Centralises the brick-lock and row-clearing
- Sends game-over states back to view and gives additional points for hard-drops
- These improvements help maintain the flow of the game while supporting the multitude of mechanics implemented

### 2. Board
**Location:** `src/main/java/com/comp2042/model/Board.java`
- Introduces methods for hard-drop support, danger-line detection, hold piece swapping and retrieving held bricks
- Makes the game much more intuitive and interactive
- Allows GameController and UI to call these features directly without any extra work

### 3. SimpleBoard
**Location:** `src/main/java/com/comp2042/model/SimpleBoard.java`
- Movement is refactored into a reusable helper which calculates ghost landing rows
- Implements hard-drops and maintains the held piece state with the "one hold per piece" rule
- Checks for whether or not the game over condition has been reached
- Packages the ghost and hold data into ViewData to ensure lightweight code around the board state
- Serves as the foundation for all new gameplay features while preventing collisions

### 4. GuiController
**Location:** `src/main/java/com/comp2042/ui/GuiController.java`
- Expanded to handle the new inputs used for controls (space for hard drop, shift for hold, escape for pause)
- Lazy-loads the game and manages the main and pause menu
- Renders the ghost-blocks, hold preview, and next 4 bricks preview in a single unified panel
- Shows the live statistic scores
- Adjusts the fall speed whenever the level changes accordingly
- Implements background music system with JavaFX MediaPlayer that plays during gameplay and responds to game state changes (pause/resume/stop)
- Implements dynamic video background system using MediaPlayer and MediaView that loops continuously at 0.5x speed with muted audio
- Video background scales automatically with window size and stays behind all UI elements
- Falls back gracefully to default background if video file is not present
- Manages game over visual effects by dimming the board (30% opacity) and hiding active/ghost bricks for clear game state feedback
- Retrieves and passes the final score to the GameOverPanel for display
- Restores full visibility and opacity when restarting games
- Implements dynamic window scaling system that maintains proportions and layout positions when the window is resized
- Manages scene and root pane with transparent backgrounds to allow video to show through

### 5. GameOverPanel
**Location:** `src/main/java/com/comp2042/ui/GameOverPanel.java`
- Displays a centered, full-screen overlay (400x500px) when the game ends
- Shows "GAME OVER" text in white Arial font (56px bold)
- Displays the final score in gold digital font (32px) for visual emphasis
- Shows restart instructions ("Press 'N' to Restart") in gray below the score
- Includes `setScore(int)` method to dynamically update the displayed score
- Uses a semi-transparent dark background (85% opacity) for text visibility

### 6. Main
**Location:** `src/main/java/com/comp2042/ui/Main.java`
- Displays the main menu screen immediately on startup
- Enables window resizing with a minimum size constraint (500x540)
- Sets initial window size to 650x740 for optimal viewing
- Initializes the scaling system in GuiController to support dynamic resizing

### 7. InputEventListener
**Location:** `src/main/java/com/comp2042/event/InputEventListener.java`
- Adds methods for hard drops and hold events so that the UI can trigger these actions without any duplicates in the code
- Keeps the event helper in synchronisation with the control scheme
- Ensures smooth communications between the UI and game logic

### 8. EventType
**Location:** `src/main/java/com/comp2042/event/EventType.java`
- Added new HARD_DROP and HOLD constants
- Gives the program clear names for the new actions
- The enum lists every move in one place, so the controller can now easily identify what to do for each given input

### 9. Score
**Location:** `src/main/java/com/comp2042/model/Score.java`
- Implemented an addlines helper, visible level and lines counter
- Automated level progression system for every 3 lines that are cleared
- All three of these properties are cleared every time the reset method is triggered
- Enables the game logic to respond to the changes in game state easily

### 10. ViewData
**Location:** `src/main/java/com/comp2042/data/ViewData.java`
- Packages the ghost-block landing position alongside held brick matrices for the hold panel
- Includes the current brick data and next 4 upcoming bricks for the preview panel
- Returns fresh copies of the array to prevent the code from accidentally changing the game state while rendering

### 11. BrickRotator
**Location:** `src/main/java/com/comp2042/model/BrickRotator.java`
- Provides a `getBrick()` accessor so SimpleBoard can retrieve the live brick needed for hold/swapping operations
- Ensures efficient handling of hold swaps while the rotator stays strictly responsible for the current brick

### 12. MatrixOperations
**Location:** `src/main/java/com/comp2042/util/MatrixOperations.java`
- Repositioned the shared collision utility into the new util package
- Refines the checkOutOfBound helper for clarity
- Continues to support the ghost calculation loop and promotes consistent reuse across the reorganised packages

### 13. RandomBrickGenerator
**Location:** `src/main/java/com/comp2042/model/bricks/RandomBrickGenerator.java`
- Maintains a queue of 5 bricks (1 current + 4 for preview) for smooth gameplay
- Adds `getNext4Bricks()` method to return the next 4 upcoming bricks without advancing the queue
- Ensures the preview panel always displays the correct upcoming pieces
- Uses `getRandomBrick()` helper to seed and refresh the queue consistently

---

## Unexpected Problems & Solutions

### 1. Main-Menu Transition
**Problem:**
- It was hard to get the main menu to work right due to hiding the gameboard until "Start Game" was pressed
- Often left behind UI nodes from previous runs when this was done

**Solution:**
- Added a `setGameElementsVisible(boolean)` helper to GuiController to fix the leftover nodes
- When the main menu comes up, the code runs `setGameElementsVisible(false)`, which hides all of the playfield panels and makes them unmanaged
- It goes back to true before the game starts when "Start Game" runs

### 2. Ghost Brick Positioning
**Problem:**
- When bricks were rotated or moved, the ghost blocks (which shows where the piece will land) appeared at the wrong places

**Solution:**
- Created a `calculateGhostPosition()` method that works exactly like the drop logic
- This ensures that the ghost block preview will always show where the brick will actually land

### 3. Game-Board Positioning
**Problem:**
- Tried to move the bordered playfield in the FXML, but the falling bricks and their ghost block preview always ended up being misplaced and misaligned
- Their layout math still thought that they were in the same place even though the layout had been modified

**Solution:**
- Instead of rewriting the positions of the bricks, put the border and board back where they were so that the blocks would line up again

### 4. UI State Management
**Problem:**
- Challenge to keep up with the different game states (paused, game over, main menu) and to prevent wrong keyboard inputs at the same time

**Solution:**
- Utilized boolean flags (`isPause`, `isGameOver`, `isGameInitialized`)
- Made sure to check them carefully before processing any keyboard input or game logic so that actions only happen in valid states
- This method stopped problems like bricks still moving when the game is paused or menu interactions getting in the way of active gameplay

### 5. Copying Grid Data Problems
**Problem:**
- When using grids to keep track of the game board and brick shapes, encountered an issue where changing one thing would lead to another changing by accident
- For example, when testing to see if a brick would move to a new spot, it would sometimes mess up the shape of the brick by changing it to another

**Solution:**
- Made methods in `MatrixOperations.java` to make full copies of the grids before testing any moves
- This way, checking to see if a move is possible doesn't change how the bricks look or break the game
