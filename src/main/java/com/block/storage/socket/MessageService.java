package com.block.storage.socket;

import com.block.storage.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageService {

    // properties should be read from file
    static String host = "127.0.0.1";
    static int port = 5555;

    // сделать ли метод статическим?
    public static Message sendMessage(Message message, String host, int port) {

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream())) {

            outToServer.writeObject(message);

            Message responseMessage = (Message)inFromServer.readObject();
            return responseMessage;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
