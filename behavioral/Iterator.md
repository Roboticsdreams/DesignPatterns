# Iterator Pattern

[Back to Home](../README.md)

## Intent

Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.

## Explanation

The Iterator pattern provides a standardized way to traverse through a collection of objects without exposing the underlying implementation of the collection. It separates the traversal behavior from the collection, allowing different traversal algorithms to be used independently.

## Real-World Example: Custom Collection Traversal

A library management system that needs to provide various ways to iterate through book collections (by title, by author, by publication date, etc.) without exposing how the books are stored internally.

### Implementation

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// Step 1: Create the Iterator interface
public interface BookIterator {
    boolean hasNext();
    Book next();
}

// Step 2: Create the Collection interface
public interface BookCollection {
    BookIterator getIterator();
    void addBook(Book book);
    int count();
}

// Step 3: Create the Book class
public class Book {
    private String title;
    private String author;
    private String isbn;
    private Date publicationDate;
    private String genre;
    
    public Book(String title, String author, String isbn, Date publicationDate, String genre) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.genre = genre;
    }
    
    // Getters and setters
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public Date getPublicationDate() {
        return publicationDate;
    }
    
    public String getGenre() {
        return genre;
    }
    
    @Override
    public String toString() {
        return "\"" + title + "\" by " + author + " (" + genre + ", ISBN: " + isbn + ")";
    }
}

// Step 4: Create concrete collections and iterators

// Default collection with default iterator (by addition order)
public class Library implements BookCollection {
    private List<Book> books;
    
    public Library() {
        this.books = new ArrayList<>();
    }
    
    @Override
    public BookIterator getIterator() {
        return new DefaultBookIterator();
    }
    
    @Override
    public void addBook(Book book) {
        books.add(book);
    }
    
    @Override
    public int count() {
        return books.size();
    }
    
    // Iterator that traverses books in the order they were added
    private class DefaultBookIterator implements BookIterator {
        private int currentIndex = 0;
        
        @Override
        public boolean hasNext() {
            return currentIndex < books.size();
        }
        
        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more books");
            }
            return books.get(currentIndex++);
        }
    }
    
    // Method to get a title-sorted iterator
    public BookIterator getTitleSortedIterator() {
        return new TitleSortedBookIterator();
    }
    
    // Iterator that traverses books sorted by title
    private class TitleSortedBookIterator implements BookIterator {
        private int currentIndex = 0;
        private List<Book> sortedBooks;
        
        public TitleSortedBookIterator() {
            sortedBooks = new ArrayList<>(books);
            sortedBooks.sort(Comparator.comparing(Book::getTitle));
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < sortedBooks.size();
        }
        
        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more books");
            }
            return sortedBooks.get(currentIndex++);
        }
    }
    
    // Method to get an author-sorted iterator
    public BookIterator getAuthorSortedIterator() {
        return new AuthorSortedBookIterator();
    }
    
    // Iterator that traverses books sorted by author
    private class AuthorSortedBookIterator implements BookIterator {
        private int currentIndex = 0;
        private List<Book> sortedBooks;
        
        public AuthorSortedBookIterator() {
            sortedBooks = new ArrayList<>(books);
            sortedBooks.sort(Comparator.comparing(Book::getAuthor));
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < sortedBooks.size();
        }
        
        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more books");
            }
            return sortedBooks.get(currentIndex++);
        }
    }
    
    // Method to get a publication date-sorted iterator
    public BookIterator getDateSortedIterator() {
        return new DateSortedBookIterator();
    }
    
    // Iterator that traverses books sorted by publication date
    private class DateSortedBookIterator implements BookIterator {
        private int currentIndex = 0;
        private List<Book> sortedBooks;
        
        public DateSortedBookIterator() {
            sortedBooks = new ArrayList<>(books);
            sortedBooks.sort(Comparator.comparing(Book::getPublicationDate));
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < sortedBooks.size();
        }
        
        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more books");
            }
            return sortedBooks.get(currentIndex++);
        }
    }
    
    // Method to get a genre-filtered iterator
    public BookIterator getGenreFilteredIterator(String genre) {
        return new GenreFilteredBookIterator(genre);
    }
    
    // Iterator that traverses only books of a specific genre
    private class GenreFilteredBookIterator implements BookIterator {
        private int currentIndex = 0;
        private List<Book> filteredBooks;
        
        public GenreFilteredBookIterator(String genre) {
            filteredBooks = new ArrayList<>();
            for (Book book : books) {
                if (book.getGenre().equalsIgnoreCase(genre)) {
                    filteredBooks.add(book);
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < filteredBooks.size();
        }
        
        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more books");
            }
            return filteredBooks.get(currentIndex++);
        }
    }
}

