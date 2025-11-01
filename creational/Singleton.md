# Singleton Pattern

[Back to Home](../README.md)

## Intent

Ensure a class has only one instance and provide a global point of access to it.

## Explanation

The Singleton pattern restricts the instantiation of a class to a single object. This is useful when exactly one object is needed to coordinate actions across the system.

## Real-World Example: Database Connection Manager

In most applications, we need to manage database connections efficiently. Having multiple connection instances might lead to resource wastage and inconsistency. Using the Singleton pattern ensures a single connection pool is maintained.

### Implementation

```java
public class DatabaseConnectionManager {
    // The private static instance variable
    private static DatabaseConnectionManager instance;
    
    // Connection pool properties
    private final String url;
    private final String username;
    private final String password;
    private final int maxConnections;
    private final List<Connection> connectionPool;
    
    // Private constructor to prevent instantiation
    private DatabaseConnectionManager() {
        // Load configuration from properties file
        Properties props = loadProperties();
        this.url = props.getProperty("db.url");
        this.username = props.getProperty("db.username");
        this.password = props.getProperty("db.password");
        this.maxConnections = Integer.parseInt(props.getProperty("db.maxConnections", "10"));
        this.connectionPool = new ArrayList<>(maxConnections);
        
        // Initialize the connection pool
        initializeConnectionPool();
    }
    
    // Thread-safe implementation of getInstance using double-checked locking
    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }
    
    // Method to get a connection from the pool
    public synchronized Connection getConnection() {
        if (connectionPool.isEmpty()) {
            // If no connections available, wait or create a new one if under max limit
            if (getTotalConnections() < maxConnections) {
                try {
                    Connection conn = DriverManager.getConnection(url, username, password);
                    return conn;
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create new database connection", e);
                }
            } else {
                throw new RuntimeException("Connection pool exhausted");
            }
        } else {
            // Return an existing connection from the pool
            return connectionPool.remove(connectionPool.size() - 1);
        }
    }
    
    // Method to release a connection back to the pool
    public synchronized void releaseConnection(Connection connection) {
        connectionPool.add(connection);
    }
    
    // Helper method to initialize the connection pool
    private void initializeConnectionPool() {
        try {
            for (int i = 0; i < maxConnections / 2; i++) { // Start with half of max connections
                Connection connection = DriverManager.getConnection(url, username, password);
                connectionPool.add(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }
    
    // Helper method to load properties
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                // Use default values if properties file not found
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/mydb");
                props.setProperty("db.username", "root");
                props.setProperty("db.password", "password");
                props.setProperty("db.maxConnections", "10");
            } else {
                props.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
        return props;
    }
    
    // Helper method to get total connections
    private int getTotalConnections() {
        // Count active connections plus those in the pool
        return maxConnections - connectionPool.size();
    }
}
```

### Usage Example

```java
public class Application {
    public static void main(String[] args) {
        // Get the singleton instance
        DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
        
        // Get a connection
        Connection connection = connectionManager.getConnection();
        
        try {
            // Use the connection for database operations
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            
            while (resultSet.next()) {
                System.out.println("User ID: " + resultSet.getInt("id") + 
                                  ", Name: " + resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Return the connection to the pool
            connectionManager.releaseConnection(connection);
        }
    }
}
```

## Benefits

1. **Controlled access to sole instance**: The Singleton class encapsulates its instance and provides strict control over how and when clients access it
2. **Reduced namespace**: Avoids polluting the global namespace with variables
3. **Supports refinement**: The Singleton class can be subclassed, and it's easy to configure an application with an instance of this extended class
4. **Can manage a variable number of instances**: The pattern makes it easy to change your mind and allow more than one instance

## Considerations

1. **Thread safety**: Implementation must consider multi-threading issues
2. **Testing**: Singletons can make unit testing more difficult
3. **Dependency Injection**: Can often replace Singleton usage with proper dependency injection

## When to Use

- For resources that are expensive to create (like database connection pools)
- For resources where you need to coordinate actions across the system
- When exactly one instance is needed to coordinate actions
