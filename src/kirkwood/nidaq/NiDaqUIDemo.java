package kirkwood.nidaq;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.ui.NIChartPanel;

/**
 * This class is intended to offer a user interface with switch buttons
 * to switch digital out channels on and off. The UI will also include
 * graphs for the analogue input channels.
 * 
 * THIS IS NOT COMPLETE.
 */
public class NiDaqUIDemo extends JFrame implements Runnable {

	private NiDaq daq;

	private NIChartPanel[] charts;
	
	private int AI_CHANNEL_COUNT = 8;
	
	private boolean running = true;

	
	public NiDaqUIDemo(String deviceName, int digitalChannelCount, int analogueChannelCount) {
		initialiseUi();
		this.setVisible(true);
		startDaq();
	}
	
	private void initialiseUi() {
		this.setSize(640,400);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		charts = new NIChartPanel[AI_CHANNEL_COUNT];
		for(int i=0; i<AI_CHANNEL_COUNT; i++) {
			charts[i] = new NIChartPanel(i);
			tabbedPane.add("AI" + i, new JScrollPane(charts[i]));
		}
		this.add(tabbedPane);
		updateCharts(null);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				closeDaq();
				System.exit(0);
			}
		});
	}

	public void updateCharts(double[] data) {
		for(int i=0; i<charts.length; i++) {
			if(data != null && data.length > i) {
				charts[i].update(data[i]);
			} else {
				charts[i].update(0);
			}
		}
	}

	public void run() {
			while(running) {
				try {
					double[] data = NiDaqSimpleDemo.readAnalogueIn();
					if(data != null) {
						updateCharts(data);
					}
				} catch (NiDaqException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
//			NiDaqSimpleDemo.writeDigitalOut(new byte[] { 0,0,0,0 });
	}

	/**************************************** DAQ Interface methods ****************************************/

	
	private void startDaq() {
		System.out.print("Initialising...");
		running = true;
		try {
			NiDaqSimpleDemo.writeDigitalOut(new byte[] { 1,1,1,1 });
		} catch(NiDaqException e) {
			System.out.println("Failed.");
		}
		System.out.println("OK");
		
		new Thread(this).start();
	}
	
	
	private synchronized void closeDaq()  {
		running = false;
		System.out.println("DAQ closed");
	}
	
	public static void main(String[] args) {
		new NiDaqUIDemo("Dev1", 4, 8);
	}
	
}
