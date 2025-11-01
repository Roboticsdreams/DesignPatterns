# Decorator Pattern

[Back to Home](../README.md)

## Intent

Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

## Explanation

The Decorator pattern allows behavior to be added to individual objects, either statically or dynamically, without affecting the behavior of other objects from the same class. It follows the Open/Closed Principle by enabling functionality to be added without changing existing code.

## Real-World Example: Coffee Ordering System

In a coffee shop application, customers can order various types of coffee with different add-ons like milk, sugar, whipped cream, etc. Instead of creating a separate class for each combination, the Decorator pattern allows us to add these "decorations" dynamically.

### Implementation

```java
// Step 1: Define the component interface
public interface Coffee {
    String getDescription();
    double getCost();
}

// Step 2: Create concrete component implementations
public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
    
    @Override
    public double getCost() {
        return 1.0;
    }
}

public class Espresso implements Coffee {
    @Override
    public String getDescription() {
        return "Espresso";
    }
    
    @Override
    public double getCost() {
        return 1.99;
    }
}

// Step 3: Create abstract decorator class
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}

// Step 4: Create concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", with Milk";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.5;
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", with Sugar";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.2;
    }
}

public class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", with Whipped Cream";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.7;
    }
}

public class ChocolateDecorator extends CoffeeDecorator {
    public ChocolateDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", with Chocolate";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.6;
    }
}

// Enhanced example with sizes
public enum Size { SMALL, MEDIUM, LARGE }

public class SizeDecorator extends CoffeeDecorator {
    private Size size;
    
    public SizeDecorator(Coffee coffee, Size size) {
        super(coffee);
        this.size = size;
    }
    
    @Override
    public String getDescription() {
        return size.name().toLowerCase() + " " + decoratedCoffee.getDescription();
    }
    
    @Override
    public double getCost() {
        double cost = decoratedCoffee.getCost();
        switch (size) {
            case SMALL: return cost;
            case MEDIUM: return cost + 0.5;
            case LARGE: return cost + 1.0;
            default: return cost;
        }
    }
}
```

### Usage Example

```java
public class CoffeeShop {
    public static void main(String[] args) {
        // Order a simple coffee
        Coffee coffee = new SimpleCoffee();
        printCoffee(coffee);
        
        // Decorate with milk
        Coffee milkCoffee = new MilkDecorator(new SimpleCoffee());
        printCoffee(milkCoffee);
        
        // Order espresso with sugar and whipped cream
        Coffee fancyEspresso = new WhipDecorator(new SugarDecorator(new Espresso()));
        printCoffee(fancyEspresso);
        
        // Create a large espresso with milk, sugar, and chocolate
        Coffee ultimateCoffee = new SizeDecorator(
            new ChocolateDecorator(
                new SugarDecorator(
                    new MilkDecorator(
                        new Espresso()
                    )
                )
            ),
            Size.LARGE
        );
        printCoffee(ultimateCoffee);
        
        // Let's build a coffee order step by step
        Coffee customCoffee = new Espresso();
        System.out.println("Starting with: " + customCoffee.getDescription() + 
                         " - $" + customCoffee.getCost());
        
        customCoffee = new SizeDecorator(customCoffee, Size.MEDIUM);
        System.out.println("Sizing up: " + customCoffee.getDescription() + 
                         " - $" + customCoffee.getCost());
        
        customCoffee = new MilkDecorator(customCoffee);
        System.out.println("Adding milk: " + customCoffee.getDescription() + 
                         " - $" + customCoffee.getCost());
        
        customCoffee = new WhipDecorator(customCoffee);
        System.out.println("Adding whipped cream: " + customCoffee.getDescription() + 
                         " - $" + customCoffee.getCost());
        
        System.out.println("Final order: " + customCoffee.getDescription() + 
                         " - $" + customCoffee.getCost());
    }
    
    private static void printCoffee(Coffee coffee) {
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());
    }
}
```

## More Advanced Example: Notifier System

Let's see another real-world example where the Decorator pattern shines - a notification system that can send notifications through multiple channels:

```java
// Base Component
public interface Notifier {
    void send(String message);
}

// Concrete Component
public class BasicNotifier implements Notifier {
    private String username;
    
    public BasicNotifier(String username) {
        this.username = username;
    }
    
    @Override
    public void send(String message) {
        System.out.println("Default notification channel: " + message);
    }
}

// Base Decorator
public abstract class NotifierDecorator implements Notifier {
    protected Notifier wrappedNotifier;
    
    public NotifierDecorator(Notifier notifier) {
        this.wrappedNotifier = notifier;
    }
    
    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}

// Concrete Decorators
public class EmailNotifier extends NotifierDecorator {
    private String emailAddress;
    
    public EmailNotifier(Notifier notifier, String emailAddress) {
        super(notifier);
        this.emailAddress = emailAddress;
    }
    
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending email to " + emailAddress + ": " + message);
    }
}

public class SMSNotifier extends NotifierDecorator {
    private String phoneNumber;
    
    public SMSNotifier(Notifier notifier, String phoneNumber) {
        super(notifier);
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending SMS to " + phoneNumber + ": " + message);
    }
}

public class SlackNotifier extends NotifierDecorator {
    private String channel;
    
    public SlackNotifier(Notifier notifier, String channel) {
        super(notifier);
        this.channel = channel;
    }
    
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Posting to Slack channel " + channel + ": " + message);
    }
}

public class PushNotifier extends NotifierDecorator {
    private String deviceToken;
    
    public PushNotifier(Notifier notifier, String deviceToken) {
        super(notifier);
        this.deviceToken = deviceToken;
    }
    
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending push notification to device " + deviceToken + ": " + message);
    }
}
```

### Usage of the Notifier System

```java
public class NotifierDemo {
    public static void main(String[] args) {
        // Create the base notifier
        Notifier notifier = new BasicNotifier("john_doe");
        
        // User who wants notifications via email and SMS
        Notifier multiNotifier = new SMSNotifier(
            new EmailNotifier(notifier, "john.doe@example.com"),
            "+1234567890"
        );
        
        multiNotifier.send("System maintenance scheduled");
        
        System.out.println("\n--- Setting up notifications for a team lead ---");
        
        // Team lead who wants notifications everywhere
        Notifier teamLeadNotifier = new PushNotifier(
            new SlackNotifier(
                new EmailNotifier(
                    new BasicNotifier("jane_smith"),
                    "jane.smith@example.com"
                ),
                "#team-updates"
            ),
            "DEVICE-TOKEN-123"
        );
        
        teamLeadNotifier.send("Urgent: Server down!");
    }
}
```

## Benefits

1. **Greater flexibility than static inheritance**: Allows adding or removing responsibilities from objects at runtime
2. **Avoids feature-laden classes high up in the hierarchy**: Features can be composed from simple classes rather than built into monolithic ones
3. **Complies with Single Responsibility Principle**: Each decorator class has a single responsibility
4. **Open/Closed Principle**: New functionality can be added without modifying existing code

## Considerations

1. **Complexity**: Can result in many small objects with similar interfaces
2. **Order of decoration**: The order of applying decorators can sometimes be important
3. **Identity confusion**: A decorated component isn't identical to the component itself, so don't rely on object identity

## When to Use

- When you need to add responsibilities to objects dynamically and transparently
- When extension by subclassing is impractical due to a large number of independent extensions
- When you want to add functionality to individual objects without affecting others
