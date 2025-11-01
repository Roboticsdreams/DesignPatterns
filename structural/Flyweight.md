# Flyweight Pattern

[Back to Home](../README.md)

## Intent

Use sharing to support large numbers of fine-grained objects efficiently.

## Explanation

The Flyweight pattern minimizes memory usage by sharing as much data as possible with similar objects. It is used when we need to create a large number of similar objects which would otherwise consume a large amount of memory.

## Real-World Example: Text Editor Character Rendering

In a text editor, representing each character as a separate object with properties like font, size, color, etc. would be memory-intensive. The Flyweight pattern allows sharing character format attributes among multiple characters, significantly reducing memory consumption.

### Implementation

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Step 1: Create the Flyweight interface
public interface TextCharacter {
    void display(int row, int column);
}

// Step 2: Create concrete Flyweight class
public class Character implements TextCharacter {
    private char symbol;
    private String fontFamily;
    private int fontSize;
    private boolean isBold;
    private boolean isItalic;
    private int color; // RGB value
    
    public Character(char symbol, String fontFamily, int fontSize, boolean isBold, boolean isItalic, int color) {
        this.symbol = symbol;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.color = color;
        
        // Simulate the memory consumption of storing a font glyph
        // In a real system, this might be megabytes of data for each unique font/style combination
        System.out.println("Creating character object for '" + symbol + "' with font: " + 
                         fontFamily + ", size: " + fontSize + 
                         (isBold ? ", bold" : "") + 
                         (isItalic ? ", italic" : "") + 
                         ", color: #" + Integer.toHexString(color));
    }
    
    @Override
    public void display(int row, int column) {
        // The row and column parameters are extrinsic state that varies per occurrence
        System.out.println("Displaying '" + symbol + "' at position (" + row + ", " + column + 
                         ") with " + fontFamily + " " + fontSize + "pt" +
                         (isBold ? " bold" : "") + 
                         (isItalic ? " italic" : "") + 
                         " and color #" + Integer.toHexString(color));
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    // Getters for other properties
    public String getFontFamily() {
        return fontFamily;
    }
    
    public int getFontSize() {
        return fontSize;
    }
    
    public boolean isBold() {
        return isBold;
    }
    
    public boolean isItalic() {
        return isItalic;
    }
    
    public int getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return "Character [symbol=" + symbol + 
               ", font=" + fontFamily + 
               ", size=" + fontSize + 
               ", bold=" + isBold + 
               ", italic=" + isItalic + 
               ", color=#" + Integer.toHexString(color) + "]";
    }
}

// Step 3: Create a Flyweight Factory
public class CharacterFactory {
    private Map<String, TextCharacter> characters = new HashMap<>();
    
    public TextCharacter getCharacter(char symbol, String fontFamily, int fontSize, boolean isBold, boolean isItalic, int color) {
        // Create a unique key for this combination of properties
        String key = symbol + fontFamily + fontSize + isBold + isItalic + color;
        
        // Retrieve the existing character if we already have one with these properties
        TextCharacter character = characters.get(key);
        
        // If not found, create a new one
        if (character == null) {
            character = new Character(symbol, fontFamily, fontSize, isBold, isItalic, color);
            characters.put(key, character);
        }
        
        return character;
    }
    
    public int getTotalCharacters() {
        return characters.size();
    }
}

// Step 4: Create the Context class to use the Flyweight
public class TextDocument {
    private final int ROWS = 100;
    private final int COLUMNS = 80;
    
    // This would store only references to the actual character objects
    private TextCharacter[][] content = new TextCharacter[ROWS][COLUMNS];
    // This stores the location information (extrinsic state)
    private int[][] positions = new int[ROWS][COLUMNS]; // Tracks position for display purposes
    
    private CharacterFactory characterFactory;
    
    public TextDocument(CharacterFactory characterFactory) {
        this.characterFactory = characterFactory;
    }
    
    public void addCharacter(int row, int column, char symbol, String fontFamily, int fontSize, boolean isBold, boolean isItalic, int color) {
        if (row >= 0 && row < ROWS && column >= 0 && column < COLUMNS) {
            TextCharacter character = characterFactory.getCharacter(symbol, fontFamily, fontSize, isBold, isItalic, color);
            content[row][column] = character;
            positions[row][column] = 1; // Mark as filled
        }
    }
    
