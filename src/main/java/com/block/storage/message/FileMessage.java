package com.block.storage.message;

public class FileMessage extends Message {

    private String fileName;
    private byte[] fileBody;

    public FileMessage(MessageType messageType, long nodeId, String fileName, byte[] fileBody) {
        setMessageType(messageType);
        setNodeId(nodeId);
        this.fileName = fileName;
        this.fileBody = fileBody;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }
}
