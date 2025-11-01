# Interpreter Pattern

[Back to Home](../README.md)

## Intent

Define a representation for a grammar of a given language along with an interpreter that uses the representation to interpret sentences in the language.

## Explanation

The Interpreter pattern is used to define a grammatical representation for a language and provides an interpreter to deal with this grammar. The pattern involves implementing an expression interface which tells how to interpret a particular context.

## Real-World Example: Simple Query Language Interpreter

A domain-specific language interpreter that can process simple queries like "SELECT name FROM employees WHERE department = 'IT' AND salary > 50000" and translate them into operations on data.

### Implementation

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Step 1: Define the Context class
public class Context {
    private Map<String, List<Map<String, Object>>> tables = new HashMap<>();
    private List<Map<String, Object>> resultSet;
    private String currentTable;
    
    public Context() {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Create employees table
        List<Map<String, Object>> employees = new ArrayList<>();
        
        Map<String, Object> employee1 = new HashMap<>();
        employee1.put("id", 1);
        employee1.put("name", "John Smith");
        employee1.put("department", "IT");
        employee1.put("salary", 55000);
        employees.add(employee1);
        
        Map<String, Object> employee2 = new HashMap<>();
        employee2.put("id", 2);
        employee2.put("name", "Jane Doe");
        employee2.put("department", "HR");
        employee2.put("salary", 48000);
        employees.add(employee2);
        
        Map<String, Object> employee3 = new HashMap<>();
        employee3.put("id", 3);
        employee3.put("name", "Michael Johnson");
        employee3.put("department", "IT");
        employee3.put("salary", 62000);
        employees.add(employee3);
        
        Map<String, Object> employee4 = new HashMap<>();
        employee4.put("id", 4);
        employee4.put("name", "Emily Williams");
        employee4.put("department", "Marketing");
        employee4.put("salary", 51000);
        employees.add(employee4);
        
        Map<String, Object> employee5 = new HashMap<>();
        employee5.put("id", 5);
        employee5.put("name", "David Brown");
        employee5.put("department", "IT");
        employee5.put("salary", 49000);
        employees.add(employee5);
        
        tables.put("employees", employees);
        
        // Create departments table
        List<Map<String, Object>> departments = new ArrayList<>();
        
        Map<String, Object> dept1 = new HashMap<>();
        dept1.put("id", 1);
        dept1.put("name", "IT");
        dept1.put("location", "Building A");
        departments.add(dept1);
        
        Map<String, Object> dept2 = new HashMap<>();
        dept2.put("id", 2);
        dept2.put("name", "HR");
        dept2.put("location", "Building B");
        departments.add(dept2);
        
        Map<String, Object> dept3 = new HashMap<>();
        dept3.put("id", 3);
        dept3.put("name", "Marketing");
        dept3.put("location", "Building C");
        departments.add(dept3);
        
        tables.put("departments", departments);
    }
    
    public void setTable(String tableName) {
        if (tables.containsKey(tableName)) {
            this.currentTable = tableName;
            this.resultSet = new ArrayList<>(tables.get(tableName));
        } else {
            throw new RuntimeException("Table not found: " + tableName);
        }
    }
    
    public List<Map<String, Object>> getResultSet() {
        return resultSet;
    }
    
    public void setResultSet(List<Map<String, Object>> resultSet) {
        this.resultSet = resultSet;
    }
    
    public String getCurrentTable() {
        return currentTable;
    }
}

// Step 2: Define the Expression interface
public interface Expression {
    void interpret(Context context);
}

// Step 3: Define Terminal Expression classes
public class SelectExpression implements Expression {
    private List<String> columns;
    
    public SelectExpression(List<String> columns) {
        this.columns = columns;
    }
    
    @Override
    public void interpret(Context context) {
        List<Map<String, Object>> currentResult = context.getResultSet();
        List<Map<String, Object>> newResult = new ArrayList<>();
        
        // If columns is empty or contains "*", select all columns
        if (columns.isEmpty() || (columns.size() == 1 && columns.get(0).equals("*"))) {
            newResult = currentResult;
        } else {
            for (Map<String, Object> row : currentResult) {
                Map<String, Object> newRow = new HashMap<>();
                for (String column : columns) {
                    if (row.containsKey(column)) {
                        newRow.put(column, row.get(column));
                    }
                }
                newResult.add(newRow);
            }
        }
        
        context.setResultSet(newResult);
    }
}

