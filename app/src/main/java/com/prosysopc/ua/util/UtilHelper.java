package com.prosysopc.ua.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.UaAddress;
import com.prosysopc.ua.android.ReadEntry;
import com.prosysopc.ua.client.AddressSpace;
import com.prosysopc.ua.client.AddressSpaceException;
import com.prosysopc.ua.client.ServerConnectionException;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.nodes.UaInstance;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaReferenceType;
import com.prosysopc.ua.nodes.UaType;
import com.prosysopc.ua.nodes.UaVariable;
import com.prosysopc.ua.types.opcua.AnalogItemType;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.DateTime;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.QualifiedName;
import org.opcfoundation.ua.builtintypes.UnsignedInteger;
import org.opcfoundation.ua.builtintypes.UnsignedShort;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.common.ServiceResultException;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.BrowseDirection;
import org.opcfoundation.ua.core.BrowsePathTarget;
import org.opcfoundation.ua.core.EUInformation;
import org.opcfoundation.ua.core.Identifiers;
import org.opcfoundation.ua.core.NodeClass;
import org.opcfoundation.ua.core.Range;
import org.opcfoundation.ua.core.ReferenceDescription;
import org.opcfoundation.ua.core.RelativePathElement;
import org.opcfoundation.ua.transport.security.SecurityMode;
import org.opcfoundation.ua.utils.AttributesUtil;
import org.opcfoundation.ua.utils.MultiDimensionArrayUtils;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by haripriyasaranya on 02/09/15.
 */
public class UtilHelper {

    private final String CLASS_NAME = UtilHelper.class.getName();
    private boolean URL_VALID_STATUS = false;
    protected SecurityMode securityMode = SecurityMode.NONE;
    protected NodeId nodeId = null;
    protected UaClient client;
    private String url = null;
    private List<String> listEntry = new ArrayList<String>();
    private Context context;
    private ProgressDialog progressIndi = null;

    private String latestNodeInformation = null;
    private Activity activity;
    private static List<ReferenceDescription> references;
   // List<ReadEntry> readListKeyValue = new ArrayList<ReadEntry>();

    public UtilHelper() {

    }

    public UtilHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    /**
     * @return
     * @throws IllegalArgumentException
     */
    public void checkServerUri(String url) throws IllegalArgumentException, ServiceException {
        Log.i(CLASS_NAME, "START of promptServerUri call...");
        this.url = url;
        // boolean statusofURL = false;
        Log.i(CLASS_NAME, "URL :" + url);
        //final Handler handler = new Handler();

        Log.i(CLASS_NAME, "END of promptServerUri call...");
        //return URL_VALID_STATUS;
    }

    public void checkServerStatus(String url) throws IllegalArgumentException, ServiceException, URISyntaxException {
        Log.i(CLASS_NAME, " START of checkServerStatus call...");
        try {
            UaAddress.validate(url);
            client = new UaClient(url);
            client.connect();
            URL_VALID_STATUS = true;
        } catch (URISyntaxException e) {
            Log.e(CLASS_NAME, e.getMessage() + "\n\n");
            URL_VALID_STATUS = false;
        }

        Log.i(CLASS_NAME, " END of checkServerStatus call...");
        //return URL_VALID_STATUS;
    }

    protected void parseSecurityMode(String arg) {
        Log.i(CLASS_NAME, "START of parseSecurityMode call...");
        char secModeStr = arg.charAt(0);
        int level = 0;
        if (arg.length() > 1)
            level = Integer.parseInt(arg.substring(1, 2));
        if (secModeStr == 'n')
            securityMode = SecurityMode.NONE;
        else if (secModeStr == 's')
            switch (level) {
                default:
                case 1:
                    securityMode = SecurityMode.BASIC128RSA15_SIGN;
                    break;
                case 2:
                    securityMode = SecurityMode.BASIC256_SIGN;
                    break;
                // Will be available in a new stack
                case 3:
                    securityMode = SecurityMode.BASIC256SHA256_SIGN;
                    break;
            }
        else if (secModeStr == 'e')
            switch (level) {
                default:
                case 1:
                    securityMode = SecurityMode.BASIC128RSA15_SIGN_ENCRYPT;
                    break;
                case 2:
                    securityMode = SecurityMode.BASIC256_SIGN_ENCRYPT;
                    break;
                // Will be available in a new stack
                case 3:
                    securityMode = SecurityMode.BASIC256SHA256_SIGN_ENCRYPT;
                    break;
            }
        else
            throw new IllegalArgumentException(
                    "parameter for SecuirtyMode (-s) is invalid, expected 'n', 's' or 'e'; was '" + secModeStr + "'");
        Log.i(CLASS_NAME, "END of parseSecurityMode call...");
    }

