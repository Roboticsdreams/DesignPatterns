# Visitor Pattern

[Back to Home](../README.md)

## Intent

Represent an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements on which it operates.

## Explanation

The Visitor pattern allows you to separate algorithms from the objects on which they operate. It lets you add new operations to existing object structures without modifying the structures themselves. This pattern is particularly useful when working with complex object hierarchies where functionality needs to be distributed across the hierarchy.

## Real-World Example: Document Object Model Operations

A document processing system with a complex structure (headings, paragraphs, tables, images, etc.) that needs to support operations like rendering, spell checking, and statistics gathering without modifying the document structure classes.

### Implementation

```java
import java.util.ArrayList;
import java.util.List;

// Step 1: Define the Visitor interface
public interface DocumentVisitor {
    void visit(HeadingElement heading);
    void visit(ParagraphElement paragraph);
    void visit(ImageElement image);
    void visit(TableElement table);
    void visit(HyperlinkElement hyperlink);
}

// Step 2: Define the Element interface
public interface DocumentElement {
    void accept(DocumentVisitor visitor);
}

// Step 3: Create concrete element classes
public class HeadingElement implements DocumentElement {
    private String text;
    private int level; // 1 for h1, 2 for h2, etc.
    
    public HeadingElement(String text, int level) {
        this.text = text;
        this.level = level;
    }
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getText() {
        return text;
    }
    
    public int getLevel() {
        return level;
    }
}

public class ParagraphElement implements DocumentElement {
    private String text;
    
    public ParagraphElement(String text) {
        this.text = text;
    }
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getText() {
        return text;
    }
}

public class ImageElement implements DocumentElement {
    private String source;
    private String altText;
    private int width;
    private int height;
    
    public ImageElement(String source, String altText, int width, int height) {
        this.source = source;
        this.altText = altText;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getSource() {
        return source;
    }
    
    public String getAltText() {
        return altText;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}

public class TableElement implements DocumentElement {
    private List<List<String>> data;
    private List<String> headers;
    
    public TableElement(List<String> headers, List<List<String>> data) {
        this.headers = headers;
        this.data = data;
    }
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
    
    public List<List<String>> getData() {
        return data;
    }
    
    public List<String> getHeaders() {
        return headers;
    }
    
    public int getRowCount() {
        return data.size();
    }
    
    public int getColumnCount() {
        return headers.size();
    }
}

public class HyperlinkElement implements DocumentElement {
    private String text;
    private String url;
    
    public HyperlinkElement(String text, String url) {
        this.text = text;
        this.url = url;
    }
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getText() {
        return text;
    }
    
    public String getUrl() {
        return url;
    }
}

// Step 4: Create a composite element to hold the document structure
public class Document implements DocumentElement {
    private List<DocumentElement> elements;
    
    public Document() {
        this.elements = new ArrayList<>();
    }
    
    public void addElement(DocumentElement element) {
        elements.add(element);
    }
    
    @Override
    public void accept(DocumentVisitor visitor) {
        for (DocumentElement element : elements) {
            element.accept(visitor);
        }
    }
    
    public List<DocumentElement> getElements() {
        return new ArrayList<>(elements);
    }
}

// Step 5: Create concrete visitors
public class HTMLExportVisitor implements DocumentVisitor {
    private StringBuilder htmlOutput;
    
    public HTMLExportVisitor() {
        htmlOutput = new StringBuilder();
        htmlOutput.append("<!DOCTYPE html>\n<html>\n<head>\n<title>Document</title>\n</head>\n<body>\n");
    }
    
    @Override
    public void visit(HeadingElement heading) {
        htmlOutput.append("<h").append(heading.getLevel()).append(">")
                 .append(heading.getText())
                 .append("</h").append(heading.getLevel()).append(">\n");
    }
    
    @Override
    public void visit(ParagraphElement paragraph) {
        htmlOutput.append("<p>").append(paragraph.getText()).append("</p>\n");
    }
    
    @Override
    public void visit(ImageElement image) {
        htmlOutput.append("<img src=\"").append(image.getSource()).append("\" ")
                 .append("alt=\"").append(image.getAltText()).append("\" ")
                 .append("width=\"").append(image.getWidth()).append("\" ")
                 .append("height=\"").append(image.getHeight()).append("\">\n");
    }
    
    @Override
    public void visit(TableElement table) {
        htmlOutput.append("<table border=\"1\">\n");
        
        // Table headers
        htmlOutput.append("  <tr>\n");
        for (String header : table.getHeaders()) {
            htmlOutput.append("    <th>").append(header).append("</th>\n");
        }
        htmlOutput.append("  </tr>\n");
        
        // Table data
        for (List<String> row : table.getData()) {
            htmlOutput.append("  <tr>\n");
            for (String cell : row) {
                htmlOutput.append("    <td>").append(cell).append("</td>\n");
            }
            htmlOutput.append("  </tr>\n");
        }
        
        htmlOutput.append("</table>\n");
    }
    
    @Override
    public void visit(HyperlinkElement hyperlink) {
        htmlOutput.append("<a href=\"").append(hyperlink.getUrl()).append("\">")
                 .append(hyperlink.getText()).append("</a>\n");
    }
    
    public String getHTML() {
        return htmlOutput.toString() + "</body>\n</html>";
    }
}

public class MarkdownExportVisitor implements DocumentVisitor {
    private StringBuilder markdownOutput;
    
    public MarkdownExportVisitor() {
        markdownOutput = new StringBuilder();
    }
    
    @Override
    public void visit(HeadingElement heading) {
        for (int i = 0; i < heading.getLevel(); i++) {
            markdownOutput.append("#");
        }
        markdownOutput.append(" ").append(heading.getText()).append("\n\n");
    }
    
    @Override
    public void visit(ParagraphElement paragraph) {
        markdownOutput.append(paragraph.getText()).append("\n\n");
    }
    
    @Override
    public void visit(ImageElement image) {
        markdownOutput.append("![").append(image.getAltText()).append("](")
                     .append(image.getSource()).append(")\n\n");
    }
    
    @Override
    public void visit(TableElement table) {
        // Table headers
        for (String header : table.getHeaders()) {
            markdownOutput.append("| ").append(header).append(" ");
        }
        markdownOutput.append("|\n");
        
        // Table separator
        for (int i = 0; i < table.getHeaders().size(); i++) {
            markdownOutput.append("| --- ");
        }
        markdownOutput.append("|\n");
        
        // Table data
        for (List<String> row : table.getData()) {
            for (String cell : row) {
                markdownOutput.append("| ").append(cell).append(" ");
            }
            markdownOutput.append("|\n");
        }
        markdownOutput.append("\n");
    }
    
    @Override
    public void visit(HyperlinkElement hyperlink) {
        markdownOutput.append("[").append(hyperlink.getText()).append("](")
                     .append(hyperlink.getUrl()).append(")\n\n");
    }
    
    public String getMarkdown() {
        return markdownOutput.toString();
    }
}

public class DocumentStatisticsVisitor implements DocumentVisitor {
    private int headingCount;
    private int paragraphCount;
    private int imageCount;
    private int tableCount;
    private int hyperlinkCount;
    private int wordCount;
    private int characterCount;
    
    public DocumentStatisticsVisitor() {
        headingCount = 0;
        paragraphCount = 0;
        imageCount = 0;
        tableCount = 0;
        hyperlinkCount = 0;
        wordCount = 0;
        characterCount = 0;
    }
    
    @Override
    public void visit(HeadingElement heading) {
        headingCount++;
        countWordsAndChars(heading.getText());
    }
    
    @Override
    public void visit(ParagraphElement paragraph) {
        paragraphCount++;
        countWordsAndChars(paragraph.getText());
    }
    
    @Override
    public void visit(ImageElement image) {
        imageCount++;
        if (image.getAltText() != null) {
            countWordsAndChars(image.getAltText());
        }
    }
    
    @Override
    public void visit(TableElement table) {
        tableCount++;
        
        // Count words in table headers
        for (String header : table.getHeaders()) {
            countWordsAndChars(header);
        }
        
        // Count words in table cells
        for (List<String> row : table.getData()) {
            for (String cell : row) {
                countWordsAndChars(cell);
            }
        }
    }
    
    @Override
    public void visit(HyperlinkElement hyperlink) {
        hyperlinkCount++;
        countWordsAndChars(hyperlink.getText());
    }
    
    private void countWordsAndChars(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // Count characters (excluding spaces)
        characterCount += text.replaceAll("\\s+", "").length();
        
        // Count words
        String[] words = text.split("\\s+");
        wordCount += words.length;
    }
    
    public void printStatistics() {
        System.out.println("Document Statistics:");
        System.out.println("-------------------");
        System.out.println("Headings: " + headingCount);
        System.out.println("Paragraphs: " + paragraphCount);
        System.out.println("Images: " + imageCount);
        System.out.println("Tables: " + tableCount);
        System.out.println("Hyperlinks: " + hyperlinkCount);
        System.out.println("Word Count: " + wordCount);
        System.out.println("Character Count: " + characterCount);
    }
    
    public int getHeadingCount() { return headingCount; }
    public int getParagraphCount() { return paragraphCount; }
    public int getImageCount() { return imageCount; }
    public int getTableCount() { return tableCount; }
    public int getHyperlinkCount() { return hyperlinkCount; }
    public int getWordCount() { return wordCount; }
    public int getCharacterCount() { return characterCount; }
}

// Spell checking visitor
public class SpellCheckVisitor implements DocumentVisitor {
    private List<String> misspelledWords;
    private List<String> dictionary;
    
    public SpellCheckVisitor() {
        misspelledWords = new ArrayList<>();
        dictionary = new ArrayList<>();
        
        // Initialize a simple dictionary
        dictionary.add("the");
        dictionary.add("quick");
        dictionary.add("brown");
        dictionary.add("fox");
        dictionary.add("jumps");
        dictionary.add("over");
        dictionary.add("lazy");
        dictionary.add("dog");
        dictionary.add("hello");
        dictionary.add("world");
        dictionary.add("document");
        dictionary.add("heading");
        dictionary.add("image");
        dictionary.add("table");
        dictionary.add("link");
        dictionary.add("visitor");
        dictionary.add("pattern");
        // Add more words as needed
    }
    
    @Override
    public void visit(HeadingElement heading) {
        checkSpelling(heading.getText());
    }
    
    @Override
    public void visit(ParagraphElement paragraph) {
        checkSpelling(paragraph.getText());
    }
    
    @Override
    public void visit(ImageElement image) {
        checkSpelling(image.getAltText());
    }
    
    @Override
    public void visit(TableElement table) {
        // Check headers
        for (String header : table.getHeaders()) {
            checkSpelling(header);
        }
        
        // Check data
        for (List<String> row : table.getData()) {
            for (String cell : row) {
                checkSpelling(cell);
            }
        }
    }
    
    @Override
    public void visit(HyperlinkElement hyperlink) {
        checkSpelling(hyperlink.getText());
    }
    
    private void checkSpelling(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // Split text into words and check each word
        String[] words = text.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
        for (String word : words) {
            if (!word.isEmpty() && !dictionary.contains(word)) {
                misspelledWords.add(word);
            }
        }
    }
    
    public List<String> getMisspelledWords() {
        return new ArrayList<>(misspelledWords);
    }
    
    public void printMisspelledWords() {
        System.out.println("Spell Check Results:");
        System.out.println("-------------------");
        if (misspelledWords.isEmpty()) {
            System.out.println("No misspelled words found.");
        } else {
            System.out.println("Found " + misspelledWords.size() + " misspelled words:");
            for (String word : misspelledWords) {
                System.out.println("- " + word);
            }
        }
    }
}
```

