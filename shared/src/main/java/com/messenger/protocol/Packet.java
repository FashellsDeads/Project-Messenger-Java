package com.messenger.protocol;

import java.io.Serializable;

public class Packet<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private PacketType type;
    private T payload;
    private long timestamp;
    private boolean success;
    private String errorMessage;


    public Packet(PacketType type, T payload) {
        this.type = type;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
        this.success = true;
    }


    public static Packet<String> error(PacketType type, String message) {
        Packet<String> packet = new Packet<>(type, message);
        packet.success = false;
        packet.errorMessage = message;
        return packet;
    }

    public static Packet<String> success(PacketType type) {
        return new Packet<>(type, "OK");
    }


    public PacketType getType() { return type; }
    public void setType(PacketType type) { this.type = type; }

    public T getPayload() { return payload; }
    public void setPayload(T payload) { this.payload = payload; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    @Override
    public String toString() {
        return "Packet{type=" + type + ", success=" + success + ", payload=" + payload + "}";
    }
}
