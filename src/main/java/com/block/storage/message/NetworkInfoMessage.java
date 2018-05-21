package com.block.storage.message;

import java.util.Map;

public class NetworkInfoMessage extends Message {

    private Map<Long, String> participants;

    public NetworkInfoMessage() {
        setMessageType(MessageType.NETWORK_INFO_MESSAGE);
    }

    public Map<Long, String> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<Long, String> participants) {
        this.participants = participants;
    }
}
