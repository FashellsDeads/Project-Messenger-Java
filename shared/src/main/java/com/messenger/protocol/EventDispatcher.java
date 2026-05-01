package com.messenger.protocol;

public interface EventDispatcher {
    void dispatch(Runnable action);
}