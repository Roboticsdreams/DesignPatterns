# Prototype Pattern

[Back to Home](../README.md)

## Intent

Specify the kinds of objects to create using a prototypical instance, and create new objects by copying this prototype.

## Explanation

The Prototype pattern is used when the type of objects to create is determined by a prototypical instance, which is cloned to produce new objects. This pattern is useful when the cost of creating a new object is more expensive than copying an existing one.

## Real-World Example: Document Template System

In a document management system, templates (prototypes) can be cloned to create new documents without starting from scratch. Each document type has predefined structure, formatting, and boilerplate text.

### Implementation

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Step 1: Create the Prototype interface
public interface DocumentPrototype extends Cloneable {
    DocumentPrototype clone();
    void setTitle(String title);
    String getTitle();
    String getContent();
    void print();
}

// Step 2: Create concrete prototypes
public class LegalDocument implements DocumentPrototype {
    private String title;
    private String disclaimer;
    private List<String> sections;
    private String legalFooter;
    
    public LegalDocument() {
        // Default values for a legal document
        this.title = "Untitled Legal Document";
        this.disclaimer = "This legal document is provided for informational purposes only " +
                         "and does not constitute legal advice.";
        this.sections = new ArrayList<>();
        this.sections.add("DEFINITIONS");
        this.sections.add("TERMS AND CONDITIONS");
        this.sections.add("LIABILITY");
        this.sections.add("GOVERNING LAW");
        this.legalFooter = "© " + java.time.Year.now().getValue() + " Legal Corp. All rights reserved.";
    }
    
    // A constructor that copies data from another LegalDocument
    public LegalDocument(LegalDocument source) {
        if (source != null) {
            this.title = source.title;
            this.disclaimer = source.disclaimer;
            this.sections = new ArrayList<>(source.sections);
            this.legalFooter = source.legalFooter;
        }
    }
    
    @Override
    public DocumentPrototype clone() {
        return new LegalDocument(this);
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
    
    public String getDisclaimer() {
        return disclaimer;
    }
    
    public void addSection(String section) {
        this.sections.add(section);
    }
    
    public List<String> getSections() {
        return new ArrayList<>(sections);
    }
    
    public void setLegalFooter(String legalFooter) {
        this.legalFooter = legalFooter;
    }
    
    public String getLegalFooter() {
        return legalFooter;
    }
    
    @Override
    public String getContent() {
        StringBuilder content = new StringBuilder();
        content.append("== ").append(title.toUpperCase()).append(" ==\n\n");
        content.append("DISCLAIMER:\n").append(disclaimer).append("\n\n");
        
        for (String section : sections) {
            content.append(section).append(":\n");
            content.append("[Section content goes here]\n\n");
        }
        
        content.append(legalFooter);
        return content.toString();
    }
    
    @Override
    public void print() {
        System.out.println(getContent());
    }
}

public class ReportDocument implements DocumentPrototype {
    private String title;
    private String author;
    private String date;
    private List<String> sections;
    private Map<String, String> charts;
    
    public ReportDocument() {
        // Default values for a report
        this.title = "Untitled Report";
        this.author = "Unknown Author";
        this.date = java.time.LocalDate.now().toString();
        this.sections = new ArrayList<>();
        this.sections.add("Executive Summary");
        this.sections.add("Introduction");
        this.sections.add("Methodology");
        this.sections.add("Findings");
        this.sections.add("Conclusion");
        this.charts = new HashMap<>();
        this.charts.put("Revenue", "Bar Chart");
        this.charts.put("Growth", "Line Chart");
    }
    
    // A constructor that copies data from another ReportDocument
    public ReportDocument(ReportDocument source) {
        if (source != null) {
            this.title = source.title;
            this.author = source.author;
            this.date = source.date;
            this.sections = new ArrayList<>(source.sections);
            this.charts = new HashMap<>(source.charts);
        }
    }
    
