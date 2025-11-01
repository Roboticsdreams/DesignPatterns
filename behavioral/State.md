# State Pattern

[Back to Home](../README.md)

## Intent

Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

## Explanation

The State pattern allows an object to change its behavior when its internal state changes. It appears as if the object changed its class. This pattern encapsulates state-specific behavior into separate state classes and delegates behavior to the current state object.

## Real-World Example: Package Delivery System

A package delivery system where a package goes through various states during shipping (ordered, in transit, delivered, etc.). Each state has different behaviors and transitions.

### Implementation

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Step 1: Define the State interface
public interface PackageState {
    void updateState(Package pkg);
    void next(Package pkg);
    void prev(Package pkg);
    void cancel(Package pkg);
    String getStatus();
    boolean canUpdate();
    boolean canCancel();
}

// Step 2: Create concrete state classes
public class OrderedState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package ordered", this);
    }
    
    @Override
    public void next(Package pkg) {
        pkg.setState(new ProcessingState());
    }
    
    @Override
    public void prev(Package pkg) {
        System.out.println("Package is in its root state.");
    }
    
    @Override
    public void cancel(Package pkg) {
        pkg.setState(new CancelledState());
    }
    
    @Override
    public String getStatus() {
        return "Ordered";
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean canCancel() {
        return true;
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

public class ProcessingState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package processing started", this);
    }
    
    @Override
    public void next(Package pkg) {
        pkg.setState(new ShippedState());
    }
    
    @Override
    public void prev(Package pkg) {
        pkg.setState(new OrderedState());
    }
    
    @Override
    public void cancel(Package pkg) {
        if (canCancel()) {
            pkg.setState(new CancelledState());
        } else {
            System.out.println("Cannot cancel package in " + getStatus() + " state.");
        }
    }
    
    @Override
    public String getStatus() {
        return "Processing";
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean canCancel() {
        return true;
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

public class ShippedState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package shipped", this);
    }
    
    @Override
    public void next(Package pkg) {
        pkg.setState(new InTransitState());
    }
    
    @Override
    public void prev(Package pkg) {
        pkg.setState(new ProcessingState());
    }
    
    @Override
    public void cancel(Package pkg) {
        if (canCancel()) {
            pkg.setState(new CancelledState());
        } else {
            System.out.println("Cannot cancel package in " + getStatus() + " state.");
        }
    }
    
    @Override
    public String getStatus() {
        return "Shipped";
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean canCancel() {
        return false; // Once shipped, cannot be cancelled
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

public class InTransitState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package in transit", this);
    }
    
    @Override
    public void next(Package pkg) {
        pkg.setState(new OutForDeliveryState());
    }
    
    @Override
    public void prev(Package pkg) {
        pkg.setState(new ShippedState());
    }
    
    @Override
    public void cancel(Package pkg) {
        if (canCancel()) {
            pkg.setState(new CancelledState());
        } else {
            System.out.println("Cannot cancel package in " + getStatus() + " state.");
        }
    }
    
    @Override
    public String getStatus() {
        return "In Transit";
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel in transit
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

public class OutForDeliveryState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package out for delivery", this);
    }
    
    @Override
    public void next(Package pkg) {
        pkg.setState(new DeliveredState());
    }
    
    @Override
    public void prev(Package pkg) {
        pkg.setState(new InTransitState());
    }
    
    @Override
    public void cancel(Package pkg) {
        if (canCancel()) {
            pkg.setState(new CancelledState());
        } else {
            System.out.println("Cannot cancel package in " + getStatus() + " state.");
        }
    }
    
    @Override
    public String getStatus() {
        return "Out For Delivery";
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel when out for delivery
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

public class DeliveredState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package delivered", this);
    }
    
    @Override
    public void next(Package pkg) {
        System.out.println("Package is already delivered.");
    }
    
    @Override
    public void prev(Package pkg) {
        pkg.setState(new OutForDeliveryState());
    }
    
    @Override
    public void cancel(Package pkg) {
        if (canCancel()) {
            pkg.setState(new CancelledState());
        } else {
            System.out.println("Cannot cancel package in " + getStatus() + " state.");
        }
    }
    
    @Override
    public String getStatus() {
        return "Delivered";
    }
    
    @Override
    public boolean canUpdate() {
        return false; // Cannot update after delivery
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel after delivery
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

public class CancelledState implements PackageState {
    @Override
    public void updateState(Package pkg) {
        pkg.addToHistory("Package cancelled", this);
    }
    
    @Override
    public void next(Package pkg) {
        System.out.println("Cancelled package cannot move to next state.");
    }
    
    @Override
    public void prev(Package pkg) {
        System.out.println("Cancelled package cannot move to previous state.");
    }
    
    @Override
    public void cancel(Package pkg) {
        System.out.println("Package is already cancelled.");
    }
    
    @Override
    public String getStatus() {
        return "Cancelled";
    }
    
    @Override
    public boolean canUpdate() {
        return false; // Cannot update cancelled package
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel already cancelled package
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

// Step 3: Create a class to track history events
public class HistoryEvent {
    private final String description;
    private final LocalDateTime timestamp;
    private final PackageState state;
    
    public HistoryEvent(String description, PackageState state) {
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.state = state;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public PackageState getState() {
        return state;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s - State: %s", timestamp.format(formatter), description, state.getStatus());
    }
}

// Step 4: Create the Context class
public class Package {
    private String id;
    private String description;
    private PackageState state;
    private List<HistoryEvent> history;
    private String currentLocation;
    
    public Package(String id, String description) {
        this.id = id;
        this.description = description;
        this.state = new OrderedState();
        this.history = new ArrayList<>();
        this.currentLocation = "Warehouse";
        
        // Initialize with the first state
        state.updateState(this);
    }
    
    public void setState(PackageState state) {
        this.state = state;
        state.updateState(this);
    }
    
    public PackageState getState() {
        return state;
    }
    
    public void nextState() {
        state.next(this);
    }
    
    public void previousState() {
        state.prev(this);
    }
    
    public void cancelDelivery() {
        state.cancel(this);
    }
    
    public void updateLocation(String location) {
        this.currentLocation = location;
        addToHistory("Package location updated to " + location, state);
    }
    
    public void addToHistory(String description, PackageState state) {
        history.add(new HistoryEvent(description, state));
    }
    
    public List<HistoryEvent> getHistory() {
        return new ArrayList<>(history);
    }
    
    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void printStatus() {
        System.out.println("\n=== Package " + id + " Status ===");
        System.out.println("Description: " + description);
        System.out.println("Current State: " + state.getStatus());
        System.out.println("Current Location: " + currentLocation);
        System.out.println("Can Update: " + (state.canUpdate() ? "Yes" : "No"));
        System.out.println("Can Cancel: " + (state.canCancel() ? "Yes" : "No"));
        System.out.println("=======================\n");
    }
    
    public void printHistory() {
        System.out.println("\n=== Package " + id + " History ===");
        for (HistoryEvent event : history) {
            System.out.println(event);
        }
        System.out.println("=======================\n");
    }
}
```

### Usage Example

```java
public class PackageDeliveryDemo {
    public static void main(String[] args) {
        // Create a new package
        Package pkg = new Package("PKG-123456", "Laptop Computer");
        
        // Check the initial status
        pkg.printStatus();
        
        // Move through the delivery process
        System.out.println("Processing the order...");
        pkg.nextState(); // Ordered -> Processing
        pkg.updateLocation("Sorting facility");
        pkg.printStatus();
        
        System.out.println("Shipping the package...");
        pkg.nextState(); // Processing -> Shipped
        pkg.updateLocation("Regional distribution center");
        pkg.printStatus();
        
        // Try to cancel the shipment
        System.out.println("Attempting to cancel the order...");
        pkg.cancelDelivery(); // Should fail because the package is already shipped
        
        System.out.println("Package in transit...");
        pkg.nextState(); // Shipped -> InTransit
        pkg.updateLocation("Local distribution center");
        pkg.printStatus();
        
        System.out.println("Package is out for delivery...");
        pkg.nextState(); // InTransit -> OutForDelivery
        pkg.updateLocation("Delivery vehicle");
        pkg.printStatus();
        
        System.out.println("Package has been delivered!");
        pkg.nextState(); // OutForDelivery -> Delivered
        pkg.updateLocation("Customer address");
        pkg.printStatus();
        
        // Print the full package history
        pkg.printHistory();
        
        // Try to modify a delivered package
        System.out.println("Trying to update a delivered package...");
        pkg.updateLocation("New location"); // Should add to history but not change state
        
        // Create another package to demonstrate cancellation
        Package pkg2 = new Package("PKG-987654", "Coffee Maker");
        System.out.println("Created a new package: " + pkg2.getId());
        pkg2.printStatus();
        
        System.out.println("Processing the order...");
        pkg2.nextState(); // Ordered -> Processing
        pkg2.printStatus();
        
        System.out.println("Cancelling the order...");
        pkg2.cancelDelivery(); // Should succeed because it's still in processing
        pkg2.printStatus();
        
        // Try to proceed with a cancelled order
        System.out.println("Trying to ship a cancelled order...");
        pkg2.nextState(); // Should not change state
        pkg2.printStatus();
        
        // Print the full history
        pkg2.printHistory();
    }
}
```

## Another Example: Document Approval System

Here's another implementation of the State pattern for a document approval workflow:

```java
// State interface
interface DocumentState {
    void review(Document document);
    void approve(Document document);
    void reject(Document document);
    String getStateName();
}

// Concrete States
class DraftState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("Document submitted for review");
        document.setState(new UnderReviewState());
    }

    @Override
    public void approve(Document document) {
        System.out.println("Cannot approve a draft document. It must be reviewed first.");
    }

    @Override
    public void reject(Document document) {
        System.out.println("Cannot reject a draft document. It must be reviewed first.");
    }

    @Override
    public String getStateName() {
        return "Draft";
    }
}

class UnderReviewState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("Document is already under review");
    }

    @Override
    public void approve(Document document) {
        System.out.println("Document has been approved");
        document.setState(new ApprovedState());
    }

    @Override
    public void reject(Document document) {
        System.out.println("Document has been rejected");
        document.setState(new RejectedState());
    }

    @Override
    public String getStateName() {
        return "Under Review";
    }
}

class ApprovedState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("Cannot review an approved document");
    }

    @Override
    public void approve(Document document) {
        System.out.println("Document is already approved");
    }

    @Override
    public void reject(Document document) {
        System.out.println("Cannot reject an approved document. Create a new revision instead.");
    }

    @Override
    public String getStateName() {
        return "Approved";
    }
}

class RejectedState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("Submitting revised document for review");
        document.setState(new UnderReviewState());
    }

    @Override
    public void approve(Document document) {
        System.out.println("Cannot approve a rejected document. It must be reviewed again.");
    }

    @Override
    public void reject(Document document) {
        System.out.println("Document is already rejected");
    }

    @Override
    public String getStateName() {
        return "Rejected";
    }
}

