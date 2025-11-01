# Proxy Pattern

[Back to Home](../README.md)

## Intent

Provide a surrogate or placeholder for another object to control access to it.

## Explanation

The Proxy pattern provides a surrogate or placeholder object that controls access to another object. This could be for various reasons like lazy initialization, access control, logging, or caching.

## Real-World Example: Image Loading System

A high-resolution image viewer that needs to display thumbnails and only load full images when requested. The proxy controls image loading to optimize performance and resource usage.

### Implementation

```java
import java.util.HashMap;
import java.util.Map;

// Step 1: Define the Subject interface
public interface Image {
    void display();
    String getFileName();
    int getWidth();
    int getHeight();
}

// Step 2: Create the Real Subject
public class RealImage implements Image {
    private String fileName;
    private int width;
    private int height;
    private byte[] imageData; // Simulating image data
    
    public RealImage(String fileName) {
        this.fileName = fileName;
        
        // Load the image
        loadFromDisk();
    }
    
    private void loadFromDisk() {
        System.out.println("Loading high-resolution image from disk: " + fileName);
        
        // Simulate loading a large image from disk
        try {
            System.out.println("Starting to load image data...");
            Thread.sleep(2000); // Simulate time-consuming operation
            
            // Determine image dimensions based on the filename
            if (fileName.contains("large")) {
                width = 3000;
                height = 2000;
                imageData = new byte[width * height * 3]; // 3 bytes per pixel (RGB)
            } else if (fileName.contains("medium")) {
                width = 1500;
                height = 1000;
                imageData = new byte[width * height * 3];
            } else {
                width = 800;
                height = 600;
                imageData = new byte[width * height * 3];
            }
            
            System.out.println("Image loaded successfully. Size: " + 
                             (imageData.length / (1024 * 1024)) + " MB");
        } catch (InterruptedException e) {
            System.out.println("Loading interrupted: " + e.getMessage());
        }
    }
    
    @Override
    public void display() {
        System.out.println("Displaying high-resolution image: " + fileName);
        System.out.println("Resolution: " + width + "x" + height + " pixels");
        // In a real application, this would render the image on screen
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
}

// Step 3: Create the Proxy
public class ImageProxy implements Image {
    private String fileName;
    private RealImage realImage;
    private int thumbnailWidth;
    private int thumbnailHeight;
    
    public ImageProxy(String fileName) {
        this.fileName = fileName;
        
        // Calculate thumbnail dimensions based on filename
        if (fileName.contains("large")) {
            thumbnailWidth = 300;
            thumbnailHeight = 200;
        } else if (fileName.contains("medium")) {
            thumbnailWidth = 150;
            thumbnailHeight = 100;
        } else {
            thumbnailWidth = 80;
            thumbnailHeight = 60;
        }
    }
    
    // Lazy initialization - only load the real image when needed
    @Override
    public void display() {
        if (realImage == null) {
            System.out.println("ImageProxy: Loading the real image now...");
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }
    
    // Method to display only the thumbnail
    public void displayThumbnail() {
        System.out.println("Displaying thumbnail of image: " + fileName);
        System.out.println("Thumbnail size: " + thumbnailWidth + "x" + thumbnailHeight + " pixels");
        // In a real application, this would render a small version of the image
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public int getWidth() {
        if (realImage != null) {
            return realImage.getWidth();
        }
        return thumbnailWidth;
    }
    
    @Override
    public int getHeight() {
        if (realImage != null) {
            return realImage.getHeight();
        }
        return thumbnailHeight;
    }
    
    // Method to check if the high-resolution image is loaded
    public boolean isLoaded() {
        return realImage != null;
    }
}

// Step 4: Create a caching proxy for additional functionality
public class CachingImageProxy implements Image {
    private static Map<String, RealImage> imageCache = new HashMap<>();
    private String fileName;
    
    public CachingImageProxy(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void display() {
        if (!imageCache.containsKey(fileName)) {
            System.out.println("CachingProxy: Image not in cache. Loading...");
            imageCache.put(fileName, new RealImage(fileName));
        } else {
            System.out.println("CachingProxy: Retrieving image from cache.");
        }
        
        imageCache.get(fileName).display();
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public int getWidth() {
        if (imageCache.containsKey(fileName)) {
            return imageCache.get(fileName).getWidth();
        }
        return 0; // Unknown until loaded
    }
    
    @Override
    public int getHeight() {
        if (imageCache.containsKey(fileName)) {
            return imageCache.get(fileName).getHeight();
        }
        return 0; // Unknown until loaded
    }
    
    public static int getCacheSize() {
        return imageCache.size();
    }
    
    public static void clearCache() {
        System.out.println("Clearing image cache, removing " + imageCache.size() + " images.");
        imageCache.clear();
    }
}

// Step 5: Create a protection proxy for access control
public class ProtectedImageProxy implements Image {
    private RealImage realImage;
    private String fileName;
    private String userRole;
    private boolean hasAccessRights = false;
    
    public ProtectedImageProxy(String fileName, String userRole) {
        this.fileName = fileName;
        this.userRole = userRole;
        checkAccess();
    }
    
    private void checkAccess() {
        // Simple access control - admin and editor roles have access to all images
        // Regular users only have access to non-restricted images
        if (userRole.equals("admin") || userRole.equals("editor")) {
            hasAccessRights = true;
        } else if (userRole.equals("user") && !fileName.contains("restricted")) {
            hasAccessRights = true;
        } else {
            hasAccessRights = false;
        }
    }
    
    @Override
    public void display() {
        if (hasAccessRights) {
            if (realImage == null) {
                realImage = new RealImage(fileName);
            }
            realImage.display();
        } else {
            System.out.println("Access denied: You don't have permission to view " + fileName);
        }
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public int getWidth() {
        if (hasAccessRights && realImage != null) {
            return realImage.getWidth();
        }
        return 0; // Not accessible or not loaded
    }
    
    @Override
    public int getHeight() {
        if (hasAccessRights && realImage != null) {
            return realImage.getHeight();
        }
        return 0; // Not accessible or not loaded
    }
    
    public boolean hasAccess() {
        return hasAccessRights;
    }
}
```

