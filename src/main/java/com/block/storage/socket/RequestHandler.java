package com.block.storage.socket;

import com.block.storage.application.AppData;
import com.block.storage.log.LogUtils;
import com.block.storage.message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler implements Runnable {

    private Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            Message message = (Message) ois.readObject();

            switch (message.getMessageType()) {
                case ADD_PEER_MESSAGE:
                    AppData.getNodeIdToAddressMap().put(message.getNodeId(), socket.getInetAddress().getHostAddress());

                    NetworkInfoMessage responseMessage = new NetworkInfoMessage();
                    responseMessage.setMessageType(MessageType.NETWORK_INFO_MESSAGE);
                    responseMessage.setParticipants(AppData.getNodeIdToAddressMap());

                    // should be broadcast...
                    oos.writeObject(responseMessage);
                    oos.flush();

                    break;

                case SAVE_FILE_MESSAGE:
                    // save file
                    FileMessage fileMessage = ((FileMessage) message);
                    String fileName = fileMessage.getFileName();
                    byte[] fileBody = fileMessage.getFileBody();

                    Files.write(Paths.get("./" + fileName), fileBody);

                    // write to log fileName, sourceNodeId
                    LogUtils.writeToStorageLog(fileName + " " + fileMessage.getNodeId());


                    // send response message
                    Message okMessage = new Message();
                    okMessage.setMessageType(MessageType.OK_MESSAGE);

                    oos.writeObject(okMessage);
                    oos.flush();
                    break;

                case RETRIEVE_FILE_MESSAGE:
                    RetrieveFileMessage retrieveFileMessage = ((RetrieveFileMessage) message);

                    String requestFileName = retrieveFileMessage.getFileName();

                    // read entry from log
                    String fileEntry = LogUtils.findFileInfo(requestFileName);

                    // get sourceNodeId
                    long sourceNodeId = 0;
                    if (fileEntry != null) {
                        sourceNodeId = Long.parseLong(fileEntry.split(" ")[1]);
                    }

                    // check sourceNodeId
                    Message fileResponseMessage;
                    if (sourceNodeId == retrieveFileMessage.getNodeId()) {
                        Path path = Paths.get("./" + requestFileName);
                        byte[] data = Files.readAllBytes(path);
                        fileResponseMessage = new FileMessage(MessageType.RETRIEVE_FILE_RESPONSE_MESSAGE, AppData.getNodeId(), requestFileName, data);
                    } else {
                        fileResponseMessage = new Message(MessageType.FAIL_MESSAGE);
                    }

                    oos.writeObject(fileResponseMessage);
                    oos.flush();
                    break;

                case FAIL_MESSAGE:
                    // fail notification...
                    break;
                case OK_MESSAGE:
                    // success notification...
                    break;
                default:
                    // wrong message type...

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}