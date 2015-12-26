package kirkwood.nidaq.access;

public class NiDaqException extends Exception {

	private int errorCode;
	
	public NiDaqException(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * TODO Complete list of DAQ error codes.
	 */
	public String toString() {
		String errorMessage = null;
		switch(errorCode) {
		case -200047:
			errorMessage = "DAQmxErrorInvalidChannel.";
			break;
		case -200170:
			errorMessage = "DAQmxErrorPhysicalChanDoesNotExist.";
			break;
		case -200220:
			errorMessage = "DAQmxErrorInvalidDeviceID";
			break;
		case -200089:
			errorMessage = "DAQmxErrorDuplicateTask";
			break;
		case -200498:
			errorMessage = "DAQmxErrorInvalidRangeOfObjectsSyntaxInString";
			break;
		case -200477:
			errorMessage = "DAQmxErrorCanNotPerformOpWhenNoDevInTask";
			break;
		case -200088:
			errorMessage = "DAQmxErrorInvalidTask";
			break;
		case -200478:
			errorMessage = "DAQmxErrorCanNotPerformOpWhenNoChansInTask";
			break;
		case -200525:
			errorMessage = "DAQmxErrorReadChanTypeMismatch";
			break;
		case -50103:
			errorMessage = "DAQmxErrorPALResourceReserved";
			break;
		case -200552:
			errorMessage = "DAQmxErrorInvalidCharInString";
			break;
		case -200081:
			errorMessage = "DAQmxErrorSampleRateNumChansConvertPeriodCombo";
			break;
		case -200229:
			errorMessage = "DAQmxErrorReadBufferTooSmall";
			break;
		case -200278:
			errorMessage = "DAQmxErrorSamplesWillNeverBeAvailable";
			break;
		case -200279:
			errorMessage = "DAQmxErrorSamplesNoLongerAvailable";
			break;
		case -200077:
			errorMessage = "DAQmxErrorInvalidAttributeValue";
			break;
		default:
			errorMessage = "Unknown error.";
		}
		return "(" + errorCode + ") " + errorMessage;
	}
	
}
