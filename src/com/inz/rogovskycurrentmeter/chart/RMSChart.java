/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.inz.rogovskycurrentmeter.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * Multiple temperature demo chart.
 */
public class RMSChart extends AbstractBuildChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "RMS CHART";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */


  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Measured Current" };
    List<double[]> x = new ArrayList<double[]>();
   // for (int i = 0; i < titles.length; i++) {
      x.add(new double[] { 1, 50, 100, 150,200, 250, 340, 360, 390, 410,435, 500 });
   // }
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] {33.7, 33.6, 34.1,34.1, 34.0,33.8, 33.8,  33.8, 33.8, 33.7, 34.1, 33.7, 34.1, 34.0, 34.1, 33.7, 33.6, 34.1,34.1, 34.0,33.8, 33.8, 33.7,33.7, 33.5, 34.1,  34.1, 33.7,  34.4, 34.1, 34.0,34.1, 34.0,33.8, 33.8,  33.8, 33.8, 33.8, });
    int[] colors = new int[] { Color.RED, Color.RED };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(1);
    setRenderer(renderer, colors, styles);
    int length = renderer.getSeriesRendererCount();
  //  for (int i = 0; i < length; i++) {
  //    XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
  //    r.setLineWidth(5f);
  //   }
    
    XYSeries serie = new XYSeries(null);
    serie.addAnnotation("CURRENT", 12, 31);
    setChartSettings(renderer, "RMS Current", "Time [s]", "Current [A]", 0.5, 500, 30, 35, Color.LTGRAY, Color.LTGRAY);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomRate(1.05f);
    renderer.setLabelsColor(Color.WHITE);
    renderer.setXLabelsColor(Color.GREEN);
    renderer.setYLabelsColor(0, Color.BLUE);
    renderer.setYLabelsColor(1, Color.BLUE);
    
    
   // renderer.addYTextLabel(32, "PRESENT VALUE OF CURRENT:", 6);
   // renderer.addYTextLabel(31, "PRESENT VALUE OF CURRENT:");
    renderer.setLabelsTextSize(16);
   
//renderer.addTextLabel(100, "PRESENT VALUE OF CURRENT:");
   // renderer.setYTitle("Hours", 1);
    renderer.setYAxisAlign(Align.RIGHT, 1);
    renderer.setYLabelsAlign(Align.LEFT, 1);

    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);

   // values.clear();
  //  values.add(new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
  //  addXYSeries(dataset, new String[] { "" }, x, values, 1);
    Intent intent = ChartFactory.getTimeChartIntent(context, dataset, renderer, "AA",
        "CurrentMeter_v1.0");
    return intent;
  }



}
