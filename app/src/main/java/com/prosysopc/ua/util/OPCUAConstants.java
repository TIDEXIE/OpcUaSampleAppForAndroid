package com.prosysopc.ua.util;

import com.prosysopc.ua.android.ReadEntry;

import org.opcfoundation.ua.builtintypes.NodeId;

import java.util.ArrayList;

/**
 * Created by haripriyasaranya on 05/09/15.
 */
public class OPCUAConstants {

    private static String CONNECTED_URL;
    private static NodeId CURRENT_NODE_ID;
    private static String NODEID_TO_STRING;
    private static ArrayList<ReadEntry> readInformation = new ArrayList<ReadEntry>();

    public static String getConnectedUrl() {
        return CONNECTED_URL;
    }


    public static void setConnectedUrl(String connectedUrl) {
        CONNECTED_URL = connectedUrl;
    }

    public static NodeId getCurrentNodeId() {
        return CURRENT_NODE_ID;
    }

    public static void setCurrentNodeId(NodeId currentNodeId) {
        CURRENT_NODE_ID = currentNodeId;
        setNodeidToString(CURRENT_NODE_ID);
    }

    public static String getNodeidToString() {
        return NODEID_TO_STRING;
    }

    public static void setNodeidToString(NodeId nodeidToString) {
        NODEID_TO_STRING = nodeidToString.toString();
    }

    public static ArrayList<ReadEntry> getReadInformation() {
        return readInformation;
    }

    public static void setReadInformation(ArrayList<ReadEntry> readInformation) {
        OPCUAConstants.readInformation = readInformation;
    }
}
