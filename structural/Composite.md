# Composite Pattern

[Back to Home](../README.md)

## Intent

Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.

## Explanation

The Composite pattern allows you to create a tree structure where individual objects and compositions of objects are treated the same way. It lets clients use individual objects and compositions of objects uniformly without having to know the difference.

## Real-World Example: File System Structure

In a file system, both directories and files share common operations (e.g., getting size, displaying name). However, directories can contain other directories and files, forming a tree structure. The Composite pattern allows treating files and directories uniformly through a common interface.

### Implementation

```java
import java.util.ArrayList;
import java.util.List;

// Step 1: Create the Component interface
public interface FileSystemComponent {
    String getName();
    long getSize();
    void display(int indentLevel);
}

// Step 2: Create the Leaf class
public class File implements FileSystemComponent {
    private String name;
    private long size;
    private String extension;
    
    public File(String name, String extension, long size) {
        this.name = name;
        this.extension = extension;
        this.size = size;
    }
    
    @Override
    public String getName() {
        return name + "." + extension;
    }
    
    @Override
    public long getSize() {
        return size;
    }
    
    @Override
    public void display(int indentLevel) {
        System.out.println(getIndentation(indentLevel) + "📄 " + getName() + " (" + formatSize(size) + ")");
    }
    
    private String getIndentation(int indentLevel) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indentation.append("    ");
        }
        return indentation.toString();
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}

// Step 3: Create the Composite class
public class Directory implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> children;
    
    public Directory(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }
    
    public void addComponent(FileSystemComponent component) {
        children.add(component);
    }
    
    public void removeComponent(FileSystemComponent component) {
        children.remove(component);
    }
    
    public List<FileSystemComponent> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public long getSize() {
        // Size of a directory is the sum of its children's sizes
        return children.stream().mapToLong(FileSystemComponent::getSize).sum();
    }
    
    @Override
    public void display(int indentLevel) {
        System.out.println(getIndentation(indentLevel) + "📁 " + getName() + " (" + formatSize(getSize()) + ")");
        
        // Display all children with increased indentation
        for (FileSystemComponent component : children) {
            component.display(indentLevel + 1);
        }
    }
    
    private String getIndentation(int indentLevel) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indentation.append("    ");
        }
        return indentation.toString();
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}

// Additional specialized File types to demonstrate the flexibility
public class ImageFile extends File {
    private int width;
    private int height;
    
    public ImageFile(String name, String extension, long size, int width, int height) {
        super(name, extension, size);
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    @Override
    public void display(int indentLevel) {
        System.out.println(getIndentation(indentLevel) + "🖼️ " + getName() + 
                         " (" + formatSize(getSize()) + ") - " + width + "x" + height);
    }
    
    private String getIndentation(int indentLevel) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indentation.append("    ");
        }
        return indentation.toString();
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}

public class TextFile extends File {
    private int wordCount;
    
    public TextFile(String name, long size, int wordCount) {
        super(name, "txt", size);
        this.wordCount = wordCount;
    }
    
    public int getWordCount() {
        return wordCount;
    }
    
    @Override
    public void display(int indentLevel) {
        System.out.println(getIndentation(indentLevel) + "📝 " + getName() + 
                         " (" + formatSize(getSize()) + ") - " + wordCount + " words");
    }
    
    private String getIndentation(int indentLevel) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indentation.append("    ");
        }
        return indentation.toString();
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
```

### Usage Example