    public List<String> checkForNodeId(UaClient client) throws ServiceException, StatusException {
        Log.i(CLASS_NAME, "START of checkForNodeId call...");
        if (nodeId == null) {
            nodeId = Identifiers.RootFolder;
            OPCUAConstants.setCurrentNodeId(nodeId);
        }
        Log.i(CLASS_NAME, "Node id : " + nodeId);
        List<String> entry = browse(nodeId, null, client);
        for (String entryValue : entry) {
            Log.i("value :", entryValue);
        }
        Log.i(CLASS_NAME, "END of checkForNodeId call...");
        return entry;
    }

    /**
     * Browse the references for a node.
     *
     * @param nodeId
     * @param prevId
     * @throws ServiceException
     * @throws StatusException
     */
    protected List<String> browse(NodeId nodeId, NodeId prevId, UaClient client1) throws ServiceException, StatusException {
        Log.i(CLASS_NAME, "START of browse call...");

        printCurrentNode(nodeId, client1);
        List<ReferenceDescription> upReferences;
        try {
            client = client1;
            client1.getAddressSpace().setMaxReferencesPerNode(1000);
            references = client1.getAddressSpace().browse(nodeId);
            String formattedTextValue;
            for (int i = 0; i < references.size(); i++) {
                formattedTextValue = referenceToString(references.get(i), client1);
                System.out.printf("%d - %s\n", i, formattedTextValue);
                if (listEntry != null) {
                    listEntry.add(formattedTextValue);
                }
            }
            listEntry.add(latestNodeInformation);
            upReferences = client1.getAddressSpace().browseUp(nodeId);
        } catch (Exception e) {
            System.out.println(e);
            references = new ArrayList<ReferenceDescription>();
            upReferences = new ArrayList<ReferenceDescription>();
        }
        Log.i(CLASS_NAME, "END of browse call...");

        return listEntry;
    }


    /**
     * @param nodeId
     */
    protected void printCurrentNode(NodeId nodeId, UaClient client1) {
        Log.i(CLASS_NAME, "START of printCurrentNode call...");
        try {
            UaNode node = client1.getAddressSpace().getNode(nodeId);
            if (node == null)
                return;
            String currentNodeStr = getCurrentNodeAsString(node, client1);
            if (currentNodeStr != null) {
                latestNodeInformation = currentNodeStr;
                Log.i(CLASS_NAME, currentNodeStr + "\n");
                //System.out.println("");
            }
        } catch (ServiceException e) {
            //System.out.println(e);
            Log.i(CLASS_NAME, e.toString());
        } catch (AddressSpaceException e) {
            //System.out.println(e);
            Log.i(CLASS_NAME, e.toString());
        }
        Log.i(CLASS_NAME, "END of printCurrentNode call...");
    }

    /**
     * @param r
     * @return
     * @throws ServiceException
     * @throws ServerConnectionException
     * @throws StatusException
     */
    protected String referenceToString(ReferenceDescription r, UaClient client1)
            throws ServerConnectionException, ServiceException, StatusException {
        //Log.i(CLASS_NAME, "START of referenceToString call...");
        if (r == null)
            return "";
        String referenceTypeStr = null;
        try {
            // Find the reference type from the NodeCache
            UaReferenceType referenceType = (UaReferenceType) client1.getAddressSpace().getType(r.getReferenceTypeId());
            if ((referenceType != null) && (referenceType.getDisplayName() != null))
                if (r.getIsForward())
                    referenceTypeStr = referenceType.getDisplayName().getText();
                else
                    referenceTypeStr = referenceType.getInverseName().getText();
        } catch (AddressSpaceException e) {
            System.out.println(e);
            System.out.println(r.toString());
            referenceTypeStr = r.getReferenceTypeId().getValue().toString();
        }
        String typeStr;
        switch (r.getNodeClass()) {
            case Object:
            case Variable:
                try {
                    // Find the type from the NodeCache
                    UaNode type = client1.getAddressSpace().getNode(r.getTypeDefinition());
                    if (type != null)
                        typeStr = type.getDisplayName().getText();
                    else
                        typeStr = r.getTypeDefinition().getValue().toString();
                } catch (AddressSpaceException e) {
                    System.out.println(e);
                    System.out.println("type not found: " + r.getTypeDefinition().toString());
                    typeStr = r.getTypeDefinition().getValue().toString();
                }
                break;
            default:
                typeStr = nodeClassToStr(r.getNodeClass());
                break;
        }
        //Log.i(CLASS_NAME, "END of referenceToString call...");
        return String.format("%s%s (ReferenceType=%s, BrowseName=%s%s)", r.getDisplayName().getText(), ": " + typeStr,
                referenceTypeStr, r.getBrowseName(), r.getIsForward() ? "" : " [Inverse]");
    }

