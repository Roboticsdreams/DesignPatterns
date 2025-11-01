# Facade Pattern

[Back to Home](../README.md)

## Intent

Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.

## Explanation

The Facade pattern provides a simplified interface to a complex subsystem of classes, a library, or a framework. It doesn't encapsulate the subsystem but provides a simplified interface to its functionality, making it easier to use.

## Real-World Example: Home Theater System

Consider a home theater system with multiple components: DVD player, projector, sound system, lighting, etc. Each has its own interface and operations. A home theater facade provides a simplified interface for common operations like "Watch Movie" or "Listen to Radio" that coordinates all the individual components.

### Implementation

```java
import java.util.ArrayList;
import java.util.List;

// Step 1: Define the complex subsystem classes
public class DVDPlayer {
    private boolean on;
    private String movie;
    
    public void on() {
        on = true;
        System.out.println("DVD player turned on");
    }
    
    public void off() {
        on = false;
        System.out.println("DVD player turned off");
    }
    
    public void play(String movie) {
        if (on) {
            this.movie = movie;
            System.out.println("Playing movie: " + movie);
        } else {
            System.out.println("Cannot play movie: DVD player is off");
        }
    }
    
    public void stop() {
        if (on && movie != null) {
            System.out.println("Stopping movie: " + movie);
            movie = null;
        } else {
            System.out.println("No movie playing");
        }
    }
    
    public void eject() {
        if (on) {
            System.out.println("DVD ejected");
            movie = null;
        } else {
            System.out.println("Cannot eject: DVD player is off");
        }
    }
    
    @Override
    public String toString() {
        return "DVD Player is " + (on ? "on" : "off") + 
               (movie != null ? ", playing " + movie : "");
    }
}

public class Projector {
    private boolean on;
    private String input;
    private int width = 1920;
    private int height = 1080;
    
    public void on() {
        on = true;
        System.out.println("Projector turned on");
    }
    
    public void off() {
        on = false;
        System.out.println("Projector turned off");
    }
    
    public void setInput(String input) {
        this.input = input;
        System.out.println("Setting projector input to: " + input);
    }
    
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        System.out.println("Setting projector dimensions to " + width + "x" + height);
    }
    
    public void wideScreenMode() {
        System.out.println("Setting projector to widescreen mode (16:9)");
    }
    
    @Override
    public String toString() {
        return "Projector is " + (on ? "on" : "off") + 
               (input != null ? ", input: " + input : "") + 
               ", resolution: " + width + "x" + height;
    }
}

public class AudioSystem {
    private boolean on;
    private int volume;
    private String input;
    private String mode; // e.g., stereo, surround, dolby
    
    public void on() {
        on = true;
        volume = 5; // Default volume
        mode = "Stereo";
        System.out.println("Audio system turned on");
    }
    
    public void off() {
        on = false;
        System.out.println("Audio system turned off");
    }
    
    public void setVolume(int volume) {
        if (on) {
            this.volume = Math.min(10, Math.max(0, volume)); // 0-10 scale
            System.out.println("Audio system volume set to " + this.volume);
        } else {
            System.out.println("Cannot set volume: Audio system is off");
        }
    }
    
    public void setInput(String input) {
        this.input = input;
        System.out.println("Audio system input set to " + input);
    }
    
    public void setSurroundSound() {
        if (on) {
            mode = "Surround";
            System.out.println("Audio system mode set to Surround Sound");
        }
    }
    
    public void setStereo() {
        if (on) {
            mode = "Stereo";
            System.out.println("Audio system mode set to Stereo");
        }
    }
    
    @Override
    public String toString() {
        return "Audio System is " + (on ? "on" : "off") + 
               (on ? ", volume: " + volume + ", mode: " + mode : "") +
               (input != null ? ", input: " + input : "");
    }
}

public class Lights {
    private boolean on;
    private int brightness;
    
    public void on() {
        on = true;
        brightness = 100; // Full brightness
        System.out.println("Lights turned on at 100% brightness");
    }
    
    public void off() {
        on = false;
        System.out.println("Lights turned off");
    }
    
    public void dim(int level) {
        if (on) {
            brightness = Math.min(100, Math.max(0, level));
            System.out.println("Lights dimmed to " + brightness + "%");
        } else {
            System.out.println("Cannot dim: Lights are off");
        }
    }
    
    @Override
    public String toString() {
        return "Lights are " + (on ? "on at " + brightness + "% brightness" : "off");
    }
}

public class PopcornPopper {
    private boolean on;
    private boolean popping;
    
    public void on() {
        on = true;
        System.out.println("Popcorn popper turned on");
    }
    
    public void off() {
        on = false;
        popping = false;
        System.out.println("Popcorn popper turned off");
    }
    
    public void pop() {
        if (on) {
            popping = true;
            System.out.println("Popcorn popper is popping popcorn!");
        } else {
            System.out.println("Cannot pop: Popcorn popper is off");
        }
    }
    
    public void stopPopping() {
        if (popping) {
            popping = false;
            System.out.println("Popcorn popper stopped popping");
        }
    }
    
    @Override
    public String toString() {
        return "Popcorn Popper is " + (on ? "on" : "off") + 
               (popping ? " and popping" : "");
    }
}

public class Screen {
    private boolean down;
    
    public void down() {
        down = true;
        System.out.println("Screen is going down");
    }
    
    public void up() {
        down = false;
        System.out.println("Screen is going up");
    }
    
    @Override
    public String toString() {
        return "Screen is " + (down ? "down" : "up");
    }
}

// Step 2: Create the facade
public class HomeTheaterFacade {
    private DVDPlayer dvdPlayer;
    private Projector projector;
    private AudioSystem audioSystem;
    private Lights lights;
    private PopcornPopper popcornPopper;
    private Screen screen;
    
    public HomeTheaterFacade(
        DVDPlayer dvdPlayer,
        Projector projector,
        AudioSystem audioSystem,
        Lights lights,
        PopcornPopper popcornPopper,
        Screen screen
    ) {
        this.dvdPlayer = dvdPlayer;
        this.projector = projector;
        this.audioSystem = audioSystem;
        this.lights = lights;
        this.popcornPopper = popcornPopper;
        this.screen = screen;
    }
    
    // High-level operation: Watch a movie
    public void watchMovie(String movie) {
        System.out.println("\n==== Getting ready to watch a movie ====");
        popcornPopper.on();
        popcornPopper.pop();
        
        lights.dim(10);
        screen.down();
        
        projector.on();
        projector.wideScreenMode();
        projector.setInput("DVD");
        
        audioSystem.on();
        audioSystem.setInput("DVD");
        audioSystem.setSurroundSound();
        audioSystem.setVolume(7);
        
        dvdPlayer.on();
        dvdPlayer.play(movie);
        
        System.out.println("\nEverything is ready. Enjoy your movie!");
    }
    
    // High-level operation: End movie
    public void endMovie() {
        System.out.println("\n==== Shutting down movie theater ====");
        popcornPopper.off();
        lights.on();
        screen.up();
        projector.off();
        audioSystem.off();
        dvdPlayer.stop();
        dvdPlayer.eject();
        dvdPlayer.off();
        
        System.out.println("\nMovie theater shutdown complete.");
    }
    
    // High-level operation: Listen to radio
    public void listenToRadio() {
        System.out.println("\n==== Getting ready to listen to the radio ====");
        audioSystem.on();
        audioSystem.setInput("Radio");
        audioSystem.setStereo();
        audioSystem.setVolume(5);
        
        System.out.println("\nRadio is now playing.");
    }
    
    // High-level operation: End radio
    public void endRadio() {
        System.out.println("\n==== Turning off radio ====");
        audioSystem.off();
        System.out.println("\nRadio is now off.");
    }
    
    // Additional method to get the status of all components
    public void getSystemStatus() {
        System.out.println("\n==== Home Theater System Status ====");
        System.out.println(dvdPlayer);
        System.out.println(projector);
        System.out.println(audioSystem);
        System.out.println(lights);
        System.out.println(popcornPopper);
        System.out.println(screen);
        System.out.println("==================================");
    }
}
```

