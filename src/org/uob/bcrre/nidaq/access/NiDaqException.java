package org.uob.bcrre.nidaq.access;

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
		default:
			errorMessage = "Unknown error.";
		}
		return "(" + errorCode + ") " + errorMessage;
	}
	
}
