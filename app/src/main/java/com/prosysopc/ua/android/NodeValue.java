package com.prosysopc.ua.android;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prosysopc.ua.util.OPCUAConstants;
import com.prosysopc.ua.util.UtilHelper;

import org.opcfoundation.ua.builtintypes.NodeId;

import java.util.ArrayList;
import java.util.List;

public class NodeValue extends Activity {
    private String CLASS_NAME = NodeValue.class.getName().toString();
    private TextView NodeInformation;
    private Bundle bundle = new Bundle();
    private String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_layout);

        NodeInformation = (TextView) findViewById(R.id.textView01);

        bundle = getIntent().getExtras();
        if (bundle.containsKey("information")) {
            url = bundle.getString("information");
            NodeInformation.setText(url);
            //serverURL.setText(url);

        }




       // listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       //     @Override
        //    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
         //       Toast.makeText(ReadNodeInformation.this, "Option chosen :" + position, Toast.LENGTH_LONG).show();
          //      Log.i(CLASS_NAME, "" + listInformation.get(position).getReadValue());
          //  }
       // });






    }









}



