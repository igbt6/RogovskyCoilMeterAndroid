package com.inz.rogovskycurrentmeter;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// For Debugging
	private static final String TAG = "MAINCurrentMeter";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_EXIT_APLICATION = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	public static final String DATA_ACTION = "DATA_ACTION";
	// Layout Views
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	public static BtService mChatService = null;
	private AlertDialogManager alertBuilder;

	private Button mStartMeasureButton;

	public static String readFullMessage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // check if
																	// bluetooth
																	// is
																	// availabl
		if (mBluetoothAdapter == null) {
			alertBuilder = new AlertDialogManager(MainActivity.this);
			alertBuilder.showAlertDialog(getString(R.string.error),
					getString(R.string.no_bluetooth_device), 0);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) { // TODO odkomentowac , WORKAROUND
												// NA WSZYSTKIE ZWISY!!!!!!
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			if (mChatService == null) {
				// Intent searchIntent = null;
				// searchIntent = new Intent(this, BtDeviceListActivity.class);
				// startActivityForResult(searchIntent, REQUEST_CONNECT_DEVICE);

				mChatService = new BtService(MainActivity.this, mHandler);
			}
			setupMainMenu();
		}

		// setupMainMenu();
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already

			if (mChatService.getState() == BtService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	private void setupMainMenu() {
		mStartMeasureButton = (Button) findViewById(R.id.button1);
		mStartMeasureButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send to uc command to start measurement
				Intent measureResultsIntent = null;
				measureResultsIntent = new Intent(MainActivity.this,
						Results.class);

				String[] message = getResources().getStringArray(
						R.array.COMMANDS_TO_METER);
				Log.i(TAG, "BEFORE COMMAND");
				sendCommand(message[0]); // ODKOMENTOWAC
				Log.i(TAG, "AFTER COMMAND");
				startActivity(measureResultsIntent);
				// mChatService = new BtService(MainActivity.this, mHandler);

				// Initialize the buffer for outgoing messages
				// mOutStringBuffer = new StringBuffer("");
			}
		});
	}

	/*
	 * private void setupChat() { Log.d(TAG, "setupChat()");
	 * 
	 * // Initialize the array adapter for the conversation thread
	 * mConversationArrayAdapter = new ArrayAdapter<String>(this,
	 * R.layout.message); mConversationView = (ListView) findViewById(R.id.in);
	 * mConversationView.setAdapter(mConversationArrayAdapter);
	 * 
	 * // Initialize the compose field with a listener for the return key
	 * mOutEditText = (EditText) findViewById(R.id.edit_text_out); // tutaj bede
	 * wysylal komendy do miernika
	 * mOutEditText.setOnEditorActionListener(mWriteListener);
	 * 
	 * // Initialize the send button with a listener that for click events
	 * mSendButton = (Button) findViewById(R.id.button_send);
	 * mSendButton.setOnClickListener(new OnClickListener() { public void
	 * onClick(View v) { // Send a message using content of the edit text widget
	 * TextView view = (TextView) findViewById(R.id.edit_text_out); String
	 * message = view.getText().toString(); sendCommand(message); } });
	 * 
	 * // Initialize the BluetoothChatService to perform bluetooth connections
	 * mChatService = new BtService(this, mHandler);
	 * 
	 * // Initialize the buffer for outgoing messages mOutStringBuffer = new
	 * StringBuffer(""); }
	 */

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendCommand(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BtService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			// /mOutStringBuffer.setLength(0);
			// /mOutEditText.setText(mOutStringBuffer);
		}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendCommand(message);
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);

	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BtService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));

					// mConversationArrayAdapter.clear();
					break;
				case BtService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BtService.STATE_LISTEN:
				case BtService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				break;
			case MESSAGE_READ:  // odbieram dane tutaj 
				
				ReadResult receiver= new ReadResult();
				receiver= parseMessageFromHandler((String) msg.obj) ;
				if (D)
					Log.i(TAG, "READ_MESSAGE: " + receiver.valueFromData);
				readFullMessage = receiver.valueFromData;
				Intent i = new Intent(DATA_ACTION);
				i.putExtra(receiver.actionStringForBroadcastReceiver, receiver.valueFromData);
				sendBroadcast(i); // wysylam Broadcasta
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	// //////////////////////////////////////////////////////////////////////////////////////////
	private class dataService extends Service {
		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

		@Override
		public void onCreate() {
			super.onCreate();
			Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent();
			// / intent
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG)
					.show();
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////

	// /public class Broadcast
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				// and then search new devices immediately
				Intent searchIntent = null;
				searchIntent = new Intent(this, BtDeviceListActivity.class);
				startActivityForResult(searchIntent, REQUEST_CONNECT_DEVICE);

			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		}
	}

	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, BtDeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;

		}

		return false;
	}

	class ReadResult {

		String valueFromData;
		String actionStringForBroadcastReceiver;
	}
	;
	
	
	private ReadResult parseMessageFromHandler(String data) {
		
		ReadResult response = new ReadResult();
		response.valueFromData="";
		for (int i = 1; i < 6; i++) {
			response.valueFromData += data.charAt(i);
		}
		switch (data.charAt(0)) {
		case 'r':
			response.actionStringForBroadcastReceiver = "RMS";
			break;
		case 'a':
			response.actionStringForBroadcastReceiver = "AVG";
			break;
		case 'm':
			response.actionStringForBroadcastReceiver = "MAX";
			break;
		case 'n':
			response.actionStringForBroadcastReceiver = "MIN";
			break;
		}
		return response;
	}

}
