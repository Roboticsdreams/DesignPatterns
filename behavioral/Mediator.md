# Mediator Pattern

[Back to Home](../README.md)

## Intent

Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly, allowing you to vary their interaction independently.

## Explanation

The Mediator pattern reduces chaotic dependencies between objects by restricting direct communications and forcing objects to collaborate only through a mediator object. Instead of components communicating directly with each other, they send messages to the mediator, which then forwards the messages to the appropriate components.

## Real-World Example: Air Traffic Control System

An air traffic control tower (mediator) coordinates the actions of multiple aircraft (colleagues). Planes don't communicate directly with each other but instead coordinate through the control tower to prevent collisions and ensure safe takeoffs and landings.

### Implementation

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Step 1: Create the Mediator interface
public interface AirTrafficControlMediator {
    void registerAircraft(Aircraft aircraft);
    void sendMessage(String message, Aircraft sender);
    void requestLanding(Aircraft aircraft);
    void requestTakeoff(Aircraft aircraft);
}

// Step 2: Create the Colleague interface
public abstract class Aircraft {
    protected String callSign;
    protected AirTrafficControlMediator mediator;
    protected double fuel;
    protected boolean isAirborne;
    protected String status;
    
    public Aircraft(String callSign, AirTrafficControlMediator mediator) {
        this.callSign = callSign;
        this.mediator = mediator;
        this.fuel = 100.0; // 100% fuel
        this.isAirborne = false;
        this.status = "Idle";
        
        // Register with the mediator
        if (mediator != null) {
            mediator.registerAircraft(this);
        }
    }
    
    // Abstract methods to be implemented by concrete colleagues
    public abstract void receiveMessage(String message);
    
    // Common methods for all aircraft
    public void sendMessage(String message) {
        System.out.println(callSign + " sends message: " + message);
        mediator.sendMessage(message, this);
    }
    
    public void requestLanding() {
        System.out.println(callSign + " requests landing permission");
        mediator.requestLanding(this);
    }
    
    public void requestTakeoff() {
        System.out.println(callSign + " requests takeoff permission");
        mediator.requestTakeoff(this);
    }
    
    public void land() {
        if (isAirborne) {
            System.out.println(callSign + " is landing");
            isAirborne = false;
            status = "Landed";
        } else {
            System.out.println(callSign + " is already on the ground");
        }
    }
    
    public void takeoff() {
        if (!isAirborne) {
            System.out.println(callSign + " is taking off");
            isAirborne = true;
            status = "Airborne";
        } else {
            System.out.println(callSign + " is already airborne");
        }
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getCallSign() {
        return callSign;
    }
    
    public boolean isAirborne() {
        return isAirborne;
    }
    
    public double getFuel() {
        return fuel;
    }
    
    public void consumeFuel(double amount) {
        this.fuel = Math.max(0, this.fuel - amount);
        if (this.fuel < 20) {
            System.out.println("WARNING: " + callSign + " is low on fuel (" + String.format("%.1f", this.fuel) + "%)");
        }
    }
    
    @Override
    public String toString() {
        return "Aircraft " + callSign + " [" + status + ", " + 
               (isAirborne ? "Airborne" : "On ground") + ", Fuel: " + 
               String.format("%.1f", fuel) + "%]";
    }
}

// Step 3: Create concrete colleagues
public class PassengerAircraft extends Aircraft {
    private int passengerCapacity;
    
    public PassengerAircraft(String callSign, AirTrafficControlMediator mediator, int passengerCapacity) {
        super(callSign, mediator);
        this.passengerCapacity = passengerCapacity;
    }
    
    @Override
    public void receiveMessage(String message) {
        System.out.println("Passenger Aircraft " + callSign + " receives message: " + message);
    }
    
    public void boardPassengers() {
        if (!isAirborne) {
            System.out.println("Passenger Aircraft " + callSign + " is boarding passengers");
            setStatus("Boarding");
        } else {
            System.out.println("Cannot board passengers while airborne");
        }
    }
    
    @Override
    public String toString() {
        return "Passenger " + super.toString() + " - Capacity: " + passengerCapacity + " passengers";
    }
}

public class CargoAircraft extends Aircraft {
    private double cargoCapacity; // in tons
    
    public CargoAircraft(String callSign, AirTrafficControlMediator mediator, double cargoCapacity) {
        super(callSign, mediator);
        this.cargoCapacity = cargoCapacity;
    }
    
    @Override
    public void receiveMessage(String message) {
        System.out.println("Cargo Aircraft " + callSign + " receives message: " + message);
    }
    
