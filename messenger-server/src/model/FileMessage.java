package model;

public class FileMessage extends AbstractMessage {

    private final String filename;
    private final long fileSize;

    public FileMessage(int channelId, int senderId, String username,
                       String filename, long fileSize) {
        super(0, senderId, channelId);
        this.filename = filename;
        this.fileSize = fileSize;
        setType(MessageType.FILE);
    }

    public String getFilename() { return filename; }
    public long getFileSize()   { return fileSize; }

    @Override
    public String getDisplayContent() { return filename; }
}