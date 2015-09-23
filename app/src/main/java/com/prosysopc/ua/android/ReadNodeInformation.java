package com.prosysopc.ua.android;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class ReadNodeInformation extends ListActivity {
    private String CLASS_NAME = ReadNodeInformation.class.getName().toString();
    private TextView NodeInformation;
    private Button home, cancel;
    private String[] testInformation = {"One item", "Two item", "Three Item", "Four item", "Five item", "Six item", "Seven Item", "Eight item", "Nine Item", "Ten Item"};
    private NodeId nodeId;
    private Bundle bundle;
    private String url;
    private ListView listView;
    private ArrayList<ReadEntry> listInformation = new ArrayList<ReadEntry>();
    private UtilHelper utilHelper = new UtilHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_information_layout);

        NodeInformation = (TextView) findViewById(R.id.current_server_information);
        home = (Button) findViewById(R.id.home);
        cancel = (Button) findViewById(R.id.cancel);
        listView = (ListView) findViewById(R.id.listview);

        bundle = getIntent().getExtras();
        //if (bundle.containsKey("url")) {
        //    url = bundle.getString("url");
            //serverURL.setText(url);

       // }
       // if (bundle.containsKey("node_information")) {
        //    NodeInformation.setText("node_information");

        //}
        //if (bundle.containsKey("readElementList")) {
        //    listInformation = (ArrayList<ReadEntry>) getIntent().getSerializableExtra("readElementList");
        //    loadLatestInformation(listInformation);
        // }

        loadLatestInformation(OPCUAConstants.getReadInformation());


        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // finish();
                Log.i(CLASS_NAME, "Node id:" + OPCUAConstants.getCurrentNodeId());
                finish();
            }
        });


        // listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //     @Override
        //    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //       Toast.makeText(ReadNodeInformation.this, "Option chosen :" + position, Toast.LENGTH_LONG).show();
        //      Log.i(CLASS_NAME, "" + listInformation.get(position).getReadValue());
        //  }
        // });


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Toast.makeText(ReadNodeInformation.this, "Option chosen :" + position, Toast.LENGTH_LONG).show();
        Log.i(CLASS_NAME, "" + listInformation.get(position).getReadValue());
        Bundle bundle = new Bundle();
        Intent intent = new Intent(ReadNodeInformation.this, NodeValue.class);
        bundle.putString("information", listInformation.get(position).getReadValue());
        intent.putExtras(bundle);
        //new Bundle().putString("information", listInformation.get(position).getReadValue());
        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_element_from_remote, menu);
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

    public void loadLatestInformation(List<ReadEntry> entry) {
        listInformation = (ArrayList<ReadEntry>) entry;
        List<String> latestEntry = new ArrayList<String>();
        String formattedValue = null;
        String remainingValue = null;

        if (entry != null) {
            for (ReadEntry e : entry) {
                formattedValue = e.getReadKey();
                latestEntry.add(formattedValue);

            }

            setListAdapter(new ArrayAdapter<String>(ReadNodeInformation.this, android.R.layout.simple_list_item_1, latestEntry));
        }
    }
}



