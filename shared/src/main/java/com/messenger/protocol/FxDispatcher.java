package com.messenger.protocol;

import javafx.application.Platform;

public class FxDispatcher implements EventDispatcher {
    @Override
    public void dispatch(Runnable action) {
        Platform.runLater(action);
    }
}