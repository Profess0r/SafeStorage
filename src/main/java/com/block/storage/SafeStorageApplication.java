package com.block.storage;

import com.block.storage.application.AppData;
import com.block.storage.message.Message;
import com.block.storage.message.MessageType;
import com.block.storage.message.NetworkInfoMessage;
import com.block.storage.socket.MessageService;
import com.block.storage.socket.SocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;

@SpringBootApplication
public class SafeStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeStorageApplication.class, args);

        // listener for exchange between nodes
        SocketListener socketListener = new SocketListener();
        socketListener.start();

        // create node id (should write to permanent storage)
        AppData.setNodeId(new Random().nextLong());


        // get ipAddress
        String ipAddress = null;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // check that this in not first node
        if (!AppData.firstNodeAddress.equals(ipAddress)) {
            // connect to network
            NetworkInfoMessage message = (NetworkInfoMessage) MessageService.sendMessage(new Message(MessageType.ADD_PEER_MESSAGE, AppData.getNodeId()), AppData.firstNodeAddress, 5555);
            AppData.setNodeIdToAddressMap((message).getParticipants());
        }
    }
}
