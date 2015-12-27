package kirkwood.nidaq;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import com.sun.jna.Pointer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

/**
 * This class demonstrates writing to a digital output port and reading
 * from analogue in channels.
 * 
 * THIS IS NOT COMPLETE.
 */
public class NiDaqSimpleDemo {

	private static NiDaq daq = new NiDaq();
	
	public static void writeDigitalOut(byte[] data) throws NiDaqException {
		Pointer doTask = daq.createTask("Task");
		daq.createDOChan(doTask, "Dev1/port0", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.startTask(doTask);
		daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
		daq.stopTask(doTask);
		daq.clearTask(doTask);
	}
	
	private static int INPUT_BUFFER_SIZE = 8;
	
	public static double[] readAnalogueIn() throws NiDaqException {
		Pointer aiTask = null;
		try {
			aiTask = daq.createTask("AITask");
			daq.createAIVoltageChannel(aiTask, "Dev1/ai0:7", "", Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
			daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, 8);
			daq.startTask(aiTask);
			Integer read = new Integer(0);
			double[] buffer = new double[INPUT_BUFFER_SIZE];
			
			DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
			IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
			daq.readAnalogF64(aiTask, 1, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, INPUT_BUFFER_SIZE, samplesPerChannelRead);
	
			daq.stopTask(aiTask);
			daq.clearTask(aiTask);
			return buffer;
			
		} catch(NiDaqException e) {
			try {
				daq.stopTask(aiTask);
				daq.clearTask(aiTask);
				return null;
			} catch(NiDaqException e2) {}
			throw(e);
		}

	}

	/************************** Main method *****************************/
	
	public static void main(String[] args) {
		try {
			writeDigitalOut(new byte[] { 1,1,1,1 });
			while(true) {
				try {
					double[] data = readAnalogueIn();
					if(data != null) {
						for(int i=0; i<data.length; i++) {
							System.out.println("AI" + i + " = " + (data[i] < 0.01 ? "" : data[i]));
						}
					} else {
						System.out.println("Error");
					}
				} catch(NiDaqException e) {
//					e.printStackTrace();
				}
			}
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
}
