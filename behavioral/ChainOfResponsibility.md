# Chain of Responsibility Pattern

[Back to Home](../README.md)

## Intent

Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it.

## Explanation

The Chain of Responsibility pattern creates a chain of receiver objects for a request. This pattern decouples sender and receivers of a request based on the type of request. Each receiver contains a reference to another receiver. If one receiver cannot handle the request, it passes the request to the next receiver in the chain.

## Real-World Example: Support Ticket Handling System

In a technical support system, different levels of support staff handle tickets based on complexity. If a support representative can't resolve an issue, it's escalated to the next level of support.

### Implementation

```java
import java.util.HashMap;
import java.util.Map;

// Step 1: Create the Handler interface
public abstract class SupportHandler {
    protected SupportHandler nextHandler;
    protected int level;
    
    // Set the next handler in the chain
    public SupportHandler setNext(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }
    
    // Method to handle support tickets
    public abstract void handleTicket(SupportTicket ticket);
    
    // Check if this handler can handle the ticket
    protected boolean canHandle(SupportTicket ticket) {
        return ticket.getPriority() <= this.level;
    }
}

// Step 2: Create Concrete Handlers
public class FrontlineSupport extends SupportHandler {
    public FrontlineSupport() {
        this.level = 1; // Can handle low-priority tickets only
    }
    
    @Override
    public void handleTicket(SupportTicket ticket) {
        if (canHandle(ticket)) {
            System.out.println("Frontline Support handling ticket #" + ticket.getId() + 
                             ": " + ticket.getDescription());
            ticket.setStatus("Resolved by Frontline Support");
            ticket.setResolution("Applied standard troubleshooting procedures");
        } else {
            System.out.println("Frontline Support escalating ticket #" + ticket.getId() + 
                             " to next level");
            if (nextHandler != null) {
                nextHandler.handleTicket(ticket);
            } else {
                System.out.println("End of chain reached. Ticket #" + ticket.getId() + 
                                 " remains unresolved.");
            }
        }
    }
}

public class TechnicalSupport extends SupportHandler {
    public TechnicalSupport() {
        this.level = 2; // Can handle medium-priority tickets
    }
    
    @Override
    public void handleTicket(SupportTicket ticket) {
        if (canHandle(ticket)) {
            System.out.println("Technical Support handling ticket #" + ticket.getId() + 
                             ": " + ticket.getDescription());
            ticket.setStatus("Resolved by Technical Support");
            ticket.setResolution("Applied advanced technical solutions");
        } else {
            System.out.println("Technical Support escalating ticket #" + ticket.getId() + 
                             " to next level");
            if (nextHandler != null) {
                nextHandler.handleTicket(ticket);
            } else {
                System.out.println("End of chain reached. Ticket #" + ticket.getId() + 
                                 " remains unresolved.");
            }
        }
    }
}

public class DeveloperSupport extends SupportHandler {
    public DeveloperSupport() {
        this.level = 3; // Can handle high-priority tickets
    }
    
    @Override
    public void handleTicket(SupportTicket ticket) {
        if (canHandle(ticket)) {
            System.out.println("Developer Support handling ticket #" + ticket.getId() + 
                             ": " + ticket.getDescription());
            ticket.setStatus("Resolved by Developer Support");
            ticket.setResolution("Implemented code fixes or workarounds");
        } else {
            System.out.println("Developer Support escalating ticket #" + ticket.getId() + 
                             " to next level");
            if (nextHandler != null) {
                nextHandler.handleTicket(ticket);
            } else {
                System.out.println("End of chain reached. Ticket #" + ticket.getId() + 
                                 " remains unresolved.");
            }
        }
    }
}

public class ProductManager extends SupportHandler {
    public ProductManager() {
        this.level = 4; // Can handle critical-priority tickets
    }
    
    @Override
    public void handleTicket(SupportTicket ticket) {
        System.out.println("Product Manager handling ticket #" + ticket.getId() + 
                         ": " + ticket.getDescription());
        ticket.setStatus("Addressed by Product Manager");
        ticket.setResolution("Strategic decision made for critical issue");
    }
}

// Step 3: Create the Support Ticket class
public class SupportTicket {
    private int id;
    private String description;
    private int priority; // 1=Low, 2=Medium, 3=High, 4=Critical
    private String status;
    private String resolution;
    
    public SupportTicket(int id, String description, int priority) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.status = "Open";
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getPriorityText() {
        switch (priority) {
            case 1: return "Low";
            case 2: return "Medium";
            case 3: return "High";
            case 4: return "Critical";
            default: return "Unknown";
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    @Override
    public String toString() {
        return "Ticket #" + id + 
               " [Priority: " + getPriorityText() + "]" +
               " - " + description + 
               " - Status: " + status +
               (resolution != null ? " - Resolution: " + resolution : "");
    }
}

// Step 4: Create the Support Ticket System
public class SupportTicketSystem {
    private SupportHandler chain;
    private Map<Integer, SupportTicket> tickets = new HashMap<>();
    private int nextTicketId = 1;
    
    public SupportTicketSystem() {
        // Create the chain of responsibility
        SupportHandler frontline = new FrontlineSupport();
        SupportHandler technical = new TechnicalSupport();
        SupportHandler developer = new DeveloperSupport();
        SupportHandler productManager = new ProductManager();
        
        // Set up the chain
        chain = frontline;
        frontline.setNext(technical);
        technical.setNext(developer);
        developer.setNext(productManager);
    }
    
    public SupportTicket createTicket(String description, int priority) {
        SupportTicket ticket = new SupportTicket(nextTicketId++, description, priority);
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }
    
    public void processTicket(int ticketId) {
        SupportTicket ticket = tickets.get(ticketId);
        if (ticket != null) {
            System.out.println("Processing " + ticket);
            chain.handleTicket(ticket);
        } else {
            System.out.println("Ticket #" + ticketId + " not found");
        }
    }
    
    public SupportTicket getTicket(int ticketId) {
        return tickets.get(ticketId);
    }
    
    public Map<Integer, SupportTicket> getAllTickets() {
        return new HashMap<>(tickets);
    }
}
```

