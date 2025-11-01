# Abstract Factory Pattern

[Back to Home](../README.md)

## Intent

Provide an interface for creating families of related or dependent objects without specifying their concrete classes.

## Explanation

The Abstract Factory pattern provides a way to encapsulate a group of individual factories that have a common theme without specifying their concrete classes. In normal usage, the client software creates a concrete implementation of the abstract factory and then uses the generic interfaces to create the concrete objects that are part of the theme.

## Real-World Example: Cross-Platform UI Components

In a GUI application that must work across different operating systems (Windows, macOS, Linux), the Abstract Factory pattern can be used to create UI components that match the look and feel of each platform while maintaining consistent API.

### Implementation

```java
// Step 1: Define abstract products
public interface Button {
    void render();
    void onClick();
}

public interface Checkbox {
    void render();
    void onToggle();
}

public interface Dropdown {
    void render();
    void onSelect(String option);
    void addOption(String option);
}

// Step 2: Create concrete products for each platform
// Windows UI components
public class WindowsButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering a button in Windows style");
    }

    @Override
    public void onClick() {
        System.out.println("Windows button click handled");
    }
}

public class WindowsCheckbox implements Checkbox {
    private boolean checked = false;

    @Override
    public void render() {
        System.out.println("Rendering a checkbox in Windows style [" + (checked ? "✓" : " ") + "]");
    }

    @Override
    public void onToggle() {
        checked = !checked;
        System.out.println("Windows checkbox toggled: " + (checked ? "checked" : "unchecked"));
    }
}

public class WindowsDropdown implements Dropdown {
    private List<String> options = new ArrayList<>();
    private String selectedOption = null;

    @Override
    public void render() {
        System.out.println("Rendering a dropdown in Windows style");
        System.out.println("Options: " + String.join(", ", options));
        System.out.println("Selected: " + (selectedOption != null ? selectedOption : "none"));
    }

    @Override
    public void onSelect(String option) {
        if (options.contains(option)) {
            selectedOption = option;
            System.out.println("Windows dropdown selection changed to: " + option);
        } else {
            System.out.println("Invalid option: " + option);
        }
    }

    @Override
    public void addOption(String option) {
        options.add(option);
        System.out.println("Added option '" + option + "' to Windows dropdown");
    }
}

// macOS UI components
public class MacOSButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering a button in macOS style");
    }

    @Override
    public void onClick() {
        System.out.println("macOS button click handled");
    }
}

public class MacOSCheckbox implements Checkbox {
    private boolean checked = false;

    @Override
    public void render() {
        System.out.println("Rendering a checkbox in macOS style [" + (checked ? "✓" : " ") + "]");
    }

    @Override
    public void onToggle() {
        checked = !checked;
        System.out.println("macOS checkbox toggled: " + (checked ? "checked" : "unchecked"));
    }
}

public class MacOSDropdown implements Dropdown {
    private List<String> options = new ArrayList<>();
    private String selectedOption = null;

    @Override
    public void render() {
        System.out.println("Rendering a dropdown in macOS style");
        System.out.println("Options: " + String.join(", ", options));
        System.out.println("Selected: " + (selectedOption != null ? selectedOption : "none"));
    }

    @Override
    public void onSelect(String option) {
        if (options.contains(option)) {
            selectedOption = option;
            System.out.println("macOS dropdown selection changed to: " + option);
        } else {
            System.out.println("Invalid option: " + option);
        }
    }

    @Override
    public void addOption(String option) {
        options.add(option);
        System.out.println("Added option '" + option + "' to macOS dropdown");
    }
}

// Linux UI components
public class LinuxButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering a button in Linux style");
    }

    @Override
    public void onClick() {
        System.out.println("Linux button click handled");
    }
}

public class LinuxCheckbox implements Checkbox {
    private boolean checked = false;

    @Override
    public void render() {
        System.out.println("Rendering a checkbox in Linux style [" + (checked ? "✓" : " ") + "]");
    }

    @Override
    public void onToggle() {
        checked = !checked;
        System.out.println("Linux checkbox toggled: " + (checked ? "checked" : "unchecked"));
    }
}

public class LinuxDropdown implements Dropdown {
    private List<String> options = new ArrayList<>();
    private String selectedOption = null;

    @Override
    public void render() {
        System.out.println("Rendering a dropdown in Linux style");
        System.out.println("Options: " + String.join(", ", options));
        System.out.println("Selected: " + (selectedOption != null ? selectedOption : "none"));
    }

    @Override
    public void onSelect(String option) {
        if (options.contains(option)) {
            selectedOption = option;
            System.out.println("Linux dropdown selection changed to: " + option);
        } else {
            System.out.println("Invalid option: " + option);
        }
    }

    @Override
    public void addOption(String option) {
        options.add(option);
        System.out.println("Added option '" + option + "' to Linux dropdown");
    }
}

// Step 3: Define the abstract factory interface
public interface UIFactory {
    Button createButton();
    Checkbox createCheckbox();
    Dropdown createDropdown();
}

// Step 4: Implement concrete factories
public class WindowsUIFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }

    @Override
    public Dropdown createDropdown() {
        return new WindowsDropdown();
    }
}

public class MacOSUIFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new MacOSButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacOSCheckbox();
    }

    @Override
    public Dropdown createDropdown() {
        return new MacOSDropdown();
    }
}

public class LinuxUIFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new LinuxButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new LinuxCheckbox();
    }

    @Override
    public Dropdown createDropdown() {
        return new LinuxDropdown();
    }
}
```