    /**
     * @param nodeClass
     * @return
     */
    private String nodeClassToStr(NodeClass nodeClass) {
        return "[" + nodeClass + "]";
    }

    /**
     * @param node
     * @return
     */
    protected String getCurrentNodeAsString(UaNode node, UaClient client1) {
        String nodeStr = "";
        String typeStr = "";
        String analogInfoStr = "";
        nodeStr = node.getDisplayName().getText();
        UaType type = null;
        if (node instanceof UaInstance)
            type = ((UaInstance) node).getTypeDefinition();
        typeStr = (type == null ? nodeClassToStr(node.getNodeClass()) : type.getDisplayName().getText());

        // This is the way to access type specific nodes and their
        // properties, for example to show the engineering units and
        // range for all AnalogItems
        if (node instanceof AnalogItemType)
            try {
                AnalogItemType analogNode = (AnalogItemType) node;
                EUInformation units = analogNode.getEngineeringUnits();
                analogInfoStr = units == null ? "" : " Units=" + units.getDisplayName().getText();
                Range range = analogNode.getEuRange();
                analogInfoStr = analogInfoStr
                        + (range == null ? "" : String.format(" Range=(%f; %f)", range.getLow(), range.getHigh()));
            } catch (Exception e) {
                System.out.println(e);
            }

        String currentNodeStr = String.format("*** Current Node: %s: %s (ID: %s)%s", nodeStr, typeStr, node.getNodeId(),
                analogInfoStr);
        return currentNodeStr;
    }

    protected String dataValueToString(NodeId nodeId, UnsignedInteger attributeId, DataValue value) {
        boolean showReadValueDataType = true;
        StringBuilder sb = new StringBuilder();
        sb.append("Node: ");
        sb.append(nodeId);
        sb.append(".");
        sb.append(AttributesUtil.toString(attributeId));
        sb.append(" | Status: ");
        sb.append(value.getStatusCode());
        if (value.getStatusCode().isNotBad()) {
            sb.append(" | Value: ");
            if (value.isNull())
                sb.append("NULL");
            else {
                if (showReadValueDataType && Attributes.Value.equals(attributeId))
                    try {
                        UaVariable variable = (UaVariable) client.getAddressSpace().getNode(nodeId);
                        if (variable == null)
                            sb.append("(Cannot read node datatype from the server) ");
                        else {

                            NodeId dataTypeId = variable.getDataTypeId();
                            UaType dataType = variable.getDataType();
                            if (dataType == null)
                                dataType = client.getAddressSpace().getType(dataTypeId);

                            Variant variant = value.getValue();
                            variant.getCompositeClass();
                            if (attributeId.equals(Attributes.Value))
                                if (dataType != null)
                                    sb.append("(" + dataType.getDisplayName().getText() + ")");
                                else
                                    sb.append("(DataTypeId: " + dataTypeId + ")");
                        }
                    } catch (ServiceException e) {
                    } catch (AddressSpaceException e) {
                    }
                final Object v = value.getValue().getValue();
                if (value.getValue().isArray())
                    sb.append(MultiDimensionArrayUtils.toString(v));
                else
                    sb.append(v);
            }
        }
        sb.append(dateTimeToString(" | ServerTimestamp: ", value.getServerTimestamp(), value.getServerPicoseconds()));
        sb.append(dateTimeToString(" | SourceTimestamp: ", value.getSourceTimestamp(), value.getSourcePicoseconds()));
        return sb.toString();
    }

    protected static String dateTimeToString(String title, DateTime timestamp, UnsignedShort picoSeconds) {
        if ((timestamp != null) && !timestamp.equals(DateTime.MIN_VALUE)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd (zzz) HH:mm:ss.SSS");
            StringBuilder sb = new StringBuilder(title);
            sb.append(format.format(timestamp.getCalendar(TimeZone.getDefault()).getTime()));
            if ((picoSeconds != null) && !picoSeconds.equals(UnsignedShort.valueOf(0)))
                sb.append(String.format("/%d picos", picoSeconds.getValue()));
            return sb.toString();
        }
        return "";
    }

