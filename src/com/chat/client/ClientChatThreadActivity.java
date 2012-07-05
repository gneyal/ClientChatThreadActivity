package com.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ClientChatThreadActivity extends Activity {
	
	public static final String TAG = ClientChatThreadActivity.class.getSimpleName();
	public static int PORT = 12335;
	public static String IP = "10.0.2.2";
	
	// extends AsyncTask
	public InputAsync inAsync;
	public BufferedReader in;
	public PrintWriter out;
	
	public String userInput;
	
	public ClientChatThreadActivity context;

	private Socket socket;
	private boolean socketAlive = false;
	
	public PrintWriter printWriter;
	private boolean printWriterAlive = false;
	
	public InputThread input;
	
	public ListView listView1;
	public String[] stringsForList;
	private ArrayAdapter<String> arrayAdapter;
	public int counter = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        listView1 = (ListView) findViewById(R.id.mylist);
        stringsForList = new String[10];
        for (int i =0; i<10; i++)
        	stringsForList[i] = "hi";
        
        counter = 0;
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.textView1, stringsForList);

        // By using setAdapter method, you plugged the ListView with adapter
        listView1.setAdapter(arrayAdapter);

        // Normally the argument of setAdapter ask for a ListAdapter instance.
        //And that is the best way of implementation of this code
        //We call it "programming to the interface"
    }
    
    public void onStart() {
    	super.onStart();
    	
    	setSocket();
        setPrintWriter();
        setInput();
        
        listenToIncomingMessages();

    }
    
    public void listenToIncomingMessages() {
    	inAsync = new InputAsync();
    	inAsync.execute();
    }
    public void setSocket() {
    	try {
			Log.d(TAG, "Getting the socket ready");
			socket = new Socket(IP, PORT);
			setSocketAlive(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
			setSocketAlive(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
			setSocketAlive(false);
		}
    }
    public boolean isSocketAlive() {
		return socketAlive;
	}

	public void setSocketAlive(boolean socketAlive) {
		this.socketAlive = socketAlive;
	}
	public boolean isPrintWriterAlive() {
		return printWriterAlive;
	}

	public void setPrintWriterAlive(boolean printWriterAlive) {
		this.printWriterAlive = printWriterAlive;
	}
	
	public void setPrintWriter() {
		if (isSocketAlive()) {
			try {
				printWriter = new PrintWriter(socket.getOutputStream(), true);
				printWriterAlive = true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				printWriterAlive = false;
			}
		}
	}
	public void printToScreen(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
	}
	
	public void setInput() {
		input = new InputThread(this, socket, printWriter);
	}
	public void onClick(View view) {
		// 1. get message from the EditText
		EditText editText = (EditText) findViewById(R.id.editText1);
		String msg = editText.getText().toString();
		
		// 2. send that message by the PrintWriter
		if (isPrintWriterAlive()) {
			printWriter.println(msg);
		}
		editText.setText(R.string.enter_msg);
	}
	
	// when incoming message arrives by in BufferedReader:
	// 1. use printToScreen function here
	public class InputAsync extends AsyncTask<Void, String, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				userInput = in.readLine();
			} catch (Exception e) {}
			while ( userInput != null) {
				System.out.println("Listening on port ");
//				context.printToScreen(userInput);
				publishProgress(userInput);
				try {
					userInput = in.readLine();
				} catch (Exception e) {}
			}
			return true;
		}
		
		@Override
        protected void onProgressUpdate(String... str) {
            if (str.length > 0) {
                Log.i("AsyncTask", "onProgressUpdate: " + str[0]);
                Toast.makeText(getApplicationContext(), str[0], Toast.LENGTH_SHORT).show();
                if (counter < 10)
                	stringsForList[counter++] = (str[0]);
                listView1.setAdapter(arrayAdapter);
            }
        }
		
		
	}
	public class InputThread {
		
		public InputThread(ClientChatThreadActivity c, Socket socket, PrintWriter o) {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = o;
				context = c;
			} catch (Exception e) {
				System.err.println("Don't know about host: taranis.");
				System.exit(1);
			}

		}

	}

	
}