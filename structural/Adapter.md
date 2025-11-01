# Adapter Pattern

[Back to Home](../README.md)

## Intent

Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.

## Explanation

The Adapter pattern allows objects with incompatible interfaces to collaborate. It acts as a bridge between two incompatible interfaces by wrapping an instance of one class into an adapter class that presents the interface expected by clients.

## Real-World Example: Legacy Payment System Integration

Imagine your e-commerce application needs to integrate with both modern and legacy payment processing systems. Your modern code expects a standard interface, but the legacy system has a completely different interface. An adapter can make the legacy system compatible with your modern code.

### Implementation

```java
// Step 1: Define the target interface that the client expects to work with
public interface PaymentProcessor {
    boolean processPayment(PaymentDetails paymentDetails);
    PaymentStatus checkStatus(String paymentId);
    boolean refundPayment(String paymentId);
}

// Data classes for the modern system
public class PaymentDetails {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private double amount;
    private String currency;
    private String customerName;
    
    // Constructor, getters and setters
    public PaymentDetails(String cardNumber, String expiryDate, String cvv, 
                          double amount, String currency, String customerName) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.amount = amount;
        this.currency = currency;
        this.customerName = customerName;
    }
    
    // Getters
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCustomerName() { return customerName; }
}

public enum PaymentStatus {
    COMPLETED, PENDING, FAILED, REFUNDED
}

// Step 2: Create a modern implementation of the PaymentProcessor interface
public class ModernPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(PaymentDetails paymentDetails) {
        System.out.println("Processing payment through modern payment gateway");
        System.out.println("Amount: " + paymentDetails.getAmount() + " " + paymentDetails.getCurrency());
        System.out.println("Customer: " + paymentDetails.getCustomerName());
        // Modern payment processing logic
        return true;
    }
    
    @Override
    public PaymentStatus checkStatus(String paymentId) {
        System.out.println("Checking status of payment: " + paymentId);
        // Logic to check payment status
        return PaymentStatus.COMPLETED;
    }
    
    @Override
    public boolean refundPayment(String paymentId) {
        System.out.println("Refunding payment: " + paymentId);
        // Logic to refund payment
        return true;
    }
}

// Step 3: Define the legacy payment system interface (incompatible with PaymentProcessor)
public class LegacyPaymentSystem {
    public String submitTransaction(String cardNum, String expDate, 
                                   String secCode, double purchaseAmount) {
        System.out.println("LegacyPaymentSystem: Processing transaction");
        System.out.println("Card: " + cardNum + ", Expiry: " + expDate);
        System.out.println("Amount: " + purchaseAmount);
        
        // Legacy payment processing logic
        String transactionId = "TXN" + System.currentTimeMillis();
        System.out.println("Transaction submitted with ID: " + transactionId);
        return transactionId;
    }
    
    public int getTransactionStatus(String transactionId) {
        System.out.println("LegacyPaymentSystem: Checking transaction status for " + transactionId);
        // 1: completed, 2: pending, 3: failed, 4: refunded
        return 1; // Assume completed for demo
    }
    
    public boolean reverseTransaction(String transactionId) {
        System.out.println("LegacyPaymentSystem: Reversing transaction " + transactionId);
        // Logic to reverse transaction
        return true;
    }
}

// Step 4: Create the adapter that makes LegacyPaymentSystem compatible with PaymentProcessor
public class LegacyPaymentSystemAdapter implements PaymentProcessor {
    private LegacyPaymentSystem legacySystem;
    private Map<String, String> paymentToTransactionMap; // To map between payment IDs and transaction IDs
    
    public LegacyPaymentSystemAdapter() {
        this.legacySystem = new LegacyPaymentSystem();
        this.paymentToTransactionMap = new HashMap<>();
    }
    
    @Override
    public boolean processPayment(PaymentDetails paymentDetails) {
        try {
            String transactionId = legacySystem.submitTransaction(
                paymentDetails.getCardNumber(),
                paymentDetails.getExpiryDate(),
                paymentDetails.getCvv(),
                paymentDetails.getAmount()
            );
            
            // Generate a payment ID in the format expected by the modern system
            String paymentId = "PAY-" + UUID.randomUUID().toString();
            
            // Map the payment ID to the transaction ID
            paymentToTransactionMap.put(paymentId, transactionId);
            
            System.out.println("Adapter: Mapped payment ID " + paymentId + " to transaction ID " + transactionId);
            return true;
        } catch (Exception e) {
            System.err.println("Payment processing failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public PaymentStatus checkStatus(String paymentId) {
        String transactionId = paymentToTransactionMap.get(paymentId);
        if (transactionId == null) {
            System.err.println("No transaction found for payment ID: " + paymentId);
            return PaymentStatus.FAILED;
        }
        
        int statusCode = legacySystem.getTransactionStatus(transactionId);
        
        // Map legacy status codes to modern PaymentStatus enum
        switch (statusCode) {
            case 1: return PaymentStatus.COMPLETED;
            case 2: return PaymentStatus.PENDING;
            case 3: return PaymentStatus.FAILED;
            case 4: return PaymentStatus.REFUNDED;
            default: return PaymentStatus.FAILED;
        }
    }
    
    @Override
    public boolean refundPayment(String paymentId) {
        String transactionId = paymentToTransactionMap.get(paymentId);
        if (transactionId == null) {
            System.err.println("No transaction found for payment ID: " + paymentId);
            return false;
        }
        
        return legacySystem.reverseTransaction(transactionId);
    }
}
```

