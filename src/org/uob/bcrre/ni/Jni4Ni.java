package org.uob.bcrre.ni;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * User interface application demonstrating how to use the DLL wrapper.
 * Edit the main method to configure the NI devices that you have attached.
 */
public class Jni4Ni extends JFrame {
	
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Device prefix for device names.
	 */
	private static final String DEVICE_PREFIX = "Dev";

	/**
	 * Device Spec class specifies each NI device connected to the host.
	 */
	static class DeviceSpec {
		public String deviceName;
		public int analogueChannelCount;
		public int digitalChannelCount;
		public DeviceSpec(int deviceNumber, int analogueChannels, int digitalChannels) {
			this.deviceName = DEVICE_PREFIX + deviceName;
			this.analogueChannelCount = analogueChannels;
			this.digitalChannelCount = digitalChannels;
		}
	}

	/**
	 * Constructor method takes arguements specifying the devices connected to the
	 * host system. Edit the main method according to the devices that you have
	 * connected.
	 * @param devices List of device specifications connected to the host system.
	 */
	public Jni4Ni(DeviceSpec[] devices) {
		this.setSize(640,400);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	/**
	 * Main method.
	 * EDIT THIS METHOD: Specify the devices you are using with the number of channels
	 * (analogue and digital). This will customise the user interface to provide test
	 * facilities.
	 * @param args
	 */
	public static void main(String[] args) {
		int i=0;
		new Jni4Ni(new DeviceSpec[] {
				new DeviceSpec((i++),8,4)				// TODO Comment with name of device
		});
	}
	
}