### Usage Example

```java
public class SupportSystemDemo {
    public static void main(String[] args) {
        SupportTicketSystem supportSystem = new SupportTicketSystem();
        
        // Create tickets with different priorities
        System.out.println("=== Creating Support Tickets ===");
        SupportTicket ticket1 = supportSystem.createTicket("Cannot login to application", 1); // Low priority
        SupportTicket ticket2 = supportSystem.createTicket("Application crashes when exporting data", 2); // Medium priority
        SupportTicket ticket3 = supportSystem.createTicket("Database connection failure", 3); // High priority
        SupportTicket ticket4 = supportSystem.createTicket("Production server down", 4); // Critical priority
        
        // Display created tickets
        System.out.println(ticket1);
        System.out.println(ticket2);
        System.out.println(ticket3);
        System.out.println(ticket4);
        
        // Process the tickets through the chain of responsibility
        System.out.println("\n=== Processing Tickets ===");
        supportSystem.processTicket(1);
        System.out.println();
        
        supportSystem.processTicket(2);
        System.out.println();
        
        supportSystem.processTicket(3);
        System.out.println();
        
        supportSystem.processTicket(4);
        System.out.println();
        
        // Display resolved tickets
        System.out.println("=== Ticket Status After Processing ===");
        for (int i = 1; i <= 4; i++) {
            System.out.println(supportSystem.getTicket(i));
        }
    }
}
```

## Another Example: Approval Workflow

Here's another implementation of the Chain of Responsibility pattern for an expense approval workflow:

