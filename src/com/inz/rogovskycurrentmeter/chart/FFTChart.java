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

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Sales demo bar chart.
 */
public class FFTChart extends AbstractBuildChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "FFT CHART";
  }

 /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Data", "First harmonic of the signal : 50 [Hz]" };
    List<double[]> values = new ArrayList<double[]>();
    
    values.add(new double[] { 34.1, 6.5, 3.4, 1.8, 1.05, 0.6, 0.6, 0.6, 0.6, 0.6, 0.3, 0.2, 0.2, 0.2, 0.2, 0.19, 0.18,0.16, 0.16, 0.16, 0.16, 0.16, 0.13, 0.12,0.12,0.11,0.10,0.06,0.06,0.03,0.01,0.01,
        0.005, 0.003,0.003,0.003,0.001,0.001 });
    values.add(new double[] { 
       });
    int[] colors = new int[] { Color.RED, Color.GREEN };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    setChartSettings(renderer, "FFT", "Number of harmonics", "Signal amplitude", 0,
       30, 0, 50, Color.GRAY, Color.LTGRAY);
    renderer.setShowGrid(true);
    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(0).setChartValuesSpacing(10);
    renderer.getSeriesRendererAt(0).setChartValuesTextSize(15);
 
   
    renderer.setScale(50f);
   // renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(50);
   // renderer.getSeriesRendererAt(0).
   // renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
 
    renderer.setXLabels(30);

    renderer.setBarSpacing(50);
    renderer.setYLabels(10);

    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setPanEnabled(true, true);
    renderer.setZoomEnabled(true);
    renderer.setZoomButtonsVisible(true);
    renderer.setZoomRate(1.5f);
    renderer.setBarSpacing(2f);
    return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
        Type.STACKED,"CurrentMeter_v1.0");
  }

}
