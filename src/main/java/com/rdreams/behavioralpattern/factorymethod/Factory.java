package com.rdreams.behavioralpattern.factorymethod;

public interface Factory {
    IBank getBankByName(Selector selector);
}
