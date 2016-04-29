package kirkwood.nidaq;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import com.sun.jna.Pointer;


public class CabTestDigital {
	
	private NiDaq daq;
	Pointer diTask1 = null;
	Pointer diTask2 = null;
	Pointer diTask3 = null;
	Pointer diTask4 = null;
	
	public CabTestDigital() {
		daq = new NiDaq();

		try {
			
			while(true) {
				initDiTask();
				try {
					while(true) {
						long di1 = diTask(diTask1);
						long di2 = diTask(diTask2);
						long di3 = diTask(diTask3);
						long di4 = diTask(diTask4);
						System.out.println(di1 + "   " + di2 + "   " + di3 + "   " + di4);
					}
				} catch(NiDaqException e) {
					e.printStackTrace();
					closeDiTask();
					try { Thread.sleep(10); } catch(InterruptedException ex) {}
				}
			}
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}


	private void initDiTask() throws NiDaqException {
		diTask1 = daq.createTask("DITask1");
		diTask2 = daq.createTask("DITask2");
		diTask3 = daq.createTask("DITask3");
		diTask4 = daq.createTask("DITask4");
		daq.createDIChan(diTask1, "Dev1/port0/line0:3", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.createDIChan(diTask2, "Dev2/port0/line0:7", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.createDIChan(diTask3, "Dev2/port1/line0:7", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.createDIChan(diTask4, "Dev2/port2/line0:7", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.startTask(diTask1);
		daq.startTask(diTask2);
		daq.startTask(diTask3);
		daq.startTask(diTask4);
		
	}
	
	private void closeDiTask() {
		
		try {
			daq.stopTask(diTask1);
			daq.clearTask(diTask1);
			daq.stopTask(diTask2);
			daq.clearTask(diTask2);
			daq.stopTask(diTask3);
			daq.clearTask(diTask3);
			daq.stopTask(diTask4);
			daq.clearTask(diTask4);
		} catch (NiDaqException e) {
		}
	}
	
	private long diTask(Pointer task) throws NiDaqException {
		int inputBufferSize = 8;
		Integer read = new Integer(0);
		double[] buffer = new double[inputBufferSize];
		DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
		IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
		daq.readDigitalF64(task, -1, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, inputBufferSize, samplesPerChannelRead);
		return Double.doubleToRawLongBits(buffer[0]);
	}

	
	public static void main(String[] args) {
		new CabTestDigital();
	}
	
}

