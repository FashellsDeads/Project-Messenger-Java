package com.messenger.model;

public enum ServerEventType {
    PRIVATE_CHAT_INVITE,  // тебя добавили в приватный чат
    USER_JOINED_CHANNEL,  // кто-то вступил в канал
    USER_ONLINE,          // пользователь появился онлайн
    USER_OFFLINE          // пользователь отключился
}