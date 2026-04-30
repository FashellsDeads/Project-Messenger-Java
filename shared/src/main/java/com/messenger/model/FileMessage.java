package com.messenger.model;

/**
 * Сообщение с файлом.
 * OOP: Наследование от AbstractMessage.
 * OOP: Полиморфизм — реализует getDisplayContent() по-своему.
 */
public class FileMessage extends AbstractMessage {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private long fileSize;
    private String filePath; // путь на сервере

    public FileMessage(int channelId, int senderId, String senderUsername,
                       String fileName, long fileSize) {
        super(channelId, senderId, senderUsername, MessageType.FILE);
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    /**
     * Полиморфизм: FileMessage показывает имя файла и размер.
     */
    @Override
    public String getDisplayContent() {
        return "📎 " + fileName + " (" + formatSize(fileSize) + ")";
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