### Usage Example

```java
public class ImageSystemDemo {
    public static void main(String[] args) {
        System.out.println("===== Virtual Proxy Demonstration =====");
        
        // Create an image gallery with proxy instances
        System.out.println("\nCreating image gallery (without loading images):");
        ImageProxy image1 = new ImageProxy("landscape_large.jpg");
        ImageProxy image2 = new ImageProxy("portrait_medium.jpg");
        ImageProxy image3 = new ImageProxy("icon_small.png");
        
        // Display thumbnails of all images (doesn't load high-resolution images)
        System.out.println("\nDisplaying thumbnails:");
        image1.displayThumbnail();
        image2.displayThumbnail();
        image3.displayThumbnail();
        
        // Only when the user selects an image, the high-resolution image is loaded
        System.out.println("\nUser clicks on the second image:");
        image2.display(); // This will load the high-resolution image
        
        // If the user clicks the same image again, it's already loaded
        System.out.println("\nUser clicks on the second image again:");
        image2.display(); // Already loaded, no loading time
        
        // Check which images are actually loaded
        System.out.println("\nStatus of images:");
        System.out.println("Image 1 (large): " + (image1.isLoaded() ? "Loaded" : "Not loaded"));
        System.out.println("Image 2 (medium): " + (image2.isLoaded() ? "Loaded" : "Not loaded"));
        System.out.println("Image 3 (small): " + (image3.isLoaded() ? "Not loaded" : "Not loaded"));
        
        // ===== Caching Proxy Demonstration =====
        System.out.println("\n\n===== Caching Proxy Demonstration =====");
        
        // Create caching proxy instances
        System.out.println("\nLoading and caching images:");
        CachingImageProxy cachedImage1 = new CachingImageProxy("landscape_large.jpg");
        cachedImage1.display(); // This will load and cache the image
        
        CachingImageProxy cachedImage2 = new CachingImageProxy("portrait_medium.jpg");
        cachedImage2.display(); // This will load and cache the image
        
        // Access the same image again - should be retrieved from cache
        System.out.println("\nAccessing cached images:");
        CachingImageProxy cachedImage1Again = new CachingImageProxy("landscape_large.jpg");
        cachedImage1Again.display(); // This should use the cached image
        
        // Check cache size
        System.out.println("\nCache status: " + CachingImageProxy.getCacheSize() + " images in cache");
        
        // Clear cache
        CachingImageProxy.clearCache();
        
        // ===== Protection Proxy Demonstration =====
        System.out.println("\n\n===== Protection Proxy Demonstration =====");
        
        // Create protected image proxies with different user roles
        System.out.println("\nAdmin accessing images:");
        ProtectedImageProxy adminImage1 = new ProtectedImageProxy("landscape_large.jpg", "admin");
        ProtectedImageProxy adminImage2 = new ProtectedImageProxy("restricted_content.jpg", "admin");
        
        adminImage1.display(); // Admin can access normal image
        adminImage2.display(); // Admin can access restricted image
        
        System.out.println("\nRegular user accessing images:");
        ProtectedImageProxy userImage1 = new ProtectedImageProxy("landscape_large.jpg", "user");
        ProtectedImageProxy userImage2 = new ProtectedImageProxy("restricted_content.jpg", "user");
        
        userImage1.display(); // User can access normal image
        userImage2.display(); // User cannot access restricted image
        
        System.out.println("\nAccess rights:");
        System.out.println("Admin - normal image: " + adminImage1.hasAccess());
        System.out.println("Admin - restricted image: " + adminImage2.hasAccess());
        System.out.println("User - normal image: " + userImage1.hasAccess());
        System.out.println("User - restricted image: " + userImage2.hasAccess());
    }
}
```