    @Override
    public DocumentPrototype clone() {
        return new ReportDocument(this);
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getDate() {
        return date;
    }
    
    public void addSection(String section) {
        this.sections.add(section);
    }
    
    public List<String> getSections() {
        return new ArrayList<>(sections);
    }
    
    public void addChart(String title, String type) {
        this.charts.put(title, type);
    }
    
    public Map<String, String> getCharts() {
        return new HashMap<>(charts);
    }
    
    @Override
    public String getContent() {
        StringBuilder content = new StringBuilder();
        content.append("=== ").append(title).append(" ===\n\n");
        content.append("Author: ").append(author).append("\n");
        content.append("Date: ").append(date).append("\n\n");
        
        for (String section : sections) {
            content.append("## ").append(section).append(" ##\n");
            content.append("[Content for ").append(section).append("]\n\n");
        }
        
        content.append("CHARTS:\n");
        for (Map.Entry<String, String> chart : charts.entrySet()) {
            content.append("- ").append(chart.getKey()).append(" (").append(chart.getValue()).append(")\n");
        }
        
        return content.toString();
    }
    
    @Override
    public void print() {
        System.out.println(getContent());
    }
}

public class EmailTemplate implements DocumentPrototype {
    private String title;
    private String recipient;
    private String subject;
    private String greeting;
    private String body;
    private String signature;
    
    public EmailTemplate() {
        // Default values for an email template
        this.title = "Standard Email Template";
        this.recipient = "[Recipient]";
        this.subject = "[Subject]";
        this.greeting = "Dear [Name],";
        this.body = "I hope this email finds you well.\n\n[Email body goes here]\n\nLooking forward to your response.";
        this.signature = "Best regards,\n[Your Name]\n[Your Position]\n[Your Contact Info]";
    }
    
    // A constructor that copies data from another EmailTemplate
    public EmailTemplate(EmailTemplate source) {
        if (source != null) {
            this.title = source.title;
            this.recipient = source.recipient;
            this.subject = source.subject;
            this.greeting = source.greeting;
            this.body = source.body;
            this.signature = source.signature;
        }
    }
    
