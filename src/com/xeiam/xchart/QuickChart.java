/**
 * Copyright 2011 - 2014 Xeiam LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xeiam.xchart;

import java.util.Collection;

/**
 * A convenience class for making Charts with one line of code
 * 
 * @author timmolter
 */
public final class QuickChart {

  private final static int WIDTH = 600;
  private final static int HEIGHT = 400;

  /**
   * private Constructor
   */
  private QuickChart() {

  }

  /**
   * Creates a Chart with default style
   * 
   * @param chartTitle the Chart title
   * @param xTitle The X-Axis title
   * @param yTitle The Y-Axis title
   * @param seriesName The name of the series
   * @param xData An array containing the X-Axis data
   * @param yData An array containing Y-Axis data
   * @return a Chart Object
   */
  public static Chart getChart(String chartTitle, String xTitle, String yTitle, String seriesName, double[] xData, double[] yData) {

    double[][] yData2d = { yData };
    if (seriesName == null) {
      return getChart(chartTitle, xTitle, yTitle, null, xData, yData2d);
    }
    else {
      return getChart(chartTitle, xTitle, yTitle, new String[] { seriesName }, xData, yData2d);
    }
  }

  /**
   * Creates a Chart with multiple Series for the same X-Axis data with default style
   * 
   * @param chartTitle the Chart title
   * @param xTitle The X-Axis title
   * @param yTitle The Y-Axis title
   * @param seriesNames An array of the name of the multiple series
   * @param xData An array containing the X-Axis data
   * @param yData An array of double arrays containing multiple Y-Axis data
   * @return a Chart Object
   */
  public static Chart getChart(String chartTitle, String xTitle, String yTitle, String[] seriesNames, double[] xData, double[][] yData) {

    // Create Chart
    Chart chart = new Chart(WIDTH, HEIGHT);

    // Customize Chart
    chart.setChartTitle(chartTitle);
    chart.setXAxisTitle(xTitle);
    chart.setYAxisTitle(yTitle);

    // Series
    for (int i = 0; i < yData.length; i++) {
      Series series;
      if (seriesNames != null) {
        series = chart.addSeries(seriesNames[i], xData, yData[i]);
      }
      else {
        chart.getStyleManager().setLegendVisible(false);
        series = chart.addSeries(" " + i, xData, yData[i]);
      }
      series.setMarker(SeriesMarker.NONE);
    }

    return chart;
  }

  /**
   * Creates a Chart with default style
   * 
   * @param chartTitle the Chart title
   * @param xTitle The X-Axis title
   * @param yTitle The Y-Axis title
   * @param seriesName The name of the series
   * @param xData A Collection containing the X-Axis data
   * @param yData A Collection containing Y-Axis data
   * @return a Chart Object
   */
  public static Chart getChart(String chartTitle, String xTitle, String yTitle, String seriesName, Collection<Number> xData, Collection<Number> yData) {

    // Create Chart
    Chart chart = new Chart(WIDTH, HEIGHT);

    // Customize Chart
    chart.setChartTitle(chartTitle);
    chart.setXAxisTitle(xTitle);
    chart.setYAxisTitle(yTitle);

    Series series = chart.addSeries(seriesName, xData, yData);
    series.setMarker(SeriesMarker.NONE);

    return chart;

  }

}
