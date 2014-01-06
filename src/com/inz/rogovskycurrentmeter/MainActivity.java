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

	// Name of the connected device
	private String mConnectedDeviceName = null;

	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	public static BtService mChatService = null;
	private AlertDialogManager alertBuilder;
	private ReadResult receiver = new ReadResult();

	private Button mStartMeasureButton, mConnectButton, mSlaveModeButton,
			mSDCardButton;

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
		if (mChatService != null) { // /tttttttttttttttttttttttttttttttttttttttttt
									// TODO
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
				sendCommand(message[0]); // start to measure
				Log.i(TAG, "AFTER COMMAND");
				startActivity(measureResultsIntent);
				// mChatService = new BtService(MainActivity.this, mHandler);

				// Initialize the buffer for outgoing messages
				// mOutStringBuffer = new StringBuffer("");
			}
		});
		mConnectButton = (Button) findViewById(R.id.connect);
		mConnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent serverIntent = null;
				serverIntent = new Intent(getApplicationContext(),
						BtDeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		});

		mSlaveModeButton = (Button) findViewById(R.id.slavemode);
		mSlaveModeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mSDCardButton = (Button) findViewById(R.id.sdcard);
		mSDCardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "-- ON PAUSE --");
		// /if (mChatService != null) mChatService.stop();
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
			Log.e(TAG, "-- ON DESTROY --");
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
			case MESSAGE_READ: // odbieram dane tutaj

				receiver = parseMessageFromHandler((String) msg.obj);
				if (D)
					Log.i(TAG, "READ_MESSAGE: " + receiver.valueFromData);
				readFullMessage = receiver.valueFromData;

				Intent i = new Intent(DATA_ACTION);
				i.putExtra(receiver.actionStringForBroadcastReceiver,
						receiver.valueFromData);
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

	class ReadResult {

		String valueFromData;
		String actionStringForBroadcastReceiver;
	};

	private ReadResult parseMessageFromHandler(String data) {

		ReadResult response = new ReadResult();
		response.valueFromData = "";
		for (int i = 1; i < 7; i++) {
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
		case 'f':
			response.actionStringForBroadcastReceiver = "FFT";
			break;
		}
		return response;
	}

}