    public void loadCargo() {
        if (!isAirborne) {
            System.out.println("Cargo Aircraft " + callSign + " is loading cargo");
            setStatus("Loading cargo");
        } else {
            System.out.println("Cannot load cargo while airborne");
        }
    }
    
    @Override
    public String toString() {
        return "Cargo " + super.toString() + " - Capacity: " + cargoCapacity + " tons";
    }
}

public class EmergencyAircraft extends Aircraft {
    private String emergencyType;
    
    public EmergencyAircraft(String callSign, AirTrafficControlMediator mediator, String emergencyType) {
        super(callSign, mediator);
        this.emergencyType = emergencyType;
    }
    
    @Override
    public void receiveMessage(String message) {
        System.out.println("Emergency Aircraft " + callSign + " receives message: " + message);
    }
    
    public void declareEmergency(String details) {
        System.out.println("EMERGENCY: " + callSign + " declares " + emergencyType + " emergency: " + details);
        sendMessage("MAYDAY MAYDAY MAYDAY. " + details);
        setStatus("Emergency - " + emergencyType);
    }
    
    // Emergency aircraft get priority landing
    @Override
    public void requestLanding() {
        System.out.println("EMERGENCY LANDING REQUEST from " + callSign);
        mediator.requestLanding(this);
    }
    
    @Override
    public String toString() {
        return "Emergency " + super.toString() + " - Type: " + emergencyType;
    }
}

// Step 4: Create the Concrete Mediator
public class ControlTower implements AirTrafficControlMediator {
    private Map<String, Aircraft> registeredAircraft;
    private List<Aircraft> landingQueue;
    private List<Aircraft> takeoffQueue;
    private int maxSimultaneousLandings;
    private int maxSimultaneousTakeoffs;
    private int activeRunways;
    
    public ControlTower(int runways) {
        this.registeredAircraft = new HashMap<>();
        this.landingQueue = new ArrayList<>();
        this.takeoffQueue = new ArrayList<>();
        this.activeRunways = runways;
        this.maxSimultaneousLandings = runways / 2 + runways % 2; // Slightly favor landings
        this.maxSimultaneousTakeoffs = runways / 2;
    }
    
    @Override
    public void registerAircraft(Aircraft aircraft) {
        registeredAircraft.put(aircraft.getCallSign(), aircraft);
        System.out.println("Control Tower: Registered " + aircraft.getCallSign());
    }
    
    @Override
    public void sendMessage(String message, Aircraft sender) {
        System.out.println("Control Tower receives message from " + sender.getCallSign() + ": " + message);
        
        // Broadcast message to all other aircraft
        for (Aircraft aircraft : registeredAircraft.values()) {
            if (aircraft != sender) {
                aircraft.receiveMessage("Broadcast from " + sender.getCallSign() + ": " + message);
            }
        }
    }
    
    @Override
    public void requestLanding(Aircraft aircraft) {
        // Emergency aircraft get immediate landing permission
        if (aircraft instanceof EmergencyAircraft) {
            System.out.println("Control Tower: EMERGENCY LANDING APPROVED for " + aircraft.getCallSign());
            aircraft.land();
            return;
        }
        
        // Handle normal landing requests
        if (landingQueue.contains(aircraft)) {
            System.out.println("Control Tower: " + aircraft.getCallSign() + " already in landing queue");
            return;
        }
        
        landingQueue.add(aircraft);
        System.out.println("Control Tower: Added " + aircraft.getCallSign() + " to landing queue");
        aircraft.setStatus("In landing queue");
        
        processLandingQueue();
    }
    
    @Override
    public void requestTakeoff(Aircraft aircraft) {
        if (!aircraft.isAirborne()) {
            if (takeoffQueue.contains(aircraft)) {
                System.out.println("Control Tower: " + aircraft.getCallSign() + " already in takeoff queue");
                return;
            }
            
            takeoffQueue.add(aircraft);
            System.out.println("Control Tower: Added " + aircraft.getCallSign() + " to takeoff queue");
            aircraft.setStatus("In takeoff queue");
            
            processTakeoffQueue();
        } else {
            System.out.println("Control Tower: Cannot process takeoff request for airborne aircraft " + 
                             aircraft.getCallSign());
        }
    }
    
    private void processLandingQueue() {
        int availableSlots = maxSimultaneousLandings;
        List<Aircraft> processedAircraft = new ArrayList<>();
        
        for (Aircraft aircraft : landingQueue) {
            if (availableSlots > 0) {
                System.out.println("Control Tower: Landing approved for " + aircraft.getCallSign());
                aircraft.land();
                processedAircraft.add(aircraft);
                availableSlots--;
            } else {
                System.out.println("Control Tower: " + aircraft.getCallSign() + 
                                 " please hold, all landing slots occupied");
                aircraft.setStatus("Holding for landing");
            }
        }
        
        // Remove processed aircraft from queue
        landingQueue.removeAll(processedAircraft);
    }
    
