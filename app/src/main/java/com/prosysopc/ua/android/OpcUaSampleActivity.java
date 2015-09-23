package com.prosysopc.ua.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OpcUaSampleActivity extends Activity {

    private String CLASS_NAME = OpcUaSampleActivity.class.getName().toString();
    // UI elements for testing purpose
    private Button connectAndReadButton;
    private Button explore, cancel;
    private TextView serverURL;
    private TextView serverTimeField;
    private Bundle bundle;
    private String url;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_screen);

        explore = (Button) findViewById(R.id.explore);
        cancel = (Button) findViewById(R.id.cancel);
        serverURL = (TextView) findViewById(R.id.url_text);

        bundle = getIntent().getExtras();
        if (bundle.containsKey("url")) {
            url = bundle.getString("url");
            serverURL.setText(url);
        }

        // Set a handler to connectAndReadButton
        explore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Read the time asynchronously by creating and executing a ConnectAndReadTimeTask
                //ConnectAndLoadElementArray task = new ConnectAndLoadElementArray(OpcUaSampleActivity.this, "DemoAppForAndroid");
                //task.execute(new String[] {"opc.tcp://10.0.2.2:52520/OPCUA/SampleConsoleServer"});
                //task.execute(new String[] {"opc.tcp://uademo.prosysopc.com:52520/OPCUA/SampleConsoleServer"});
                //task.execute(new String[] {"opc.tcp://192.168.43.38:52520/OPCUA/SampleConsoleServer"});

                // startActivity(new Intent(OpcUaSampleActivity.this, LoadElementFromRemote.class));

                Intent intent = new Intent(getApplicationContext(), LoadElementFromRemote.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                //intent.putExtra("bundle", bundle);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Sets the time on the UI. Only call this with the UI thread, in this example ConnectAndReadTimeTask.onPostExecute will call this.
     *
     * @param value
     */
    public void setTime(String value) {
        serverTimeField.setText(value);
    }
}