```java
// Handler interface
public abstract class ApprovalHandler {
    protected ApprovalHandler nextHandler;
    protected double approvalLimit;
    protected String role;
    
    public ApprovalHandler(String role, double approvalLimit) {
        this.role = role;
        this.approvalLimit = approvalLimit;
    }
    
    public void setNext(ApprovalHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    public void processExpense(ExpenseReport expense) {
        if (canApprove(expense)) {
            approveExpense(expense);
        } else if (nextHandler != null) {
            System.out.println(role + " cannot approve this expense. Forwarding to " + nextHandler.role);
            nextHandler.processExpense(expense);
        } else {
            System.out.println("Expense cannot be approved by any handler in the chain.");
            expense.setStatus("Rejected");
        }
    }
    
    protected boolean canApprove(ExpenseReport expense) {
        return expense.getAmount() <= approvalLimit;
    }
    
    protected void approveExpense(ExpenseReport expense) {
        expense.setStatus("Approved");
        expense.setApprovedBy(role);
        System.out.println(role + " approved expense: " + expense.getDescription() + 
                         ", Amount: $" + expense.getAmount());
    }
}

// Concrete Handlers
public class TeamLead extends ApprovalHandler {
    public TeamLead() {
        super("Team Lead", 1000.0); // Can approve up to $1000
    }
}

public class DepartmentManager extends ApprovalHandler {
    public DepartmentManager() {
        super("Department Manager", 5000.0); // Can approve up to $5000
    }
}

public class Director extends ApprovalHandler {
    public Director() {
        super("Director", 10000.0); // Can approve up to $10000
    }
}

public class CEO extends ApprovalHandler {
    public CEO() {
        super("CEO", 50000.0); // Can approve up to $50000
    }
}

// Request class
public class ExpenseReport {
    private String description;
    private double amount;
    private String status = "Pending";
    private String approvedBy;
    
    public ExpenseReport(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    @Override
    public String toString() {
        return "ExpenseReport [description=" + description + 
               ", amount=$" + amount + 
               ", status=" + status + 
               (approvedBy != null ? ", approved by=" + approvedBy : "") + 
               "]";
    }
}

// Client
public class ExpenseApprovalDemo {
    public static void main(String[] args) {
        // Create the chain of responsibility
        ApprovalHandler teamLead = new TeamLead();
        ApprovalHandler manager = new DepartmentManager();
        ApprovalHandler director = new Director();
        ApprovalHandler ceo = new CEO();
        
        teamLead.setNext(manager);
        manager.setNext(director);
        director.setNext(ceo);
        
        // Create some expense reports
        ExpenseReport officeSupplies = new ExpenseReport("Office Supplies", 300.0);
        ExpenseReport softwareLicense = new ExpenseReport("Software Licenses", 3200.0);
        ExpenseReport conferenceRoom = new ExpenseReport("Conference Room Renovation", 9500.0);
        ExpenseReport newOffice = new ExpenseReport("New Office Setup", 25000.0);
        ExpenseReport companyAcquisition = new ExpenseReport("Company Acquisition", 100000.0);
        
        // Process the expenses
        System.out.println("Processing expenses through approval chain:");
        teamLead.processExpense(officeSupplies);
        teamLead.processExpense(softwareLicense);
        teamLead.processExpense(conferenceRoom);
        teamLead.processExpense(newOffice);
        teamLead.processExpense(companyAcquisition);
        
        // Check the status
        System.out.println("\nFinal Status of Expense Reports:");
        System.out.println(officeSupplies);
        System.out.println(softwareLicense);
        System.out.println(conferenceRoom);
        System.out.println(newOffice);
        System.out.println(companyAcquisition);
    }
}
```

## Benefits

1. **Reduced coupling**: The sender doesn't need to know which object will handle the request
2. **Flexibility in assigning responsibilities**: You can change the chain at runtime
3. **Added flexibility in distributing responsibilities**: You can add or remove responsibilities by changing the chain
4. **Single Responsibility Principle**: Each handler addresses a specific level of responsibility

## Considerations

1. **Request handling guarantee**: There's no guarantee the request will be handled - it might reach the end of the chain without being processed
2. **Performance concerns**: Long chains can cause noticeable delays and debugging challenges
3. **Complexity**: Can be challenging to follow the flow of a request through the chain
4. **Possible recursion**: If not carefully implemented, handlers might create infinite loops

## When to Use

- When more than one object may handle a request, and the handler isn't known beforehand
- When you want to issue a request to one of several objects without specifying the receiver explicitly
- When the set of objects that can handle a request should be specified dynamically
- When you want to decouple objects that make requests from objects that process them
