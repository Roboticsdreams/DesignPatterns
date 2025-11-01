# Command Pattern

[Back to Home](../README.md)

## Intent

Encapsulate a request as an object, thereby allowing for parameterization of clients with different requests, queuing of requests, and logging of the requests. Also supports undoable operations.

## Explanation

The Command pattern turns a request into a stand-alone object that contains all information about the request. This transformation allows you to pass requests as method arguments, delay or queue a request's execution, and support undoable operations.

## Real-World Example: Smart Home Automation

In a smart home system, various commands control different devices (lights, thermostat, entertainment system, etc.). Using the Command pattern allows creating a unified remote control interface that can operate all devices while supporting features like macro commands and undo functionality.

### Implementation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Step 1: Create the Command interface
public interface Command {
    void execute();
    void undo();
    String getDescription();
}

// Step 2: Create Receiver classes - the actual smart home devices
public class Light {
    private String location;
    private boolean isOn = false;
    private int brightness = 0; // 0-100%
    
    public Light(String location) {
        this.location = location;
    }
    
    public void turnOn() {
        isOn = true;
        System.out.println(location + " light is now ON");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println(location + " light is now OFF");
    }
    
    public void setBrightness(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
        isOn = (this.brightness > 0);
        System.out.println(location + " light brightness set to " + this.brightness + "%");
    }
    
    public boolean isOn() {
        return isOn;
    }
    
    public int getBrightness() {
        return brightness;
    }
    
    @Override
    public String toString() {
        return location + " Light [" + (isOn ? "ON" : "OFF") + 
               (isOn ? ", brightness: " + brightness + "%" : "") + "]";
    }
}

public class Thermostat {
    private String location;
    private int temperature;
    private String mode; // heat, cool, off
    
    public Thermostat(String location) {
        this.location = location;
        this.temperature = 72; // Default temperature
        this.mode = "off";
    }
    
    public void setTemperature(int temperature) {
        this.temperature = temperature;
        System.out.println(location + " thermostat temperature set to " + temperature + "°F");
    }
    
    public void setMode(String mode) {
        if ("heat".equals(mode) || "cool".equals(mode) || "off".equals(mode)) {
            this.mode = mode;
            System.out.println(location + " thermostat mode set to " + mode.toUpperCase());
        } else {
            System.out.println("Invalid mode: " + mode);
        }
    }
    
    public int getTemperature() {
        return temperature;
    }
    
    public String getMode() {
        return mode;
    }
    
    @Override
    public String toString() {
        return location + " Thermostat [Mode: " + mode.toUpperCase() + 
               ", Temperature: " + temperature + "°F]";
    }
}

public class MusicPlayer {
    private boolean isOn = false;
    private String currentTrack = "";
    private int volume = 0; // 0-100%
    
    public void turnOn() {
        isOn = true;
        System.out.println("Music player is now ON");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println("Music player is now OFF");
        currentTrack = "";
    }
    
    public void setVolume(int volume) {
        if (!isOn) {
            System.out.println("Cannot set volume: Music player is off");
            return;
        }
        this.volume = Math.max(0, Math.min(100, volume));
        System.out.println("Music player volume set to " + this.volume + "%");
    }
    
    public void play(String track) {
        if (!isOn) {
            System.out.println("Cannot play music: Music player is off");
            return;
        }
        this.currentTrack = track;
        System.out.println("Now playing: " + track);
    }
    
    public void stop() {
        if (!isOn) {
            System.out.println("Cannot stop music: Music player is off");
            return;
        }
        if (!currentTrack.isEmpty()) {
            System.out.println("Stopped playing: " + currentTrack);
            currentTrack = "";
        } else {
            System.out.println("No track is currently playing");
        }
    }
    
    public boolean isOn() {
        return isOn;
    }
    
    public String getCurrentTrack() {
        return currentTrack;
    }
    
    public int getVolume() {
        return volume;
    }
    
    @Override
    public String toString() {
        return "Music Player [" + (isOn ? "ON" : "OFF") + 
               (isOn ? ", Volume: " + volume + "%" : "") + 
               (isOn && !currentTrack.isEmpty() ? ", Playing: " + currentTrack : "") + "]";
    }
}

