package com.messenger.protocol;

import java.io.Serializable;


public class FileChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] data;
    private int chunkNumber;
    private boolean isLastChunk;
    private int channelId;

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