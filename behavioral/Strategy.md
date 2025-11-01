# Strategy Pattern

[Back to Home](../README.md)

## Intent

Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

## Explanation

The Strategy pattern enables selecting an algorithm's behavior at runtime. Instead of implementing a single algorithm directly, code receives run-time instructions specifying which algorithm to use.

## Real-World Example: Payment Processing Strategy

In an e-commerce application, customers can choose different payment methods like credit card, PayPal, or bank transfer. The Strategy pattern allows us to encapsulate each payment method as a strategy and select the appropriate one at runtime based on the customer's choice.

### Implementation

```java
// Step 1: Define the strategy interface
public interface PaymentStrategy {
    boolean pay(double amount);
    String getDescription();
}

// Step 2: Implement concrete strategies
public class CreditCardStrategy implements PaymentStrategy {
    private String name;
    private String cardNumber;
    private String cvv;
    private String expirationDate;
    
    public CreditCardStrategy(String name, String cardNumber, 
                             String cvv, String expirationDate) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
    }
    
    @Override
    public boolean pay(double amount) {
        // In a real application, this would connect to a payment gateway
        System.out.println(amount + " paid with credit card ending with " + 
                          cardNumber.substring(cardNumber.length() - 4));
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Credit Card (" + cardNumber.substring(cardNumber.length() - 4) + ")";
    }
}

public class PayPalStrategy implements PaymentStrategy {
    private String email;
    private String password;
    
    public PayPalStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean pay(double amount) {
        // In a real application, this would connect to PayPal API
        System.out.println(amount + " paid using PayPal account " + email);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "PayPal (" + email + ")";
    }
}

public class BankTransferStrategy implements PaymentStrategy {
    private String bankName;
    private String accountNumber;
    
    public BankTransferStrategy(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }
    
    @Override
    public boolean pay(double amount) {
        // In a real application, this would initiate a bank transfer
        System.out.println(amount + " paid by bank transfer from account " + 
                          "xxxx" + accountNumber.substring(accountNumber.length() - 4) + 
                          " at " + bankName);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Bank Transfer (" + bankName + ")";
    }
}

public class CryptocurrencyStrategy implements PaymentStrategy {
    private String walletAddress;
    private String cryptocurrency;
    
    public CryptocurrencyStrategy(String walletAddress, String cryptocurrency) {
        this.walletAddress = walletAddress;
        this.cryptocurrency = cryptocurrency;
    }
    
    @Override
    public boolean pay(double amount) {
        // In a real application, this would initiate a crypto transaction
        System.out.println(amount + " USD equivalent paid using " + cryptocurrency + 
                          " from wallet " + 
                          walletAddress.substring(0, 6) + "..." +
                          walletAddress.substring(walletAddress.length() - 4));
        return true;
    }
    
    @Override
    public String getDescription() {
        return cryptocurrency + " payment";
    }
}

// Step 3: Create a context that uses the strategy
public class ShoppingCart {
    private List<Item> items;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public void removeItem(Item item) {
        items.remove(item);
    }
    
    public double calculateTotal() {
        return items.stream().mapToDouble(Item::getPrice).sum();
    }
    
    public boolean pay(PaymentStrategy paymentStrategy) {
        double amount = calculateTotal();
        return paymentStrategy.pay(amount);
    }
    
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
}

// Supporting class
public class Item {
    private String id;
    private String name;
    private double price;
    
    public Item(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return name + " - $" + price;
    }
}
```

### Usage Example

```java
public class PaymentDemo {
    public static void main(String[] args) {
        // Create shopping cart and add items
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item("1", "Laptop", 999.99));
        cart.addItem(new Item("2", "Headphones", 99.99));
        cart.addItem(new Item("3", "Mouse", 29.99));
        
        // Display cart items and total
        System.out.println("Shopping Cart:");
        for (Item item : cart.getItems()) {
            System.out.println(" - " + item);
        }
        System.out.println("Total: $" + cart.calculateTotal());
        
        // Process payment with credit card
        System.out.println("\nChoosing payment method: Credit Card");
        PaymentStrategy creditCardStrategy = new CreditCardStrategy(
            "John Doe",
            "4111111111111111",
            "123",
            "12/2025"
        );
        boolean creditCardResult = cart.pay(creditCardStrategy);
        System.out.println("Payment status: " + (creditCardResult ? "Success" : "Failed"));
        
        // Create a new cart for another example
        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItem(new Item("4", "Smartphone", 799.99));
        cart2.addItem(new Item("5", "Charger", 19.99));
        
        // Display cart items and total
        System.out.println("\nShopping Cart 2:");
        for (Item item : cart2.getItems()) {
            System.out.println(" - " + item);
        }
        System.out.println("Total: $" + cart2.calculateTotal());
        
        // Process payment with PayPal
        System.out.println("\nChoosing payment method: PayPal");
        PaymentStrategy payPalStrategy = new PayPalStrategy(
            "john.doe@example.com",
            "password123"
        );
        boolean payPalResult = cart2.pay(payPalStrategy);
        System.out.println("Payment status: " + (payPalResult ? "Success" : "Failed"));
        
        // Another cart with bank transfer
        ShoppingCart cart3 = new ShoppingCart();
        cart3.addItem(new Item("6", "Monitor", 349.99));
        System.out.println("\nShopping Cart 3:");
        System.out.println(" - " + cart3.getItems().get(0));
        System.out.println("Total: $" + cart3.calculateTotal());
        
        System.out.println("\nChoosing payment method: Bank Transfer");
        PaymentStrategy bankStrategy = new BankTransferStrategy(
            "National Bank",
            "9876543210"
        );
        boolean bankResult = cart3.pay(bankStrategy);
        System.out.println("Payment status: " + (bankResult ? "Success" : "Failed"));
        
        // One more with cryptocurrency
        ShoppingCart cart4 = new ShoppingCart();
        cart4.addItem(new Item("7", "Gaming Console", 499.99));
        System.out.println("\nShopping Cart 4:");
        System.out.println(" - " + cart4.getItems().get(0));
        System.out.println("Total: $" + cart4.calculateTotal());
        
        System.out.println("\nChoosing payment method: Bitcoin");
        PaymentStrategy cryptoStrategy = new CryptocurrencyStrategy(
            "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            "Bitcoin"
        );
        boolean cryptoResult = cart4.pay(cryptoStrategy);
        System.out.println("Payment status: " + (cryptoResult ? "Success" : "Failed"));
    }
}
```