// Step 3: Create Concrete Command classes
public class LightOnCommand implements Command {
    private Light light;
    private int previousBrightness;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        previousBrightness = light.getBrightness();
        light.turnOn();
    }
    
    @Override
    public void undo() {
        if (previousBrightness == 0) {
            light.turnOff();
        } else {
            light.setBrightness(previousBrightness);
        }
    }
    
    @Override
    public String getDescription() {
        return "Turn " + light.toString().split(" ")[0] + " light on";
    }
}

public class LightOffCommand implements Command {
    private Light light;
    private int previousBrightness;
    
    public LightOffCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        previousBrightness = light.getBrightness();
        light.turnOff();
    }
    
    @Override
    public void undo() {
        if (previousBrightness > 0) {
            light.turnOn();
            light.setBrightness(previousBrightness);
        }
    }
    
    @Override
    public String getDescription() {
        return "Turn " + light.toString().split(" ")[0] + " light off";
    }
}

public class LightDimCommand implements Command {
    private Light light;
    private int previousBrightness;
    private int targetBrightness;
    
    public LightDimCommand(Light light, int targetBrightness) {
        this.light = light;
        this.targetBrightness = targetBrightness;
    }
    
    @Override
    public void execute() {
        previousBrightness = light.getBrightness();
        light.setBrightness(targetBrightness);
    }
    
    @Override
    public void undo() {
        light.setBrightness(previousBrightness);
    }
    
    @Override
    public String getDescription() {
        return "Set " + light.toString().split(" ")[0] + " light brightness to " + targetBrightness + "%";
    }
}

public class ThermostatSetCommand implements Command {
    private Thermostat thermostat;
    private int previousTemperature;
    private int targetTemperature;
    
    public ThermostatSetCommand(Thermostat thermostat, int targetTemperature) {
        this.thermostat = thermostat;
        this.targetTemperature = targetTemperature;
    }
    
    @Override
    public void execute() {
        previousTemperature = thermostat.getTemperature();
        thermostat.setTemperature(targetTemperature);
    }
    
    @Override
    public void undo() {
        thermostat.setTemperature(previousTemperature);
    }
    
    @Override
    public String getDescription() {
        return "Set " + thermostat.toString().split(" ")[0] + " thermostat to " + targetTemperature + "°F";
    }
}

public class ThermostatModeCommand implements Command {
    private Thermostat thermostat;
    private String previousMode;
    private String targetMode;
    
    public ThermostatModeCommand(Thermostat thermostat, String targetMode) {
        this.thermostat = thermostat;
        this.targetMode = targetMode;
    }
    
    @Override
    public void execute() {
        previousMode = thermostat.getMode();
        thermostat.setMode(targetMode);
    }
    
    @Override
    public void undo() {
        thermostat.setMode(previousMode);
    }
    
    @Override
    public String getDescription() {
        return "Set " + thermostat.toString().split(" ")[0] + " thermostat mode to " + targetMode.toUpperCase();
    }
}

public class MusicPlayerOnCommand implements Command {
    private MusicPlayer musicPlayer;
    private boolean wasPlaying;
    private String previousTrack;
    private int previousVolume;
    
    public MusicPlayerOnCommand(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }
    
    @Override
    public void execute() {
        wasPlaying = !musicPlayer.getCurrentTrack().isEmpty();
        previousTrack = musicPlayer.getCurrentTrack();
        previousVolume = musicPlayer.getVolume();
        musicPlayer.turnOn();
    }
    
    @Override
    public void undo() {
        musicPlayer.turnOff();
    }
    
    @Override
    public String getDescription() {
        return "Turn music player on";
    }
}

public class MusicPlayerOffCommand implements Command {
    private MusicPlayer musicPlayer;
    private boolean wasPlaying;
    private String previousTrack;
    private int previousVolume;
    
    public MusicPlayerOffCommand(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }
    
    @Override
    public void execute() {
        wasPlaying = !musicPlayer.getCurrentTrack().isEmpty();
        previousTrack = musicPlayer.getCurrentTrack();
        previousVolume = musicPlayer.getVolume();
        musicPlayer.turnOff();
    }
    
    @Override
    public void undo() {
        musicPlayer.turnOn();
        musicPlayer.setVolume(previousVolume);
        if (wasPlaying && !previousTrack.isEmpty()) {
            musicPlayer.play(previousTrack);
        }
    }
    
    @Override
    public String getDescription() {
        return "Turn music player off";
    }
}

public class MusicPlayerPlayCommand implements Command {
    private MusicPlayer musicPlayer;
    private String previousTrack;
    private String track;
    private boolean wasOn;
    
