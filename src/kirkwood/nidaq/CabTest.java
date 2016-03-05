package kirkwood.nidaq;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import com.sun.jna.Pointer;

public class CabTest {

	private NiDaq daq;
	Pointer aiTask = null;
	
	public CabTest() {
		daq = new NiDaq();
//		writeDigitalOut(new byte[] { 1,1,1,1 });
		
		try {
			
			while(true) {
				initAiTask();
				try {
					while(true) {
						aiTask();
					}
				} catch(NiDaqException e) {
					closeAiTask();
					try { Thread.sleep(10); } catch(InterruptedException ex) {}
				}
			}
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Write the specified data to the digital out lines.
	 * @param data
	 * @throws NiDaqException
	 */
	public void writeDigitalOut(byte[] data) {
		try {
			Pointer doTask = daq.createTask("Task");
			daq.createDOChan(doTask, "Dev1/port0/line0:3", "", Nicaiu.DAQmx_Val_ChanForAllLines);
			daq.startTask(doTask);
			daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
			daq.stopTask(doTask);
			daq.clearTask(doTask);
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}

	private void initAiTask() throws NiDaqException {
		aiTask = daq.createTask("AITask2");
		daq.createAIVoltageChannel(aiTask, "Dev1/ai0:7", "", Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
		daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_ContSamps, 8);
		daq.startTask(aiTask);
	}
	
	private void closeAiTask() {
		
		try {
			daq.stopTask(aiTask);
			daq.clearTask(aiTask);
		} catch (NiDaqException e) {
		}
	}
	
	private void aiTask() throws NiDaqException {
		int inputBufferSize = 8;

		Integer read = new Integer(0);
		double[] buffer = new double[inputBufferSize];
		
		DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
		IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
		daq.readAnalogF64(aiTask, -1, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, inputBufferSize, samplesPerChannelRead);

		if(buffer[0] != 0) {
			for(int i=0; i<inputBufferSize; i++) {
				System.out.print(i + " = " + buffer[i]);
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		new CabTest();
	}
	
}
