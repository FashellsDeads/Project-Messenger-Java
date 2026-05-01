package client;

import com.messenger.protocol.FileChunk;
import com.messenger.protocol.Packet;
import com.messenger.protocol.PacketType;

import java.io.File;
import java.io.FileInputStream;

public class FileTransferManager {

    private final NetworkClient networkClient;
    private static final int CHUNK_SIZE = 8192;

    public FileTransferManager(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }


    public void uploadFile(File file, int channelId) {
        new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[CHUNK_SIZE];
                int bytesRead;
                int chunkNumber = 0;

                System.out.println("Starting: " + file.getName());

                while ((bytesRead = fis.read(buffer)) != -1) {

                    byte[] actualData;
                    if (bytesRead == CHUNK_SIZE) {
                        actualData = buffer;
                    } else {
                        actualData = new byte[bytesRead];
                        System.arraycopy(buffer, 0, actualData, 0, bytesRead);
                    }

                    boolean isLast = (fis.available() == 0);

                    FileChunk chunk = new FileChunk(file.getName(), actualData, chunkNumber, isLast, channelId);

                    networkClient.sendPacket(new Packet<>(PacketType.FILE_UPLOAD, chunk));

                    chunkNumber++;

                    Thread.sleep(10);
                }

                System.out.println("File " + file.getName() + " successfully sended");

            } catch (Exception e) {
                System.err.println("Error while sending " + e.getMessage());
            }
        }).start();
    }
}