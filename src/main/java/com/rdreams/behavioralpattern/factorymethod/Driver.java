package com.rdreams.behavioralpattern.factorymethod;

public class Driver {
    public static void main(String[] args) {
        Selector selector = new Selector();
        selector.setBankName("SBI");
        Factory factory = new ConcreteFactory();
        IBank bank= factory.getBankByName(selector);
        bank.deposit(1000);
        bank.withdraw(500);
    }
}
