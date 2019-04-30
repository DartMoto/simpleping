package com.jasmin.simpleping.model;

public class CommandOutputHolder<T> {
    private T output;

    public synchronized void produce(T item) {
        this.output = item;
        notify();
    }

    public synchronized T consume() throws InterruptedException {
        while (output == null) {
            wait();
        }
        T result = output;
        output = null;
        return result;
    }
}