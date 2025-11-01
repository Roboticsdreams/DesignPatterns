# Builder Pattern

[Back to Home](../README.md)

## Intent

Separate the construction of a complex object from its representation, allowing the same construction process to create different representations.

## Explanation

The Builder pattern is useful when creating complex objects with numerous parameters, especially when many are optional or when the construction process needs to produce different representations of the same object.

## Real-World Example: Document Generator

In a document generation system, we might need to create various types of documents (PDF, HTML, plain text) with different components (headers, footers, tables, images). The Builder pattern allows us to construct documents step by step using the same process regardless of the output format.

### Implementation

```java
// Step 1: Define the product class
public class Document {
    private String title;
    private String header;
    private String footer;
    private List<String> paragraphs = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<List<String>> tables = new ArrayList<>();
    private String style;
    private String format; // PDF, HTML, TXT etc.
    
    // Private constructor to force using the builder
    private Document() {}
    
    // Getters for all properties
    public String getTitle() { return title; }
    public String getHeader() { return header; }
    public String getFooter() { return footer; }
    public List<String> getParagraphs() { return new ArrayList<>(paragraphs); }
    public List<String> getImages() { return new ArrayList<>(images); }
    public List<List<String>> getTables() { return new ArrayList<>(tables); }
    public String getStyle() { return style; }
    public String getFormat() { return format; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Document[format=").append(format)
          .append(", title=").append(title)
          .append(", header=").append(header != null ? "present" : "absent")
          .append(", footer=").append(footer != null ? "present" : "absent")
          .append(", paragraphs=").append(paragraphs.size())
          .append(", images=").append(images.size())
          .append(", tables=").append(tables.size())
          .append(", style=").append(style)
          .append("]");
        return sb.toString();
    }
    
    // Static Builder class
    public static class Builder {
        private Document document = new Document();
        
        public Builder(String format) {
            document.format = format;
        }
        
        public Builder title(String title) {
            document.title = title;
            return this;
        }
        
        public Builder header(String header) {
            document.header = header;
            return this;
        }
        
        public Builder footer(String footer) {
            document.footer = footer;
            return this;
        }
        
        public Builder addParagraph(String paragraph) {
            document.paragraphs.add(paragraph);
            return this;
        }
        
        public Builder addImage(String imagePath) {
            document.images.add(imagePath);
            return this;
        }
        
        public Builder addTable(List<String> tableData) {
            document.tables.add(new ArrayList<>(tableData));
            return this;
        }
        
        public Builder style(String style) {
            document.style = style;
            return this;
        }
        
        public Document build() {
            return document;
        }
    }
}

// Step 2: Define the abstract Builder interface
public interface DocumentBuilder {
    void buildTitle(String title);
    void buildHeader(String header);
    void buildFooter(String footer);
    void addParagraph(String text);
    void addImage(String imagePath);
    void addTable(List<String> tableData);
    void setStyle(String style);
    Document getResult();
}

// Step 3: Create concrete builders for different formats
public class PDFDocumentBuilder implements DocumentBuilder {
    private Document.Builder builder;
    
    public PDFDocumentBuilder() {
        this.builder = new Document.Builder("PDF");
    }
    
    @Override
    public void buildTitle(String title) {
        builder.title(title);
    }
    
    @Override
    public void buildHeader(String header) {
        builder.header(header);
    }
    
    @Override
    public void buildFooter(String footer) {
        builder.footer(footer);
    }
    
    @Override
    public void addParagraph(String text) {
        builder.addParagraph(text);
    }
    
    @Override
    public void addImage(String imagePath) {
        builder.addImage(imagePath);
    }
    
    @Override
    public void addTable(List<String> tableData) {
        builder.addTable(tableData);
    }
    
    @Override
    public void setStyle(String style) {
        builder.style(style);
    }
    
    @Override
    public Document getResult() {
        return builder.build();
    }
}

public class HTMLDocumentBuilder implements DocumentBuilder {
    private Document.Builder builder;
    
    public HTMLDocumentBuilder() {
        this.builder = new Document.Builder("HTML");
    }
    
    @Override
    public void buildTitle(String title) {
        builder.title(title);
        // HTML-specific formatting could go here
    }
    
    @Override
    public void buildHeader(String header) {
        builder.header("<header>" + header + "</header>");
    }
    
    @Override
    public void buildFooter(String footer) {
        builder.footer("<footer>" + footer + "</footer>");
    }
    
    @Override
    public void addParagraph(String text) {
        builder.addParagraph("<p>" + text + "</p>");
    }
    
    @Override
    public void addImage(String imagePath) {
        builder.addImage("<img src='" + imagePath + "' />");
    }
    
    @Override
    public void addTable(List<String> tableData) {
        List<String> htmlTable = new ArrayList<>();
        htmlTable.add("<table>");
        for (String data : tableData) {
            htmlTable.add("<tr><td>" + data + "</td></tr>");
        }
        htmlTable.add("</table>");
        builder.addTable(htmlTable);
    }
    
    @Override
    public void setStyle(String style) {
        builder.style("<style>" + style + "</style>");
    }
    
    @Override
    public Document getResult() {
        return builder.build();
    }
}

public class PlainTextDocumentBuilder implements DocumentBuilder {
    private Document.Builder builder;
    
    public PlainTextDocumentBuilder() {
        this.builder = new Document.Builder("TXT");
    }
    
    @Override
    public void buildTitle(String title) {
        builder.title(title.toUpperCase());
    }
    
    @Override
    public void buildHeader(String header) {
        builder.header(header + "\n" + "-".repeat(header.length()));
    }
    
    @Override
    public void buildFooter(String footer) {
        builder.footer("-".repeat(footer.length()) + "\n" + footer);
    }
    
    @Override
    public void addParagraph(String text) {
        builder.addParagraph(text + "\n\n");
    }
    
    @Override
    public void addImage(String imagePath) {
        builder.addImage("[IMAGE: " + imagePath + "]");
    }
    
    @Override
    public void addTable(List<String> tableData) {
        List<String> textTable = new ArrayList<>();
        for (String data : tableData) {
            textTable.add("| " + data + " |");
        }
        builder.addTable(textTable);
    }
    
    @Override
    public void setStyle(String style) {
        // Plain text doesn't support styling
        builder.style("PLAIN");
    }
    
    @Override
    public Document getResult() {
        return builder.build();
    }
}

// Step 4: Create a Director that knows how to use a builder to construct documents
public class DocumentDirector {
    private DocumentBuilder builder;
    
    public DocumentDirector(DocumentBuilder builder) {
        this.builder = builder;
    }
    
    public void setBuilder(DocumentBuilder builder) {
        this.builder = builder;
    }
    
    // Creates a simple document with minimal elements
    public void constructSimpleDocument(String title, String content) {
        builder.buildTitle(title);
        builder.addParagraph(content);
    }
    
    // Creates a comprehensive report with all document elements
    public void constructFullReport(String title, String header, String footer, 
                                  List<String> paragraphs, List<String> images,
                                  List<List<String>> tables, String style) {
        builder.buildTitle(title);
        builder.buildHeader(header);
        builder.buildFooter(footer);
        
        for (String paragraph : paragraphs) {
            builder.addParagraph(paragraph);
        }
        
        for (String image : images) {
            builder.addImage(image);
        }
        
        for (List<String> table : tables) {
            builder.addTable(table);
        }
        
        builder.setStyle(style);
    }
    
    public Document getDocument() {
        return builder.getResult();
    }
}
```

