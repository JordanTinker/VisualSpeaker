package com.example.jtink.visualspeaker;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jtink on 3/11/2017.
 */

public class ConnectionAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        try {

            ServerSocket serverSocket = new ServerSocket(8888);
            Log.d("Group", "SocketAddr= " + serverSocket.getInetAddress().toString());
            Socket client = serverSocket.accept();
            Log.d("Group", "Connection achieved");

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            OutputStream stream = client.getOutputStream();
            stream.write("lol".getBytes());

            serverSocket.close();
            return null;
        } catch (IOException e) {
            Log.d("Group", e.getMessage());
            return null;
        }

    }
}
