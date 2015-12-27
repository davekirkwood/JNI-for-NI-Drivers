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
 *  * This class is developed to demonstrate the NI-DAQ functions with a NI-Daq USB-6000
 * device. The hardware is demonstrated by connecting the digital output pins to the
 * analogue input pins and observing the output of the console.
 * 
 * The demo may well work with other National Instruments DAQ devices, but it is hard
 * coded to treat the device as Dev1, with 4 digital out lines and 8 analog in lines.
 */
public class NiDaqSimpleDemo {

	/**
	 * NiDaq middle layer to call NiDaq function.
	 */
	private static NiDaq daq = new NiDaq();
	
	/**
	 * Write the specified data to the digital out lines.
	 * @param data
	 * @throws NiDaqException
	 */
	public static void writeDigitalOut(byte[] data) throws NiDaqException {
		Pointer doTask = daq.createTask("Task");
		daq.createDOChan(doTask, "Dev1/port0", "", Nicaiu.DAQmx_Val_ChanForAllLines);
		daq.startTask(doTask);
		daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
		daq.stopTask(doTask);
		daq.clearTask(doTask);
	}
	
	
	public static double[] readAnalogueIn(int inputBufferSize) throws NiDaqException {
		Pointer aiTask = null;
		try {
			aiTask = daq.createTask("AITask");
			daq.createAIVoltageChannel(aiTask, "Dev1/ai0:7", "", Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
			daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, 8);
			daq.startTask(aiTask);
			Integer read = new Integer(0);
			double[] buffer = new double[inputBufferSize];
			
			DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
			IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
			daq.readAnalogF64(aiTask, -1, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, inputBufferSize, samplesPerChannelRead);
	
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

	/**
	 * Simple demo main method. Writes to the digital lines and then loops, reading
	 * the analog input lines.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			writeDigitalOut(new byte[] { 1,1,1,1 });
			while(true) {
				try {
					double[] data = readAnalogueIn(8);
					if(data != null) {
						for(int i=0; i<data.length; i++) {
							System.out.println("AI" + i + " = " + (data[i] < 0.01 ? "" : data[i]));
						}
					} else {
						System.out.println("Error");
					}
				} catch(NiDaqException e) {
					e.printStackTrace();
				}
			}
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
}