// Context
class Document {
    private String title;
    private String content;
    private DocumentState state;
    private List<String> history = new ArrayList<>();
    
    public Document(String title, String content) {
        this.title = title;
        this.content = content;
        this.state = new DraftState();
        addToHistory("Document created in " + state.getStateName() + " state");
    }
    
    public void setState(DocumentState state) {
        this.state = state;
        addToHistory("Document moved to " + state.getStateName() + " state");
    }
    
    public void submitForReview() {
        state.review(this);
    }
    
    public void approve() {
        state.approve(this);
    }
    
    public void reject() {
        state.reject(this);
    }
    
    public void updateContent(String newContent) {
        if (state.getStateName().equals("Draft") || state.getStateName().equals("Rejected")) {
            this.content = newContent;
            addToHistory("Document content updated");
        } else {
            System.out.println("Cannot update content in " + state.getStateName() + " state");
        }
    }
    
    private void addToHistory(String event) {
        history.add(event + " - " + LocalDateTime.now());
    }
    
    public void printStatus() {
        System.out.println("\n=== Document: " + title + " ===");
        System.out.println("State: " + state.getStateName());
        System.out.println("Content length: " + content.length() + " characters");
        System.out.println("==========================\n");
    }
    
    public void printHistory() {
        System.out.println("\n=== Document History ===");
        for (String event : history) {
            System.out.println(event);
        }
        System.out.println("=======================\n");
    }
}
```

## Benefits

1. **Single Responsibility Principle**: State-specific behaviors are isolated in separate classes
2. **Open/Closed Principle**: You can introduce new states without changing existing state classes
3. **Eliminates complex conditionals**: Replaces conditional logic with polymorphism
4. **Explicit state transitions**: Makes state transitions explicit and easier to manage
5. **State hierarchies**: Allows sharing of common functionality through inheritance of state classes

## Considerations

1. **Increased number of classes**: Can lead to a proliferation of state classes
2. **Complexity for simple state machines**: May be overkill for simple cases with few states and transitions
3. **Difficulty in adding new behavior**: Can be challenging to introduce new behavior that affects all states
4. **Thread safety concerns**: Multiple threads could cause race conditions during state transitions

## When to Use

- When an object's behavior changes based on its internal state
- When there are numerous state-specific behaviors and operations controlled by the state
- When conditional code related to state management becomes complex and hard to maintain
- When state transitions follow well-defined rules
