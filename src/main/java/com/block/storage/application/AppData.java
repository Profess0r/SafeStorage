package com.block.storage.application;


import java.util.*;

public class AppData {

    static Map<Long, String> nodeIdToAddressMap = new HashMap<>();

    static long nodeId;

    public static final String firstNodeAddress = "192.168.100.1";

    public static long getNodeId() {
        return nodeId;
    }

    public static void setNodeId(long nodeId) {
        AppData.nodeId = nodeId;
    }

    public static Map<Long, String> getNodeIdToAddressMap() {
        return nodeIdToAddressMap;
    }

    public static void setNodeIdToAddressMap(Map<Long, String> nodeIdToAddressMap) {
        AppData.nodeIdToAddressMap = nodeIdToAddressMap;
    }
}