    public void display() {
        System.out.println("\nDocument content:");
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (content[row][column] != null) {
                    content[row][column].display(row, column);
                }
            }
        }
    }
}
```

### Usage Example

```java
public class TextEditorDemo {
    public static void main(String[] args) {
        // Create a character factory
        CharacterFactory characterFactory = new CharacterFactory();
        
        // Create a text document
        TextDocument document = new TextDocument(characterFactory);
        
        System.out.println("=== Simulating text input in a document ===");
        
        // Common formatting properties for most text
        String defaultFont = "Arial";
        int defaultSize = 12;
        boolean defaultBold = false;
        boolean defaultItalic = false;
        int defaultColor = 0x000000; // Black
        
        // Add a regular paragraph of text
        String paragraph = "The Flyweight pattern is a structural design pattern that lets you fit more objects " +
                          "into the available amount of RAM by sharing common parts of state between multiple objects " +
                          "instead of keeping all of the data in each object.";
        
        int row = 5;
        int column = 0;
        
        System.out.println("\nAdding a paragraph of regular text...");
        for (char c : paragraph.toCharArray()) {
            document.addCharacter(row, column, c, defaultFont, defaultSize, defaultBold, defaultItalic, defaultColor);
            column++;
            
            // Line wrapping
            if (column >= 60) {
                column = 5; // Indent next line
                row++;
            }
        }
        
        // Add some emphasized text with different formatting
        String emphasisText = "Memory Efficiency is Crucial!";
        row = 12;
        column = 10;
        
        System.out.println("\nAdding emphasized text...");
        for (char c : emphasisText.toCharArray()) {
            document.addCharacter(row, column, c, "Times New Roman", 16, true, false, 0xFF0000); // Red, bold text
            column++;
        }
        
        // Add a section title
        String titleText = "Understanding the Flyweight Pattern";
        row = 2;
        column = 15;
        
        System.out.println("\nAdding a title...");
        for (char c : titleText.toCharArray()) {
            document.addCharacter(row, column, c, "Verdana", 18, true, false, 0x0000FF); // Blue, bold text
            column++;
        }
        
        // Display some statistics
        System.out.println("\n=== Document Statistics ===");
        int totalChars = paragraph.length() + emphasisText.length() + titleText.length();
        System.out.println("Total characters in document: " + totalChars);
        System.out.println("Unique character objects created (flyweights): " + characterFactory.getTotalCharacters());
        System.out.println("Memory saving: " + (totalChars - characterFactory.getTotalCharacters()) + " character objects");
        
        // Display a sample of the document (for demonstration purposes)
        System.out.println("\n=== Sample Document Display ===");
        document.display();
    }
}
```

## Another Example: Game Object Rendering

Another common application of the Flyweight pattern is in game development, where many similar objects (like trees, bullets, or particles) need to be rendered:

```java
// Flyweight interface
interface Particle {
    void render(float x, float y, float velocity, float direction, int color);
}

// Concrete Flyweight
class BasicParticle implements Particle {
    private String texture;
    private int[] shapePoints;
    private boolean isLightSource;
    
    public BasicParticle(String texture, int[] shapePoints, boolean isLightSource) {
        // These properties are intrinsic - shared across all particles of this type
        this.texture = texture;
        this.shapePoints = shapePoints;
        this.isLightSource = isLightSource;
        
        // In a real game, loading textures is memory intensive
        System.out.println("Loading texture: " + texture);
    }
    
    @Override
    public void render(float x, float y, float velocity, float direction, int color) {
        // Extrinsic state is passed in as parameters
        System.out.println("Rendering " + texture + " particle at (" + x + "," + y + ") " + 
                         "velocity: " + velocity + ", direction: " + direction + ", " +
                         "color: #" + Integer.toHexString(color));
    }
}

// Flyweight Factory
class ParticleFactory {
    private Map<String, Particle> particlePool = new HashMap<>();
    
    public Particle getParticle(String type) {
        Particle particle = particlePool.get(type);
        
        if (particle == null) {
            switch (type) {
                case "fire":
                    particle = new BasicParticle("fire.png", new int[] {0, 0, 10, 10}, true);
                    break;
                case "smoke":
                    particle = new BasicParticle("smoke.png", new int[] {0, 0, 15, 15}, false);
                    break;
                case "explosion":
                    particle = new BasicParticle("explosion.png", new int[] {0, 0, 20, 20}, true);
                    break;
                default:
                    particle = new BasicParticle("default.png", new int[] {0, 0, 5, 5}, false);
            }
            particlePool.put(type, particle);
        }
        
        return particle;
    }
    
    public int getParticleTypesCount() {
        return particlePool.size();
    }
}

// Client context
class ParticleSystem {
    private ParticleFactory factory;
    private List<ParticleInstance> activeParticles = new ArrayList<>();
    
    public ParticleSystem(ParticleFactory factory) {
        this.factory = factory;
    }
    
    public void createParticle(String type, float x, float y, float velocity, float direction, int color) {
        Particle particleType = factory.getParticle(type);
        activeParticles.add(new ParticleInstance(particleType, x, y, velocity, direction, color));
    }
    
    public void animate() {
        for (ParticleInstance instance : activeParticles) {
            // Update particle position based on velocity and direction
            instance.update();
            
            // Render the particle
            instance.render();
        }
    }
    
    public int getActiveParticlesCount() {
        return activeParticles.size();
    }
    
    // This class holds the extrinsic state for each particle instance
    private class ParticleInstance {
        private Particle particleType; // Reference to the flyweight
        private float x, y; // Position
        private float velocity;
        private float direction;
        private int color;
        
        public ParticleInstance(Particle particleType, float x, float y, float velocity, float direction, int color) {
            this.particleType = particleType;
            this.x = x;
            this.y = y;
            this.velocity = velocity;
            this.direction = direction;
            this.color = color;
        }
        
        public void update() {
            // Update position based on velocity and direction
            x += velocity * Math.cos(direction);
            y += velocity * Math.sin(direction);
            
            // In a real system, we might also decrease velocity over time, etc.
            velocity *= 0.99f;
        }
        
        public void render() {
            particleType.render(x, y, velocity, direction, color);
        }
    }
}
```

## Benefits

1. **Memory efficiency**: Reduces memory usage by sharing common data between multiple objects
2. **Performance improvement**: Fewer objects means less memory allocation and garbage collection overhead
3. **Transparency**: Clients can use flyweight objects without knowing about the sharing mechanism
4. **Large scale applications**: Makes it feasible to use object-oriented designs in memory-constrained scenarios

## Considerations

1. **Added complexity**: The pattern introduces additional complexity with the factory and shared/non-shared state management
2. **Thread safety**: Shared objects must be immutable or synchronized if used in a multi-threaded context
3. **Runtime costs**: There may be computation overhead in managing the flyweights
4. **Extrinsic state management**: Clients need to maintain the extrinsic state, which can be cumbersome

## When to Use

- When an application uses a large number of objects that have shared state
- When the application's memory footprint is a critical concern
- When most of an object's state can be made extrinsic (stored outside the object)
- When removing shared state would result in too many separate objects
