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

package kirkwood.nidaq;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.ui.NIChartPanel;

/**
 * This demo contains a graph (using JFreeChart charts) that outputs a rolling
 * view of the output of the analogue input lines.
 * 
 * This class is developed to demonstrate the NI-DAQ functions with a NI-Daq USB-6000
 * device. The hardware is demonstrated by connecting the digital output pins to the
 * analogue input pins and observing the output of the console.
 * 
 * The demo may well work with other National Instruments DAQ devices, but it is hard
 * coded to treat the device as Dev1, with 4 digital out lines and 8 analog in lines.
 */
public class NiDaqUIDemo extends JFrame implements Runnable {

	/**
	 * Default Serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Array of JFreeChart charts in line order.
	 */
	private NIChartPanel[] charts;
	
	/**
	 * Number of analog input channels.
	 */
	private int AI_CHANNEL_COUNT = 8;
	
	/**
	 * Simple flag to indicate the thread should continue running. This
	 * is set to false when the window closes, causing the 
	 */
	private boolean running = true;

	/**
	 * Synchronised getter method for the running flag.
	 * @return
	 */
	public synchronized boolean isRunning() {
		return running;
	}

	/**
	 * Synchronised setter method for the running flag.
	 * @param running
	 */
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	
	/**
	 * Constructor method. Currently this class only demonstrates the simple use of the
	 * NI-Daq USB-6000 device. TODO Add device configuration parameters, such as number of lines,
	 * device name, etc.
	 */
	public NiDaqUIDemo() {
		initialiseUi();
		this.setVisible(true);
		startDaq();
	}
	
	/**
	 * Initialise the graphics.
	 */
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

	/**
	 * Update the charts with the array of data read from the DAQ device.
	 * @param data
	 */
	public void updateCharts(double[] data) {
		for(int i=0; i<charts.length; i++) {
			if(data != null && data.length > i) {
				charts[i].update(data[i]);
			} else {
				charts[i].update(0);
			}
		}
	}

	/**
	 * Thread run method, loops until the running flag is cleared (by the user
	 * closing the window). The method reads from the analog in channels of the DAQ
	 * device and updates the UI charts.
	 */
	public void run() {
			while(isRunning()) {
				try {
					double[] data = NiDaqSimpleDemo.readAnalogueIn(8);
					if(data != null) {
						updateCharts(data);
					}
				} catch (NiDaqException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			NiDaqSimpleDemo.writeDigitalOut(new byte[] { 0,0,0,0 });
	}

	/**
	 * Start the DAQ by writing to the digital out lines and starting the
	 * thread to read the analog in lines.
	 */
	private void startDaq() {
		System.out.print("Initialising...");
		setRunning(true);
		try {
			NiDaqSimpleDemo.writeDigitalOut(new byte[] { 1,1,1,1 });
		} catch(NiDaqException e) {
			System.out.println("Failed.");
		}
		System.out.println("OK");
		
		new Thread(this).start();
	}

	/**
	 * Close the DAQ by setting the running flag to false.
	 */
	private synchronized void closeDaq()  {
		setRunning(false);
		System.out.println("DAQ closed");
	}
	
	/**
	 * Main method to demonstrate the use of a NI-DAQ USB-6000 device. 
	 * @param args
	 */
	public static void main(String[] args) {
		new NiDaqUIDemo();
	}
	
}
