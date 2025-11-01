# Memento Pattern

[Back to Home](../README.md)

## Intent

Without violating encapsulation, capture and externalize an object's internal state so that the object can be restored to this state later.

## Explanation

The Memento pattern allows capturing an object's internal state without exposing its internal structure, so the object can be restored to this state later. It helps implement undo/redo functionality, history features, or checkpoints.

## Real-World Example: Text Editor with Undo/Redo Functionality

A text editor that allows users to make changes and then undo/redo those changes. The Memento pattern helps preserve document states for restoration without exposing the document's implementation details.

### Implementation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Step 1: Create the Memento class
public class TextEditorMemento {
    private final String content;
    private final int cursorPosition;
    private final String selectedText;
    private final long timestamp;
    
    public TextEditorMemento(String content, int cursorPosition, String selectedText) {
        this.content = content;
        this.cursorPosition = cursorPosition;
        this.selectedText = selectedText;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Only the Originator class can access state
    protected String getContent() {
        return content;
    }
    
    protected int getCursorPosition() {
        return cursorPosition;
    }
    
    protected String getSelectedText() {
        return selectedText;
    }
    
    // Getters that can be accessed by the Caretaker
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("Memento [timestamp=%tT, content length=%d, cursor=%d]", 
                           timestamp, content.length(), cursorPosition);
    }
}

// Step 2: Create the Originator class
public class TextEditor {
    private StringBuilder content;
    private int cursorPosition;
    private String selectedText;
    private boolean isDirty;
    
    public TextEditor() {
        this.content = new StringBuilder();
        this.cursorPosition = 0;
        this.selectedText = "";
        this.isDirty = false;
    }
    
    public void write(String text) {
        // Insert text at cursor position
        content.insert(cursorPosition, text);
        cursorPosition += text.length();
        selectedText = "";
        isDirty = true;
        
        System.out.println("Wrote: \"" + text + "\"");
    }
    
    public void delete() {
        if (!selectedText.isEmpty()) {
            // Delete the selected text
            int selectionStart = content.indexOf(selectedText, Math.max(0, cursorPosition - selectedText.length()));
            if (selectionStart >= 0) {
                content.delete(selectionStart, selectionStart + selectedText.length());
                cursorPosition = selectionStart;
                System.out.println("Deleted selected text: \"" + selectedText + "\"");
                selectedText = "";
                isDirty = true;
            }
        } else if (cursorPosition > 0) {
            // Delete character before cursor
            content.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            System.out.println("Deleted character at position " + cursorPosition);
            isDirty = true;
        }
    }
    
    public void selectText(int startPosition, int endPosition) {
        if (startPosition >= 0 && endPosition <= content.length() && startPosition < endPosition) {
            selectedText = content.substring(startPosition, endPosition);
            cursorPosition = endPosition;
            System.out.println("Selected text: \"" + selectedText + "\"");
        } else {
            System.out.println("Invalid selection range");
        }
    }
    
    public void setCursorPosition(int position) {
        if (position >= 0 && position <= content.length()) {
            cursorPosition = position;
            selectedText = "";
            System.out.println("Cursor moved to position " + position);
        } else {
            System.out.println("Invalid cursor position");
        }
    }
    
    public void clear() {
        content = new StringBuilder();
        cursorPosition = 0;
        selectedText = "";
        isDirty = true;
        System.out.println("Cleared document");
    }
    
    // Create a memento (save state)
    public TextEditorMemento save() {
        System.out.println("Saving editor state...");
        return new TextEditorMemento(content.toString(), cursorPosition, selectedText);
    }
    
    // Restore from memento
    public void restore(TextEditorMemento memento) {
        content = new StringBuilder(memento.getContent());
        cursorPosition = memento.getCursorPosition();
        selectedText = memento.getSelectedText();
        isDirty = false;
        System.out.println("Restored editor state from " + memento);
    }
    