// Step 5: Create a more complex collection with a different internal structure
public class BookCatalog implements BookCollection {
    private Map<String, Book> booksByIsbn;
    
    public BookCatalog() {
        this.booksByIsbn = new HashMap<>();
    }
    
    @Override
    public BookIterator getIterator() {
        return new CatalogIterator();
    }
    
    @Override
    public void addBook(Book book) {
        booksByIsbn.put(book.getIsbn(), book);
    }
    
    @Override
    public int count() {
        return booksByIsbn.size();
    }
    
    public Book findByIsbn(String isbn) {
        return booksByIsbn.get(isbn);
    }
    
    // Default iterator that traverses all books in the catalog
    private class CatalogIterator implements BookIterator {
        private List<Book> books;
        private int currentIndex = 0;
        
        public CatalogIterator() {
            books = new ArrayList<>(booksByIsbn.values());
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < books.size();
        }
        
        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more books");
            }
            return books.get(currentIndex++);
        }
    }
}
```

### Usage Example

```java
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LibraryDemo {
    public static void main(String[] args) throws ParseException {
        // Create a date formatter to parse publication dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Create a library collection
        Library library = new Library();
        
        // Add books to the library
        library.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", 
                     dateFormat.parse("1925-04-10"), "Fiction"));
        library.addBook(new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084", 
                     dateFormat.parse("1960-07-11"), "Fiction"));
        library.addBook(new Book("1984", "George Orwell", "978-0451524935", 
                     dateFormat.parse("1949-06-08"), "Science Fiction"));
        library.addBook(new Book("Pride and Prejudice", "Jane Austen", "978-0141439518", 
                     dateFormat.parse("1813-01-28"), "Romance"));
        library.addBook(new Book("The Hobbit", "J.R.R. Tolkien", "978-0618260300", 
                     dateFormat.parse("1937-09-21"), "Fantasy"));
        library.addBook(new Book("Dune", "Frank Herbert", "978-0441172719", 
                     dateFormat.parse("1965-08-01"), "Science Fiction"));
        library.addBook(new Book("The Lord of the Rings", "J.R.R. Tolkien", "978-0618640157", 
                     dateFormat.parse("1954-07-29"), "Fantasy"));
        library.addBook(new Book("The Catcher in the Rye", "J.D. Salinger", "978-0316769480", 
                     dateFormat.parse("1951-07-16"), "Fiction"));
        
        // Use the default iterator (by insertion order)
        System.out.println("=== Books in default order ===");
        BookIterator defaultIterator = library.getIterator();
        while (defaultIterator.hasNext()) {
            Book book = defaultIterator.next();
            System.out.println(book);
        }
        
        // Use the title-sorted iterator
        System.out.println("\n=== Books sorted by title ===");
        BookIterator titleIterator = library.getTitleSortedIterator();
        while (titleIterator.hasNext()) {
            Book book = titleIterator.next();
            System.out.println(book);
        }
        
        // Use the author-sorted iterator
        System.out.println("\n=== Books sorted by author ===");
        BookIterator authorIterator = library.getAuthorSortedIterator();
        while (authorIterator.hasNext()) {
            Book book = authorIterator.next();
            System.out.println(book);
        }
        
        // Use the publication date-sorted iterator
        System.out.println("\n=== Books sorted by publication date ===");
        BookIterator dateIterator = library.getDateSortedIterator();
        while (dateIterator.hasNext()) {
            Book book = dateIterator.next();
            System.out.println(book + " (Published: " + 
                             new SimpleDateFormat("MMMM d, yyyy").format(book.getPublicationDate()) + ")");
        }
        
        // Use the genre-filtered iterator
        System.out.println("\n=== Science Fiction books ===");
        BookIterator sciFiIterator = library.getGenreFilteredIterator("Science Fiction");
        while (sciFiIterator.hasNext()) {
            Book book = sciFiIterator.next();
            System.out.println(book);
        }
        
        System.out.println("\n=== Fantasy books ===");
        BookIterator fantasyIterator = library.getGenreFilteredIterator("Fantasy");
        while (fantasyIterator.hasNext()) {
            Book book = fantasyIterator.next();
            System.out.println(book);
        }
        
        // Create a book catalog (different internal structure)
        BookCatalog catalog = new BookCatalog();
        
        // Add the same books to the catalog
        catalog.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", 
                    dateFormat.parse("1925-04-10"), "Fiction"));
        catalog.addBook(new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084", 
                    dateFormat.parse("1960-07-11"), "Fiction"));
        catalog.addBook(new Book("1984", "George Orwell", "978-0451524935", 
                    dateFormat.parse("1949-06-08"), "Science Fiction"));
        
        // Use the catalog iterator
        System.out.println("\n=== Books in catalog ===");
        BookIterator catalogIterator = catalog.getIterator();
        while (catalogIterator.hasNext()) {
            Book book = catalogIterator.next();
            System.out.println(book);
        }
        
        // Demonstrate finding a book by ISBN
        System.out.println("\n=== Finding book by ISBN ===");
        Book foundBook = catalog.findByIsbn("978-0451524935");
        if (foundBook != null) {
            System.out.println("Found: " + foundBook);
        } else {
            System.out.println("Book not found");
        }
    }
}
```

## Another Example: Tree Traversal Iterators

Here's another example showing different iterators for traversing a tree structure:

```java
// Tree Node class
class TreeNode<T> {
    private T value;
    private List<TreeNode<T>> children;
    
