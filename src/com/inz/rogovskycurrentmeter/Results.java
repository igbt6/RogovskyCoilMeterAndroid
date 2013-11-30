package com.inz.rogovskycurrentmeter;

import com.inz.rogovskycurrentmeter.chart.FFTChart;
import com.inz.rogovskycurrentmeter.chart.RMSTimeChart;



import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

	
	
	// Key names received from the BluetoothService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Name of the connected device
	private String mConnectedDeviceName = null;

	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	private Handler rmsHandler = new Handler();
	
	private TextView rmsValue, avgValue, minValue,maxValue;
	private Button rmsChart;
	private Button fftChart;
	
	private class DataResults{	private String rmsData; // used in Broadcast receiver
	
	public DataResults() {
		this.setRmsData("0");
		this.setAvgData("0");
		this.setMinData("0");  
		this.setMaxData("0");
	}
	private String avgData; // used in Broadcast receiver
	private String maxData; // used in Broadcast receiver
	private String minData; // used in Broadcast receiver
	private String fftData; // used in Broadcast receiver
	 //setters getters  TODO for FFT
	public void setRmsData(String rmsData){this.rmsData=rmsData;}
	public void  setAvgData(String avgData){this.avgData=avgData;}
	public void setMinData(String minData){this.minData=minData;}
	public void setMaxData(String maxData){this.maxData=maxData;}
	
	public String getRmsData(){return rmsData;}
	public String getAvgData(){return avgData;}
	public String getMinData(){return minData;}
	public String getMaxData(){return maxData;}
	
	
};
	
   DataResults allDataResults = new DataResults();
   public static double rmsResult =0;
   MyDataReceiver myData;
   IntentFilter intentDataFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
	/*	
		Context ctx = getApplicationContext();
		Resources res = ctx.getResources();
		String[] names = res.getStringArray(R.array.data_names);
		String[] values = res.getStringArray(R.array.data_values);
		setListAdapter(new DataAdapter(ctx, R.layout.results_items, names, values));
		*/
	//	 ListView listView = new ListView(ctx) ;
		// listView.setAdapter(new DataAdapter(ctx, R.layout.results_items, names, values));
	      //listView.setItemsCanFocus(false);
	   
		
		
		
		rmsValue = (TextView) findViewById(R.id.rmsValue);
		avgValue = (TextView) findViewById(R.id.avgValue);
		minValue = (TextView) findViewById(R.id.minValue);
		maxValue = (TextView) findViewById(R.id.maxValue);
		rmsChart = (Button) findViewById(R.id.btnRmsChart);
		fftChart = (Button) findViewById(R.id.btnFFTChart);
       
	
		//ListView resultsList = (ListView) findViewById(R.id.);
		
		
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
	
		myData =new MyDataReceiver();    // new broadcast receiver for result data
        intentDataFilter = new IntentFilter(MainActivity.DATA_ACTION);
	   
        rmsValue.setText(allDataResults.getRmsData());
		avgValue.setText(allDataResults.getAvgData());
		minValue.setText(allDataResults.getMinData());
		maxValue.setText(allDataResults.getMaxData());
		
		
		//rmsValue.setText(MainActivity.readFullMessage);     //TODO odkomentowac WORKAROUND NA ZWISYYYYYY!!!

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
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							rmsHandler.post(new Runnable() {

								@Override
								public void run() {
									rmsValue.setText(allDataResults.getRmsData());
									//avgValue.setText(allDataResults.getAvgData());
									//minValue.setText(allDataResults.getMinData());
									//maxValue.setText(allDataResults.getMaxData());
									
								}
							});

						}

					}
				}).start();
				
				
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
		registerReceiver(myData, intentDataFilter); 
	}
	

	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(myData); 
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
			// Get the message bytes and tell the BluetoothService to write
			byte[] send = message.getBytes();
			MainActivity.mChatService.write(send);
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
	
	public class MyDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context , Intent i){
			allDataResults.setRmsData(i.getStringExtra("RMS"));
			///allDataResults.setAvgData(i.getStringExtra("AVG"));   
			///allDataResults.setMinData(i.getStringExtra("MIN"));   
			///allDataResults.setMaxData(i.getStringExtra("MAX"));   
	        //abortBroadcast();
		}
	}
	

}