    public String getContent() {
        return content.toString();
    }
    
    public int getCursorPosition() {
        return cursorPosition;
    }
    
    public String getSelectedText() {
        return selectedText;
    }
    
    public boolean isDirty() {
        return isDirty;
    }
    
    public void resetDirtyFlag() {
        isDirty = false;
    }
    
    public void displayStatus() {
        System.out.println("\n--- Editor Status ---");
        System.out.println("Content: \"" + content + "\"");
        System.out.println("Length: " + content.length() + " characters");
        System.out.println("Cursor position: " + cursorPosition);
        if (!selectedText.isEmpty()) {
            System.out.println("Selected text: \"" + selectedText + "\"");
        }
        System.out.println("Document is " + (isDirty ? "modified" : "unchanged") + " since last save");
        System.out.println("--------------------\n");
    }
}

// Step 3: Create the Caretaker class
public class TextEditorCaretaker {
    private final Stack<TextEditorMemento> undoStack;
    private final Stack<TextEditorMemento> redoStack;
    private final TextEditor editor;
    
    public TextEditorCaretaker(TextEditor editor) {
        this.editor = editor;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    public void save() {
        if (editor.isDirty()) {
            undoStack.push(editor.save());
            editor.resetDirtyFlag();
            redoStack.clear(); // Clear redo stack when new changes are made
        }
    }
    
    public boolean undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return false;
        }
        
        // Save current state to redo stack
        redoStack.push(editor.save());
        
        // If this is the last item in the undo stack, keep it for initial state
        if (undoStack.size() == 1) {
            editor.restore(undoStack.peek());
            System.out.println("Reached initial document state");
            return true;
        }
        
        // Pop the current state and restore the previous one
        undoStack.pop();
        editor.restore(undoStack.peek());
        return true;
    }
    
    public boolean redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo");
            return false;
        }
        
        TextEditorMemento memento = redoStack.pop();
        undoStack.push(memento); // Add to undo stack
        editor.restore(memento);
        return true;
    }
    
    public void createInitialSave() {
        if (undoStack.isEmpty()) {
            undoStack.push(editor.save());
        }
    }
    
    public void displayHistory() {
        System.out.println("\n=== History ===");
        System.out.println("Undo Stack (oldest to newest):");
        List<TextEditorMemento> undoList = new ArrayList<>(undoStack);
        for (int i = 0; i < undoList.size(); i++) {
            TextEditorMemento memento = undoList.get(i);
            System.out.println((i + 1) + ". " + memento);
        }
        
        System.out.println("\nRedo Stack (newest to oldest):");
        List<TextEditorMemento> redoList = new ArrayList<>(redoStack);
        for (int i = 0; i < redoList.size(); i++) {
            TextEditorMemento memento = redoList.get(i);
            System.out.println((i + 1) + ". " + memento);
        }
        System.out.println("=============\n");
    }
}

// Step 4: Create a checkpoint manager for advanced state management
public class CheckpointManager {
    private final TextEditor editor;
    private final Map<String, TextEditorMemento> checkpoints;
    
    public CheckpointManager(TextEditor editor) {
        this.editor = editor;
        this.checkpoints = new HashMap<>();
    }
    
    public void createCheckpoint(String name) {
        TextEditorMemento memento = editor.save();
        checkpoints.put(name, memento);
        System.out.println("Created checkpoint: " + name);
    }
    
    public boolean restoreCheckpoint(String name) {
        TextEditorMemento memento = checkpoints.get(name);
        if (memento != null) {
            editor.restore(memento);
            System.out.println("Restored checkpoint: " + name);
            return true;
        } else {
            System.out.println("Checkpoint not found: " + name);
            return false;
        }
    }
    