    @Override
    public DocumentPrototype clone() {
        return new EmailTemplate(this);
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
    
    public String getGreeting() {
        return greeting;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public String getSignature() {
        return signature;
    }
    
    @Override
    public String getContent() {
        StringBuilder content = new StringBuilder();
        content.append("TO: ").append(recipient).append("\n");
        content.append("SUBJECT: ").append(subject).append("\n\n");
        content.append(greeting).append("\n\n");
        content.append(body).append("\n\n");
        content.append(signature);
        return content.toString();
    }
    
    @Override
    public void print() {
        System.out.println("--- ").append(title).append(" ---\n");
        System.out.println(getContent());
        System.out.println("-----------------------");
    }
}

// Step 3: Create a prototype registry/manager
public class DocumentPrototypeManager {
    private Map<String, DocumentPrototype> prototypes = new HashMap<>();
    
    public DocumentPrototypeManager() {
        // Initialize with standard prototypes
        prototypes.put("legal", new LegalDocument());
        prototypes.put("report", new ReportDocument());
        prototypes.put("email", new EmailTemplate());
    }
    
    public DocumentPrototype getClone(String type) {
        DocumentPrototype prototype = prototypes.get(type);
        return prototype != null ? prototype.clone() : null;
    }
    
    public void addPrototype(String type, DocumentPrototype prototype) {
        prototypes.put(type, prototype);
    }
    
    public DocumentPrototype getPrototype(String type) {
        return prototypes.get(type);
    }
}
```

### Usage Example

```java
public class DocumentSystemDemo {
    public static void main(String[] args) {
        // Initialize the prototype manager
        DocumentPrototypeManager manager = new DocumentPrototypeManager();
        
        // Create a specialized legal document prototype for NDAs
        LegalDocument ndaTemplate = (LegalDocument) manager.getClone("legal");
        ndaTemplate.setTitle("Non-Disclosure Agreement Template");
        ndaTemplate.addSection("CONFIDENTIAL INFORMATION");
        ndaTemplate.addSection("NON-DISCLOSURE OBLIGATIONS");
        ndaTemplate.addSection("TERM AND TERMINATION");
        manager.addPrototype("nda", ndaTemplate);
        
        // Create a specialized report prototype for quarterly reports
        ReportDocument quarterlyReportTemplate = (ReportDocument) manager.getClone("report");
        quarterlyReportTemplate.setTitle("Quarterly Performance Report Template");
        quarterlyReportTemplate.addSection("Financial Highlights");
        quarterlyReportTemplate.addSection("Sales Analysis");
        quarterlyReportTemplate.addSection("Future Outlook");
        manager.addPrototype("quarterly-report", quarterlyReportTemplate);
        
        // Create a specialized email template for meeting invitations
        EmailTemplate meetingInviteTemplate = (EmailTemplate) manager.getClone("email");
        meetingInviteTemplate.setTitle("Meeting Invitation Template");
        meetingInviteTemplate.setSubject("Invitation: [Meeting Title] - [Date]");
        meetingInviteTemplate.setBody("I would like to invite you to a meeting to discuss [Topic].\n\n" +
                                   "Date: [Date]\n" +
                                   "Time: [Time]\n" +
                                   "Location: [Location]\n\n" +
                                   "Agenda:\n" +
                                   "1. [Agenda Item 1]\n" +
                                   "2. [Agenda Item 2]\n" +
                                   "3. [Agenda Item 3]\n\n" +
                                   "Please confirm your availability.");
        manager.addPrototype("meeting-invite", meetingInviteTemplate);
        
        System.out.println("=== Available Document Templates ===");
        System.out.println("- legal");
        System.out.println("- report");
        System.out.println("- email");
        System.out.println("- nda");
        System.out.println("- quarterly-report");
        System.out.println("- meeting-invite");
        
        // Demo: Create specific documents from templates
        System.out.println("\n=== Creating documents from templates ===");
        
        // Create an NDA for a specific company
        DocumentPrototype acmeNda = manager.getClone("nda");
        acmeNda.setTitle("Acme Corp. Non-Disclosure Agreement");
        System.out.println("\n>> Created NDA for Acme Corp:");
        acmeNda.print();
        
        // Create a quarterly report for Q2 2023
        DocumentPrototype q2Report = manager.getClone("quarterly-report");
        q2Report.setTitle("Q2 2023 Performance Report");
        ReportDocument typedQ2Report = (ReportDocument) q2Report;
        typedQ2Report.setAuthor("John Smith, CFO");
        typedQ2Report.setDate("June 30, 2023");
        System.out.println("\n>> Created Q2 2023 Report:");
        q2Report.print();
        
        // Create a meeting invitation
        DocumentPrototype projectMeeting = manager.getClone("meeting-invite");
        EmailTemplate typedMeeting = (EmailTemplate) projectMeeting;
        typedMeeting.setRecipient("team@company.com");
        typedMeeting.setSubject("Invitation: Project Kickoff Meeting - July 15, 2023");
        typedMeeting.setGreeting("Hi Team,");
        // Customize other fields...
        System.out.println("\n>> Created Project Kickoff Meeting Invitation:");
        projectMeeting.print();
    }
}
```

## Benefits

1. **Adding and removing products at runtime**: You can incorporate or remove products by adding or removing prototypes from the prototype manager
2. **Reduced subclassing**: You create new objects by copying a prototypical instance rather than requiring new classes
3. **Configuring an application with classes dynamically**: You can load classes into an application without knowing them in advance
4. **Reduced object creation cost**: Cloning can be less expensive than creating a new object from scratch, especially for complex objects

## Considerations

1. **Implementing the clone method**: Can be challenging in languages that don't have built-in cloning mechanisms
2. **Managing deep vs. shallow copies**: You need to decide how deep the clone operation should go
3. **Initialization vs. cloning**: The cloning process might require resetting an object's state
4. **Managing prototype mutations**: When prototypes are modified, the mutations affect all objects subsequently cloned from them

## When to Use

- When a system should be independent of how its products are created
- When classes to instantiate are specified at runtime
- When you want to avoid building a class hierarchy of factories that parallels the class hierarchy of products
- When instances of a class can have one of only a few different combinations of state
