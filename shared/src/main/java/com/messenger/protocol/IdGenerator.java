package com.messenger.protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1);

    public static int generateId() {
        return counter.getAndIncrement();
    }
}