    public void listCheckpoints() {
        System.out.println("\n=== Checkpoints ===");
        if (checkpoints.isEmpty()) {
            System.out.println("No checkpoints created");
        } else {
            for (Map.Entry<String, TextEditorMemento> entry : checkpoints.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println("=================\n");
    }
}
```

### Usage Example

```java
public class TextEditorDemo {
    public static void main(String[] args) {
        // Create the text editor (Originator)
        TextEditor editor = new TextEditor();
        
        // Create the caretaker
        TextEditorCaretaker caretaker = new TextEditorCaretaker(editor);
        
        // Create the checkpoint manager
        CheckpointManager checkpoints = new CheckpointManager(editor);
        
        // Initial state
        editor.displayStatus();
        caretaker.createInitialSave();
        
        // Start writing some text
        editor.write("Hello, ");
        caretaker.save();
        editor.displayStatus();
        
        editor.write("world!");
        caretaker.save();
        editor.displayStatus();
        
        // Create a checkpoint
        checkpoints.createCheckpoint("basic greeting");
        
        // Add more text
        editor.setCursorPosition(editor.getContent().length());
        editor.write(" Welcome to the Memento Pattern.");
        caretaker.save();
        editor.displayStatus();
        
        // Select and modify text
        editor.selectText(0, 5);
        editor.write("Hi");
        caretaker.save();
        editor.displayStatus();
        
        // Display history
        caretaker.displayHistory();
        
        // Perform some undo operations
        System.out.println("Performing undo operation...");
        caretaker.undo();
        editor.displayStatus();
        
        System.out.println("Performing another undo operation...");
        caretaker.undo();
        editor.displayStatus();
        
        // Perform a redo operation
        System.out.println("Performing redo operation...");
        caretaker.redo();
        editor.displayStatus();
        
        // Add some more content and create another checkpoint
        editor.setCursorPosition(editor.getContent().length());
        editor.write(" This is an example of the Memento design pattern.");
        caretaker.save();
        editor.displayStatus();
        
        checkpoints.createCheckpoint("full explanation");
        
        // Show all checkpoints
        checkpoints.listCheckpoints();
        
        // Restore the first checkpoint
        System.out.println("Restoring to 'basic greeting' checkpoint...");
        checkpoints.restoreCheckpoint("basic greeting");
        editor.displayStatus();
        
        // Add different content from this restored state
        editor.setCursorPosition(editor.getContent().length());
        editor.write(" Let's go in a different direction.");
        caretaker.save();
        editor.displayStatus();
        
        // Go back to the full explanation
        System.out.println("Restoring to 'full explanation' checkpoint...");
        checkpoints.restoreCheckpoint("full explanation");
        editor.displayStatus();
        
        // Final display of history and checkpoints
        caretaker.displayHistory();
        checkpoints.listCheckpoints();
    }
}
```

## Another Example: Game Save States

Here's another implementation of the Memento pattern for a game's save state system:

```java
// Memento
class GameMemento {
    private final int level;
    private final int score;
    private final int lives;
    private final Map<String, Integer> inventory;
    private final String playerPosition;
    private final long timestamp;
    
    public GameMemento(int level, int score, int lives, Map<String, Integer> inventory, String playerPosition) {
        this.level = level;
        this.score = score;
        this.lives = lives;
        this.inventory = new HashMap<>(inventory);
        this.playerPosition = playerPosition;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Methods accessible only to the originator
    protected int getLevel() {
        return level;
    }
    
    protected int getScore() {
        return score;
    }
    
    protected int getLives() {
        return lives;
    }
    
    protected Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }
    
    protected String getPlayerPosition() {
        return playerPosition;
    }
    
    // Metadata accessible to the caretaker
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Save [" + formatter.format(new Date(timestamp)) + 
               "] Level: " + level + ", Score: " + score + 
               ", Lives: " + lives + ", Position: " + playerPosition;
    }
}

// Originator
class Game {
    private int level;
    private int score;
    private int lives;
    private Map<String, Integer> inventory;
    private String playerPosition;
    
    public Game() {
        this.level = 1;
        this.score = 0;
        this.lives = 3;
        this.inventory = new HashMap<>();
        this.playerPosition = "start";
    }
    
    public void play() {
        // Simulate game progression
        level++;
        score += 1000;
        playerPosition = "level " + level + " entrance";
        System.out.println("Advanced to level " + level + ", score is now " + score);
    }
    
    public void collectItem(String item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
        score += 100;
        System.out.println("Collected item: " + item + ", inventory now has " + inventory.get(item) + " of them");
    }
    
    public void useItem(String item) {
        if (inventory.containsKey(item) && inventory.get(item) > 0) {
            inventory.put(item, inventory.get(item) - 1);
            System.out.println("Used item: " + item + ", inventory now has " + inventory.get(item) + " of them");
        } else {
            System.out.println("Cannot use item: " + item + " - not in inventory");
        }
    }
    
    public void takeDamage() {
        lives--;
        System.out.println("Player took damage, lives remaining: " + lives);
        if (lives <= 0) {
            System.out.println("Game Over!");
        }
    }
    
    public void movePlayer(String position) {
        playerPosition = position;
        System.out.println("Player moved to: " + position);
    }
    
    // Create a save state (memento)
    public GameMemento save() {
        System.out.println("Saving game state...");
        return new GameMemento(level, score, lives, inventory, playerPosition);
    }
    
    // Restore from a save state (memento)
    public void restore(GameMemento memento) {
        this.level = memento.getLevel();
        this.score = memento.getScore();
        this.lives = memento.getLives();
        this.inventory = memento.getInventory();
        this.playerPosition = memento.getPlayerPosition();
        System.out.println("Game state restored from save");
    }
    
    public void displayStatus() {
        System.out.println("\n=== Game Status ===");
        System.out.println("Level: " + level);
        System.out.println("Score: " + score);
        System.out.println("Lives: " + lives);
        System.out.println("Position: " + playerPosition);
        
        System.out.println("Inventory:");
        if (inventory.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Map.Entry<String, Integer> item : inventory.entrySet()) {
                System.out.println("  - " + item.getKey() + ": " + item.getValue());
            }
        }
        System.out.println("==================\n");
    }
}

// Caretaker
class GameSaveManager {
    private Map<String, GameMemento> savedGames = new HashMap<>();
    
