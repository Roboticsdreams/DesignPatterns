# Observer Pattern

[Back to Home](../README.md)

## Intent

Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.

## Explanation

The Observer pattern establishes a relationship where multiple observer objects are notified when a subject object changes state. This pattern is key to implementing distributed event handling systems.

## Real-World Example: Stock Market Monitoring System

In a stock market application, traders need to be notified when stock prices change. Using the Observer pattern, a stock can be the subject that notifies registered traders (observers) when its price changes.

### Implementation

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Step 1: Define the Observer interface
public interface StockObserver {
    void update(String stockSymbol, double price);
}

// Step 2: Define the Subject interface
public interface Subject {
    void registerObserver(StockObserver observer);
    void removeObserver(StockObserver observer);
    void notifyObservers();
}

// Step 3: Create concrete Subject - the Stock
public class Stock implements Subject {
    private String symbol;
    private double price;
    private List<StockObserver> observers;
    
    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.observers = new ArrayList<>();
    }
    
    @Override
    public void registerObserver(StockObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(symbol, price);
        }
    }
    
    public void setPrice(double price) {
        System.out.println("\nStock: " + symbol + " price changing from " + this.price + " to " + price);
        this.price = price;
        notifyObservers();
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public double getPrice() {
        return price;
    }
}

// Step 4: Create concrete observers - different types of traders
public class StockTrader implements StockObserver {
    private String name;
    private Map<String, Double> watchlist;
    
    public StockTrader(String name) {
        this.name = name;
        this.watchlist = new HashMap<>();
    }
    
    public void addToWatchlist(Stock stock) {
        watchlist.put(stock.getSymbol(), stock.getPrice());
        stock.registerObserver(this);
        System.out.println(name + " is now watching " + stock.getSymbol() + " at initial price: " + stock.getPrice());
    }
    
    public void removeFromWatchlist(Stock stock) {
        watchlist.remove(stock.getSymbol());
        stock.removeObserver(this);
        System.out.println(name + " is no longer watching " + stock.getSymbol());
    }
    
    @Override
    public void update(String stockSymbol, double newPrice) {
        if (!watchlist.containsKey(stockSymbol)) {
            return; // Not watching this stock
        }
        
        double oldPrice = watchlist.get(stockSymbol);
        double priceDifference = newPrice - oldPrice;
        double percentChange = (priceDifference / oldPrice) * 100;
        
        System.out.println(name + " received update for " + stockSymbol + 
                         ": new price = " + newPrice + 
                         " (" + (priceDifference >= 0 ? "+" : "") + 
                         String.format("%.2f", priceDifference) + 
                         ", " + String.format("%.2f", percentChange) + "%)");
        
        // Update our tracked price
        watchlist.put(stockSymbol, newPrice);
        
        // Trader can take actions based on the update
        if (percentChange > 5) {
            System.out.println(name + " is considering selling " + stockSymbol + " due to significant price increase");
        } else if (percentChange < -5) {
            System.out.println(name + " is considering buying more " + stockSymbol + " due to significant price drop");
        }
    }
}

// Special type of observer - an automated trading bot
public class TradingBot implements StockObserver {
    private String name;
    private Map<String, Double> portfolio;
    private Map<String, TradingStrategy> strategies;
    
    public TradingBot(String name) {
        this.name = name;
        this.portfolio = new HashMap<>();
        this.strategies = new HashMap<>();
    }
    
    public void setStrategy(String stockSymbol, TradingStrategy strategy) {
        strategies.put(stockSymbol, strategy);
        System.out.println(name + " bot set " + strategy.getName() + " strategy for " + stockSymbol);
    }
    
    public void watchStock(Stock stock, TradingStrategy strategy) {
        portfolio.put(stock.getSymbol(), stock.getPrice());
        strategies.put(stock.getSymbol(), strategy);
        stock.registerObserver(this);
        System.out.println(name + " bot is now watching " + stock.getSymbol() + 
                         " with " + strategy.getName() + " strategy");
    }
    
    @Override
    public void update(String stockSymbol, double newPrice) {
        if (!portfolio.containsKey(stockSymbol)) {
            return;
        }
        
        double oldPrice = portfolio.get(stockSymbol);
        TradingStrategy strategy = strategies.get(stockSymbol);
        
        System.out.println(name + " bot received update for " + stockSymbol + ": " + oldPrice + " -> " + newPrice);
        
        // Update tracked price
        portfolio.put(stockSymbol, newPrice);
        
        // Apply trading strategy
        if (strategy != null) {
            strategy.execute(stockSymbol, oldPrice, newPrice);
        }
    }
}

// Trading strategies for the bot
public interface TradingStrategy {
    String getName();
    void execute(String symbol, double oldPrice, double newPrice);
}

public class MomentumStrategy implements TradingStrategy {
    @Override
    public String getName() {
        return "Momentum";
    }
    
    @Override
    public void execute(String symbol, double oldPrice, double newPrice) {
        double percentChange = ((newPrice - oldPrice) / oldPrice) * 100;
        
        if (percentChange > 3) {
            System.out.println("MOMENTUM STRATEGY: BUY " + symbol + " - price is trending up");
        } else if (percentChange < -3) {
            System.out.println("MOMENTUM STRATEGY: SELL " + symbol + " - price is trending down");
        } else {
            System.out.println("MOMENTUM STRATEGY: HOLD " + symbol + " - insufficient price movement");
        }
    }
}

public class MeanReversionStrategy implements TradingStrategy {
    private double avgPrice;
    private int count = 0;
    
    @Override
    public String getName() {
        return "Mean Reversion";
    }
    
