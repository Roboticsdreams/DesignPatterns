# Bridge Pattern

[Back to Home](../README.md)

## Intent

Decouple an abstraction from its implementation so that the two can vary independently.

## Explanation

The Bridge pattern separates an abstraction from its implementation so that both can change independently without affecting each other. The pattern involves an interface acting as a bridge between the abstract class and its implementation classes.

## Real-World Example: Device Remote Control System

Consider a system for controlling different types of devices (TV, radio, etc.) using various types of remote controls (basic, advanced, etc.). The Bridge pattern allows new devices and remotes to be added without modifying existing code.

### Implementation

```java
// Step 1: Define the implementation interface
public interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int volume);
    int getChannel();
    void setChannel(int channel);
    void printStatus();
}

// Step 2: Create concrete implementations
public class TV implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() {
        return on;
    }
    
    @Override
    public void enable() {
        on = true;
        System.out.println("TV turned on");
    }
    
    @Override
    public void disable() {
        on = false;
        System.out.println("TV turned off");
    }
    
    @Override
    public int getVolume() {
        return volume;
    }
    
    @Override
    public void setVolume(int volume) {
        if (volume > 100) {
            this.volume = 100;
        } else if (volume < 0) {
            this.volume = 0;
        } else {
            this.volume = volume;
        }
        System.out.println("TV volume set to " + this.volume);
    }
    
    @Override
    public int getChannel() {
        return channel;
    }
    
    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("TV channel set to " + this.channel);
    }
    
    @Override
    public void printStatus() {
        System.out.println("--------------------");
        System.out.println("| TV status");
        System.out.println("| Power: " + (on ? "on" : "off"));
        System.out.println("| Volume: " + volume);
        System.out.println("| Channel: " + channel);
        System.out.println("--------------------");
    }
}

public class Radio implements Device {
    private boolean on = false;
    private int volume = 20;
    private int channel = 87; // FM frequency
    
    @Override
    public boolean isEnabled() {
        return on;
    }
    
    @Override
    public void enable() {
        on = true;
        System.out.println("Radio turned on");
    }
    
    @Override
    public void disable() {
        on = false;
        System.out.println("Radio turned off");
    }
    
    @Override
    public int getVolume() {
        return volume;
    }
    
    @Override
    public void setVolume(int volume) {
        if (volume > 100) {
            this.volume = 100;
        } else if (volume < 0) {
            this.volume = 0;
        } else {
            this.volume = volume;
        }
        System.out.println("Radio volume set to " + this.volume);
    }
    
    @Override
    public int getChannel() {
        return channel;
    }
    
    @Override
    public void setChannel(int channel) {
        // FM frequencies typically range from 87.5 to 108.0 MHz
        if (channel < 87) {
            this.channel = 87;
        } else if (channel > 108) {
            this.channel = 108;
        } else {
            this.channel = channel;
        }
        System.out.println("Radio frequency set to " + this.channel + ".0 MHz");
    }
    
    @Override
    public void printStatus() {
        System.out.println("--------------------");
        System.out.println("| Radio status");
        System.out.println("| Power: " + (on ? "on" : "off"));
        System.out.println("| Volume: " + volume);
        System.out.println("| Frequency: " + channel + ".0 MHz");
        System.out.println("--------------------");
    }
}

public class SmartSpeaker implements Device {
    private boolean on = false;
    private int volume = 50;
    private int channel = 0; // Music playlist
    private List<String> playlists = Arrays.asList(
        "Top Hits", "Classical", "Rock", "Jazz", "Ambient"
    );
    
    @Override
    public boolean isEnabled() {
        return on;
    }
    
    @Override
    public void enable() {
        on = true;
        System.out.println("Smart Speaker turned on");
    }
    
    @Override
    public void disable() {
        on = false;
        System.out.println("Smart Speaker turned off");
    }
    
    @Override
    public int getVolume() {
        return volume;
    }
    
    @Override
    public void setVolume(int volume) {
        if (volume > 100) {
            this.volume = 100;
        } else if (volume < 0) {
            this.volume = 0;
        } else {
            this.volume = volume;
        }
        System.out.println("Smart Speaker volume set to " + this.volume);
    }
    
    @Override
    public int getChannel() {
        return channel;
    }
    
    @Override
    public void setChannel(int channel) {
        if (channel < 0) {
            this.channel = 0;
        } else if (channel >= playlists.size()) {
            this.channel = playlists.size() - 1;
        } else {
            this.channel = channel;
        }
        System.out.println("Smart Speaker playlist set to " + playlists.get(this.channel));
    }
    
    @Override
    public void printStatus() {
        System.out.println("--------------------");
        System.out.println("| Smart Speaker status");
        System.out.println("| Power: " + (on ? "on" : "off"));
        System.out.println("| Volume: " + volume);
        System.out.println("| Playlist: " + playlists.get(channel));
        System.out.println("--------------------");
    }
}

// Step 3: Define the abstraction
public abstract class RemoteControl {
    protected Device device;
    
    public RemoteControl(Device device) {
        this.device = device;
    }
    
    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }
    
    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
    }
    
    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
    }
    
    public void channelDown() {
        device.setChannel(device.getChannel() - 1);
    }
    
    public void channelUp() {
        device.setChannel(device.getChannel() + 1);
    }
    
    public void showStatus() {
        device.printStatus();
    }
}

// Step 4: Create refined abstractions
public class BasicRemote extends RemoteControl {
    public BasicRemote(Device device) {
        super(device);
    }
    
    // BasicRemote provides only the fundamental remote control operations
}

public class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(Device device) {
        super(device);
    }
    
    // Advanced remote provides additional functionality
    public void mute() {
        System.out.println("Mute");
        device.setVolume(0);
    }
    
    // Memorize current channel
    private int savedChannel = 1;
    
    public void saveChannel() {
        savedChannel = device.getChannel();
        System.out.println("Saved current channel: " + savedChannel);
    }
    
    public void recallChannel() {
        device.setChannel(savedChannel);
        System.out.println("Switched to saved channel: " + savedChannel);
    }
}

public class TouchScreenRemote extends RemoteControl {
    public TouchScreenRemote(Device device) {
        super(device);
    }
    
    // Touchscreen remote provides direct channel access
    public void setExactChannel(int channel) {
        System.out.println("Setting exact channel: " + channel);
        device.setChannel(channel);
    }
    
    // Preset volume levels
    public void setLowVolume() {
        device.setVolume(10);
    }
    
    public void setMediumVolume() {
        device.setVolume(50);
    }
    
    public void setHighVolume() {
        device.setVolume(90);
    }
    
    // Smart features
    public void showProgramGuide() {
        System.out.println("Displaying program guide on touchscreen...");
        // Implementation would connect to program guide service
    }
}
```

