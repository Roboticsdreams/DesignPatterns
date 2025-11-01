# Template Method Pattern

[Back to Home](../README.md)

## Intent

Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.

## Explanation

The Template Method pattern defines the program skeleton of an algorithm in a method, deferring some steps to subclasses. It lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure. The pattern consists of an abstract class that defines the template method(s) and concrete subclasses that implement the deferred steps.

## Real-World Example: Data Mining Application

A data processing application that extracts, transforms, and analyzes data from various sources (databases, CSV files, etc.) using the same high-level algorithm but with source-specific implementations.

### Implementation

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Step 1: Create the abstract class with the template method
public abstract class DataMiner {
    // The template method that defines the algorithm skeleton
    public final void mineData(String source) {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting data mining process from: " + source);
        
        // Step 1: Open the data source
        Object data = openDataSource(source);
        
        // Step 2: Extract the raw data
        List<Map<String, Object>> rawData = extractData(data);
        
        // Step 3: Transform the data
        List<Map<String, Object>> transformedData = transformData(rawData);
        
        // Step 4: Analyze the data
        Map<String, Object> analysisResults = analyzeData(transformedData);
        
        // Step 5: Send the results
        sendResults(analysisResults);
        
        // Step 6: Close the data source
        closeDataSource(data);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Data mining completed in " + (endTime - startTime) + " ms");
        
        // Optional hook for custom operations after mining
        hook();
    }
    
    // Abstract methods that must be implemented by subclasses
    protected abstract Object openDataSource(String source);
    protected abstract List<Map<String, Object>> extractData(Object dataSource);
    protected abstract void closeDataSource(Object dataSource);
    
    // Methods with default implementation that can be overridden
    protected List<Map<String, Object>> transformData(List<Map<String, Object>> data) {
        System.out.println("Performing standard data transformation");
        // Standard transformation logic - can be overridden
        return data;
    }
    
