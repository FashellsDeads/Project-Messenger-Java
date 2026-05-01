package com.messenger.protocol;

import java.io.Serializable;


public class FileChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] data;       // Сам кусочек файла (максимум 8192 байт)
    private int chunkNumber;   // Порядковый номер кусочка
    private boolean isLastChunk; // Флаг: это последний кусок?
    private int channelId;     // В какой канал отправляем

    public FileChunk(String fileName, byte[] data, int chunkNumber, boolean isLastChunk, int channelId) {
        this.fileName = fileName;
        this.data = data;
        this.chunkNumber = chunkNumber;
        this.isLastChunk = isLastChunk;
        this.channelId = channelId;
    }

    public String getFileName() { return fileName; }
    public byte[] getData() { return data; }
    public int getChunkNumber() { return chunkNumber; }
    public boolean isLastChunk() { return isLastChunk; }
    public int getChannelId() { return channelId; }
}