    public MusicPlayerPlayCommand(MusicPlayer musicPlayer, String track) {
        this.musicPlayer = musicPlayer;
        this.track = track;
    }
    
    @Override
    public void execute() {
        wasOn = musicPlayer.isOn();
        previousTrack = musicPlayer.getCurrentTrack();
        
        if (!wasOn) {
            musicPlayer.turnOn();
        }
        
        musicPlayer.play(track);
    }
    
    @Override
    public void undo() {
        if (previousTrack.isEmpty()) {
            musicPlayer.stop();
        } else {
            musicPlayer.play(previousTrack);
        }
        
        if (!wasOn) {
            musicPlayer.turnOff();
        }
    }
    
    @Override
    public String getDescription() {
        return "Play music track: " + track;
    }
}

// Step 4: Create a Macro Command for executing multiple commands at once
public class MacroCommand implements Command {
    private List<Command> commands;
    private String name;
    
    public MacroCommand(String name) {
        this.name = name;
        this.commands = new ArrayList<>();
    }
    
    public void addCommand(Command command) {
        commands.add(command);
    }
    
    @Override
    public void execute() {
        System.out.println("Executing Macro: " + name);
        for (Command command : commands) {
            command.execute();
        }
    }
    
    @Override
    public void undo() {
        System.out.println("Undoing Macro: " + name);
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
    
    @Override
    public String getDescription() {
        return "Macro: " + name + " (" + commands.size() + " commands)";
    }
}

// Step 5: Create a No Operation Command
public class NoOpCommand implements Command {
    @Override
    public void execute() {
        // Do nothing
    }
    
    @Override
    public void undo() {
        // Do nothing
    }
    
    @Override
    public String getDescription() {
        return "No operation";
    }
}

// Step 6: Create the Invoker - Remote Control
public class RemoteControl {
    private Command[] onCommands;
    private Command[] offCommands;
    private Stack<Command> commandHistory;
    
    public RemoteControl(int slots) {
        onCommands = new Command[slots];
        offCommands = new Command[slots];
        commandHistory = new Stack<>();
        
        // Initialize with NoOpCommand
        Command noOp = new NoOpCommand();
        for (int i = 0; i < slots; i++) {
            onCommands[i] = noOp;
            offCommands[i] = noOp;
        }
    }
    
    public void setCommand(int slot, Command onCommand, Command offCommand) {
        onCommands[slot] = onCommand;
        offCommands[slot] = offCommand;
    }
    
    public void onButtonPressed(int slot) {
        if (slot >= 0 && slot < onCommands.length) {
            onCommands[slot].execute();
            commandHistory.push(onCommands[slot]);
        } else {
            System.out.println("Invalid slot: " + slot);
        }
    }
    
