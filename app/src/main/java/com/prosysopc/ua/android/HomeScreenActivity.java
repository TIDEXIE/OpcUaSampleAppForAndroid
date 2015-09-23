package com.prosysopc.ua.android;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.util.UtilHelper;

import java.net.URISyntaxException;

public class HomeScreenActivity extends Activity {

    private final String CLASS_NAME = HomeScreenActivity.class.getName();
    private boolean URL_VALID_STATUS = false;

    private EditText URLText;
    private Button submit, cancel;
    private SharedPreferences preferences;
    private String URL;
    private UtilHelper util;

    private Thread checkServerBackgroundThread = null;
    private ProgressDialog progressIndi = null;

    public static String SUCCESS_MESSAGE = "Connection between the mobile client and the server is successful !";
    public static String FAILURE_MESSAGE = "Connection between the mobile client and the server is unsuccessful , Please make sure the server is up or the URL entered in the mobile client is correct !";

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        URLText = (EditText) findViewById(R.id.url_text);
        submit = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);

        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        URL = preferences.getString("URLText", "");
        URLText.setText(URL.toString());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editor != null) {

                    url = URLText.getText().toString();
                    if (url.toString().trim().length() > 0) {
                        //util = new UtilHelper();
                        try {
                            editor.putString("URLText", url);
                            editor.commit();
                            Toast.makeText(getApplicationContext(), "Information saved !", Toast.LENGTH_LONG).show();
                            //checkServerBackgroundThread.start();
                            ConnectAndReadTask task = new ConnectAndReadTask(HomeScreenActivity.this, "DemoAppForAndroid");
                            //task.execute(new String[] {"opc.tcp://10.0.2.2:52520/OPCUA/SampleConsoleServer"});
                            //task.execute(new String[] {"opc.tcp://uademo.prosysopc.com:52520/OPCUA/SampleConsoleServer"});
                            //task.execute(new String[] {"opc.tcp://192.168.43.38:52520/OPCUA/SampleConsoleServer"});
                            task.execute(new String[]{url});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                //startActivity(new Intent(getApplicationContext(), OpcUaSampleActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        checkServerBackgroundThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    util.checkServerStatus(url);
                    mGenericHandler.sendEmptyMessage(0);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    mGenericHandler.sendEmptyMessage(-1);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    mGenericHandler.sendEmptyMessage(-1);
                } catch (Exception e) {
                    e.printStackTrace();
                    mGenericHandler.sendEmptyMessage(-1);
                }
            }
        });
        // checkServerBackgroundThread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Handler mGenericHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                if (progressIndi.isShowing()) {
                    progressIndi.dismiss();
                }
                if (msg.arg1 == 0) {
                    showAlertIntimation(SUCCESS_MESSAGE);
                } else {
                    showAlertIntimation(FAILURE_MESSAGE);
                }

            }
        }
    };


    public void showAlertIntimation(final String message) {
        Log.i(CLASS_NAME, "START of showAlertIntimation call...");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreenActivity.this
        );

        // set title
        alertDialogBuilder.setTitle("Alert Intimation");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();
                        if (message.equalsIgnoreCase(HomeScreenActivity.SUCCESS_MESSAGE)) {
                            Intent intent = new Intent(getApplicationContext(), OpcUaSampleActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("url", url);
                            //intent.putExtra("bundle", bundle);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            finish();
                        }

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        Log.i(CLASS_NAME, " END of showAlertIntimation call...");
    }
}
