package com.chat.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Client1 implements Runnable {
	
	public static final String TAG = Client1.class.getSimpleName();
	public static int PORT = 12335;
	public static String IP = "10.0.2.2";
	
	public Socket socket;
	
	public Client1() {
		try {
			Log.d(TAG, "Getting the socket ready");
			socket = new Socket(IP, PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		}
	}
	public void run() {
		Log.d(TAG, " in run of Client1");
	}

}
