# JNI-for-NI-Drivers
JNI Interface for National Instruments Drivers. National Instruments does not currently provide language interfaces for Java. This package is a DLL wrapper using Java Native Interface for National Instruments drivers. It includes the DLL wrapper and some sample code for doing some of the most common NI tasks such as creating a task, outputting a digital signal and reading an analogue input.

National instruments describe the problem:
http://digital.ni.com/public.nsf/allkb/802A9D349090D3F5862565CC0076BCF9

As the package uses JNI, it is platform dependent and only works on windows systems.

A selection of methods are used from the DLL. A complete list of methods is in the text file Nicaiu_Full.txt. If you require additional methods, they can be cut/pasted into the Nicaiu.java class.

This is a work in progress, the wrapper and examples are still being created and will be improved soon. The examples will be paramaterised, hopefully allowing you to customise them for your device. I am not an advanced user of NI devices. I am making this class as part of a train cab simulator. Once the package fulfils my requirements I will be happy. If you wish to develop it further to cover more advanced features, then please do.

Email me: d.kirkwood@bham.ac.uk if you'd like to help out with this project.
