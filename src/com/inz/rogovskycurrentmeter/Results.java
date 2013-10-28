package com.inz.rogovskycurrentmeter;

import com.inz.rogovskycurrentmeter.chart.FFTChart;
import com.inz.rogovskycurrentmeter.chart.RMSChart;
import com.inz.rogovskycurrentmeter.chart.RMSTimeChart;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Results extends Activity {
	public Results() {
		// TODO Auto-generated constructor stub
	}

	// For Debugging
	private static final String TAG = "CurrentMeterV1.0";
	private static final boolean D = true;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Name of the connected device
	private String mConnectedDeviceName = null;

	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	private Handler rmsHandler = new Handler();
	// private BtService mBtService = null;

	private TextView rmsValue;
	private Button rmsChart;
	private Button fftChart;
	
	public static double rmsResult =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		rmsValue = (TextView) findViewById(R.id.rmsValue);

		rmsChart = (Button) findViewById(R.id.btnRmsChart);
		fftChart = (Button) findViewById(R.id.btnFFTChart);

		View.OnClickListener buttonHandler = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent chartIntent = null;
				switch (v.getId()) {

				case R.id.btnRmsChart:
					//RMSChart rmsChartBuilder = new RMSChart();
				//	intent = rmsChartBuilder.execute(getApplicationContext());
					chartIntent = new Intent(Results.this, RMSTimeChart.class);

					startActivity(chartIntent);
					break;
				case R.id.btnFFTChart:
					FFTChart fftChartBuilder = new FFTChart();
					chartIntent = fftChartBuilder.execute(getApplicationContext());
					startActivity(chartIntent);
					break;
				}

			}
		};
		rmsChart.setOnClickListener(buttonHandler);
		fftChart.setOnClickListener(buttonHandler);

		rmsValue.setText("2.54[V]");
		rmsValue.setText(MainActivity.readFullMessage);     //TODO odkomentowac WORKAROUND NA ZWISYYYYYY!!!

		// /rmsValue.setText(getIntent().getStringExtra("DATA"));
		// mBtService = new BtService(Results.this, mHandler);
		// MainActivity.mChatService=new BtService(Results.this, mHandler);
		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
		sendCommand("FUMIKO19");

		new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							rmsHandler.post(new Runnable() {

								@Override
								public void run() {
									rmsValue.setText(MainActivity.readFullMessage);
									if(MainActivity.readFullMessage!=null){
									rmsResult=Double.parseDouble(MainActivity.readFullMessage); // her I send to RMS Chart // RMSTimeChart Activity
									}if (D)
										Log.i(TAG, Double.toString(rmsResult));
								}
							});

						}

					}
				}).start();
		
		
		
		

	}

	private void sendCommand(String message) {
		// Check that we're actually connected before trying anything
		if (MainActivity.mChatService.getState() != BtService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			MainActivity.mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			// /mOutStringBuffer.setLength(0);
			// /mOutEditText.setText(mOutStringBuffer);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BtService.STATE_CONNECTED:

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
				// /mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				rmsValue.setText(mConnectedDeviceName + ":  " + readMessage);
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

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);

	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

}