    @Override
    public void execute(String symbol, double oldPrice, double newPrice) {
        // Update running average
        count++;
        if (count == 1) {
            avgPrice = newPrice;
            System.out.println("MEAN REVERSION: Establishing baseline price for " + symbol + ": " + avgPrice);
            return;
        }
        
        avgPrice = ((avgPrice * (count - 1)) + newPrice) / count;
        double deviation = ((newPrice - avgPrice) / avgPrice) * 100;
        
        if (deviation > 5) {
            System.out.println("MEAN REVERSION: SELL " + symbol + " - price is above average by " + 
                             String.format("%.2f", deviation) + "%");
        } else if (deviation < -5) {
            System.out.println("MEAN REVERSION: BUY " + symbol + " - price is below average by " + 
                             String.format("%.2f", Math.abs(deviation)) + "%");
        } else {
            System.out.println("MEAN REVERSION: HOLD " + symbol + " - price is close to average");
        }
    }
}
```

### Usage Example

```java
public class StockMarketDemo {
    public static void main(String[] args) {
        // Create some stocks (subjects)
        Stock appleStock = new Stock("AAPL", 150.25);
        Stock googleStock = new Stock("GOOGL", 2725.80);
        Stock amazonStock = new Stock("AMZN", 3380.50);
        
        // Create human traders (observers)
        StockTrader trader1 = new StockTrader("John Smith");
        StockTrader trader2 = new StockTrader("Alice Johnson");
        
        // Create trading bots (observers)
        TradingBot momentumBot = new TradingBot("MomentumMaster");
        TradingBot reversionBot = new TradingBot("ReversionPro");
        
        // Set up trading strategies for bots
        MomentumStrategy momentumStrategy = new MomentumStrategy();
        MeanReversionStrategy reversionStrategy = new MeanReversionStrategy();
        
        // Register observers to subjects
        System.out.println("===== Setting up watchers =====");
        
        trader1.addToWatchlist(appleStock);
        trader1.addToWatchlist(googleStock);
        
        trader2.addToWatchlist(appleStock);
        trader2.addToWatchlist(amazonStock);
        
        momentumBot.watchStock(googleStock, momentumStrategy);
        reversionBot.watchStock(appleStock, reversionStrategy);
        reversionBot.watchStock(amazonStock, reversionStrategy);
        
        // Simulate price changes
        System.out.println("\n===== Market Activity =====");
        
        // Apple stock goes up 4%
        appleStock.setPrice(156.26);
        
        // Google stock drops 2%
        googleStock.setPrice(2671.28);
        
        // Amazon stock jumps 6%
        amazonStock.setPrice(3583.33);
        
        // More price movements to show strategies in action
        appleStock.setPrice(162.50); // Another significant increase
        amazonStock.setPrice(3400.00); // Reversal
        
        // Trader removes a stock from watchlist
        System.out.println("\n===== Watchlist Changes =====");
        trader1.removeFromWatchlist(googleStock);
        
        // Price change after removal - trader shouldn't get update
        googleStock.setPrice(2700.00);
    }
}
```

## Additional Example: Weather Station

Here's another common example of the Observer pattern - a weather station that notifies various displays when weather data changes:

```java
public interface Observer {
    void update(float temperature, float humidity, float pressure);
}

public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

public class WeatherData implements Subject {
    private List<Observer> observers;
    private float temperature;
    private float humidity;
    private float pressure;
    
    public WeatherData() {
        observers = new ArrayList<>();
    }
    
    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }
    
    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(temperature, humidity, pressure);
        }
    }
    
    public void measurementsChanged() {
        notifyObservers();
    }
    
    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }
}

public class CurrentConditionsDisplay implements Observer {
    private float temperature;
    private float humidity;
    private Subject weatherData;
    
    public CurrentConditionsDisplay(Subject weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }
    
    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }
    
    public void display() {
        System.out.println("Current conditions: " + temperature + "F degrees and " + 
                         humidity + "% humidity");
    }
}

public class StatisticsDisplay implements Observer {
    private float maxTemp = 0.0f;
    private float minTemp = 200;
    private float tempSum= 0.0f;
    private int numReadings = 0;
    private Subject weatherData;
    
    public StatisticsDisplay(Subject weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }
    
    @Override
    public void update(float temperature, float humidity, float pressure) {
        tempSum += temperature;
        numReadings++;
        
        if (temperature > maxTemp) {
            maxTemp = temperature;
        }
        
        if (temperature < minTemp) {
            minTemp = temperature;
        }
        
        display();
    }
    
    public void display() {
        System.out.println("Avg/Max/Min temperature = " + (tempSum / numReadings) + 
                         "/" + maxTemp + "/" + minTemp);
    }
}
```

## Benefits

1. **Loose coupling**: Subjects and observers are loosely coupled, as they can interact without detailed knowledge of each other
2. **Support for broadcast communication**: Information can be sent to all interested objects without knowing who they are
3. **Dynamic relationships**: Relationships can be established at runtime rather than compile time
4. **Open/Closed Principle**: New observer classes can be added without modifying the subject

## Considerations

1. **Unexpected updates**: Observers might be notified when irrelevant changes happen
2. **Update overhead**: If there are many observers or complex dependencies, updates might become costly
3. **Memory leaks**: Failing to remove observers when they're no longer needed can cause memory leaks
4. **Order of notification**: Sometimes the order in which observers are notified can be important

## When to Use

- When changes to one object require changing others, and you don't know how many objects need to change
- When an object should be able to notify other objects without making assumptions about what those objects are
- When you need to maintain consistency between related objects without making them tightly coupled