```java
public class FileSystemDemo {
    public static void main(String[] args) {
        // Create a file system structure
        
        // Root directory
        Directory root = new Directory("root");
        
        // Documents directory
        Directory documents = new Directory("Documents");
        File resume = new TextFile("Resume", 25600, 450);
        File report = new TextFile("AnnualReport", 1200000, 15000);
        documents.addComponent(resume);
        documents.addComponent(report);
        
        // Photos directory with subdirectories
        Directory photos = new Directory("Photos");
        Directory vacation = new Directory("Vacation2023");
        ImageFile beach = new ImageFile("BeachSunset", "jpg", 2500000, 4000, 3000);
        ImageFile mountain = new ImageFile("MountainView", "png", 3200000, 5000, 3500);
        vacation.addComponent(beach);
        vacation.addComponent(mountain);
        
        Directory family = new Directory("Family");
        ImageFile familyPhoto = new ImageFile("FamilyReunion", "jpg", 1800000, 3500, 2500);
        family.addComponent(familyPhoto);
        
        photos.addComponent(vacation);
        photos.addComponent(family);
        
        // Music directory
        Directory music = new Directory("Music");
        File song1 = new File("FavoriteSong", "mp3", 8500000);
        File song2 = new File("ClassicalPiece", "flac", 32000000);
        music.addComponent(song1);
        music.addComponent(song2);
        
        // Add all main directories to root
        root.addComponent(documents);
        root.addComponent(photos);
        root.addComponent(music);
        
        // Add a few loose files to root
        File readme = new TextFile("README", 1024, 50);
        File config = new File("config", "ini", 2048);
        root.addComponent(readme);
        root.addComponent(config);
        
        // Display the entire file system
        System.out.println("File System Structure:");
        root.display(0);
        
        // Display total size
        System.out.println("\nTotal size: " + formatSize(root.getSize()));
        
        // Example of treating a single file and a directory uniformly
        System.out.println("\nDisplaying a single file:");
        FileSystemComponent singleFile = resume;
        singleFile.display(0);
        
        System.out.println("\nDisplaying a directory:");
        FileSystemComponent singleDirectory = music;
        singleDirectory.display(0);
        
        // Operations on individual elements
        System.out.println("\nSize of Photos directory: " + formatSize(photos.getSize()));
        System.out.println("Size of README file: " + formatSize(readme.getSize()));
        
        // Demonstrating directory manipulation
        System.out.println("\nRemoving Vacation photos directory...");
        photos.removeComponent(vacation);
        System.out.println("Updated Photos directory:");
        photos.display(0);
    }
    
    private static String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
```

## Another Example: Organization Structure

The Composite pattern can also be applied to represent organizational hierarchies where both individual employees and departments with multiple employees are treated uniformly:

```java
// Component
interface OrganizationalUnit {
    String getName();
    double getExpenses();
    int getHeadCount();
    void display(int depth);
}

// Leaf
class Employee implements OrganizationalUnit {
    private String name;
    private String position;
    private double salary;
    
    public Employee(String name, String position, double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public String getPosition() {
        return position;
    }
    
    @Override
    public double getExpenses() {
        return salary;
    }
    
    @Override
    public int getHeadCount() {
        return 1;
    }
    
    @Override
    public void display(int depth) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indentation.append("    ");
        }
        System.out.println(indentation + "👤 " + name + " - " + position + " ($" + salary + "/year)");
    }
}

// Composite
class Department implements OrganizationalUnit {
    private String name;
    private List<OrganizationalUnit> units = new ArrayList<>();
    
    public Department(String name) {
        this.name = name;
    }
    
    public void addUnit(OrganizationalUnit unit) {
        units.add(unit);
    }
    
    public void removeUnit(OrganizationalUnit unit) {
        units.remove(unit);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public double getExpenses() {
        return units.stream().mapToDouble(OrganizationalUnit::getExpenses).sum();
    }
    
    @Override
    public int getHeadCount() {
        return units.stream().mapToInt(OrganizationalUnit::getHeadCount).sum();
    }
    
    @Override
    public void display(int depth) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indentation.append("    ");
        }
        System.out.println(indentation + "🏢 " + name + " Department (Headcount: " + 
                         getHeadCount() + ", Budget: $" + getExpenses() + "/year)");
        
        for (OrganizationalUnit unit : units) {
            unit.display(depth + 1);
        }
    }
}
```

## Benefits

1. **Simplified client code**: Clients can treat composite structures and individual objects uniformly
2. **Easy to add new kinds of components**: You can define new components without changing existing code
3. **Promotes object composition**: Encourages composition over inheritance
4. **Naturally represents hierarchies**: Makes it easier to work with tree-structured data

## Considerations

1. **Difficult to restrict components**: It can be challenging to restrict what types of components can be added to a composite
2. **Overhead for simple leaf objects**: The Component interface may include operations that don't make sense for some leaf objects
3. **Heavy design**: Might be overkill for simple object hierarchies

## When to Use

- When you want to represent part-whole hierarchies of objects
- When you want clients to be able to ignore the difference between compositions of objects and individual objects
- When the structure can have any level of complexity and is dynamic
- When operations need to be performed uniformly across all components in a hierarchy
