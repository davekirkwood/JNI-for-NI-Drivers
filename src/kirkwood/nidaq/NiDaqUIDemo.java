package kirkwood.nidaq;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import com.sun.jna.Pointer;

/**
 * This class is intended to offer a user interface with switch buttons
 * to switch digital out channels on and off. The UI will also include
 * graphs for the analogue input channels.
 * 
 * THIS IS NOT COMPLETE.
 */
public class NiDaqUIDemo extends JFrame {

	private NiDaq daq;
	
	
	/**
	 * Digital out task
	 */
	private Pointer doTask;
	
	/**
	 * Analog in task
	 */
	private Pointer aiTask;
	
	private byte[] digitalOutData;
	
	private Pointer diTask;
	
	private boolean running;
	
	private AnalogueChannelsPanel analogChannelsPanel;
	
	private class DigitalChannelsPanel extends JPanel {
		private JButton[] switches;
		private JLabel[] statusLabels;
		private boolean[] toggles;
		public DigitalChannelsPanel(int channelCount) {
			this.setLayout(new GridLayout(2,channelCount));
			switches = new JButton[channelCount];
			statusLabels = new JLabel[channelCount];
			toggles = new boolean[channelCount];
			for(int i=0; i<channelCount; i++) {
				switches[i] = new JButton("DO:" + i);
				this.add(switches[i]);
				final int index = i;
				switches[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						toggle(index);
					}
				});
				toggles[i] = false;
			}
			for(int i=0; i<channelCount; i++) {
				statusLabels[i] = new JLabel();
				this.add(statusLabels[i]);
			}
			setStatusLabels();
		}
		private void toggle(int index) {
			toggles[index] = !toggles[index];
			setStatusLabels();
		}
		private void setStatusLabels() {
			byte[] daqData = new byte[toggles.length];
			for(int i=0; i<toggles.length; i++) {
				statusLabels[i].setText(toggles[i] ? "ON" : "OFF");
				daqData[i] = (byte)(toggles[i] ? 1 : 0);
			}
			writeDOData(daqData);
		}
	}

	private class AnalogueChannelsPanel extends JPanel {
		private JLabel[] statusLabels;
		public AnalogueChannelsPanel(int channelCount) {
			this.setLayout(new GridLayout(1,channelCount));
			statusLabels = new JLabel[channelCount];
			for(int i=0; i<channelCount; i++) {
				statusLabels[i] = new JLabel();
				this.add(statusLabels[i]);
			}
			setStatusLabels(null);
		}
		public void setStatusLabels(double[] data) {
			for(int i=0; i<statusLabels.length; i++) {
				if(data != null && data.length > i) {
					statusLabels[i].setText(String.valueOf(data[i]));
				} else {
					statusLabels[i].setText("?");
				}
			}
		}
	}

	
	public NiDaqUIDemo(String deviceName, int digitalChannelCount, int analogueChannelCount) {

//		digitalOutData = new byte[digitalChannelCount];
//		for(int i=0; i<digitalChannelCount; i++) {
//			digitalOutData[i] = 0;
//		}
//		
		initialiseDaq();
		initialiseUi();
		this.setVisible(true);
		startDaq();
	}
	
	private void initialiseUi() {
		this.setSize(640,400);
//		this.setLayout(new GridLaFlowLayout());
//		for(int i=0;i<digitalOutData.length; i++) {
//			this.add(createDoButton(i));
//		}
	
		this.setLayout(new GridLayout(2,1));
		this.add(new DigitalChannelsPanel(4));
		analogChannelsPanel = new AnalogueChannelsPanel(8);
		this.add(analogChannelsPanel);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				closeDaq();
				System.exit(0);
			}
		});
	}
	
	/**
	 * AnalogInThread makes one read of the analogue input.
	 */
	class AnalogInThread implements Runnable {
		public AnalogInThread() {
			new Thread(this).start();
		}
		public void run() {
			try {
				while(running) {
					readAIData();
					Thread.sleep(3000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	/**************************************** DAQ Interface methods ****************************************/
	
	private void initialiseDaq() {
		try {
			daq = new NiDaq();
			
			System.out.print("Initialising digital out...");
			doTask = daq.createTask("Task1");
			daq.createDOChan(doTask, "Dev1/port0/line0:3", "", Nicaiu.DAQmx_Val_ChanForAllLines);
			daq.startTask(doTask);
			System.out.println("Done.");
			
//			aiTask = doTask;
//			System.out.print("Initialising analog in...");
//			aiTask = daq.createTask("AITask");
//			daq.createAIVoltageChannel(aiTask, "Dev1/ai0:0", "", Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
//			daq.cfgSampClkTiming(aiTask, "", 10000.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, 1000);
//			daq.startTask(aiTask);
//			System.out.println("Done.");
						
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
	
	private void startDaq() {
		System.out.print("Initialised read thread...");
		running = true;
		new AnalogInThread();
		System.out.println("Done.");
	}

	private void writeDOData(byte[] data) {
		try {
			daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void readAIData() {
//		Integer read = new Integer(0);
//		double[] buffer = new double[1];
//		DoubleBuffer inputBuffer =DoubleBuffer.wrap(buffer);
//		IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
//		try {
//			daq.readAnalogF64(aiTask, 1000, 10.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, 1000, samplesPerChannelRead);
//			for(int i=0; i<buffer.length; i++) {
//				System.out.println(buffer[i]);
//			}
//			analogChannelsPanel.setStatusLabels(buffer);
//		} catch(NiDaqException e) {
//			e.printStackTrace();
//		}

		Pointer aiTask;
		try {
			aiTask = daq.createTask("AITask");
	//		daq.createAICurrentChannel(aiTask, "Dev1/ai0:0", "", Nicaiu.DAQmx_Val_Cfg_Default, 0.0, 0.02, Nicaiu.DAQmx_Val_Amps, Nicaiu.DAQmx_Val_Default, 249.0, "");
			daq.createAIVoltageChannel(aiTask, "Dev1/ai0:0", "", Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
			daq.cfgSampClkTiming(aiTask, "", 10000.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, 1000);
			
			daq.startTask(aiTask);
	
			Integer read = new Integer(0);
			double[] buffer = new double[1000];
			
			DoubleBuffer inputBuffer =DoubleBuffer.wrap(buffer);
			IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
			daq.readAnalogF64(aiTask, 1000, 10.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, 1000, samplesPerChannelRead);
			
			daq.stopTask(aiTask);
			daq.clearTask(aiTask);
			
			System.out.println("Acquired " + read + " points.");
	
			for(int i=0; i<buffer.length; i++) {
				System.out.println(buffer[i]);
			}

			analogChannelsPanel.setStatusLabels(buffer);
			
		} catch (NiDaqException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private synchronized void closeDaq()  {
		try {
			System.out.print("Closing digital out...");
			daq.stopTask(doTask);
			daq.clearTask(doTask);
			System.out.println("Done.");

//			System.out.print("Closing analog in...");
//			daq.stopTask(aiTask);
//			daq.clearTask(aiTask);
//			System.out.println("Done.");

			System.out.println("DAQ closed");
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new NiDaqUIDemo("Dev1", 4, 8);
	}
	
}
