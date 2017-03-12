package com.example.jtink.visualspeaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pGroup mGroup;
    Collection<WifiP2pDevice> connectedList;

    ArrayAdapter<Song> songAdapter;
    ListView songListView;

    ServerSocket mSocket;
    Socket mClient;
    OutputStream mStream;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String[] songs = {"Harder, Better, Faster, Stronger", "Fireworks", "Titanium", "Seven Nation Army"};
        ArrayList<Song> songs = Song.test();
        songAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, songs);
        songListView = (ListView) findViewById(R.id.listView);
        songListView.setAdapter(songAdapter);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        makeGroup();

        connected = false;
        ServerConnectTask connectTask = new ServerConnectTask();
        connectTask.execute();
    }

    /**
     * A background task which initializes the socket and waits for the client
     * to connect.
     */
    public class ServerConnectTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                mSocket = new ServerSocket(8888);
                Log.d("Group", "SocketAddr= " + mSocket.getInetAddress().toString());
                mClient = mSocket.accept();
                Log.d("Group", "Connection achieved");
                connected = true;
                mStream = mClient.getOutputStream();


            } catch (IOException e) {
                Log.d("Group", e.getMessage());
            }
            return null;
        }
    }

    /**
     * Returns whether the WiFi Direct group has a client
     */
    private boolean groupPopulated() {
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group == null) {
                    Log.d("Group", "Group was null...");
                    return;
                }
                mGroup = new WifiP2pGroup(group);
                connectedList = mGroup.getClientList();
                if(connectedList.size() == 0) {
                    Log.d("Group", "No devices connected");

                }
                else {
                    List<WifiP2pDevice> devList = new ArrayList(connectedList);
                    String devName = devList.get(0).deviceAddress;
                    Log.d("Group", "Connected to: " + devName);
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(connectedList.size() == 0)
            return false;
        else
            return true;

    }

    /**
     * Creates a WiFi direct group. Will fail if already paired, or other issues.
     */
    private void makeGroup() {
        //create group and establish connection
        if(mGroup != null) {
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("Group", "Group created successfully");
                        }
                        public void onFailure(int reason) {
                            Log.d("Group", "Group creation failed with error= " + reason);
                        }

                    });
                }

                public void onFailure(int reason) {

                }
            });
        }
        else {
            mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("Group", "Group2 created successfully");
                }
                public void onFailure(int reason) {
                    Log.d("Group", "Group creation failed with error= " + reason);
                }

            });

        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group == null) {
                            Log.d("Group", "Group was null...");
                            return;
                        }
                        Log.d("Group", "Group info updated");
                        //Log.d("Group", "Group name is " + group.getNetworkName());
                        mGroup = new WifiP2pGroup(group);
                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String ssid = mGroup.getNetworkName();
                String passphrase = mGroup.getPassphrase();
                Log.d("Group", "ssid = " + ssid);
                Log.d("Group", "passphrase =" + passphrase);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the transfer button is pressed
     * @param view
     */
    public void transferSong(View view)
    {
        Context context = MainActivity.this;
        String text = "Transferring song now";
        int duration = Toast.LENGTH_LONG;
        //ConnectionAsyncTask task = new ConnectionAsyncTask();
        //task.execute();


        Toast.makeText(context, text, duration).show();
    }

    /**
     * Called when the play button is pressed
     * @param view
     */
    public void playSong(View view)
    {
        try {
            mStream.write("play".getBytes());

        } catch (IOException e) {
            Log.d("Group", "Error with command "+ e.getMessage());

        }

        Context context = MainActivity.this;
        String text = "Playing";
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }

    /**
     * Called when the pause button is pressed
     * @param view
     */
    public void pauseSong(View view)
    {
        try {
            mStream.write("pause".getBytes());

        } catch (IOException e) {
            Log.d("Group", "Error with command "+ e.getMessage());

        }

        Context context = MainActivity.this;
        String text = "Paused";
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }
}