    public List<ReadEntry> read(NodeId nodeId, UaClient client1) throws ServiceException, StatusException {
        List<ReadEntry> listEntry;
        Log.i(CLASS_NAME, "read node :" + nodeId);
        OPCUAConstants.setCurrentNodeId(nodeId);

        client = client1;
       // client.connect();
        listEntry = readAttributeEntry(nodeId,client1);
       // for(ReadEntry e : readListKeyValue) {
        //    Log.i(CLASS_NAME, "Key :" + e.getReadKey() +"," + "Value :" + e.getReadValue());
       //}
        return listEntry;
    }

    public void readAttributeId(NodeId nodeId, int position, UaClient client1) throws ServiceException, StatusException {
        //UnsignedInteger attributeId = readAttributeId(position);
        int action = position;
        UnsignedInteger attributeId = UnsignedInteger.valueOf(action);
        System.out.println("attribute: " + AttributesUtil.toString(attributeId));
        DataValue value = client1.readAttribute(nodeId, attributeId);
        //Log.i(CLASS_NAME, dataValueToString(nodeId, attributeId, value));
    }

    public List<ReadEntry> readAttributeEntry(NodeId nodeid,UaClient client1)  {
        //List<String> readListEntry = new ArrayList<String>();
       // List<String> readListValueEntry = new ArrayList<String>();

        List<ReadEntry> readListKeyValue = new ArrayList<ReadEntry>();


        Log.i(CLASS_NAME, "Select the node attribute.");
        for (long i = Attributes.NodeId.getValue(); i < Attributes.UserExecutable.getValue(); i++) {
            System.out.printf("%d - %s\n", i, AttributesUtil.toString(UnsignedInteger.valueOf(i)));
            ReadEntry entry = new ReadEntry();
            entry.setReadKey(AttributesUtil.toString(UnsignedInteger.valueOf(i)));

            //readListEntry.add(AttributesUtil.toString(UnsignedInteger.valueOf(i)));

            if(client1 != null) {
                DataValue value = null;
                try {
                    value = client1.readAttribute(nodeid, UnsignedInteger.valueOf(i));
                    entry.setReadValue(dataValueToString(nodeid, UnsignedInteger.valueOf(i), value));

                } catch (ServiceException e) {
                    e.printStackTrace();
                    //Log.i(CLASS_NAME, e.toString());
                    entry.setReadValue(null);

                } catch (StatusException e) {
                    e.printStackTrace();
                    //Log.i(CLASS_NAME, e.toString());
                    entry.setReadValue(null);
                }
                // DataValue value = client1.readValue(nodeid);
               // Log.i(CLASS_NAME, dataValueToString(nodeid, UnsignedInteger.valueOf(i), value));
                //Log.i(CLASS_NAME, value.getValue().getValue().toString());
                //readListValueEntry.add(dataValueToString(nodeid, UnsignedInteger.valueOf(i), value));
                //entry.setReadValue(dataValueToString(nodeid, UnsignedInteger.valueOf(i), value));

                readListKeyValue.add(entry);
                //Log.i(CLASS_NAME, entry.getReadKey() + "," + entry.getReadValue());
                //Log.i(CLASS_NAME, "I am here!");
            } else {
                Log.i(CLASS_NAME, "client is null");
            }
        }
        //Log.i(CLASS_NAME, "I am here!");
        //for(ReadEntry entry : readListKeyValue) {
          //  Log.i(CLASS_NAME, "Key :" + entry.getReadKey() +"," +"Value :" + entry.getReadValue());
        //}

        //Log.i(CLASS_NAME, "Hi , Iam here !");

       // return readListEntry;
        return readListKeyValue;
    }


    public List<String> browse(NodeId nodeId, NodeId prevId, int position, UaClient client1) throws ServiceException, StatusException {
        Log.i(CLASS_NAME, "START of browseoFindClickedNodeId call...");

        try {
            printCurrentNode(nodeId, client1);
            Log.i(CLASS_NAME, "Reference :" + references.get(position));
            ReferenceDescription r = references.get(position);
            Log.i(CLASS_NAME, "Clicked Node id:" + r.getNodeId() + "," + client1.getAddressSpace().getNamespaceTable().toNodeId(r.getNodeId()));

            listEntry = browse(client1.getAddressSpace().getNamespaceTable().toNodeId(r.getNodeId()), null, client1);
            OPCUAConstants.setCurrentNodeId(client1.getAddressSpace().getNamespaceTable().toNodeId(r.getNodeId()));
            //read(client1.getAddressSpace().getNamespaceTable().toNodeId(r.getNodeId()), position);
        } catch (Exception err) {
            err.printStackTrace();
        }

        Log.i(CLASS_NAME, "END of browseoFindClickedNodeId call...");

        return listEntry;
    }


}