### Usage Example

```java
public class HomeTheaterDemo {
    public static void main(String[] args) {
        // Create all the individual components
        DVDPlayer dvdPlayer = new DVDPlayer();
        Projector projector = new Projector();
        AudioSystem audioSystem = new AudioSystem();
        Lights lights = new Lights();
        PopcornPopper popcornPopper = new PopcornPopper();
        Screen screen = new Screen();
        
        // Create the facade
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(
            dvdPlayer, projector, audioSystem, lights, popcornPopper, screen
        );
        
        // Get initial status
        homeTheater.getSystemStatus();
        
        // Use the simplified interface to watch a movie
        homeTheater.watchMovie("Inception");
        
        // Check status during movie
        homeTheater.getSystemStatus();
        
        // End the movie
        homeTheater.endMovie();
        
        // Check status after movie
        homeTheater.getSystemStatus();
        
        // Use the simplified interface to listen to radio
        homeTheater.listenToRadio();
        
        // Check status during radio
        homeTheater.getSystemStatus();
        
        // End radio
        homeTheater.endRadio();
        
        // Final status
        homeTheater.getSystemStatus();
        
        // ========= Demonstrate without facade (complex) =========
        System.out.println("\n\n=== Without Facade (Complex Process) ===");
        System.out.println("Setting up to watch a movie manually:");
        
        // Manual process to watch a movie
        popcornPopper.on();
        popcornPopper.pop();
        
        lights.dim(10);
        screen.down();
        
        projector.on();
        projector.wideScreenMode();
        projector.setInput("DVD");
        
        audioSystem.on();
        audioSystem.setInput("DVD");
        audioSystem.setSurroundSound();
        audioSystem.setVolume(7);
        
        dvdPlayer.on();
        dvdPlayer.play("The Matrix");
        
        System.out.println("\nManual setup complete.");
        
        // Manual process to end a movie
        System.out.println("\nShutting down manually:");
        popcornPopper.off();
        lights.on();
        screen.up();
        projector.off();
        audioSystem.off();
        dvdPlayer.stop();
        dvdPlayer.eject();
        dvdPlayer.off();
        
        System.out.println("\nManual shutdown complete.");
    }
}
```

