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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.UaAddress;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.util.OPCUAConstants;
import com.prosysopc.ua.util.UtilHelper;

import org.opcfoundation.ua.builtintypes.NodeId;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LoadElementFromRemote extends Activity {
    private String CLASS_NAME = LoadElementFromRemote.class.getName().toString();
    private TextView NodeInformation;
    private Button home, cancel;
    private String[] testInformation = {"One item", "Two item", "Three Item", "Four item", "Five item", "Six item", "Seven Item", "Eight item", "Nine Item", "Ten Item"};
    private NodeId nodeId;
    private Bundle bundle;
    private String url;
    private ListView listView;
    private ArrayList<SearchInformation> listInformation = new ArrayList<SearchInformation>();
    private UtilHelper utilHelper = new UtilHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_element_from_remote);

        NodeInformation = (TextView) findViewById(R.id.current_server_information);
        home = (Button) findViewById(R.id.home);
        cancel = (Button) findViewById(R.id.cancel);
        listView = (ListView) findViewById(R.id.listview);

        bundle = getIntent().getExtras();
        if (bundle.containsKey("url")) {
            url = bundle.getString("url");
            //serverURL.setText(url);
        }

        ConnectAndLoadElementArray connect = new ConnectAndLoadElementArray(LoadElementFromRemote.this, "Application");
        connect.execute(new String[]{url, "ROOT"});

        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                ConnectAndLoadElementArray connect_1 = new ConnectAndLoadElementArray(LoadElementFromRemote.this, "Application");
                connect_1.execute(new String[]{url, "ROOT"});

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               // finish();
               Log.i(CLASS_NAME, "Node id:" + OPCUAConstants.getCurrentNodeId());
                List<String> listEntry = null;
               // try {

                    //listEntry = utilHelper.read(OPCUAConstants.getCurrentNodeId(), new UaClient(new UaAddress(url)));
               // } catch (ServiceException e) {
                //    e.printStackTrace();
              //  } catch (StatusException e) {
              //      e.printStackTrace();
              //  } catch (URISyntaxException e) {
              //      e.printStackTrace();
              //  }



                ConnectAndReadTimeTask connect_1 = new ConnectAndReadTimeTask(LoadElementFromRemote.this, "Application");
                connect_1.execute(new String[]{url, NodeInformation.getText().toString()});

                if(listEntry != null) {
                    for(String entry: listEntry) {
                        Log.i(CLASS_NAME , entry);
                    }
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(LoadElementFromRemote.this, "Option chosen :" + position, Toast.LENGTH_LONG).show();
                String nodeidtostring = OPCUAConstants.getNodeidToString();
                ConnectAndLoadElementArray connect_1 = new ConnectAndLoadElementArray(LoadElementFromRemote.this, "Application");
                connect_1.execute(new String[]{url, "ROOT1", nodeidtostring, String.valueOf(position)});

            }
        });
    }

    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(LoadElementFromRemote.this, "" + position + "," + l.getItemAtPosition(position), Toast.LENGTH_LONG).show();
        String nodeidtostring = OPCUAConstants.getNodeidToString();
        ConnectAndLoadElementArray connect_1 = new ConnectAndLoadElementArray(LoadElementFromRemote.this, "Application");
        connect_1.execute(new String[]{url, "ROOT1", nodeidtostring, String.valueOf(position)});

    }*/


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

    public void loadLatestInformation(List<String> entry) {

        List<String> latestEntry = new ArrayList<String>();
        listInformation.clear();
        String formattedValue = null;
        String remainingValue = null;
        int i = 0;
        if (entry != null) {
            NodeInformation.setText(entry.get(entry.size() - 1));
            entry.remove(entry.size() - 1);
            for (String e : entry) {
                formattedValue = e.substring(0, e.indexOf(":"));
                remainingValue = e.substring(e.indexOf(":") + 1, e.length());
                latestEntry.add(formattedValue);

                SearchInformation searchInformation = new SearchInformation();
                searchInformation.setHeadLineInformation(formattedValue);
                searchInformation.setSubLineInformation(remainingValue);
                listInformation.add(searchInformation);


            }

            //listView.setAdapter(new ArrayAdapter<String>(LoadElementFromRemote.this, android.R.layout.simple_list_item_1, listInformation));
            listView.setAdapter(new ListAdapter(LoadElementFromRemote.this,listInformation) {
            });
        } else {
            listView.setAdapter(new ArrayAdapter<String>(LoadElementFromRemote.this, android.R.layout.simple_list_item_1, testInformation));

        }
    }

    public void moveIntent() {
        Intent intent = new Intent(getApplicationContext(), ReadNodeInformation.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("node_information", NodeInformation.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }




}

