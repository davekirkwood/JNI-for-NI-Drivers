# JNI-for-NI-Drivers
JNI Interface for National Instruments Drivers. National Instruments does not currently provide language interfaces for Java. This package is a DLL wrapper using Java Native Interface for National Instruments drivers. It includes the DLL wrapper and some sample code for doing some of the most common NI tasks such as creating a task, outputting a digital signal and reading an analogue input.

National instruments describe the problem:
http://digital.ni.com/public.nsf/allkb/802A9D349090D3F5862565CC0076BCF9

As the package uses JNI, it is platform dependent and only works on windows systems.

The kirkwood.nidaq.jna.Nicaiu class is a JNI wrapper, created using JNAerator (http://jnaerator.googlecode.com/), for the National instruments DLL. A selection of methods are used from the DLL, enabling my device (Ni-DAQ USB-6000) to be demonstrated by writing digital out lines and reading back analog input lines. A complete list of methods is in the text file Nicaiu_Full.txt. If you require additional methods, they can be cut/pasted into the Nicaiu.java class.

The kirkwood.nidaq.access.NiDaq class is a middle layer between the application and the wrapper class that simplifies some of the interfaces and handles errors via the kirkwood.nidaq.access.NiDaqException class. My intention is to add methods to this class as I add methods to the DLL wrapper, however you may prefer to access the wrapper class directly, or write your own middle layer, depending on your application.

This is a work in progress, the wrapper is only partially implemented and the examples are buggy, throwing out intermittent errors from the DAQ DLL. In future, the examples will be paramaterised, hopefully allowing you to customise them for your device.

I am not an advanced user of NI devices and have only developed this wrapper because I was unable to find an existing example on the internet. I am making this class as part of a train cab simulator that will use two NI-DAQ devices to interface with train controller handles and buttons. Once the package fulfils my requirements I will be happy. If you wish to develop it further to cover more advanced features, then please do.

Email me: d.kirkwood@bham.ac.uk if you'd like to help out with this project.

I will be revisiting this library early in 2016, when it is integrated with the train cab simulator.
