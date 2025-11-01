# Factory Method Pattern

[Back to Home](../README.md)

## Intent

Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.

## Explanation

The Factory Method pattern provides an interface for creating objects but allows subclasses to alter the type of objects that will be created. This is particularly useful when a class cannot anticipate the type of objects it needs to create.

## Real-World Example: Payment Processing System

In an e-commerce application, we might need to process payments through different payment gateways like PayPal, Stripe, or credit cards. The Factory Method pattern allows us to create appropriate payment processors based on the customer's selection.

### Implementation

```java
// Step 1: Create the product interface
public interface PaymentProcessor {
    boolean processPayment(double amount);
    String getPaymentGateway();
}

// Step 2: Create concrete implementations of the product
public class PayPalProcessor implements PaymentProcessor {
    private String email;
    
    public PayPalProcessor(String email) {
        this.email = email;
    }
    
    @Override
    public boolean processPayment(double amount) {
        // Connect to PayPal API and process the payment
        System.out.println("Processing $" + amount + " payment using PayPal for account: " + email);
        // Logic for PayPal payment processing
        return true;
    }
    
    @Override
    public String getPaymentGateway() {
        return "PayPal";
    }
}

public class StripeProcessor implements PaymentProcessor {
    private String apiKey;
    private String cardToken;
    
    public StripeProcessor(String apiKey, String cardToken) {
        this.apiKey = apiKey;
        this.cardToken = cardToken;
    }
    
    @Override
    public boolean processPayment(double amount) {
        // Connect to Stripe API and process the payment
        System.out.println("Processing $" + amount + " payment using Stripe with card token: " + cardToken);
        // Logic for Stripe payment processing
        return true;
    }
    
    @Override
    public String getPaymentGateway() {
        return "Stripe";
    }
}

public class BankTransferProcessor implements PaymentProcessor {
    private String accountNumber;
    private String bankCode;
    
    public BankTransferProcessor(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }
    
    @Override
    public boolean processPayment(double amount) {
        // Connect to bank API and process the transfer
        System.out.println("Processing $" + amount + " payment using bank transfer to account: " + 
                          accountNumber + " with bank code: " + bankCode);
        // Logic for bank transfer processing
        return true;
    }
    
    @Override
    public String getPaymentGateway() {
        return "Bank Transfer";
    }
}

// Step 3: Create the creator abstract class with the factory method
public abstract class PaymentFactory {
    // This is the factory method
    public abstract PaymentProcessor createPaymentProcessor();
    
    // This is a template method that uses the factory method
    public boolean processPayment(double amount) {
        PaymentProcessor processor = createPaymentProcessor();
        System.out.println("Using payment gateway: " + processor.getPaymentGateway());
        return processor.processPayment(amount);
    }
}

// Step 4: Create concrete creator classes
public class PayPalPaymentFactory extends PaymentFactory {
    private String email;
    
    public PayPalPaymentFactory(String email) {
        this.email = email;
    }
    
    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new PayPalProcessor(email);
    }
}

public class StripePaymentFactory extends PaymentFactory {
    private String apiKey;
    private String cardToken;
    
    public StripePaymentFactory(String apiKey, String cardToken) {
        this.apiKey = apiKey;
        this.cardToken = cardToken;
    }
    
    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new StripeProcessor(apiKey, cardToken);
    }
}

public class BankTransferPaymentFactory extends PaymentFactory {
    private String accountNumber;
    private String bankCode;
    
    public BankTransferPaymentFactory(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }
    
    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new BankTransferProcessor(accountNumber, bankCode);
    }
}
```

### Usage Example

```java
public class PaymentDemo {
    public static void main(String[] args) {
        // Process payment using PayPal
        PaymentFactory paymentFactory = new PayPalPaymentFactory("customer@example.com");
        boolean payPalResult = paymentFactory.processPayment(99.99);
        System.out.println("PayPal Payment Result: " + (payPalResult ? "Success" : "Failed"));
        
        // Process payment using Stripe
        paymentFactory = new StripePaymentFactory("sk_test_123456", "tok_visa_123456");
        boolean stripeResult = paymentFactory.processPayment(149.99);
        System.out.println("Stripe Payment Result: " + (stripeResult ? "Success" : "Failed"));
        
        // Process payment using Bank Transfer
        paymentFactory = new BankTransferPaymentFactory("1234567890", "BANK123");
        boolean bankResult = paymentFactory.processPayment(299.99);
        System.out.println("Bank Transfer Result: " + (bankResult ? "Success" : "Failed"));
    }
}
```

## Benefits

1. **Decouples the creator from the products**: The client code works with abstract classes and interfaces, not concrete implementations
2. **Single Responsibility Principle**: Moves the product creation code into one place
3. **Open/Closed Principle**: You can introduce new types of products without breaking existing code
4. **Provides hooks for subclasses**: Gives subclasses the flexibility to change the type of objects being created

## Considerations

1. **Complexity**: Adds complexity by requiring many new subclasses
2. **Requires inheritance**: The pattern depends on inheritance rather than composition
3. **Implementation challenges**: Sometimes may require implementing all products even if not all are needed

## When to Use

- When a class can't anticipate the type of objects it must create
- When a class wants its subclasses to specify the objects it creates
- When you need to delegate responsibility to one of several helper subclasses, and you want to localize the knowledge of which helper subclass is the delegate