## Other Types of Proxies

### Remote Proxy

A remote proxy provides a local representative for an object in a different address space. This is often used in distributed systems:

```java
// Example interface that both the proxy and real object implement
public interface RemoteService {
    String performAction(String command);
}

// The actual service implementation (this would be on a remote server)
public class RemoteServiceImpl implements RemoteService {
    @Override
    public String performAction(String command) {
        System.out.println("Remote service executing command: " + command);
        // Execute the actual command logic
        return "Result of " + command;
    }
}

// Remote proxy that handles network communication
public class RemoteServiceProxy implements RemoteService {
    private String serviceUrl;
    
    public RemoteServiceProxy(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
    @Override
    public String performAction(String command) {
        System.out.println("Remote proxy connecting to " + serviceUrl);
        
        // In a real implementation, this would send a network request
        try {
            System.out.println("Establishing connection...");
            Thread.sleep(500); // Simulate network delay
            
            // This would actually call the remote service
            // For demonstration, we're creating the real implementation directly
            RemoteService realService = new RemoteServiceImpl();
            String result = realService.performAction(command);
            
            System.out.println("Received response from remote service");
            return result;
        } catch (Exception e) {
            System.out.println("Error connecting to remote service: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
```

### Smart Reference Proxy

A smart reference proxy provides additional actions when an object is accessed:

```java
// Interface for database operations
public interface Database {
    void executeQuery(String query);
}

// Real database implementation
public class RealDatabase implements Database {
    private String databaseName;
    private boolean isConnected;
    
    public RealDatabase(String databaseName) {
        this.databaseName = databaseName;
        connect();
    }
    
    private void connect() {
        System.out.println("Connecting to database: " + databaseName);
        // Simulating connection establishment
        try {
            Thread.sleep(1000);
            isConnected = true;
            System.out.println("Connected to " + databaseName);
        } catch (InterruptedException e) {
            System.out.println("Connection interrupted");
        }
    }
    
    public void disconnect() {
        if (isConnected) {
            System.out.println("Disconnecting from database: " + databaseName);
            isConnected = false;
        }
    }
    
    @Override
    public void executeQuery(String query) {
        if (isConnected) {
            System.out.println("Executing query on " + databaseName + ": " + query);
        } else {
            System.out.println("Cannot execute query: not connected to the database");
        }
    }
}

// Smart reference proxy that manages database connections
public class DatabaseProxy implements Database {
    private RealDatabase realDatabase;
    private String databaseName;
    private long lastAccessTime;
    private static final long TIMEOUT = 30000; // 30 seconds timeout
    
    public DatabaseProxy(String databaseName) {
        this.databaseName = databaseName;
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    private void checkConnection() {
        if (realDatabase == null) {
            realDatabase = new RealDatabase(databaseName);
        } else {
            // Check if connection has timed out
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAccessTime > TIMEOUT) {
                System.out.println("Connection timed out. Reconnecting...");
                realDatabase.disconnect();
                realDatabase = new RealDatabase(databaseName);
            }
        }
        lastAccessTime = System.currentTimeMillis();
    }
    
    @Override
    public void executeQuery(String query) {
        checkConnection();
        
        // Logging the query for auditing purposes
        System.out.println("Proxy: Logging query: " + query);
        
        // Execute the actual query
        realDatabase.executeQuery(query);
    }
}
```

## Benefits

1. **Lazy initialization**: The proxy can create expensive objects on demand
2. **Access control**: The proxy can restrict access to the subject based on client rights
3. **Remote representation**: The proxy can represent an object that exists in a different address space
4. **Logging**: The proxy can keep a history of requests to the subject
5. **Caching**: The proxy can cache operations' results when applicable

## Considerations

1. **Performance impact**: Proxies add a level of indirection, which might affect performance
2. **Complexity**: Introducing proxies increases the complexity of the code
3. **Implementation challenges**: Some proxy implementations (like remote proxies) might be complex to set up

## When to Use

- When you need lazy initialization for a resource-intensive object
- When you need access control for the original object
- When you need to add additional behaviors when accessing an object
- When you need a local representation for a remote object
- When you need to cache results of expensive operations
