package com.block.storage.message;

public class RetrieveFileMessage extends Message{

    private String fileName;

    public RetrieveFileMessage(long nodeId, String fileName) {
        setMessageType(MessageType.RETRIEVE_FILE_MESSAGE);
        setNodeId(nodeId);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
