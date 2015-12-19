package kirkwood.nidaq;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import com.sun.jna.Pointer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

/**
 * This class demonstrates writing to a digital output port and reading
 * from an analogue in channel.
 *
 * THIS IS NOT COMPLETE
 */
public class NiDaqSimpleDemo {
	private static NiDaq daq = new NiDaq();
	
	public static void writeDigitalOut(byte[] data) throws NiDaqException {
		Pointer doTask = daq.createTask("Task");
		daq.createDOChan(doTask, "Dev1/port0/line0:3", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.startTask(doTask);
		daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
		daq.stopTask(doTask);
		daq.clearTask(doTask);
	}
	
	public static void readDigitalIn() throws NiDaqException {
		
	}
	
	public static void writeAnalogueOut() throws NiDaqException {
		
	}
	
	public static void readAnalogueIn() throws NiDaqException {
		Pointer aiTask = daq.createTask("AITask");
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
		
	}

	/************************ Threads ***********************************/
	
	/**
	 * Digital Out thread sends a signal on the digital out ports for a specified
	 * period of time and then times out.
	 */
	static class DigitalOutThread implements Runnable {
		public DigitalOutThread() {
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			try {
				writeDigitalOut(new byte[] { 1,1,1,1 });
				try {
					Thread.sleep(1000);
				} catch(Exception e) {}
				writeDigitalOut(new byte[] { 0,0,0,0 });
				
			} catch(NiDaqException e) {
				e.printStackTrace();
			}		
		}
	}
	
	/**
	 * AnalogInThread makes one read of the analogue input.
	 */
	static class AnalogInThread implements Runnable {
		public AnalogInThread() {
			new Thread(this).start();
		}
		public void run() {
			try {
				readAnalogueIn();
			} catch (NiDaqException e) {
				e.printStackTrace();
			}
		}
	}
	
	/************************** Main method *****************************/
	
	public static void main(String[] args) {
		try {
			writeDigitalOut(new byte[] { 1,1,1,1 });
		} catch(NiDaqException e) {}
		new AnalogInThread();
	}
}
