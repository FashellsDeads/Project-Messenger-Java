package com.messenger.model;

public enum CommandType {
    CREATE_PRIVATE_CHAT,
    JOIN_CHANNEL,
    CREATE_CHANNEL,
    GET_MY_CHATS,      // ← новое
    GET_HISTORY,       // ← новое: args[0] = chatId
    GET_ONLINE_USERS   // ← новое
}