package com.prosysopc.ua.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.PkiFileBasedCertificateValidator;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.util.OPCUAConstants;
import com.prosysopc.ua.util.UtilHelper;

import org.opcfoundation.ua.builtintypes.DateTime;
import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.transport.security.SecurityMode;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/**
 * This is an AsyncTask for connecting to an OPC UA server
 */
public class ConnectAndLoadElementArray extends AsyncTask<String, Void, List<String>> {
    private String TAG_NAME = ConnectAndLoadElementArray.class.getName().toString();
    private String applicationName;
    private DateTime time;
    private Activity activity;
    private String error;
    private ProgressDialog progressDialog;
    private NodeId nodeId;
    private String operation = "";


    /**
     * Creates a new Task
     *
     * @param appName
     */
    public ConnectAndLoadElementArray(Activity activity, String appName) {
        this.activity = activity;
        this.applicationName = appName;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show the progressdialog, once the execution gets started
        progressDialog = ProgressDialog.show(this.activity, "Loading...", "Please wait...");
        nodeId = OPCUAConstants.getCurrentNodeId();
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     *
     * This method is executed in the background, and will do the reading
     */
    protected List<String> doInBackground(String... strings) {
        String serverUri = strings[0];
        operation = strings[1];
        UaClient myClient;
        List<String> entry = null;

        try {
            myClient = createClient(serverUri);
            myClient.connect();
        } catch (Exception e) {
            error = e.toString();
            //return false;
            return null;
        }


        if (operation.equalsIgnoreCase("ROOT")) {
            try {
                entry = new UtilHelper().checkForNodeId(myClient);
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (StatusException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Log.i("Current Node Id : ", OPCUAConstants.getCurrentNodeId().toString());
                entry = new UtilHelper().browse(OPCUAConstants.getCurrentNodeId(), null, Integer.parseInt(strings[3]), myClient);
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (StatusException e) {
                e.printStackTrace();
            }
        }

        return entry;

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

    protected void onPostExecute(List<String> entry) {
        String result = null;
        // Check the progress dialog is been shown in the UI,
        // if it is shown, dismiss the progress dialog
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (entry != null) {
            Log.i("List entry :", "" + entry);
            for (String entryValue : entry) {
                Log.i("Entry in list :", entryValue);
            }
            ((LoadElementFromRemote) activity).loadLatestInformation(entry);

        } else {
            Log.i(TAG_NAME, "Entry is null");
            ((LoadElementFromRemote) activity).loadLatestInformation(entry);
        }

    }
}