public class FromExpression implements Expression {
    private String tableName;
    
    public FromExpression(String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    public void interpret(Context context) {
        context.setTable(tableName);
    }
}

// Terminal expressions for conditions
public interface ConditionExpression extends Expression {
    boolean evaluate(Map<String, Object> row);
}

public class EqualsCondition implements ConditionExpression {
    private String column;
    private Object value;
    
    public EqualsCondition(String column, Object value) {
        this.column = column;
        this.value = value;
    }
    
    @Override
    public void interpret(Context context) {
        List<Map<String, Object>> currentResult = context.getResultSet();
        List<Map<String, Object>> newResult = new ArrayList<>();
        
        for (Map<String, Object> row : currentResult) {
            if (evaluate(row)) {
                newResult.add(row);
            }
        }
        
        context.setResultSet(newResult);
    }
    
    @Override
    public boolean evaluate(Map<String, Object> row) {
        if (!row.containsKey(column)) {
            return false;
        }
        
        Object rowValue = row.get(column);
        
        if (rowValue == null && value == null) {
            return true;
        }
        
        if (rowValue == null || value == null) {
            return false;
        }
        
        return rowValue.equals(value);
    }
    
    @Override
    public String toString() {
        return column + " = " + (value instanceof String ? "'" + value + "'" : value);
    }
}

public class GreaterThanCondition implements ConditionExpression {
    private String column;
    private Number value;
    
    public GreaterThanCondition(String column, Number value) {
        this.column = column;
        this.value = value;
    }
    
    @Override
    public void interpret(Context context) {
        List<Map<String, Object>> currentResult = context.getResultSet();
        List<Map<String, Object>> newResult = new ArrayList<>();
        
        for (Map<String, Object> row : currentResult) {
            if (evaluate(row)) {
                newResult.add(row);
            }
        }
        
        context.setResultSet(newResult);
    }
    
    @Override
    public boolean evaluate(Map<String, Object> row) {
        if (!row.containsKey(column)) {
            return false;
        }
        
        Object rowValue = row.get(column);
        
        if (!(rowValue instanceof Number)) {
            return false;
        }
        
        return ((Number) rowValue).doubleValue() > value.doubleValue();
    }
    
    @Override
    public String toString() {
        return column + " > " + value;
    }
}

public class LessThanCondition implements ConditionExpression {
    private String column;
    private Number value;
    
    public LessThanCondition(String column, Number value) {
        this.column = column;
        this.value = value;
    }
    
    @Override
    public void interpret(Context context) {
        List<Map<String, Object>> currentResult = context.getResultSet();
        List<Map<String, Object>> newResult = new ArrayList<>();
        
        for (Map<String, Object> row : currentResult) {
            if (evaluate(row)) {
                newResult.add(row);
            }
        }
        
        context.setResultSet(newResult);
    }
    
    @Override
    public boolean evaluate(Map<String, Object> row) {
        if (!row.containsKey(column)) {
            return false;
        }
        
        Object rowValue = row.get(column);
        
        if (!(rowValue instanceof Number)) {
            return false;
        }
        
        return ((Number) rowValue).doubleValue() < value.doubleValue();
    }
    
    @Override
    public String toString() {
        return column + " < " + value;
    }
}

// Step 4: Define Non-terminal Expression classes
public class AndExpression implements ConditionExpression {
    private ConditionExpression expr1;
    private ConditionExpression expr2;
    
    public AndExpression(ConditionExpression expr1, ConditionExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public void interpret(Context context) {
        List<Map<String, Object>> currentResult = context.getResultSet();
        List<Map<String, Object>> newResult = new ArrayList<>();
        
        for (Map<String, Object> row : currentResult) {
            if (evaluate(row)) {
                newResult.add(row);
            }
        }
        
        context.setResultSet(newResult);
    }
    
    @Override
    public boolean evaluate(Map<String, Object> row) {
        return expr1.evaluate(row) && expr2.evaluate(row);
    }
    
    @Override
    public String toString() {
        return "(" + expr1 + " AND " + expr2 + ")";
    }
}

public class OrExpression implements ConditionExpression {
    private ConditionExpression expr1;
    private ConditionExpression expr2;
    