    protected Map<String, Object> analyzeData(List<Map<String, Object>> data) {
        System.out.println("Performing standard data analysis");
        // Standard analysis logic - can be overridden
        
        Map<String, Object> results = new HashMap<>();
        results.put("recordCount", data.size());
        
        // Calculate some basic statistics
        if (!data.isEmpty() && data.get(0).containsKey("value")) {
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            
            for (Map<String, Object> record : data) {
                double value = ((Number) record.get("value")).doubleValue();
                sum += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            
            double avg = sum / data.size();
            results.put("sum", sum);
            results.put("average", avg);
            results.put("min", min);
            results.put("max", max);
        }
        
        return results;
    }
    
    protected void sendResults(Map<String, Object> results) {
        System.out.println("Sending analysis results");
        System.out.println("Results: " + results);
    }
    
    // Hook method - subclasses can override this to extend functionality
    protected void hook() {
        // Default implementation does nothing
    }
}

// Step 2: Create concrete subclasses that implement the abstract methods
public class CSVDataMiner extends DataMiner {
    @Override
    protected Object openDataSource(String source) {
        System.out.println("Opening CSV file: " + source);
        try {
            return new BufferedReader(new FileReader(new File(source)));
        } catch (IOException e) {
            System.err.println("Error opening CSV file: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    protected List<Map<String, Object>> extractData(Object dataSource) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        if (dataSource == null) {
            return data;
        }
        
        BufferedReader reader = (BufferedReader) dataSource;
        System.out.println("Extracting data from CSV");
        
        try {
            String line;
            String[] headers = null;
            
            // Read the header row
            if ((line = reader.readLine()) != null) {
                headers = line.split(",");
            }
            
            // Read data rows
            while (headers != null && (line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, Object> record = new HashMap<>();
                
                for (int i = 0; i < Math.min(headers.length, values.length); i++) {
                    // Try to parse numeric values
                    String value = values[i].trim();
                    try {
                        record.put(headers[i], Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        record.put(headers[i], value);
                    }
                }
                
                data.add(record);
            }
            
            System.out.println("Extracted " + data.size() + " records from CSV");
        } catch (IOException e) {
            System.err.println("Error reading CSV data: " + e.getMessage());
        }
        
        return data;
    }
    
    @Override
    protected void closeDataSource(Object dataSource) {
        if (dataSource != null) {
            try {
                ((BufferedReader) dataSource).close();
                System.out.println("Closed CSV file");
            } catch (IOException e) {
                System.err.println("Error closing CSV file: " + e.getMessage());
            }
        }
    }
    
    @Override
    protected List<Map<String, Object>> transformData(List<Map<String, Object>> data) {
        System.out.println("Applying CSV-specific transformations");
        
        // Apply transformations specific to CSV data
        List<Map<String, Object>> transformed = new ArrayList<>();
        
        for (Map<String, Object> record : data) {
            Map<String, Object> newRecord = new HashMap<>(record);
            
            // Example: Convert string dates to proper Date objects
            if (newRecord.containsKey("date")) {
                String dateStr = (String) newRecord.get("date");
                // In a real implementation, we would parse the date here
                newRecord.put("date", "parsed_" + dateStr);
            }
            
            transformed.add(newRecord);
        }
        
        return transformed;
    }
    
    @Override
    protected void hook() {
        System.out.println("CSV processing complete. Running cleanup operations...");
        // Additional operations specific to CSV processing
    }
}

public class DatabaseDataMiner extends DataMiner {
    private String dbUrl;
    private String username;
    private String password;
    
    public DatabaseDataMiner(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }
    
    @Override
    protected Object openDataSource(String source) {
        System.out.println("Connecting to database and preparing query: " + source);
        try {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            return conn;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    protected List<Map<String, Object>> extractData(Object dataSource) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        if (dataSource == null) {
            return data;
        }
        
        Connection conn = (Connection) dataSource;
        System.out.println("Extracting data from database");
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery((String) dataSource);
            
            int columnCount = rs.getMetaData().getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    Object value = rs.getObject(i);
                    record.put(columnName, value);
                }
                
                data.add(record);
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Extracted " + data.size() + " records from database");
        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
        }
        
        return data;
    }
    
    @Override
    protected void closeDataSource(Object dataSource) {
        if (dataSource != null) {
            try {
                ((Connection) dataSource).close();
                System.out.println("Closed database connection");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    @Override
    protected Map<String, Object> analyzeData(List<Map<String, Object>> data) {
        System.out.println("Performing database-specific data analysis");
        
        // Standard analysis from parent class
        Map<String, Object> results = super.analyzeData(data);
        
        // Add some database-specific analysis
        int numericColumns = 0;
        int textColumns = 0;
        
        if (!data.isEmpty()) {
            Map<String, Object> firstRow = data.get(0);
            for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    numericColumns++;
                } else if (entry.getValue() instanceof String) {
                    textColumns++;
                }
            }
        }
        
        results.put("numericColumns", numericColumns);
        results.put("textColumns", textColumns);
        
        return results;
    }
    
    @Override
    protected void sendResults(Map<String, Object> results) {
        System.out.println("Storing database analysis results in the data warehouse");
        super.sendResults(results);
        // In a real implementation, we would store the results in another database table
    }
}

// Additional concrete implementation for web APIs
public class APIDataMiner extends DataMiner {
    private String apiKey;
    
    public APIDataMiner(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    protected Object openDataSource(String source) {
        System.out.println("Initializing API connection to: " + source);
        // In a real implementation, this would initialize an HTTP client
        return source;
    }
    
    @Override
    protected List<Map<String, Object>> extractData(Object dataSource) {
        List<Map<String, Object>> data = new ArrayList<>();
        System.out.println("Fetching data from API using key: " + apiKey);
        
        // Simulate API call and response parsing
        // In a real implementation, this would make HTTP requests
        
        // Mock response data
        for (int i = 0; i < 5; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", i + 1);
            record.put("value", Math.random() * 100);
            record.put("name", "API Record " + (i + 1));
            data.add(record);
        }
        
        System.out.println("Extracted " + data.size() + " records from API");
        return data;
    }
    
    @Override
    protected void closeDataSource(Object dataSource) {
        System.out.println("Closing API connection");
        // In a real implementation, this would close the HTTP client
    }
    
    @Override
    protected List<Map<String, Object>> transformData(List<Map<String, Object>> data) {
        System.out.println("Normalizing API response format");
        
        // API-specific transformations
        List<Map<String, Object>> transformed = new ArrayList<>();
        
        for (Map<String, Object> record : data) {
            Map<String, Object> newRecord = new HashMap<>();
            
            // Standardize field names and formats
            newRecord.put("identifier", record.get("id"));
            newRecord.put("value", record.get("value"));
            newRecord.put("description", record.get("name"));
            
            transformed.add(newRecord);
        }
        
        return transformed;
    }
    
    @Override
    protected void hook() {
        System.out.println("API processing complete. Refreshing API rate limit quota...");
        // Additional operations specific to API handling
    }
}
```

### Usage Example

```java
public class DataMinerDemo {
    public static void main(String[] args) {
        System.out.println("===== CSV Data Mining Demo =====");
        DataMiner csvMiner = new CSVDataMiner();
        // In a real scenario, this would be an actual CSV file path
        // We're using a mock path for demonstration
        csvMiner.mineData("data/sales_records.csv");
        
        System.out.println("\n===== Database Data Mining Demo =====");
        DataMiner dbMiner = new DatabaseDataMiner(
            "jdbc:mysql://localhost:3306/analytics", "user", "password");
        // In a real scenario, this would be an actual SQL query
        dbMiner.mineData("SELECT * FROM monthly_sales");
        
        System.out.println("\n===== API Data Mining Demo =====");
        DataMiner apiMiner = new APIDataMiner("api-key-12345");
        // In a real scenario, this would be an actual API endpoint
        apiMiner.mineData("https://api.example.com/v1/products");
        
        // Demonstrate how the same algorithm structure is maintained
        // across different data sources
        System.out.println("\n===== Processing Multiple Sources =====");
        DataMiner[] miners = {
            new CSVDataMiner(),
            new DatabaseDataMiner("jdbc:mysql://localhost:3306/analytics", "user", "password"),
            new APIDataMiner("api-key-67890")
        };
        
        String[] sources = {
            "data/inventory.csv",
            "SELECT * FROM customers",
            "https://api.example.com/v1/orders"
        };
        
        for (int i = 0; i < miners.length; i++) {
            System.out.println("\nProcessing source " + (i + 1) + ":");
            miners[i].mineData(sources[i]);
        }
    }
}
```

## Another Example: Document Processing System

Here's another implementation of the Template Method pattern for a document processing system:

```java
// Abstract class defining the template method
abstract class DocumentProcessor {
    // Template method defining the algorithm
    public final void processDocument(String document) {
        System.out.println("Starting document processing...");
        
        String content = readDocument(document);
        content = parseDocument(content);
        content = processContent(content);
        saveDocument(document, content);
        
        System.out.println("Document processing completed.");
    }
    
    // Concrete method used by all subclasses
    protected String readDocument(String path) {
        System.out.println("Reading document from: " + path);
        // In a real application, this would read from the actual path
        return "This is the content of " + path;
    }
    
    // Abstract method that must be implemented by subclasses
    protected abstract String parseDocument(String content);
    
    // Hook method with a default implementation
    protected String processContent(String content) {
        // Default implementation does nothing
        return content;
    }
    
    // Concrete method used by all subclasses
    protected void saveDocument(String path, String content) {
        System.out.println("Saving processed document to: " + path + ".processed");
        // In a real application, this would save to the file system
        System.out.println("Content: " + content);
    }
}

// Concrete implementation for XML documents
class XMLDocumentProcessor extends DocumentProcessor {
    @Override
    protected String parseDocument(String content) {
        System.out.println("Parsing XML document...");
        return "<parsed>" + content + "</parsed>";
    }
    
    @Override
    protected String processContent(String content) {
        System.out.println("Applying XML-specific transformations...");
        return content + " (XML processed)";
    }
}

// Concrete implementation for JSON documents
class JSONDocumentProcessor extends DocumentProcessor {
    @Override
    protected String parseDocument(String content) {
        System.out.println("Parsing JSON document...");
        return "{\"content\": \"" + content + "\"}";
    }
    
    @Override
    protected String processContent(String content) {
        System.out.println("Applying JSON-specific transformations...");
        return content + " (JSON processed)";
    }
}

// Concrete implementation for plain text documents
class TextDocumentProcessor extends DocumentProcessor {
    @Override
    protected String parseDocument(String content) {
        System.out.println("Processing plain text document...");
        return content;
    }
    
    // We don't override the processContent hook method,
    // so the default implementation will be used
}
```

## Benefits

1. **Code reuse**: Template methods define the skeleton of an algorithm, avoiding code duplication
2. **Fine-grained control**: Allows subclasses to redefine only certain steps of an algorithm
3. **Inversion of control**: The "Hollywood Principle" - "Don't call us, we'll call you"
4. **Enforces uniformity**: Ensures core algorithm steps are followed in the same sequence
5. **Flexibility and extensibility**: Subclasses can implement the abstract steps in different ways without changing the algorithm structure

## Considerations

1. **Rigid algorithm structure**: The sequence of steps can't be changed by subclasses
2. **Violating Liskov Substitution Principle**: If subclasses deviate from expected behavior
3. **Debugging challenges**: The flow of control can be harder to follow
4. **Finding the right abstraction level**: Too abstract templates might limit subclasses; too specific ones reduce flexibility

## When to Use

- When you want to let subclasses implement behavior that varies, while keeping the invariant parts in a single place
- When common behavior among subclasses should be factored and localized to avoid code duplication
- When you want to control the points where subclassing is allowed
- When you need to provide hooks for subclasses to extend the algorithm at specific points