## Another Example: Computer System Startup

Another common application of the Facade pattern is to simplify complex system initialization:

```java
// Subsystem components
class CPU {
    public void freeze() { System.out.println("CPU: Freezing processor"); }
    public void jump(long position) { System.out.println("CPU: Jumping to position " + position); }
    public void execute() { System.out.println("CPU: Executing commands"); }
}

class Memory {
    public void load(long position, String data) {
        System.out.println("Memory: Loading data to position " + position + ": " + data);
    }
}

class HardDrive {
    public String read(long lba, int size) {
        System.out.println("HardDrive: Reading " + size + " bytes from sector " + lba);
        return "Data from sector " + lba;
    }
}

// Facade
class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    private static final long BOOT_ADDRESS = 0x0;
    private static final long BOOT_SECTOR = 0x001;
    private static final int SECTOR_SIZE = 512;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }
    
    public void start() {
        System.out.println("\n=== Starting Computer ===");
        cpu.freeze();
        String bootData = hardDrive.read(BOOT_SECTOR, SECTOR_SIZE);
        memory.load(BOOT_ADDRESS, bootData);
        cpu.jump(BOOT_ADDRESS);
        cpu.execute();
        System.out.println("=== Computer Started Successfully ===\n");
    }
}

// Client code
public class ComputerDemo {
    public static void main(String[] args) {
        ComputerFacade computer = new ComputerFacade();
        computer.start();
    }
}
```

## Benefits

1. **Simplified interface**: Shields clients from subsystem complexity
2. **Reduced coupling**: Clients interact with a single facade rather than multiple subsystem classes
3. **Subsystem independence**: Changes to the subsystem don't affect clients as long as the facade's interface remains unchanged
4. **Entry point**: Provides a clear entry point to a complex subsystem

## Considerations

1. **Can become a god object**: The facade can become a complex class with too many responsibilities
2. **Limited flexibility**: The simplified interface may not support all the functionality of the subsystem
3. **Performance overhead**: The facade adds another layer of indirection

## When to Use

- When you want to provide a simple interface to a complex subsystem
- When there are many dependencies between clients and implementation classes
- When you want to layer your systems, using facades to define entry points to each layer
- When you want to reduce the learning curve for using a complex system