    public OrExpression(ConditionExpression expr1, ConditionExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public void interpret(Context context) {
        List<Map<String, Object>> currentResult = context.getResultSet();
        List<Map<String, Object>> newResult = new ArrayList<>();
        
        for (Map<String, Object> row : currentResult) {
            if (evaluate(row)) {
                newResult.add(row);
            }
        }
        
        context.setResultSet(newResult);
    }
    
    @Override
    public boolean evaluate(Map<String, Object> row) {
        return expr1.evaluate(row) || expr2.evaluate(row);
    }
    
    @Override
    public String toString() {
        return "(" + expr1 + " OR " + expr2 + ")";
    }
}

public class WhereExpression implements Expression {
    private ConditionExpression condition;
    
    public WhereExpression(ConditionExpression condition) {
        this.condition = condition;
    }
    
    @Override
    public void interpret(Context context) {
        condition.interpret(context);
    }
}

// Step 5: Create a query class to represent the whole SQL expression
public class QueryExpression implements Expression {
    private SelectExpression selectExpression;
    private FromExpression fromExpression;
    private WhereExpression whereExpression;
    
    public QueryExpression(
            SelectExpression selectExpression,
            FromExpression fromExpression,
            WhereExpression whereExpression) {
        this.selectExpression = selectExpression;
        this.fromExpression = fromExpression;
        this.whereExpression = whereExpression;
    }
    
    @Override
    public void interpret(Context context) {
        fromExpression.interpret(context);
        
        if (whereExpression != null) {
            whereExpression.interpret(context);
        }
        
        selectExpression.interpret(context);
    }
}

// Step 6: Create a parser to parse the SQL query
public class SQLParser {
    public static QueryExpression parse(String query) {
        // This is a simplified parser for demonstration
        // A real SQL parser would be much more complex
        
        String queryUpperCase = query.trim().toUpperCase();
        
        // Parse SELECT clause
        int selectIndex = queryUpperCase.indexOf("SELECT");
        int fromIndex = queryUpperCase.indexOf("FROM");
        int whereIndex = queryUpperCase.indexOf("WHERE");
        
        if (selectIndex == -1 || fromIndex == -1) {
            throw new RuntimeException("Invalid query: SELECT and FROM are required");
        }
        
        // Parse columns
        String columnsString = query.substring(selectIndex + 6, fromIndex).trim();
        List<String> columns = new ArrayList<>();
        if (columnsString.equals("*")) {
            columns.add("*");
        } else {
            String[] columnArray = columnsString.split(",");
            for (String column : columnArray) {
                columns.add(column.trim());
            }
        }
        
        // Parse table name
        String tableName;
        if (whereIndex == -1) {
            tableName = query.substring(fromIndex + 4).trim();
        } else {
            tableName = query.substring(fromIndex + 4, whereIndex).trim();
        }
        
        // Create SELECT and FROM expressions
        SelectExpression selectExpr = new SelectExpression(columns);
        FromExpression fromExpr = new FromExpression(tableName);
        
        // Parse WHERE clause if present
        WhereExpression whereExpr = null;
        if (whereIndex != -1) {
            String whereClause = query.substring(whereIndex + 5).trim();
            whereExpr = new WhereExpression(parseCondition(whereClause));
        }
        
        return new QueryExpression(selectExpr, fromExpr, whereExpr);
    }
    