### Usage Example

```java
public class CrossPlatformApplication {
    private Button loginButton;
    private Checkbox rememberMeCheckbox;
    private Dropdown countryDropdown;
    
    public CrossPlatformApplication(UIFactory factory) {
        loginButton = factory.createButton();
        rememberMeCheckbox = factory.createCheckbox();
        countryDropdown = factory.createDropdown();
        
        // Add options to the dropdown
        countryDropdown.addOption("USA");
        countryDropdown.addOption("UK");
        countryDropdown.addOption("Canada");
        countryDropdown.addOption("Australia");
    }
    
    public void render() {
        System.out.println("\n=== Rendering Login Form ===");
        loginButton.render();
        rememberMeCheckbox.render();
        countryDropdown.render();
        System.out.println("============================\n");
    }
    
    public void simulateUserInteraction() {
        System.out.println("=== User Interaction Simulation ===");
        loginButton.onClick();
        rememberMeCheckbox.onToggle();
        countryDropdown.onSelect("Canada");
        System.out.println("==================================\n");
    }
    
    public static void main(String[] args) {
        // Determine the current OS
        String osName = System.getProperty("os.name").toLowerCase();
        UIFactory factory;
        
        // Create appropriate factory based on the OS
        if (osName.contains("windows")) {
            System.out.println("Detected Windows OS - using Windows UI components");
            factory = new WindowsUIFactory();
        } else if (osName.contains("mac")) {
            System.out.println("Detected macOS - using macOS UI components");
            factory = new MacOSUIFactory();
        } else {
            System.out.println("Detected Linux/Other OS - using Linux UI components");
            factory = new LinuxUIFactory();
        }
        
        // Create and run the application with the appropriate factory
        CrossPlatformApplication app = new CrossPlatformApplication(factory);
        app.render();
        app.simulateUserInteraction();
        
        // For demonstration purposes, let's also show how the app would look on other platforms
        System.out.println("--- Demo: Same application on Windows ---");
        CrossPlatformApplication windowsApp = new CrossPlatformApplication(new WindowsUIFactory());
        windowsApp.render();
        
        System.out.println("--- Demo: Same application on macOS ---");
        CrossPlatformApplication macApp = new CrossPlatformApplication(new MacOSUIFactory());
        macApp.render();
        
        System.out.println("--- Demo: Same application on Linux ---");
        CrossPlatformApplication linuxApp = new CrossPlatformApplication(new LinuxUIFactory());
        linuxApp.render();
    }
}
```

## Benefits

1. **Isolation of concrete classes**: The pattern helps you control the classes of objects that an application creates
2. **Exchanging product families easily**: You can swap entire families of products by changing just the concrete factory
3. **Consistency among products**: When products in a family are designed to work together, the Abstract Factory ensures this consistency
4. **Single Responsibility Principle**: Isolates the creation of objects from their use

## Considerations

1. **Supporting new kinds of products**: Extending abstract factories to produce new types of products can be difficult
2. **Complexity**: The pattern introduces a lot of new interfaces and classes to implement a concept that may not be necessary in simpler scenarios
3. **Implementation inheritance**: Often relies on implementation inheritance for abstract products

## When to Use

- When a system should be independent of how its products are created, composed, and represented
- When a system should be configured with one of multiple families of products
- When you want to provide a class library of products, and you want to reveal just their interfaces, not their implementations
- When a family of related product objects is designed to be used together