    public void saveGame(String saveName, GameMemento memento) {
        savedGames.put(saveName, memento);
        System.out.println("Game saved as: " + saveName);
    }
    
    public GameMemento loadGame(String saveName) {
        GameMemento memento = savedGames.get(saveName);
        if (memento != null) {
            System.out.println("Loading save: " + saveName);
            return memento;
        }
        System.out.println("Save file not found: " + saveName);
        return null;
    }
    
    public void listSaveFiles() {
        System.out.println("\n=== Available Save Files ===");
        if (savedGames.isEmpty()) {
            System.out.println("No save files found");
        } else {
            for (Map.Entry<String, GameMemento> entry : savedGames.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println("==========================\n");
    }
}
```

## Benefits

1. **Preserves encapsulation**: The originator's internal state remains private
2. **Simplifies originator**: The originator doesn't need to manage state history
3. **Support for incremental changes**: Multiple mementos can capture incremental changes
4. **Clean interfaces**: No need to expose complex internal state through getters/setters
5. **Single Responsibility Principle**: Separation of concerns between originator, memento and caretaker

## Considerations

1. **Memory usage**: Storing many mementos can consume significant memory
2. **Potential performance cost**: Creating deep copies of large objects can be expensive
3. **Version compatibility**: Handling changes to the originator class structure over time
4. **Encapsulation trade-offs**: Balancing access to memento state versus true encapsulation

## When to Use

- When you need to provide undo functionality
- When you need to create snapshots of an object's state
- When direct access to an object's fields would violate encapsulation
- When you need to restore an object to a previous state
