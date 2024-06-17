package com.rdreams.behavioralpattern.factorymethod;

public class HDFCBank implements IBank{
    @Override
    public void deposit(double amount) {
        System.out.println("Amount deposited to HDFC Bank");
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Amount withdrawn from HDFC Bank");
    }
}
