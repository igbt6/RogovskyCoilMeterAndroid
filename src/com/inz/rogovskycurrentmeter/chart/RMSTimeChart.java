package com.inz.rogovskycurrentmeter.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.inz.rogovskycurrentmeter.MainActivity;
import com.inz.rogovskycurrentmeter.R;
import com.inz.rogovskycurrentmeter.Results;
import com.inz.rogovskycurrentmeter.Results.MyDataReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class RMSTimeChart extends Activity {

	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;
	private GraphicalView mChartView;
	private XYSeries mTimeSerie;

	// a few variables that describe all the sizes of chart that vary during
	// measurement
	private double xAxisMin;
	private double xAxisMax;
	private double yAxisMin;
	private double yAxisMax;
	private String rmsData;

	// chart container
	private LinearLayout layout;
	
	MyDataReceiver myData;
	IntentFilter intentDataFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.rms_time_chart);

		layout = (LinearLayout) findViewById(R.id.chart);
		rmsData="0";
		myData =new MyDataReceiver();   
	    intentDataFilter = new IntentFilter(MainActivity.DATA_ACTION); //for broadcast receiver
		double[] x = { 1, 50, 100, 150, 200, 250, 340, 360, 390, 410, 435, 500 };
		Date dateValue = new Date();
		// for (int i = 0; i < titles.length; i++) {
		// x.add(new double[] { 1, 50, 100, 150,200, 250, 340, 360, 390,
		// 410,435, 500 });
		// }
		double[] yValues = { 33.7, 33.6, 34.1, 34.1, 34.0, 33.8, 33.8, 33.8,
				33.8, 33.7, 34.1, 33.7, 34.1, 34.0, 34.1, 33.7, 33.6, 34.1,
				34.1, 34.0, 33.8, 33.8, 33.7, 33.7, 33.5, 34.1, 34.1, 33.7,
				34.4, 34.1, 34.0, 34.1, 34.0, 33.8, 33.8, 33.8, 33.8, 33.8, };
		// values.add(new double[] {33.7, 33.6, 34.1,34.1, 34.0,33.8, 33.8,
		// 33.8, 33.8, 33.7, 34.1, 33.7, 34.1, 34.0, 34.1, 33.7, 33.6,
		// 34.1,34.1, 34.0,33.8, 33.8, 33.7,33.7, 33.5, 34.1, 34.1, 33.7, 34.4,
		// 34.1, 34.0,34.1, 34.0,33.8, 33.8, 33.8, 33.8, 33.8, });

		mRenderer = new XYMultipleSeriesRenderer();
		mDataset = new XYMultipleSeriesDataset();
		xAxisMin = 0;
		xAxisMax = 500;
		yAxisMin = 0;
		yAxisMax = 20;

		setChartSettings(mRenderer, "RMS Current", "Time [s]", "Current [A]",
				xAxisMin, xAxisMax, yAxisMin, yAxisMax, Color.LTGRAY,
				Color.LTGRAY);
		mRenderer.setXLabels(12);
		mRenderer.setYLabels(10);
		mRenderer.setShowGrid(true);

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPanLimits(new double[] { 0, 10000, 0, 100 });
		mRenderer.setZoomLimits(new double[] { 0, 10000, 0, 100 });
		mRenderer.setZoomRate(10);
		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setXLabelsColor(Color.GREEN);
		mRenderer.setYLabelsColor(0, Color.BLUE);
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		//mRenderer.setPointSize(5f);
		mRenderer.setInScroll(true);
		mRenderer.setMargins(new int[] { 20, 30, 15, 20 });

		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.RED);
		//r.setPointStyle(PointStyle.CIRCLE);
		//r.setFillPoints(true);
		r.setDisplayChartValues(true);
		r.setLineWidth(1);
		mRenderer.addSeriesRenderer(r);
		mRenderer.setClickEnabled(true);
		mRenderer.setSelectableBuffer(20);
		mRenderer.setPanEnabled(true);
		mRenderer.setPanEnabled(true,true);
		mRenderer.setZoomEnabled(true,true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setLabelsTextSize(16);
		mRenderer.setAntialiasing(false);

		mRenderer.setXLabelsAlign(Align.LEFT);
		mRenderer.setYAxisAlign(Align.LEFT, 0);
		mRenderer.setYLabelsAlign(Align.LEFT, 0);

		// addXYSerie( mDataset,"CurrentRMSValue",x,yValues,0);
		// addRealTimeXYSerie()
		// fillData();
		// addDateXYSerie(mDataset,"CurrentRMSValue",dateValue,values);
		mTimeSerie = new XYSeries("VALUE");
		// mTimeSerie.add(10, 10);
		mDataset.addSeries(mTimeSerie);
		mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
		// "hh:mm:ss");

		layout.addView(mChartView);
		new RMSChartTask().execute();
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
		registerReceiver(myData, intentDataFilter);
	}
	

	
	@Override
	protected void onDestroy(){
		super.onResume();
		unregisterReceiver(myData);
	}

	/*
	 * private void fillData() { long value = new Date().getTime() - 3 *
	 * TimeChart.DAY; for (int i = 0; i < 100; i++) { time_series.add(new
	 * Date(value + i * TimeChart.DAY / 4), i); } }
	 */

	private void addXYSerie(XYMultipleSeriesDataset dataset, String title,
			double[] xValues, double[] yValues, int scale) {

		mTimeSerie = new XYSeries(title);

		mTimeSerie.addAnnotation("CURRENT", 12, 31);

		double[] xV = xValues;
		double[] yV = yValues;
		int seriesLength = xV.length;
		for (int k = 0; k < seriesLength; k++) {
			mTimeSerie.add(xV[k], yV[k]);
		}
		dataset.addSeries(mTimeSerie);

	}

	private void addRealTimeXYSerie(XYSeries serie, double xValue, double yValue) {

		serie.add(xValue, yValue);

	}

	private void addDateXYSerie(XYMultipleSeriesDataset dataset, String title,
			Date xValues, double[] yValues) {

		TimeSeries serie = new TimeSeries(title);
		Date/* [] */xV = xValues;
		double[] yV = yValues;
		int timeLength = 10;// xV.length;
		for (int k = 0; k < timeLength; k++) {
			serie.add(xV/* [k] */, yV[k]);
		}
		dataset.addSeries(serie);

	}

	private void adjustAxisLengths(double xLastValue ,double yLastValue) {

		if (xLastValue > xAxisMax) {

			if (((int)xAxisMax / 1000) > 0) {
				
				Log.i("xMAXAxis", Double.toString(xAxisMax / 1000));
				xAxisMax += 1000;
				xAxisMin += 100;
			} else if (((int)xAxisMax / 100) > 0) {

				xAxisMax += 100;
				xAxisMin += 100;
			} else {
				xAxisMax += 10;
				xAxisMin += 10;
			}
		}
		if (yLastValue > yAxisMax) {
			yAxisMax += yLastValue + 1;
		}
		if (yLastValue < yAxisMin) {
			yAxisMin = yLastValue - 1;
		}
		mRenderer.setXAxisMax(xAxisMax);
		mRenderer.setXAxisMin(xAxisMin);
		mRenderer.setYAxisMax(yAxisMax);
		mRenderer.setYAxisMin(yAxisMin);
	}

	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param renderer
	 *            the renderer to set the properties to
	 * @param title
	 *            the chart title
	 * @param xTitle
	 *            the title for the X axis
	 * @param yTitle
	 *            the title for the Y axis
	 * @param xMin
	 *            the minimum value on the X axis
	 * @param xMax
	 *            the maximum value on the X axis
	 * @param yMin
	 *            the minimum value on the Y axis
	 * @param yMax
	 *            the maximum value on the Y axis
	 * @param axesColor
	 *            the axes color
	 * @param labelsColor
	 *            the labels color
	 */
	private void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	private class RMSChartTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			double i = 0;

			try {

				while(true){
					String[] values = new String[2];
					Random r = new Random();
					// double Current =r.nextDouble()+r.nextInt(4)+30;

					values[0] = Double.toString(i);
					// /values[1]=Double.toString(Current);

					publishProgress(values);
					Thread.sleep(20);
					i++;
				}// while (i < 500);
			} catch (Exception e) {

				e.printStackTrace();
				Log.d("ERROR", "ASYNC_TASK");
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {

			super.onProgressUpdate(values);
			// addRealTimeXYSerie(mTimeSerie,Double.parseDouble(values[0]),Double.parseDouble(values[1]));
			//Log.d("ASYNC_TASK" , "BFR_ADDED" );
			double yValue = Double.parseDouble(rmsData);
			double xValue = Double.parseDouble(values[0]);
			mTimeSerie.add(xValue, yValue);
			adjustAxisLengths(xValue,yValue);
			// Log.d("ASYNC_TASK" , "AFTER_ADDED" );
			mChartView.repaint();
		}

	}

	public class MyDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context , Intent i){
			
			rmsData= i.getStringExtra("DATA");
	                      
	        //abortBroadcast(); for sending ordererd broadcasts
		}
	}
}