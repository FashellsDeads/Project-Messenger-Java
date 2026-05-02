package com.messenger.protocol;

import com.messenger.model.AbstractMessage;

/**
 * Интерфейс обработчика сообщений.
 * OOP: Интерфейс — контракт который реализуют сервер и клиент.
 */
public interface MessageHandler {

    /**
     * Обработать входящее сообщение.
     * @param message сообщение любого типа (полиморфизм!)
     */
    void handleMessage(AbstractMessage message);
}
