package kirkwood.nidaq.access;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import kirkwood.nidaq.jna.Nicaiu;

/**
 * Middle layer to call methods in the DLL wrapper.
 * You might prefer to call the DLL wrapper directly, but this class simplifies
 * the process by handling errors and simplifying the interface.
 */
public class NiDaq {

	/**
	 * Creates a task . If you use this function to create a task, you must use DAQmxClearTask to destroy it. 
	 * If you use this function within a loop, NI-DAQmx creates a new task in each iteration of the loop. Use 
	 * the DAQmxClearTask function within the loop after you finish with the task to avoid allocating 
	 * unnecessary memory. 
	 * 
	 * @param taskName Name assigned to the task. Note: This name may be changed internally. If you are using
	 * the C API, call DAQmxGetTaskName to verify whether the name was changed during creation. If you are 
	 * using the CVI API, call DAQmxGetTaskAttribute with attribute ID DAQmx_Task_Name to verify the name change.
	 *  
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
	 * Creates channel(s) to generate digital signals and adds the channel(s) to the task you specify with 
	 * taskHandle. You can group digital lines into one digital channel or separate them into multiple digital
	 * channels. If you specify one or more entire ports in lines by using port physical channel names, you 
	 * cannot separate the ports into multiple channels. To separate ports into multiple channels, use this 
	 * function multiple times with a different port each time.
	 * 
	 * @param taskHandle TaskHandle The task to which to add the channels that this function creates.
	 * 
	 * @param lines The names of the digital lines used to create a virtual channel. You can specify a list or 
	 * range of lines. Specifying a port and no lines is the equivalent of specifying all the lines of that 
	 * port in order. Therefore, if you specify Dev1/port0 and port 0 has eight lines, this is expanded to Dev1/port0/line0:7.
	 * 
	 * @param nameToAssignToLines The name of the created virtual channel(s). If you create multiple virtual channels with 
	 * one call to this function, you can specify a list of names separated by commas. If you do not specify a name, NI-DAQmx 
	 * uses the physical channel name as the virtual channel name. If you specify your own names for nameToAssignToLines, you 
	 * must use the names when you refer to these channels in other NI-DAQmx functions.
	 * 
	 * @param lineGrouping Specifies whether to group digital lines into one or more virtual channels. If you specify 
	 * one or more entire ports in lines, you must set lineGrouping to DAQmx_Val_ChanForAllLines.
	 * DAQmx_Val_ChanPerLine One channel for each line 
	 * DAQmx_Val_ChanForAllLines One channel for all lines
	 *  
	 * @throws NiDaqException
	 */
	public void createDOChan(Pointer taskHandle, String lines, String nameToAssignToLines, int lineGrouping) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxCreateDOChan(taskHandle, lines.getBytes(), nameToAssignToLines.getBytes(), lineGrouping));
	}
	
	/**
	 * Creates channel(s) to measure voltage and adds the channel(s) to the task you specify with taskHandle. If your 
	 * measurement requires the use of internal excitation or you need the voltage to be scaled by excitation, call 
	 * DAQmxCreateAIVoltageChanWithExcit.
	 * 



Value  Description 
 
minVal  float64  
maxVal  float64  
units  int32 
Return Value
	 * 
	 * 
	 * 
	 * @param taskHandle The task to which to add the channels that this function creates.
	 * 
	 * @param physicalChannel The names of the physical channels to use to create virtual channels. You can specify a 
	 * list or range of physical channels.
	 * 
	 * @param nameToAssignToChannel The name(s) to assign to the created virtual channel(s). If you do not specify a name, 
	 * NI-DAQmx uses the physical channel name as the virtual channel name. If you specify your own names for 
	 * nameToAssignToChannel, you must use the names when you refer to these channels in other NI-DAQmx functions.
	 * If you create multiple virtual channels with one call to this function, you can specify a list of names separated 
	 * by commas. If you provide fewer names than the number of virtual channels you create, NI-DAQmx automatically assigns 
	 * names to the virtual channels.
	 * 
	 * @param terminalConfig The input terminal configuration for the channel.
	 * 		DAQmx_Val_Cfg_Default   At run time, NI-DAQmx chooses the default terminal configuration for the channel. 
	 * 		DAQmx_Val_RSE   Referenced single-ended mode  
	 * 		DAQmx_Val_NRSE   Non-referenced single-ended mode  
	 * 		DAQmx_Val_Diff   Differential mode  
	 * 		DAQmx_Val_PseudoDiff   Pseudodifferential mode  
	 * 
	 * @param minVal The minimum value, in units, that you expect to measure.
	 * 
	 * @param maxVal The maximum value, in units, that you expect to measure.
	 * 
	 * @param units The units to use to return the voltage measurements. 
	 * 		DAQmx_Val_Volts   volts 
	 * 		DAQmx_Val_FromCustomScale Units a custom scale specifies. Use customScaleName to specify a custom scale. 
	 * 
	 * @param customScaleName The name of a custom scale to apply to the channel. To use this parameter, you must set 
	 * units to DAQmx_Val_FromCustomScale. If you do not set units to DAQmx_Val_FromCustomScale, you must set customScaleName 
	 * to NULL.
	 * 
	 * @throws NiDaqException
	 */
	public void createAIVoltageChannel(Pointer taskHandle, String physicalChannel, String nameToAssignToChannel, int terminalConfig, double minVal, double maxVal, int units, String customScaleName) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxCreateAIVoltageChan(taskHandle, physicalChannel.getBytes(), nameToAssignToChannel.getBytes(), terminalConfig, minVal, maxVal, units, customScaleName == null ? null : customScaleName.getBytes()));
	}

	/**
	 * Creates channel(s) for current measurement and adds the channel(s) to the task you specify with taskHandle.
	 * 
	 * @param taskHandle The task to which to add the channels that this function creates.
	 * 
	 * @param physicalChannel The names of the physical channels to use to create virtual channels. You can specify a 
	 * list or range of physical channels.
	 * 
	 * @param nameToAssignToChannel The name(s) to assign to the created virtual channel(s). If you do not specify a 
	 * name, NI-DAQmx uses the physical channel name as the virtual channel name. If you specify your own names for 
	 * nameToAssignToChannel, you must use the names when you refer to these channels in other NI-DAQmx functions. 
	 * 
	 * If you create multiple virtual channels with one call to this function, you can specify a list of names separated 
	 * by commas. If you provide fewer names than the number of virtual channels you create, NI-DAQmx automatically 
	 * assigns names to the virtual channels. 
	 * 
	 * @param terminalConfig The input terminal configuration for the channel.
	 * 		DAQmx_Val_Cfg_Default   At run time, NI-DAQmx chooses the default terminal configuration for the channel. 
	 * 		DAQmx_Val_RSE   Referenced single-ended mode  
	 * 		DAQmx_Val_NRSE   Non-referenced single-ended mode  
	 * 		DAQmx_Val_Diff   Differential mode  
	 * 		DAQmx_Val_PseudoDiff   Pseudodifferential mode  
	 * 
	 * @param minVal The minimum value, in units, that you expect to measure.
	 * 
	 * @param maxVal The maximum value, in units, that you expect to measure.
	 * 
	 * @param units The units to use to return the measurement. 
	 * 		DAQmx_Val_Amps   amperes 
	 * 		DAQmx_Val_FromCustomScale   Units a custom scale specifies. If you select this value, you must specify a 
	 * custom scale name. 
	 * 
	 * @param shuntResistorLoc The location of the shunt resistor. Value  Description 
	 * 		DAQmx_Val_Default   At run time, NI-DAQmx chooses the default shunt resistor location for the channel. 
	 * 		DAQmx_Val_Internal   Use the built-in shunt resistor of the device. 
	 * 		DAQmx_Val_External   Use a shunt resistor external to the device. You must specify the value of the shunt 
	 * resistor in extShuntResistorVal. 
	 * 
	 * @param extShuntResistorVal The value, in ohms, of an external shunt resistor. 
	 * 
	 * @param customScaleName The name of a custom scale to apply to the channel. To use this parameter, you must 
	 * set units to DAQmx_Val_FromCustomScale. If you do not set units to DAQmx_Val_FromCustomScale, you must set 
	 * customScaleName to NULL.
	 * 
	 * @throws NiDaqException
	 */
	public void createAICurrentChannel(Pointer taskHandle, String physicalChannel, String nameToAssignToChannel, int terminalConfig, double minVal, double maxVal, int units, int shuntResistorLoc, double extShuntResistorVal, String customScaleName) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxCreateAICurrentChan(taskHandle, physicalChannel.getBytes(), nameToAssignToChannel.getBytes(), terminalConfig, minVal, maxVal, units, shuntResistorLoc, extShuntResistorVal, customScaleName.getBytes()));
	}
	
	/**
	 * 
Transitions the task from the committed state to the running state, which begins measurement or generation. Using this function is required for some applications and optional for others.

If you do not use this function, a measurement task starts automatically when a read operation begins. The autoStart parameter of the NI-DAQmx Write functions determines if a generation task starts automatically when you use an NI-DAQmx Write function.

If you do not call DAQmxStartTask and DAQmxStopTask when you call NI-DAQmx Read functions or NI-DAQmx Write functions multiple times, such as in a loop, the task starts and stops repeatedly. Starting and stopping a task repeatedly reduces the performance of the application.

Parameters
Input  
Name Type Description 
taskHandle  TaskHandle The task to start. 

Return Value

	 * 
	 * 
	 * Start task.
	 * @param taskHandle The pointer to the task.
	 * @throws NiDaqException
	 */
	public void startTask(Pointer taskHandle) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxStartTask(taskHandle));
	}
	
	/**
	 * Writes multiple samples to each digital line in a task. When you create your write array, each sample per channel 
	 * must contain the number of bytes returned by the DAQmx_Write_DigitalLines_BytesPerChan property.
	 * Note If you configured timing for your task, your write is considered a buffered write. Buffered writes require a 
	 * minimum buffer size of 2 samples. If you do not configure the buffer size using DAQmxCfgOutputBuffer, NI-DAQmx 
	 * automatically configures the buffer when you configure sample timing. If you attempt to write one sample for a 
	 * buffered write without configuring the buffer, you will receive an error.
	 * 
	 * @param taskHandle The task to write samples to.
	 * 
	 * @param numSamplesPerChannel The number of samples, per channel, to write. You must pass in a value of 0 or more
	 * in order for the sample to write. If you pass a negative number, this function returns an error.
	 * 
	 * @param autoStart Specifies whether or not this function automatically starts the task if you do not start it.
	 * 
	 * @param timeOut The amount of time, in seconds, to wait for this function to write all the samples. To specify 
	 * an infinite wait, pass -1 (DAQmx_Val_WaitInfinitely). This function returns an error if the timeout elapses.
	 * 
	 * @param dataLayout Specifies how the samples are arranged, either interleaved or noninterleaved.
	 * DAQmx_Val_GroupByChannel = Group by channel (noninterleaved), DAQmx_Val_GroupByScanNumber = Group by sample (interleaved).
	 *  
	 * @param data The samples to write to the task.
	 * 
	 * @throws NiDaqException
	 */
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
	
	/**
	 * Reads multiple floating-point samples from a task that contains one or more analog input channels.
	 * 
	 * @param taskHandle The task to read samples from.
	 * 
	 * @param numSampsPerChan The number of samples, per channel, to read. The default value of -1 (DAQmx_Val_Auto) reads 
	 * all available samples. If readArray does not contain enough space, this function returns as many samples as fit in 
	 * readArray. NI-DAQmx determines how many samples to read based on whether the task acquires samples continuously or 
	 * acquires a finite number of samples. If the task acquires samples continuously and you set this parameter to -1, 
	 * this function reads all the samples currently available in the buffer. If the task acquires a finite number of 
	 * samples and you set this parameter to -1, the function waits for the task to acquire all requested samples, then 
	 * reads those samples. If you set the Read All Available Samples property to TRUE, the function reads the samples 
	 * currently available in the buffer and does not wait for the task to acquire all requested samples.
	 * 
	 * @param timeout The amount of time, in seconds, to wait for the function to read the sample(s). To specify an 
	 * infinite wait, pass -1 (DAQmx_Val_WaitInfinitely). This function returns an error if the timeout elapses. A 
	 * value of 0 indicates to try once to read the requested samples. If all the requested samples are read, the function 
	 * is successful. Otherwise, the function returns a timeout error and returns the samples that were actually read.
	 * 
	 * @param fillMode Specifies whether or not the samples are interleaved. 
	 * DAQmx_Val_GroupByChannel Group by channel (non-interleaved) or DAQmx_Val_GroupByScanNumber Group by scan number (interleaved).
	 *  
	 * @param readArray The array to read samples into, organized according to fillMode.
	 * 
	 * @param arraySizeInSamps The size of the array, in samples, into which samples are read.
	 * 
	 * @param sampsPerChanRead The actual number of samples read from each channel.
	 * 
	 * @throws NiDaqException
	 */
	public void readAnalogF64(Pointer taskHandle, int numSampsPerChan, double timeout, int fillMode, DoubleBuffer readArray, int arraySizeInSamps, IntBuffer sampsPerChanRead) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxReadAnalogF64(taskHandle, numSampsPerChan, timeout, new NativeLong(fillMode), readArray, new NativeLong(arraySizeInSamps), sampsPerChanRead, null));
	}
	
	/**
	 * Stops the task and returns it to the state it was in before you called DAQmxStartTask or called an NI-DAQmx 
	 * Write function with autoStart set to TRUE.
	 * 
	 * If you do not call DAQmxStartTask and DAQmxStopTask when you call NI-DAQmx Read functions or NI-DAQmx Write functions 
	 * multiple times, such as in a loop, the task starts and stops repeatedly. Starting and stopping a task repeatedly reduces 
	 * the performance of the application.
	 * 
	 * @param taskHandle The task to stop.
	 * 
	 * @throws NiDaqException
	 */
	public void stopTask(Pointer taskHandle) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxStopTask(taskHandle));
	}
	
	/**
	 * Clears the task. Before clearing, this function aborts the task, if necessary, and releases any resources reserved 
	 * by the task. You cannot use a task once you clear the task without recreating or reloading the task.
	 * 
	 * If you use the DAQmxCreateTask function or any of the NI-DAQmx Create Channel functions within a loop, use this 
	 * function within the loop after you finish with the task to avoid allocating unnecessary memory.
	 * 
	 * @param taskHandle The task to clear.
	 * 
	 * @throws NiDaqException
	 */
	public void clearTask(Pointer taskHandle) throws NiDaqException {
		checkError(Nicaiu.INSTANCE.DAQmxClearTask(taskHandle));
	}
	
	/**
	 * Sets the source of the Sample Clock, the rate of the Sample Clock, and the number of samples to acquire or 
	 * generate.
	 *  
	 * @param taskHandle The task used in this function.
	 * 
	 * @param source The source terminal of the Sample Clock. To use the internal clock of the device, use NULL or 
	 * use OnboardClock.
	 * 
	 * @param rate The sampling rate in samples per second per channel. If you use an external source for the Sample 
	 * Clock, set this value to the maximum expected rate of that clock.
	 * 
	 * @param activeEdge Specifies on which edge of the clock to acquire or generate samples.
	 * 		DAQmx_Val_Rising   Acquire or generate samples on the rising edges of the Sample Clock. 
	 * 		DAQmx_Val_Falling   Acquire or generate samples on the falling edges of the Sample Clock.
	 * 
	 * @param sampleMode Specifies whether the task acquires or generates samples continuously or if it acquires 
	 * or generates a finite number of samples. 
	 * 		DAQmx_Val_FiniteSamps   Acquire or generate a finite number of samples. 
	 * 		DAQmx_Val_ContSamps   Acquire or generate samples until you stop the task. 
	 * 		DAQmx_Val_HWTimedSinglePoint Acquire or generate samples continuously using hardware timing without 
	 * 									 a buffer. Hardware timed single point sample mode is supported only for
	 * 									 the sample clock and change detection timing types. 
	 * 
	 * @param sampsPerChan  The number of samples to acquire or generate for each channel in the task if sampleMode 
	 * is DAQmx_Val_FiniteSamps. If sampleMode is DAQmx_Val_ContSamps, NI-DAQmx uses this value to determine the buffer size.
	 * 
	 * @throws NiDaqException
	 */
	public void cfgSampClkTiming(Pointer taskHandle, String source, double rate, int activeEdge, int sampleMode, long sampsPerChan) throws NiDaqException{
		checkError(Nicaiu.INSTANCE.DAQmxCfgSampClkTiming(taskHandle, source.getBytes(), rate, activeEdge, sampleMode, sampsPerChan));
	}
	
	/**
	 * Checks the return value from the DLL call and throws a NiDaqException to report the error
	 * if the return value indicates a warning or an error.
	 * @param errorCode
	 */
	private void checkError(int errorCode) throws NiDaqException {
		if(errorCode >= 0 && errorCode < 200000) {
			return;
		}
		throw new NiDaqException(errorCode);
	}

}