    private void processTakeoffQueue() {
        int availableSlots = maxSimultaneousTakeoffs;
        List<Aircraft> processedAircraft = new ArrayList<>();
        
        for (Aircraft aircraft : takeoffQueue) {
            if (availableSlots > 0) {
                System.out.println("Control Tower: Takeoff approved for " + aircraft.getCallSign());
                aircraft.takeoff();
                processedAircraft.add(aircraft);
                availableSlots--;
            } else {
                System.out.println("Control Tower: " + aircraft.getCallSign() + 
                                 " please wait, all takeoff slots occupied");
                aircraft.setStatus("Waiting for takeoff");
            }
        }
        
        // Remove processed aircraft from queue
        takeoffQueue.removeAll(processedAircraft);
    }
    
    public void sendStatusUpdateToAll() {
        String message = "Control Tower status update: " + 
                        landingQueue.size() + " aircraft in landing queue, " +
                        takeoffQueue.size() + " aircraft in takeoff queue";
        
        // Broadcast message to all aircraft
        for (Aircraft aircraft : registeredAircraft.values()) {
            aircraft.receiveMessage(message);
        }
    }
    
    public void printStatus() {
        System.out.println("\n===== CONTROL TOWER STATUS =====");
        System.out.println("Active runways: " + activeRunways);
        System.out.println("Landing slots: " + maxSimultaneousLandings);
        System.out.println("Takeoff slots: " + maxSimultaneousTakeoffs);
        System.out.println("Aircraft in landing queue: " + landingQueue.size());
        for (Aircraft aircraft : landingQueue) {
            System.out.println("  - " + aircraft.getCallSign());
        }
        System.out.println("Aircraft in takeoff queue: " + takeoffQueue.size());
        for (Aircraft aircraft : takeoffQueue) {
            System.out.println("  - " + aircraft.getCallSign());
        }
        System.out.println("All registered aircraft:");
        for (Aircraft aircraft : registeredAircraft.values()) {
            System.out.println("  - " + aircraft);
        }
        System.out.println("===============================\n");
    }
}
```

### Usage Example

```java
public class AirTrafficControlDemo {
    public static void main(String[] args) {
        // Create the mediator
        ControlTower controlTower = new ControlTower(3); // 3 runways
        
        // Create aircraft
        PassengerAircraft flight101 = new PassengerAircraft("Flight101", controlTower, 150);
        PassengerAircraft flight202 = new PassengerAircraft("Flight202", controlTower, 300);
        PassengerAircraft flight303 = new PassengerAircraft("Flight303", controlTower, 220);
        CargoAircraft cargo1 = new CargoAircraft("Cargo1", controlTower, 50.5);
        CargoAircraft cargo2 = new CargoAircraft("Cargo2", controlTower, 75.0);
        EmergencyAircraft ambulance1 = new EmergencyAircraft("Medevac1", controlTower, "Medical");
        
        // Initial status
        controlTower.printStatus();
        
        // Simulate some activities
        System.out.println("\n=== Simulation Start ===\n");
        
        // First round of operations
        flight101.takeoff();
        flight202.takeoff();
        cargo1.loadCargo();
        cargo1.requestTakeoff();
        
        controlTower.printStatus();
        
        // Simulate passage of time
        System.out.println("\n=== 30 minutes later ===\n");
        
        // Consume some fuel for airborne aircraft
        flight101.consumeFuel(10);
        flight202.consumeFuel(12);
        cargo1.consumeFuel(15);
        
        // More operations
        flight303.boardPassengers();
        flight303.requestTakeoff();
        cargo2.loadCargo();
        cargo2.requestTakeoff();
        
        controlTower.printStatus();
        
        // Simulate passage of time
        System.out.println("\n=== 1 hour later ===\n");
        
        // Consume more fuel
        flight101.consumeFuel(25);
        flight202.consumeFuel(20);
        flight303.consumeFuel(10);
        cargo1.consumeFuel(30);
        cargo2.consumeFuel(15);
        
        // Requesting landings
        flight101.requestLanding();
        flight202.requestLanding();
        flight303.requestLanding(); // This will wait in the queue
        
        controlTower.printStatus();
        
        // Simulate an emergency
        System.out.println("\n=== EMERGENCY SITUATION ===\n");
        
        ambulance1.takeoff();
        controlTower.sendStatusUpdateToAll();
        
        // The emergency aircraft is on a mission
        ambulance1.consumeFuel(60); // Heavy fuel consumption during emergency operations
        
        // Now it needs to land urgently
        System.out.println("\nEmergency aircraft returning to airport");
        ambulance1.declareEmergency("Critical patient onboard, low fuel");
        ambulance1.requestLanding();
        
        // Process landing queue after emergency landing
        controlTower.printStatus();
        
        // Final operations
        System.out.println("\n=== Final Operations ===\n");
        
        cargo1.requestLanding();
        cargo2.requestLanding();
        
        controlTower.printStatus();
        
        // Demonstrate direct communication between aircraft
        System.out.println("\n=== Direct Communication Attempt ===\n");
        flight101.sendMessage("Experiencing turbulence at 30,000 feet");
        
        System.out.println("\n=== End of Simulation ===");
    }
}
```

## Another Example: Chat Room Mediator

Here's another implementation of the Mediator pattern for a chat application:

```java
// Mediator interface
interface ChatMediator {
    void sendMessage(String message, User sender);
    void addUser(User user);
    void removeUser(User user);
}

// Colleague abstract class
abstract class User {
    protected String name;
    protected ChatMediator mediator;
    
