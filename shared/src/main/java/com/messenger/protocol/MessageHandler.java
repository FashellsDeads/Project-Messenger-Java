package com.messenger.protocol;

import com.messenger.model.AbstractMessage;

public interface MessageHandler {

    void handleMessage(AbstractMessage message);
}