### Usage Example

```java
public class DocumentDemo {
    public static void main(String[] args) {
        // Create builders for different document formats
        DocumentBuilder pdfBuilder = new PDFDocumentBuilder();
        DocumentBuilder htmlBuilder = new HTMLDocumentBuilder();
        DocumentBuilder textBuilder = new PlainTextDocumentBuilder();
        
        // Create a director to construct the documents
        DocumentDirector director = new DocumentDirector(pdfBuilder);
        
        // Construct a simple PDF document
        director.constructSimpleDocument("Sample PDF", "This is a simple PDF document.");
        Document pdfDocument = director.getDocument();
        System.out.println(pdfDocument);
        
        // Switch to HTML builder and create a full report
        director.setBuilder(htmlBuilder);
        
        List<String> paragraphs = Arrays.asList(
            "First paragraph of the HTML report.",
            "Second paragraph with more detailed information.",
            "Conclusion paragraph summarizing the findings."
        );
        
        List<String> images = Arrays.asList(
            "chart1.png",
            "graph2.png"
        );
        
        List<List<String>> tables = Arrays.asList(
            Arrays.asList("Row 1, Cell 1", "Row 1, Cell 2"),
            Arrays.asList("Row 2, Cell 1", "Row 2, Cell 2")
        );
        
        director.constructFullReport(
            "Comprehensive HTML Report",
            "Quarterly Performance Review",
            "© 2023 Company Name",
            paragraphs,
            images,
            tables,
            "body { font-family: Arial; }"
        );
        
        Document htmlDocument = director.getDocument();
        System.out.println(htmlDocument);
        
        // Create a plain text document with the same content
        director.setBuilder(textBuilder);
        director.constructFullReport(
            "Comprehensive Text Report",
            "Quarterly Performance Review",
            "© 2023 Company Name",
            paragraphs,
            images,
            tables,
            null
        );
        
        Document textDocument = director.getDocument();
        System.out.println(textDocument);
    }
}
```

## Benefits

1. **Construct objects step-by-step**: Creates complex objects with many components incrementally
2. **Code reuse**: The same construction process can create different representations
3. **Single Responsibility Principle**: Isolates complex construction code from business logic
4. **Flexibility**: Provides more control over the construction process than constructors

## Considerations

1. **Complexity**: Adds complexity by requiring many new classes
2. **Mutability**: Requires objects to be mutable during construction

## When to Use

- When the algorithm for creating a complex object should be independent of the parts that make up the object
- When the construction process must allow different representations of the constructed object
- When you need to construct complex objects with many optional components or configuration parameters