    public void offButtonPressed(int slot) {
        if (slot >= 0 && slot < offCommands.length) {
            offCommands[slot].execute();
            commandHistory.push(offCommands[slot]);
        } else {
            System.out.println("Invalid slot: " + slot);
        }
    }
    
    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }
    
    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            System.out.println("Undoing: " + lastCommand.getDescription());
            lastCommand.undo();
        } else {
            System.out.println("No commands to undo");
        }
    }
    
    public void printStatus() {
        System.out.println("\n------ Remote Control Status ------");
        for (int i = 0; i < onCommands.length; i++) {
            System.out.println("[Slot " + i + "] " + 
                             "ON: " + onCommands[i].getDescription() + "   " +
                             "OFF: " + offCommands[i].getDescription());
        }
        System.out.println("----------------------------------\n");
    }
}
```

### Usage Example

```java
public class SmartHomeDemo {
    public static void main(String[] args) {
        // Create the receivers (smart home devices)
        Light livingRoomLight = new Light("Living Room");
        Light kitchenLight = new Light("Kitchen");
        Light bedroomLight = new Light("Bedroom");
        Thermostat homeThermostat = new Thermostat("Home");
        MusicPlayer musicPlayer = new MusicPlayer();
        
        // Create the remote control with 7 slots
        RemoteControl remote = new RemoteControl(7);
        
        // Create commands for living room light
        Command livingRoomLightOn = new LightOnCommand(livingRoomLight);
        Command livingRoomLightOff = new LightOffCommand(livingRoomLight);
        Command livingRoomLightDim = new LightDimCommand(livingRoomLight, 50);
        
        // Create commands for kitchen light
        Command kitchenLightOn = new LightOnCommand(kitchenLight);
        Command kitchenLightOff = new LightOffCommand(kitchenLight);
        
        // Create commands for bedroom light
        Command bedroomLightOn = new LightOnCommand(bedroomLight);
        Command bedroomLightOff = new LightOffCommand(bedroomLight);
        
        // Create commands for thermostat
        Command thermostatHeat = new ThermostatModeCommand(homeThermostat, "heat");
        Command thermostatCool = new ThermostatModeCommand(homeThermostat, "cool");
        Command thermostatOff = new ThermostatModeCommand(homeThermostat, "off");
        Command thermostatSet72 = new ThermostatSetCommand(homeThermostat, 72);
        Command thermostatSet68 = new ThermostatSetCommand(homeThermostat, 68);
        
        // Create commands for music player
        Command musicPlayerOn = new MusicPlayerOnCommand(musicPlayer);
        Command musicPlayerOff = new MusicPlayerOffCommand(musicPlayer);
        Command playJazz = new MusicPlayerPlayCommand(musicPlayer, "Jazz Playlist");
        
        // Set up the remote control slots
        remote.setCommand(0, livingRoomLightOn, livingRoomLightOff);
        remote.setCommand(1, kitchenLightOn, kitchenLightOff);
        remote.setCommand(2, bedroomLightOn, bedroomLightOff);
        remote.setCommand(3, thermostatHeat, thermostatOff);
        remote.setCommand(4, thermostatSet72, thermostatSet68);
        remote.setCommand(5, musicPlayerOn, musicPlayerOff);
        
        // Create a macro command for "Movie Mode"
        MacroCommand movieMode = new MacroCommand("Movie Mode");
        movieMode.addCommand(livingRoomLightDim);
        movieMode.addCommand(kitchenLightOff);
        movieMode.addCommand(thermostatSet72);
        movieMode.addCommand(musicPlayerOn);
        movieMode.addCommand(playJazz);
        
        // Display remote control status
        remote.printStatus();
        
        // Use the remote control
        System.out.println("=== Using Remote Control ===");
        System.out.println("Initial state of devices:");
        System.out.println(livingRoomLight);
        System.out.println(kitchenLight);
        System.out.println(homeThermostat);
        System.out.println(musicPlayer);
        
        System.out.println("\nTurning on living room light (slot 0):");
        remote.onButtonPressed(0);
        
        System.out.println("\nTurning on kitchen light (slot 1):");
        remote.onButtonPressed(1);
        
        System.out.println("\nSetting thermostat to heat mode (slot 3):");
        remote.onButtonPressed(3);
        
        System.out.println("\nSetting thermostat to 72°F (slot 4):");
        remote.onButtonPressed(4);
        
        System.out.println("\nCurrent state of devices:");
        System.out.println(livingRoomLight);
        System.out.println(kitchenLight);
        System.out.println(homeThermostat);
        System.out.println(musicPlayer);
        
        System.out.println("\n=== Undo Functionality ===");
        System.out.println("Undoing last command (set thermostat to 72°F):");
        remote.undoLastCommand();
        
        System.out.println("\nUndoing another command (set thermostat to heat mode):");
        remote.undoLastCommand();
        
        System.out.println("\nCurrent state after undos:");
        System.out.println(livingRoomLight);
        System.out.println(kitchenLight);
        System.out.println(homeThermostat);
        System.out.println(musicPlayer);
        
        System.out.println("\n=== Macro Command ===");
        System.out.println("Activating Movie Mode macro:");
        remote.executeCommand(movieMode);
        
        System.out.println("\nCurrent state after Movie Mode:");
        System.out.println(livingRoomLight);
        System.out.println(kitchenLight);
        System.out.println(homeThermostat);
        System.out.println(musicPlayer);
        
        System.out.println("\nUndoing Movie Mode macro:");
        remote.undoLastCommand();
        
        System.out.println("\nFinal state of devices:");
        System.out.println(livingRoomLight);
        System.out.println(kitchenLight);
        System.out.println(homeThermostat);
        System.out.println(musicPlayer);
    }
}
```

## Another Example: Text Editor Commands

Here's another implementation showing the Command pattern in a simple text editor:

```java
// Receiver
class TextDocument {
    private StringBuilder content = new StringBuilder();
    private int cursorPosition = 0;
    
    public void insert(String text) {
        content.insert(cursorPosition, text);
        cursorPosition += text.length();
        System.out.println("Inserted: " + text);
    }
    