### Usage Example

```java
import java.util.Arrays;
import java.util.List;

public class DocumentVisitorDemo {
    public static void main(String[] args) {
        // Create a document with various elements
        Document document = createSampleDocument();
        
        System.out.println("=== Demonstrating HTML Export Visitor ===");
        HTMLExportVisitor htmlVisitor = new HTMLExportVisitor();
        document.accept(htmlVisitor);
        System.out.println(htmlVisitor.getHTML());
        
        System.out.println("\n=== Demonstrating Markdown Export Visitor ===");
        MarkdownExportVisitor markdownVisitor = new MarkdownExportVisitor();
        document.accept(markdownVisitor);
        System.out.println(markdownVisitor.getMarkdown());
        
        System.out.println("\n=== Demonstrating Statistics Visitor ===");
        DocumentStatisticsVisitor statisticsVisitor = new DocumentStatisticsVisitor();
        document.accept(statisticsVisitor);
        statisticsVisitor.printStatistics();
        
        System.out.println("\n=== Demonstrating Spell Check Visitor ===");
        SpellCheckVisitor spellCheckVisitor = new SpellCheckVisitor();
        document.accept(spellCheckVisitor);
        spellCheckVisitor.printMisspelledWords();
    }
    
    private static Document createSampleDocument() {
        Document document = new Document();
        
        // Add heading
        document.addElement(new HeadingElement("Visitor Pattern Example", 1));
        
        // Add paragraph
        document.addElement(new ParagraphElement(
            "The Visitor pattern is a behavioral design pattern that lets you separate " +
            "algorithms from the objects on which they operate. This example demonstrates " +
            "how to apply the visitor pattern to a document structure."
        ));
        
        // Add subheading
        document.addElement(new HeadingElement("Key Benefits", 2));
        
        // Add another paragraph
        document.addElement(new ParagraphElement(
            "The main benefit of the visitor pattern is that it allows you to add new " +
            "operations without changing the classes of the elements on which it operates. " +
            "This is particularly useful when working with complex object structures."
        ));
        
        // Add an image
        document.addElement(new ImageElement(
            "visitor-pattern.png",
            "Visitor Pattern UML Diagram",
            640,
            480
        ));
        
        // Add a table
        List<String> headers = Arrays.asList("Pattern", "Type", "Intent");
        List<List<String>> data = Arrays.asList(
            Arrays.asList("Visitor", "Behavioral", "Add operations without changing classes"),
            Arrays.asList("Singleton", "Creational", "Ensure a class has only one instance"),
            Arrays.asList("Observer", "Behavioral", "Notify objects about state changes")
        );
        document.addElement(new TableElement(headers, data));
        
        // Add a hyperlink
        document.addElement(new HyperlinkElement(
            "Learn more about design patterns",
            "https://refactoring.guru/design-patterns"
        ));
        
        // Add a misspelled paragraph to demonstrate spell checking
        document.addElement(new ParagraphElement(
            "This pragraf containes sum mispeled wrds for testing the spel check visitor."
        ));
        
        return document;
    }
}
```