### Usage Example

```java
public class PaymentSystemDemo {
    public static void main(String[] args) {
        // Client code that works with PaymentProcessor interface
        PaymentDetails paymentDetails = new PaymentDetails(
            "4111111111111111", // Card number
            "12/25", // Expiry date
            "123", // CVV
            99.99, // Amount
            "USD", // Currency
            "John Doe" // Customer name
        );
        
        System.out.println("===== Using Modern Payment Processor =====");
        PaymentProcessor modernProcessor = new ModernPaymentProcessor();
        processPayment(modernProcessor, paymentDetails);
        
        System.out.println("\n===== Using Legacy Payment System through Adapter =====");
        PaymentProcessor legacyAdapter = new LegacyPaymentSystemAdapter();
        processPayment(legacyAdapter, paymentDetails);
    }
    
    private static void processPayment(PaymentProcessor processor, PaymentDetails details) {
        // This client code works with any PaymentProcessor implementation
        boolean result = processor.processPayment(details);
        System.out.println("Payment result: " + (result ? "Successful" : "Failed"));
        
        // For demonstration, assume we have a payment ID
        String paymentId = "PAY-12345";
        
        PaymentStatus status = processor.checkStatus(paymentId);
        System.out.println("Payment status: " + status);
        
        boolean refundResult = processor.refundPayment(paymentId);
        System.out.println("Refund result: " + (refundResult ? "Successful" : "Failed"));
    }
}
```

## Benefits

1. **Integrates incompatible interfaces**: Enables objects with incompatible interfaces to work together
2. **Single Responsibility Principle**: Separates the interface conversion logic from business logic
3. **Open/Closed Principle**: Allows adding new adapters without changing existing code
4. **Reuse existing code**: Makes it possible to reuse existing classes that lack expected interfaces

## Considerations

1. **Complexity**: Introduces additional complexity with new interfaces and classes
2. **Performance overhead**: May introduce a small performance overhead due to the extra layer
3. **Multiple adaptation**: Sometimes you might need multiple adapters when dealing with multiple incompatible systems

## When to Use

- When you need to use an existing class but its interface doesn't match what you need
- When you want to create a reusable class that cooperates with classes that don't necessarily have compatible interfaces
- When you need to use several existing subclasses but it's impractical to adapt their interfaces by subclassing each one
