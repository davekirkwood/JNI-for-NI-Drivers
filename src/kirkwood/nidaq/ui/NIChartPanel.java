/**
 ********************************************************************* 
 * JNI4NI Java Native Interface wrapper for National Instruments DAQ.
 ********************************************************************* 
 * (c) 2015 David Kirkwood            Email: davekirkwood@hotmail.com 
 ********************************************************************* 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *    
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *    
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package kirkwood.nidaq.ui;

import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class NIChartPanel extends JPanel {
	
	private XYSeries series1;
	private JFreeChart chart;
	private XYPlot plot;
	private ChartPanel chartPanel;

	private int channelNum;
	
	private int t = 0;
	
	public NIChartPanel(int channelNum) {
		final XYDataset dataset = createDataset();
		chart = createChart(dataset);
		chartPanel = new ChartPanel(chart, true);
		this.add(chartPanel);
	}

	/**
	 * Creates a simple dataset.
	 * 
	 * @return a simple dataset.
	 */
	private XYDataset createDataset() {
		series1 = new XYSeries("Input");
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		return dataset;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the data for the chart.
	 * 
	 * @return a chart.
	 */
	private JFreeChart createChart(final XYDataset dataset) {
		chart = ChartFactory.createXYLineChart(String.valueOf(channelNum), // chart title
				"t", // x axis label
				"v", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, false, // include legend
				true, // tooltips
				false // urls
		);

		chart.setBackgroundPaint(Color.white);

		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);
		plot.getRangeAxis().setVisible(true);
		plot.getDomainAxis().setVisible(true);
		
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		plot.setRenderer(renderer);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		return chart;

	}

	public void update(double i) {
//		series1.clear();
		if(series1.getItemCount() > 50) {
			series1.remove(0);
		}
		series1.add(++t, i);
	}

}
