package org.uob.bcrre.nidaq.access;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.uob.bcrre.nidaq.jna.Nicaiu;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Middle layer to call methods in the DLL wrapper.
 * You might prefer to call the DLL wrapper directly, but this class simplifies
 * the process by handling errors and simplifying the interface.
 */
public class NiDaq {

	/**
	 * Create Task
	 * @param taskName
	 * @return
	 */
	public Pointer createTask(String taskName) throws NiDaqException {
		byte[] btName = taskName.getBytes();
		PointerByReference taskHandleRef = new PointerByReference();
		checkError(Nicaiu.INSTANCE.DAQmxCreateTask(btName, taskHandleRef));
		Pointer taskHandle = taskHandleRef.getValue();
		return taskHandle;
	}
	
	/**
	 * Connect Digital out channels to task
	 */
	public void createDOChan(Pointer taskHandle, String lines, String nameToAssignToLines, int lineGrouping) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxCreateDOChan(taskHandle, lines.getBytes(), nameToAssignToLines.getBytes(), lineGrouping));
	}
	
	public void createAIVoltageChannel(Pointer taskHandle, String physicalChannel, String nameToAssignToChannel, int terminalConfig, double minVal, double maxVal, int units, String customScaleName) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxCreateAIVoltageChan(taskHandle, physicalChannel.getBytes(), nameToAssignToChannel.getBytes(), terminalConfig, minVal, maxVal, units, customScaleName == null ? null : customScaleName.getBytes()));
	}
	
	public void createAICurrentChannel(Pointer taskHandle, String physicalChannel, String nameToAssignToChannel, int terminalConfig, double minVal, double maxVal, int units, int shuntResistorLoc, double extShuntResistorVal, String customScaleName) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxCreateAICurrentChan(taskHandle, physicalChannel.getBytes(), nameToAssignToChannel.getBytes(), terminalConfig, minVal, maxVal, units, shuntResistorLoc, extShuntResistorVal, customScaleName.getBytes()));
	}
	
	public void startTask(Pointer taskHandle) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxStartTask(taskHandle));
	}
	
	public void writeDigitalLines(Pointer taskHandle, int numSamplesPerChannel, int autoStart, double timeOut, int dataLayout, byte[] data) throws NiDaqException {
		Pointer writeArray = new Memory(data.length);
		writeArray.write(0, data ,0, data.length);
		
		checkError(Nicaiu.INSTANCE.DAQmxWriteDigitalLines(  taskHandle, 
						/* numSampsPerChan*/   		numSamplesPerChannel,
						/* autoStart */        		new NativeLong(autoStart),
						/* timeout */          		timeOut,
						/* dataLayout */       		new NativeLong(dataLayout),
						/* writeArray */       		writeArray,
						/* sampsPerChanWritten */  	null,
						/* reserved */             	null ));
	}
	
	public void readAnalogF64(Pointer taskHandle, int numSampsPerChan, double timeout, int fillMode, DoubleBuffer readArray, int arraySizeInSamps, IntBuffer sampsPerChanRead) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxReadAnalogF64(taskHandle, numSampsPerChan, timeout, new NativeLong(fillMode), readArray, new NativeLong(arraySizeInSamps), sampsPerChanRead, null));
	}
	
	public void stopTask(Pointer taskHandle) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxStopTask(taskHandle));
	}
	
	public void clearTask(Pointer taskHandle) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxClearTask(taskHandle));
	}
	
	public void cfgSampClkTiming(Pointer taskHandle, String source, double rate, int activeEdge, int sampleMode, long sampsPerChan) throws NiDaqException{
		checkError(Nicaiu.INSTANCE.DAQmxCfgSampClkTiming(taskHandle, source.getBytes(), rate, activeEdge, sampleMode, sampsPerChan));
	}
	
	/**
	 * TODO Error codes
	 * @param errorCode
	 */
	private void checkError(int errorCode) throws NiDaqException {
		if(errorCode >= 0) {
			return;
		}
		throw new NiDaqException(errorCode);
	}

}
