Automatic Testing
------------------------------

Author: Pontus Laestadius
Since: 03-05-2017

------------------------------
How do I run it?
------------------------------
This test suite is run in multiple steps.

Make sure you have android studio, either up and running or the latest project
was your app you wish to run the test case on.
Preferably on the Automated Testing branch.

The intial step is launching the android emulator.
This needs to be initiated by adding Android to your PATH system variables.
Google should help you with that one.
Next if your computer does not have a system image with android23/24 on it.
It will unsuccessfully launch the test suite, read the emulator instructions below.


To create an emulator that will run this test suite, follow these steps:
	1. Open up a your command promt
	2. Run this command to install the system image required.
	echo "y" | android update sdk -a --no-ui --filter sys-img-x86_64-android-24
	3. Time to make the emulator. Use this 
	echo "n" | android create avd --name myandroid22 -t "android-24"


------------------------------
What do I execute?
------------------------------
Run the main.bat file if you want to passively check and run test cases from git.
Run the universe.bat file to execute a test case.

The results will be opened with a default web browser and a text document
for the GUI testing and expected value testing respectively.



