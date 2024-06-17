package com.rdreams.behavioralpattern.factorymethod;

public class ConcreteFactory implements Factory{
    @Override
    public IBank getBankByName(Selector selector) {
        switch (selector.getBankName()) {
            case "HBFC":
                return new HDFCBank();
            case "SBI" :
                return new SBIBank();
            default:
                throw new IllegalArgumentException();
        }
    }
}