    public TreeNode(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }
    
    public T getValue() {
        return value;
    }
    
    public void addChild(TreeNode<T> child) {
        children.add(child);
    }
    
    public List<TreeNode<T>> getChildren() {
        return new ArrayList<>(children);
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
}

// Iterator interface
interface TreeIterator<T> {
    boolean hasNext();
    TreeNode<T> next();
}

// Depth First Iterator
class DepthFirstIterator<T> implements TreeIterator<T> {
    private Stack<TreeNode<T>> stack = new Stack<>();
    
    public DepthFirstIterator(TreeNode<T> root) {
        if (root != null) {
            stack.push(root);
        }
    }
    
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }
    
    @Override
    public TreeNode<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        TreeNode<T> node = stack.pop();
        
        // Add children in reverse order to get left-to-right traversal when popping
        List<TreeNode<T>> children = node.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            stack.push(children.get(i));
        }
        
        return node;
    }
}

// Breadth First Iterator
class BreadthFirstIterator<T> implements TreeIterator<T> {
    private Queue<TreeNode<T>> queue = new LinkedList<>();
    
    public BreadthFirstIterator(TreeNode<T> root) {
        if (root != null) {
            queue.offer(root);
        }
    }
    
    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }
    
    @Override
    public TreeNode<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        TreeNode<T> node = queue.poll();
        
        // Add all children to the queue
        for (TreeNode<T> child : node.getChildren()) {
            queue.offer(child);
        }
        
        return node;
    }
}

// Leaf-Only Iterator
class LeafNodeIterator<T> implements TreeIterator<T> {
    private TreeIterator<T> iterator;
    private TreeNode<T> nextLeaf;
    
    public LeafNodeIterator(TreeNode<T> root) {
        // Use depth-first traversal to find leaves
        this.iterator = new DepthFirstIterator<>(root);
        findNextLeaf();
    }
    
    private void findNextLeaf() {
        nextLeaf = null;
        while (iterator.hasNext() && nextLeaf == null) {
            TreeNode<T> node = iterator.next();
            if (node.isLeaf()) {
                nextLeaf = node;
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return nextLeaf != null;
    }
    
    @Override
    public TreeNode<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        TreeNode<T> result = nextLeaf;
        findNextLeaf();
        return result;
    }
}

// Tree class that provides different iterators
class Tree<T> {
    private TreeNode<T> root;
    
    public Tree(T rootValue) {
        this.root = new TreeNode<>(rootValue);
    }
    
    public TreeNode<T> getRoot() {
        return root;
    }
    
    public TreeIterator<T> depthFirstIterator() {
        return new DepthFirstIterator<>(root);
    }
    
    public TreeIterator<T> breadthFirstIterator() {
        return new BreadthFirstIterator<>(root);
    }
    
    public TreeIterator<T> leafNodeIterator() {
        return new LeafNodeIterator<>(root);
    }
}
```

## Benefits

1. **Single Responsibility Principle**: Extracts traversal behavior from the collection class
2. **Open/Closed Principle**: New traversal algorithms can be added without changing collection classes
3. **Multiple simultaneous traversals**: You can have multiple iterators on the same collection simultaneously
4. **Abstraction**: Clients don't need to know how collections are implemented internally

## Considerations

1. **Efficiency**: Iterator implementations may impact performance depending on the collection's structure
2. **Complexity**: Additional classes and interfaces are required
3. **State consistency**: Modifications to collections during iteration can cause problems
4. **Modern languages**: Many programming languages now have built-in iterator support

## When to Use

- When you want to access a collection's elements without exposing its internal representation
- When you want to support multiple traversal algorithms for a collection
- When you need a uniform interface for traversing different collection types
- When you want to decouple collection traversal from the collection itself