### Usage Example

```java
public class RemoteControlDemo {
    public static void main(String[] args) {
        testDevice(new TV());
        testDevice(new Radio());
        testDevice(new SmartSpeaker());
    }
    
    public static void testDevice(Device device) {
        System.out.println("Tests with basic remote:");
        BasicRemote basicRemote = new BasicRemote(device);
        basicRemote.togglePower();
        basicRemote.showStatus();
        
        System.out.println("Tests with advanced remote:");
        AdvancedRemote advancedRemote = new AdvancedRemote(device);
        advancedRemote.togglePower();
        advancedRemote.channelUp();
        advancedRemote.saveChannel();
        advancedRemote.channelUp();
        advancedRemote.channelUp();
        advancedRemote.recallChannel();
        advancedRemote.volumeUp();
        advancedRemote.mute();
        advancedRemote.showStatus();
        
        System.out.println("Tests with touchscreen remote:");
        TouchScreenRemote touchRemote = new TouchScreenRemote(device);
        touchRemote.togglePower();
        touchRemote.setExactChannel(5);
        touchRemote.setMediumVolume();
        touchRemote.showProgramGuide();
        touchRemote.showStatus();
    }
}
```

## Applying the Bridge Pattern in Other Contexts

### Shapes with Different Rendering Engines

Another classic example of the Bridge pattern is separating shape abstractions from their rendering implementations:

```java
// Implementation interface
interface Renderer {
    void renderCircle(float radius, float x, float y);
    void renderRectangle(float width, float height, float x, float y);
}

// Concrete implementations
class VectorRenderer implements Renderer {
    @Override
    public void renderCircle(float radius, float x, float y) {
        System.out.println("Drawing a vector circle of radius " + radius + " at (" + x + ", " + y + ")");
    }
    
    @Override
    public void renderRectangle(float width, float height, float x, float y) {
        System.out.println("Drawing a vector rectangle of width " + width + 
                         " and height " + height + " at (" + x + ", " + y + ")");
    }
}

class RasterRenderer implements Renderer {
    @Override
    public void renderCircle(float radius, float x, float y) {
        System.out.println("Rasterizing a circle of radius " + radius + " at (" + x + ", " + y + ")");
    }
    
    @Override
    public void renderRectangle(float width, float height, float x, float y) {
        System.out.println("Rasterizing a rectangle of width " + width + 
                         " and height " + height + " at (" + x + ", " + y + ")");
    }
}

// Abstraction
abstract class Shape {
    protected Renderer renderer;
    
    public Shape(Renderer renderer) {
        this.renderer = renderer;
    }
    
    public abstract void draw();
    public abstract void resize(float factor);
}

// Refined abstractions
class Circle extends Shape {
    private float radius;
    private float x;
    private float y;
    
    public Circle(Renderer renderer, float radius, float x, float y) {
        super(renderer);
        this.radius = radius;
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void draw() {
        renderer.renderCircle(radius, x, y);
    }
    
    @Override
    public void resize(float factor) {
        radius *= factor;
    }
}

class Rectangle extends Shape {
    private float width;
    private float height;
    private float x;
    private float y;
    
    public Rectangle(Renderer renderer, float width, float height, float x, float y) {
        super(renderer);
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void draw() {
        renderer.renderRectangle(width, height, x, y);
    }
    
    @Override
    public void resize(float factor) {
        width *= factor;
        height *= factor;
    }
}
```

## Benefits

1. **Decoupling interface and implementation**: Allows them to vary independently
2. **Better extensibility**: You can extend both the abstractions and implementations through inheritance
3. **Hiding implementation details**: Clients interact only with high-level abstractions
4. **Runtime binding of the implementation**: The abstraction can select or switch implementations at runtime

## Considerations

1. **Increased complexity**: Adds another layer of abstraction which can make the system harder to understand
2. **More planning required**: Requires proper identification of the dimensions that should vary independently
3. **Overhead for simple systems**: May be overkill for systems where a single implementation is all that's needed

## When to Use

- When you want to avoid a permanent binding between an abstraction and its implementation
- When both the abstractions and their implementations should be extensible through subclasses
- When changes in the implementation shouldn't impact the client code
- When you have multiple orthogonal dimensions of variation in a system (e.g., different types of UI controls and different platforms)
