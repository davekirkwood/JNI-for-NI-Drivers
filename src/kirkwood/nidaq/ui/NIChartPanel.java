package kirkwood.nidaq.ui;

import java.awt.Color;
import java.awt.Font;

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
