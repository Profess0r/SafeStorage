package com.block.storage.message;

import java.io.Serializable;

public class Message implements Serializable {

    public static final long serialVersionUID = 125L;

    private MessageType messageType;
    private long nodeId;

    public Message() {
    }

    public Message(MessageType messageType, long nodeId) {
        this.messageType = messageType;
        this.nodeId = nodeId;
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
}