    private static ConditionExpression parseCondition(String condition) {
        // Simplified condition parser
        if (condition.toLowerCase().contains(" and ")) {
            String[] parts = condition.split("(?i)\\s+and\\s+");
            return new AndExpression(
                parseCondition(parts[0].trim()),
                parseCondition(parts[1].trim())
            );
        } else if (condition.toLowerCase().contains(" or ")) {
            String[] parts = condition.split("(?i)\\s+or\\s+");
            return new OrExpression(
                parseCondition(parts[0].trim()),
                parseCondition(parts[1].trim())
            );
        } else if (condition.contains("=")) {
            String[] parts = condition.split("=");
            String column = parts[0].trim();
            String valueStr = parts[1].trim();
            
            Object value;
            if (valueStr.startsWith("'") && valueStr.endsWith("'")) {
                value = valueStr.substring(1, valueStr.length() - 1);
            } else if (valueStr.contains(".")) {
                value = Double.parseDouble(valueStr);
            } else {
                try {
                    value = Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    value = valueStr;
                }
            }
            
            return new EqualsCondition(column, value);
        } else if (condition.contains(">")) {
            String[] parts = condition.split(">");
            String column = parts[0].trim();
            String valueStr = parts[1].trim();
            
            Number value;
            if (valueStr.contains(".")) {
                value = Double.parseDouble(valueStr);
            } else {
                value = Integer.parseInt(valueStr);
            }
            
            return new GreaterThanCondition(column, value);
        } else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            String column = parts[0].trim();
            String valueStr = parts[1].trim();
            
            Number value;
            if (valueStr.contains(".")) {
                value = Double.parseDouble(valueStr);
            } else {
                value = Integer.parseInt(valueStr);
            }
            
            return new LessThanCondition(column, value);
        }
        
        throw new RuntimeException("Unsupported condition: " + condition);
    }
}
```

### Usage Example

```java
public class QueryInterpreterDemo {
    public static void main(String[] args) {
        Context context = new Context();
        
        System.out.println("===== Simple Query Language Interpreter =====");
        System.out.println("Available tables: employees, departments");
        
        executeQuery("SELECT * FROM employees", context);
        
        executeQuery("SELECT name, department, salary FROM employees", context);
        
        executeQuery("SELECT name, salary FROM employees WHERE department = 'IT'", context);
        
        executeQuery("SELECT name, department, salary FROM employees WHERE salary > 50000", context);
        
        executeQuery("SELECT name, department, salary FROM employees WHERE department = 'IT' AND salary > 50000", context);
        
        executeQuery("SELECT name, department, salary FROM employees WHERE department = 'IT' OR salary > 60000", context);
        
        executeQuery("SELECT id, name, location FROM departments WHERE id < 3", context);
    }
    
    private static void executeQuery(String query, Context context) {
        System.out.println("\nExecuting query: " + query);
        System.out.println("--------------------------------");
        
        try {
            QueryExpression parsedQuery = SQLParser.parse(query);
            parsedQuery.interpret(context);
            
            List<Map<String, Object>> result = context.getResultSet();
            if (result.isEmpty()) {
                System.out.println("No results found");
            } else {
                // Print column headers
                Map<String, Object> firstRow = result.get(0);
                printRow(firstRow.keySet().toArray(new String[0]));
                System.out.println("--------------------------------");
                
                // Print rows
                for (Map<String, Object> row : result) {
                    printRow(row.values().toArray());
                }
                
                System.out.println("--------------------------------");
                System.out.println(result.size() + " rows returned");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void printRow(Object[] values) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append(value != null ? value.toString() : "null");
            sb.append("\t");
        }
        System.out.println(sb.toString());
    }
}
```

## Another Example: Simple Mathematical Expression Interpreter

Here's another example of the Interpreter pattern for evaluating simple mathematical expressions:

```java
// Context
class MathContext {
    private Map<String, Integer> variables = new HashMap<>();
    
    public void setVariable(String name, int value) {
        variables.put(name, value);
    }
    
    public int getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not defined: " + name);
        }
        return variables.get(name);
    }
}

// Abstract Expression
interface Expression {
    int interpret(MathContext context);
}

// Terminal Expressions
class NumberExpression implements Expression {
    private int number;
    
    public NumberExpression(int number) {
        this.number = number;
    }
    
    @Override
    public int interpret(MathContext context) {
        return number;
    }
    
    @Override
    public String toString() {
        return String.valueOf(number);
    }
}

class VariableExpression implements Expression {
    private String name;
    
    public VariableExpression(String name) {
        this.name = name;
    }
    
