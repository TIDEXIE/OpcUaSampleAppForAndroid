package com.prosysopc.ua.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.PkiFileBasedCertificateValidator;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.util.OPCUAConstants;
import com.prosysopc.ua.util.UtilHelper;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.DateTime;
import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.Identifiers;
import org.opcfoundation.ua.transport.security.SecurityMode;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is an AsyncTask for connecting to an OPC UA server and reading the server time
 */
public class ConnectAndReadTimeTask extends AsyncTask<String, Void, ArrayList<ReadEntry>> {
    private String TAG_NAME = ConnectAndReadTimeTask.class.getName().toString();
    private String applicationName;
    private DateTime time;
    private String value;
    private Activity activity;
    private String error;
    private ProgressDialog progressDialog;
    private UtilHelper utilHelper;
    private ArrayList<ReadEntry> readKeyValue = new ArrayList<ReadEntry>();
    //private Ua

    /**
     * Creates a new Task
     *
     * @param appName
     */
    public ConnectAndReadTimeTask(Activity activity, String appName) {
        this.activity = activity;
        this.applicationName = appName;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show the progressdialog, once the execution gets started
        progressDialog = ProgressDialog.show(this.activity, "Loading...", "Please wait...");
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     *
     * This method is executed in the background, and will do the reading
     */
    protected ArrayList<ReadEntry> doInBackground(String... strings) {
        String serverUri = strings[0];
        UaClient myClient = null;
        try {
            myClient = createClient(serverUri);
            myClient.connect();
        } catch (Exception e) { 
            error = e.toString();
            //return false;
            readKeyValue = null;
        }

        DataValue dv;
        try {
            //dv = myClient.readValue(Identifiers.Server_ServerStatus_CurrentTime);
            //dv = myClient.readValue(OPCUAConstants.getCurrentNodeId());
            //time = (DateTime) dv.getValue().getValue();
            //value = dv.getValue().getValue().toString();
            utilHelper = new UtilHelper();
            //utilHelper.readAttributeId(OPCUAConstants.getCurrentNodeId(), 0, myClient);
            readKeyValue = (ArrayList<ReadEntry>) utilHelper.read(OPCUAConstants.getCurrentNodeId(), myClient);
        } catch (Exception e) {
            error = e.toString();
            //return false;
            readKeyValue = null;

        }

        myClient.disconnect();

        return readKeyValue;
    }

    /**
     * This is an example on how to create and set parameters to an UaClient
     *
     * @param serverUri
     * @return
     * @throws URISyntaxException
     * @throws SessionActivationException
     */
    public UaClient createClient(String serverUri) throws URISyntaxException,
            SessionActivationException {
        // Create the UaClient
        UaClient myClient = new UaClient(serverUri);

        // Create and set certificate validator
        PkiFileBasedCertificateValidator validator = new PkiFileBasedCertificateValidator("/sdcard/PKI/CA");
        myClient.setCertificateValidator(validator);

        // Create application description
        ApplicationDescription appDescription = new ApplicationDescription();
        appDescription.setApplicationName(new LocalizedText(applicationName, Locale.ENGLISH));
        appDescription.setApplicationUri("urn:localhost:UA:" + applicationName);
        appDescription.setProductUri("urn:prosysopc.com:UA:" + applicationName);
        appDescription.setApplicationType(ApplicationType.Client);

        // Create and set application identity
        ApplicationIdentity identity = new ApplicationIdentity();
        identity.setApplicationDescription(appDescription);
        identity.setOrganisation("Prosys");
        myClient.setApplicationIdentity(identity);

        // Set locale
        myClient.setLocale(Locale.ENGLISH);

        // Set default timeout to 60 seconds
        myClient.setTimeout(60000);

        // Set security mode to NONE (others not currently supported on Android)
        myClient.setSecurityMode(SecurityMode.NONE);

        // Set anonymous user identity
        myClient.setUserIdentity(new UserIdentity());

        return myClient;
    }

    protected void onPostExecute(ArrayList<ReadEntry> entry) {
        String result = null;
        // Check the progress dialog is been shown in the UI,
        // if it is shown, dismiss the progress dialog
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
         OPCUAConstants.setReadInformation(entry);
        Intent intent = new Intent(activity, ReadNodeInformation.class);
        //intent.putExtra("readElementList", entry);

        //bundle.putString("url", url);
        //bundle.putString("node_information", NodeInformation.getText().toString());

        activity.startActivity(intent);

        //new LoadElementFromRemote().moveIntent();

    }
}
