package com.rdreams.behavioralpattern.factorymethod;

public class SBIBank implements IBank {
    @Override
    public void deposit(double amount) {
        System.out.println("Amount deposited to SBI Bank");
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Amount withdrawn from SBI Bank");
    }
}