    public User(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }
    
    public abstract void send(String message);
    public abstract void receive(String message);
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof User)) {
            return false;
        }
        return name.equals(((User)obj).name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

// Concrete Colleague
class ChatUser extends User {
    public ChatUser(String name, ChatMediator mediator) {
        super(name, mediator);
    }
    
    @Override
    public void send(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);
    }
    
    @Override
    public void receive(String message) {
        System.out.println(name + " receives: " + message);
    }
}

// Admin user with special privileges
class AdminUser extends User {
    public AdminUser(String name, ChatMediator mediator) {
        super(name, mediator);
    }
    
    @Override
    public void send(String message) {
        System.out.println("ADMIN " + name + " sends: " + message);
        mediator.sendMessage(message, this);
    }
    
    @Override
    public void receive(String message) {
        System.out.println("ADMIN " + name + " receives: " + message);
    }
    
    public void sendWarning(String message) {
        System.out.println("ADMIN " + name + " sends warning: " + message);
        mediator.sendMessage("WARNING: " + message, this);
    }
    
    public void kickUser(User user) {
        System.out.println("ADMIN " + name + " kicks user: " + user.name);
        mediator.removeUser(user);
    }
}

// Concrete Mediator
class ChatRoom implements ChatMediator {
    private List<User> users;
    private String roomName;
    
    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.users = new ArrayList<>();
    }
    
    @Override
    public void sendMessage(String message, User sender) {
        System.out.println("\n[" + roomName + " Room] Message from " + sender.name);
        
        for (User user : new ArrayList<>(users)) {
            // Don't send message back to sender
            if (!user.equals(sender)) {
                user.receive(message);
            }
        }
    }
    
    @Override
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            System.out.println(user.name + " joined [" + roomName + " Room]");
        }
    }
    
    @Override
    public void removeUser(User user) {
        if (users.remove(user)) {
            System.out.println(user.name + " left [" + roomName + " Room]");
            // Notify remaining users
            for (User remaining : users) {
                remaining.receive("System: " + user.name + " has left the chat.");
            }
        }
    }
    
    public void displayUsers() {
        System.out.println("\n[" + roomName + " Room] Current users:");
        for (User user : users) {
            System.out.println("- " + user.name + (user instanceof AdminUser ? " (admin)" : ""));
        }
        System.out.println();
    }
}
```

## Benefits

1. **Reduced coupling**: Objects no longer communicate directly with each other
2. **Centralized control**: Interactions between objects are consolidated in the mediator
3. **Simplified object protocols**: Colleagues have simpler interfaces since they communicate only with the mediator
4. **Easier maintenance**: Changes to the interaction logic are localized in the mediator
5. **Reusability**: Colleagues can be reused with different mediators

## Considerations

1. **Potential bottleneck**: The mediator can become a central point of failure and a bottleneck
2. **Complexity shift**: While colleague classes are simplified, the mediator can become complex
3. **God object**: The mediator may grow into a "god object" that's difficult to maintain
4. **Indirection overhead**: Additional layer of indirection can impact performance

## When to Use

- When multiple objects communicate in well-defined but complex ways
- When reusing an object is difficult because it communicates with many other objects
- When you want to customize interaction behavior without subclassing
- When a set of objects must work together in a highly structured way
