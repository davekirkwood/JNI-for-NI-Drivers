package org.uob.bcrre.nidaq;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.uob.bcrre.nidaq.access.NiDaq;
import org.uob.bcrre.nidaq.access.NiDaqException;
import org.uob.bcrre.nidaq.jna.Nicaiu;

import com.sun.jna.Pointer;

public class NiDaqUIDemo extends JFrame {

	private NiDaq daq;
	
	private Pointer doTask;
	private byte[] digitalOutData;
	
	private Pointer diTask;
	
	public NiDaqUIDemo(String deviceName, int digitalChannelCount, int analogueChannelCount) {

		digitalOutData = new byte[digitalChannelCount];
		for(int i=0; i<digitalChannelCount; i++) {
			digitalOutData[i] = 0;
		}
		
		initialiseDaq();
		initialiseUi();
		this.setVisible(true);
	}
	
	private void initialiseDaq() {
		try {
			daq = new NiDaq();
			doTask = daq.createTask("Task");
			daq.createDOChan(doTask, "Dev1/port0/line0:3", "", Nicaiu.DAQmx_Val_ChanForAllLines);
			daq.startTask(doTask);
			System.out.println("DAQ initialised");
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
	
	private void closeDaq()  {
		try {
			daq.stopTask(doTask);
			daq.clearTask(doTask);
			System.out.println("DAQ closed");
			System.exit(0);
		} catch(NiDaqException e) {
			e.printStackTrace();
		}
	}
	
	private void initialiseUi() {
		this.setSize(640,400);
		this.setLayout(new FlowLayout());
		for(int i=0;i<digitalOutData.length; i++) {
			this.add(createDoButton(i));
		}
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				closeDaq();
			}
		});
	}
	
	private JButton createDoButton(final int i) {
		JButton doButton = new JButton("Digital Out " + i);
		this.add(doButton);
		doButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(digitalOutData[i] == 0) {
					digitalOutData[i] = 1;
				} else {
					digitalOutData[i] = 0;
				}
				try {
					daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, digitalOutData);
				} catch(NiDaqException e) {
					e.printStackTrace();
				}
			}
		});
		return doButton;
	}
	
	public static void main(String[] args) {
		new NiDaqUIDemo("Dev1", 4, 8);
	}
	
}