## Another Example: File System Visitor

Here's another implementation of the Visitor pattern for traversing and performing operations on a file system:

```java
// Element interface
interface FileSystemElement {
    void accept(FileSystemVisitor visitor);
    String getName();
    String getPath();
}

// Concrete Elements
class File implements FileSystemElement {
    private String name;
    private String path;
    private String extension;
    private long size;
    
    public File(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
        
        int dotIndex = name.lastIndexOf('.');
        this.extension = (dotIndex > 0) ? name.substring(dotIndex + 1) : "";
    }
    
    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public long getSize() {
        return size;
    }
}

class Directory implements FileSystemElement {
    private String name;
    private String path;
    private List<FileSystemElement> children;
    
    public Directory(String name, String path) {
        this.name = name;
        this.path = path;
        this.children = new ArrayList<>();
    }
    
    public void addElement(FileSystemElement element) {
        children.add(element);
    }
    
    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
        
        for (FileSystemElement element : children) {
            element.accept(visitor);
        }
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    public List<FileSystemElement> getChildren() {
        return new ArrayList<>(children);
    }
}

// Visitor interface
interface FileSystemVisitor {
    void visit(File file);
    void visit(Directory directory);
}

// Concrete Visitors
class FileCountVisitor implements FileSystemVisitor {
    private int fileCount = 0;
    private int directoryCount = 0;
    private Map<String, Integer> extensionCounts = new HashMap<>();
    
    @Override
    public void visit(File file) {
        fileCount++;
        
        // Count file extensions
        String extension = file.getExtension();
        extensionCounts.put(extension, extensionCounts.getOrDefault(extension, 0) + 1);
    }
    
    @Override
    public void visit(Directory directory) {
        directoryCount++;
    }
    
    public int getFileCount() {
        return fileCount;
    }
    
    public int getDirectoryCount() {
        return directoryCount;
    }
    
    public Map<String, Integer> getExtensionCounts() {
        return new HashMap<>(extensionCounts);
    }
    
    public void printResults() {
        System.out.println("File System Statistics:");
        System.out.println("----------------------");
        System.out.println("Total directories: " + directoryCount);
        System.out.println("Total files: " + fileCount);
        System.out.println("\nFiles by extension:");
        
        for (Map.Entry<String, Integer> entry : extensionCounts.entrySet()) {
            System.out.println("." + entry.getKey() + ": " + entry.getValue());
        }
    }
}

class FileSizeVisitor implements FileSystemVisitor {
    private long totalSize = 0;
    private Map<String, Long> sizeByExtension = new HashMap<>();
    
    @Override
    public void visit(File file) {
        totalSize += file.getSize();
        
        // Aggregate size by extension
        String extension = file.getExtension();
        sizeByExtension.put(extension, 
                          sizeByExtension.getOrDefault(extension, 0L) + file.getSize());
    }
    
    @Override
    public void visit(Directory directory) {
        // Do nothing for directories
    }
    
    public long getTotalSize() {
        return totalSize;
    }
    
    public Map<String, Long> getSizeByExtension() {
        return new HashMap<>(sizeByExtension);
    }
    
    public void printResults() {
        System.out.println("File System Size Analysis:");
        System.out.println("------------------------");
        System.out.println("Total size: " + formatSize(totalSize));
        System.out.println("\nSize by extension:");
        
        for (Map.Entry<String, Long> entry : sizeByExtension.entrySet()) {
            System.out.println("." + entry.getKey() + ": " + formatSize(entry.getValue()));
        }
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}

class SearchVisitor implements FileSystemVisitor {
    private String searchTerm;
    private boolean caseSensitive;
    private List<FileSystemElement> matches = new ArrayList<>();
    
    public SearchVisitor(String searchTerm, boolean caseSensitive) {
        this.searchTerm = caseSensitive ? searchTerm : searchTerm.toLowerCase();
        this.caseSensitive = caseSensitive;
    }
    
    @Override
    public void visit(File file) {
        String name = caseSensitive ? file.getName() : file.getName().toLowerCase();
        if (name.contains(searchTerm)) {
            matches.add(file);
        }
    }
    
    @Override
    public void visit(Directory directory) {
        String name = caseSensitive ? directory.getName() : directory.getName().toLowerCase();
        if (name.contains(searchTerm)) {
            matches.add(directory);
        }
    }
    
    public List<FileSystemElement> getMatches() {
        return new ArrayList<>(matches);
    }
    
    public void printResults() {
        System.out.println("Search Results for '" + searchTerm + "':");
        System.out.println("-----------------------" + "-".repeat(searchTerm.length()));
        
        if (matches.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        
        for (FileSystemElement element : matches) {
            String type = (element instanceof File) ? "File" : "Directory";
            System.out.println(type + ": " + element.getPath());
        }
        System.out.println("\nTotal matches: " + matches.size());
    }
}
```

## Benefits

1. **Open/Closed Principle**: Add new operations without modifying element classes
2. **Single Responsibility Principle**: Operations on elements are centralized in visitors
3. **Accumulating state**: Visitors can collect data while traversing an object structure
4. **Multiple operations**: Different visitors can implement different operations
5. **Related operations**: Centralizes related operations in one visitor class

## Considerations

1. **Double dispatch complexity**: The pattern relies on double dispatch which can be hard to understand
2. **Breaking encapsulation**: Elements may need to expose internal state to visitors
3. **Evolution difficulty**: Adding new element types requires updating all visitor interfaces and implementations
4. **Too many methods**: The visitor interface can grow large with many element types
5. **Traversal control**: Elements control the traversal order, which might not be ideal for all operations

## When to Use

- When you need to perform operations on objects in a complex structure and want to avoid "polluting" their classes with these operations
- When you need to add new operations frequently but the object structure rarely changes
- When the object structure classes have different interfaces but you want to perform operations that depend on their concrete classes
- When you need to collect state across multiple elements in an object structure