    @Override
    public int interpret(MathContext context) {
        return context.getVariable(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}

// Non-terminal Expressions
class AddExpression implements Expression {
    private Expression left;
    private Expression right;
    
    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public int interpret(MathContext context) {
        return left.interpret(context) + right.interpret(context);
    }
    
    @Override
    public String toString() {
        return "(" + left + " + " + right + ")";
    }
}

class SubtractExpression implements Expression {
    private Expression left;
    private Expression right;
    
    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public int interpret(MathContext context) {
        return left.interpret(context) - right.interpret(context);
    }
    
    @Override
    public String toString() {
        return "(" + left + " - " + right + ")";
    }
}

class MultiplyExpression implements Expression {
    private Expression left;
    private Expression right;
    
    public MultiplyExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public int interpret(MathContext context) {
        return left.interpret(context) * right.interpret(context);
    }
    
    @Override
    public String toString() {
        return left + " * " + right;
    }
}

class DivideExpression implements Expression {
    private Expression left;
    private Expression right;
    
    public DivideExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public int interpret(MathContext context) {
        int rightVal = right.interpret(context);
        if (rightVal == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left.interpret(context) / rightVal;
    }
    
    @Override
    public String toString() {
        return left + " / " + right;
    }
}

// Parser
class MathExpressionParser {
    public static Expression parse(String expression) {
        // This is a very simplified parser for demonstration purposes
        // In a real application, you would use a proper parsing algorithm
        
        expression = expression.replaceAll("\\s+", "");
        
        // Try to find addition or subtraction
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '+' && isValidOperatorPosition(expression, i)) {
                return new AddExpression(
                    parse(expression.substring(0, i)),
                    parse(expression.substring(i + 1))
                );
            } else if (c == '-' && isValidOperatorPosition(expression, i)) {
                return new SubtractExpression(
                    parse(expression.substring(0, i)),
                    parse(expression.substring(i + 1))
                );
            }
        }
        
        // Try to find multiplication or division
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '*' && isValidOperatorPosition(expression, i)) {
                return new MultiplyExpression(
                    parse(expression.substring(0, i)),
                    parse(expression.substring(i + 1))
                );
            } else if (c == '/' && isValidOperatorPosition(expression, i)) {
                return new DivideExpression(
                    parse(expression.substring(0, i)),
                    parse(expression.substring(i + 1))
                );
            }
        }
        
        // Handle parentheses
        if (expression.charAt(0) == '(' && expression.charAt(expression.length() - 1) == ')') {
            return parse(expression.substring(1, expression.length() - 1));
        }
        
        // Try to parse as a number
        try {
            return new NumberExpression(Integer.parseInt(expression));
        } catch (NumberFormatException e) {
            // Must be a variable
            return new VariableExpression(expression);
        }
    }
    
    private static boolean isValidOperatorPosition(String expression, int pos) {
        int parenthesesCount = 0;
        
        for (int i = 0; i < pos; i++) {
            if (expression.charAt(i) == '(') {
                parenthesesCount++;
            } else if (expression.charAt(i) == ')') {
                parenthesesCount--;
            }
        }
        
        return parenthesesCount == 0;
    }
}
```

## Benefits

1. **Grammar Encapsulation**: The pattern encapsulates the grammar rules in a class hierarchy
2. **Easier grammar implementation**: Each rule in the grammar becomes a class
3. **Grammar extension**: You can easily extend the grammar by defining new expressions
4. **Implementation of interpretation**: The pattern defines a simple interpreter for sentences in the language

## Considerations

1. **Complexity**: For complex grammars, the class hierarchy can become very large and unwieldy
2. **Alternative approaches**: For complex languages, parser generators or parser combinator libraries may be more appropriate
3. **Performance**: Interpreting a sentence can be inefficient compared to compiled code
4. **Limited scope**: The pattern works best for simple languages with a limited set of expressions

## When to Use

- When you need to interpret a simple language or domain-specific language (DSL)
- When the grammar of the language is simple
- When efficiency is not a critical concern
- When you need to quickly implement a simple interpreter without using parser generators or other specialized tools