    public void delete(int length) {
        if (cursorPosition >= length) {
            content.delete(cursorPosition - length, cursorPosition);
            cursorPosition -= length;
            System.out.println("Deleted " + length + " characters");
        } else {
            System.out.println("Cannot delete: Not enough characters before cursor");
        }
    }
    
    public void setCursorPosition(int position) {
        if (position >= 0 && position <= content.length()) {
            cursorPosition = position;
            System.out.println("Cursor moved to position " + position);
        } else {
            System.out.println("Invalid cursor position: " + position);
        }
    }
    
    public int getCursorPosition() {
        return cursorPosition;
    }
    
    public String getContent() {
        return content.toString();
    }
    
    public void displayDocument() {
        System.out.println("\n--- Document Content ---");
        System.out.println(content.toString());
        
        StringBuilder cursorDisplay = new StringBuilder();
        for (int i = 0; i < cursorPosition; i++) {
            cursorDisplay.append(" ");
        }
        cursorDisplay.append("^");
        System.out.println(cursorDisplay.toString());
        System.out.println("----------------------");
    }
}

// Command interface
interface TextEditorCommand {
    void execute();
    void undo();
}

// Concrete Commands
class InsertTextCommand implements TextEditorCommand {
    private TextDocument document;
    private String text;
    
    public InsertTextCommand(TextDocument document, String text) {
        this.document = document;
        this.text = text;
    }
    
    @Override
    public void execute() {
        document.insert(text);
    }
    
    @Override
    public void undo() {
        document.setCursorPosition(document.getCursorPosition());
        document.delete(text.length());
    }
}

class DeleteTextCommand implements TextEditorCommand {
    private TextDocument document;
    private int length;
    private String deletedText;
    private int originalPosition;
    
    public DeleteTextCommand(TextDocument document, int length) {
        this.document = document;
        this.length = length;
    }
    
    @Override
    public void execute() {
        originalPosition = document.getCursorPosition();
        if (originalPosition >= length) {
            deletedText = document.getContent().substring(originalPosition - length, originalPosition);
            document.delete(length);
        } else {
            System.out.println("Cannot delete: Not enough characters");
        }
    }
    
    @Override
    public void undo() {
        document.setCursorPosition(originalPosition - length);
        if (deletedText != null) {
            document.insert(deletedText);
        }
    }
}

// Invoker
class TextEditor {
    private TextDocument document;
    private List<TextEditorCommand> commandHistory = new ArrayList<>();
    private int currentHistoryPosition = -1;
    
    public TextEditor() {
        document = new TextDocument();
    }
    
    public void executeCommand(TextEditorCommand command) {
        // If we've undone some commands and are now executing a new one,
        // we need to clear the redo history
        if (currentHistoryPosition < commandHistory.size() - 1) {
            commandHistory = commandHistory.subList(0, currentHistoryPosition + 1);
        }
        
        command.execute();
        commandHistory.add(command);
        currentHistoryPosition = commandHistory.size() - 1;
    }
    
    public void undo() {
        if (currentHistoryPosition >= 0) {
            commandHistory.get(currentHistoryPosition).undo();
            currentHistoryPosition--;
        } else {
            System.out.println("Nothing to undo");
        }
    }
    
    public void redo() {
        if (currentHistoryPosition < commandHistory.size() - 1) {
            currentHistoryPosition++;
            commandHistory.get(currentHistoryPosition).execute();
        } else {
            System.out.println("Nothing to redo");
        }
    }
    
    public void displayDocument() {
        document.displayDocument();
    }
    
    public TextDocument getDocument() {
        return document;
    }
}
```

## Benefits

1. **Decoupling**: The Command pattern decouples the object that invokes the operation from the object that performs it
2. **Extensibility**: You can introduce new commands without changing existing code
3. **Composite commands**: Simple commands can be assembled into larger ones
4. **Undoable operations**: Commands can store state for undoing operations
5. **Logging and auditing**: Commands can be serialized for later execution or auditing

## Considerations

1. **Increased number of classes**: Each command requires a separate class, which can lead to an explosion of classes
2. **Complexity**: For simple operations, the pattern might be overkill
3. **Performance overhead**: Command objects add a layer of indirection and may impact performance in time-critical applications

## When to Use

- When you need to parameterize objects with operations
- When you need to queue operations, schedule their execution, or execute them remotely
- When you need to support undo operations
- When you want to structure a system around high-level operations built on primitive operations
- When you need to implement callbacks