## Another Example: Navigation Strategies

Here's another practical example of the Strategy pattern applied to a navigation app that can use different routing algorithms:

```java
// Strategy interface
public interface RoutingStrategy {
    List<String> calculateRoute(String start, String end);
    String getName();
    boolean supportsRealTimeUpdates();
}

// Concrete strategies
public class FastestRouteStrategy implements RoutingStrategy {
    @Override
    public List<String> calculateRoute(String start, String end) {
        // Logic to calculate fastest route using traffic data
        List<String> route = new ArrayList<>();
        route.add("Start at " + start);
        route.add("Take Highway 101");
        route.add("Exit at Main Street");
        route.add("Arrive at " + end);
        return route;
    }
    
    @Override
    public String getName() {
        return "Fastest Route";
    }
    
    @Override
    public boolean supportsRealTimeUpdates() {
        return true;
    }
}

public class ShortestRouteStrategy implements RoutingStrategy {
    @Override
    public List<String> calculateRoute(String start, String end) {
        // Logic to calculate shortest distance route
        List<String> route = new ArrayList<>();
        route.add("Start at " + start);
        route.add("Go straight on Oak Avenue");
        route.add("Turn right on Pine Street");
        route.add("Arrive at " + end);
        return route;
    }
    
    @Override
    public String getName() {
        return "Shortest Route";
    }
    
    @Override
    public boolean supportsRealTimeUpdates() {
        return false;
    }
}

public class ScenicRouteStrategy implements RoutingStrategy {
    @Override
    public List<String> calculateRoute(String start, String end) {
        // Logic to calculate scenic route
        List<String> route = new ArrayList<>();
        route.add("Start at " + start);
        route.add("Drive along the Coastal Highway");
        route.add("Pass by Mountain View Park");
        route.add("Continue on Lakeside Drive");
        route.add("Arrive at " + end);
        return route;
    }
    
    @Override
    public String getName() {
        return "Scenic Route";
    }
    
    @Override
    public boolean supportsRealTimeUpdates() {
        return false;
    }
}

// Context class
public class NavigationSystem {
    private RoutingStrategy routingStrategy;
    
    public void setRoutingStrategy(RoutingStrategy routingStrategy) {
        this.routingStrategy = routingStrategy;
        System.out.println("Navigation mode set to: " + routingStrategy.getName());
    }
    
    public List<String> navigateTo(String start, String end) {
        if (routingStrategy == null) {
            throw new IllegalStateException("Routing strategy not set");
        }
        
        System.out.println("Calculating " + routingStrategy.getName() + 
                         " from " + start + " to " + end);
        
        if (routingStrategy.supportsRealTimeUpdates()) {
            System.out.println("Using real-time traffic data for route calculation");
        }
        
        return routingStrategy.calculateRoute(start, end);
    }
}
```

### Using the Navigation System

```java
public class NavigationDemo {
    public static void main(String[] args) {
        NavigationSystem navigationSystem = new NavigationSystem();
        
        System.out.println("=== Trip Planning: Home to Office ===");
        
        // Set strategy to fastest route
        navigationSystem.setRoutingStrategy(new FastestRouteStrategy());
        List<String> fastestRoute = navigationSystem.navigateTo("Home", "Office");
        
        System.out.println("\nFastest Route Directions:");
        for (String direction : fastestRoute) {
            System.out.println(" - " + direction);
        }
        
        System.out.println("\n=== Weekend Trip Planning: Home to Beach ===");
        
        // Switch to scenic route strategy
        navigationSystem.setRoutingStrategy(new ScenicRouteStrategy());
        List<String> scenicRoute = navigationSystem.navigateTo("Home", "Beach");
        
        System.out.println("\nScenic Route Directions:");
        for (String direction : scenicRoute) {
            System.out.println(" - " + direction);
        }
        
        System.out.println("\n=== Walking Directions: Hotel to Restaurant ===");
        
        // Switch to shortest route strategy
        navigationSystem.setRoutingStrategy(new ShortestRouteStrategy());
        List<String> shortestRoute = navigationSystem.navigateTo("Hotel", "Restaurant");
        
        System.out.println("\nShortest Route Directions:");
        for (String direction : shortestRoute) {
            System.out.println(" - " + direction);
        }
    }
}
```

## Benefits

1. **Flexibility**: Allows changing algorithms used inside an object at runtime
2. **Eliminates conditional statements**: Replaces complex conditional logic with polymorphism
3. **Isolation of algorithm implementation**: Each algorithm can evolve independently
4. **Code reuse**: Strategy implementations can be shared between different contexts

## Considerations

1. **Increased number of objects**: The pattern requires creating a strategy object even for simple algorithms
2. **Client must be aware of different strategies**: The client needs to understand the differences to choose the right strategy
3. **Communication overhead**: Context and strategy may need to exchange data, which could create overhead

## When to Use

- When you want to define a class that will have one behavior that is similar to other behaviors in a list
- When you need different variants of an algorithm
- When an algorithm uses data that clients shouldn't know about
- When a class defines many behaviors, and these appear as multiple conditional statements in its operations
