package com.messenger.protocol;

public class DirectDispatcher implements EventDispatcher {
    @Override
    public void dispatch(Runnable action) {
        action.run();
    }
}