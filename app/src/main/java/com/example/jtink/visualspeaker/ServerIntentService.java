package com.example.jtink.visualspeaker;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jtink on 3/12/2017.
 */

public class ServerIntentService extends IntentService {
    public static final String INTENT_CMD = "cmd";
    public static final String SONG_PATH = "path";
    ServerSocket mSocket;
    Socket mClient;
    OutputStream mStream;

    public ServerIntentService() {
        super("ServerIntentService");


    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {

            mSocket = new ServerSocket(8888);
            Log.d("Group", "SocketAddr= " + mSocket.getInetAddress().toString());
            mClient = mSocket.accept();
            Log.d("Group", "Connection achieved");
            mStream = mClient.getOutputStream();


        } catch (IOException e) {
            Log.d("Group", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("Group", "Service call");
        String cmd = intent.getStringExtra(INTENT_CMD);
        if(cmd.equals("play"))
            play();
        else if(cmd.equals("pause"))
            pause();
        else if(cmd.equals("transfer"))
            transfer(intent.getStringExtra(SONG_PATH));
    }

    private boolean play() {
        try {
            mStream.write("play".getBytes());
            return true;
        } catch (IOException e) {
            Log.d("Group", "Error with command "+ e.getMessage());
            return false;
        }
    }

    private boolean pause() {
        try {
            mStream.write("pause".getBytes());
            return true;
        } catch (IOException e) {
            Log.d("Group", "Error with command "+ e.getMessage());
            return false;
        }
    }

    private boolean transfer(String path) {
        return false;
    }
}
