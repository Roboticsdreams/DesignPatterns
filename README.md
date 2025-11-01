# Design Patterns in Java: Reference Guide

Design patterns are proven solutions to recurring software design problems. They provide templates for solving common challenges in a maintainable and elegant way. This guide links to detailed explanations and real-world examples for all 23 design patterns organized by their purpose.

## Creational Patterns

These patterns focus on object creation mechanisms, abstracting the instantiation process.

### [Abstract Factory](creational/AbstractFactory.md)

Provides an interface for creating families of related or dependent objects without specifying their concrete classes.

**Real-world example:** Cross-platform UI components

### [Builder](creational/Builder.md)

Separates the construction of a complex object from its representation.

**Real-world example:** Document generator

### [Factory Method](creational/FactoryMethod.md)

Defines an interface for creating an object, but lets subclasses decide which class to instantiate.

**Real-world example:** Payment processing system

### [Prototype](creational/Prototype.md)

Specify the kinds of objects to create using a prototypical instance, and create new objects by copying this prototype.

**Real-world example:** Document template system

### [Singleton](creational/Singleton.md)

Ensures a class has only one instance and provides a global point of access to it.

**Real-world example:** Database connection manager

## Structural Patterns

These patterns deal with object composition, creating relationships between objects.

### [Adapter](structural/Adapter.md)

Converts the interface of a class into another interface clients expect.

**Real-world example:** Legacy payment system integration

### [Bridge](structural/Bridge.md)

Decouples an abstraction from its implementation so that the two can vary independently.

**Real-world example:** Device remote control system

### [Composite](structural/Composite.md)

Composes objects into tree structures to represent part-whole hierarchies. Lets clients treat individual objects and compositions uniformly.

**Real-world example:** File system structure

### [Decorator](structural/Decorator.md)

Attaches additional responsibilities to an object dynamically.

**Real-world example:** Coffee ordering system

### [Facade](structural/Facade.md)

Provides a unified interface to a set of interfaces in a subsystem. Defines a higher-level interface that makes the subsystem easier to use.

**Real-world example:** Home theater system

### [Flyweight](structural/Flyweight.md)

Uses sharing to support large numbers of similar objects efficiently.

**Real-world example:** Text editor character rendering

### [Proxy](structural/Proxy.md)

Provides a surrogate or placeholder for another object to control access to it.

**Real-world example:** Image loading system

## Behavioral Patterns

These patterns focus on communication between objects.

### [Chain of Responsibility](behavioral/ChainOfResponsibility.md)

Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request.

**Real-world example:** Support ticket handling system

### [Command](behavioral/Command.md)

Encapsulate a request as an object, thereby allowing for parameterization of clients with different requests, queuing of requests, and logging of the requests.

**Real-world example:** Smart home automation

### [Interpreter](behavioral/Interpreter.md)

Define a representation for a grammar of a given language along with an interpreter that uses the representation to interpret sentences in the language.

**Real-world example:** Simple query language interpreter

### [Iterator](behavioral/Iterator.md)

Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.

**Real-world example:** Library book collection traversal

### [Mediator](behavioral/Mediator.md)

Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly.

**Real-world example:** Air traffic control system

### [Memento](behavioral/Memento.md)

Without violating encapsulation, capture and externalize an object's internal state so that the object can be restored to this state later.

**Real-world example:** Text editor undo/redo functionality

### [Observer](behavioral/Observer.md)

Defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified automatically.

**Real-world example:** Stock market monitoring system

### [State](behavioral/State.md)

Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

**Real-world example:** Package delivery system

### [Strategy](behavioral/Strategy.md)

Defines a family of algorithms, encapsulates each one, and makes them interchangeable.

**Real-world example:** Payment processing strategy

### [Template Method](behavioral/TemplateMethod.md)

Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.

**Real-world example:** Data mining application

### [Visitor](behavioral/Visitor.md)

Represent an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements on which it operates.

**Real-world example:** Document object model operations

## When to Use Design Patterns

- **Creational**: When you need flexibility in creating objects
- **Structural**: When you need to compose objects into larger structures
- **Behavioral**: When you need to define how objects communicate

## Best Practices

1. **Don't overuse patterns** - only apply them where they add value
2. **Understand the intent** behind each pattern
3. **Start simple** - refactor to patterns when needed
4. **Combine patterns** for complex solutions

Design patterns should make your code more maintainable and flexible, not more complex. Always evaluate if a pattern truly solves your specific problem before implementing it.
