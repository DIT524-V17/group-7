Automatic Testing
------------------------------

Author: Pontus Laestadius
Since: 03-05-2017



------------------------------
Pre-requirements
------------------------------
Be able to use java, android and git commands in the command line.
This can be done by adding them to your PATH.
This is done by opening your system environment variables. 
	2. Press edit variables.
	3. For android add:
		AppData/Local/Android/sdk/tools
		AppData/Local/Android/sdk/platform-tools
	4. for Java, figure it out yourself.

Have a system.img emulator with android 23 or 24.
Either install the ABI's from the commandline or use the Android SDK Manager.
To create an emulator with the command line:
	1. Open up a your command promt
	2. Run this command to install the system image required.
	echo "y" | android update sdk -a --no-ui --filter sys-img-x86_64-android-24
	3. Time to make the emulator. Use this 
	echo "n" | android create avd --name myandroid22 -t "android-24"
If you get a MISSING KERNAL error, you just missed to install some of the packages.

Have Android studio either running and the last project you ran should be the one you 
wish to install. As signing debug builds can only occur in the IDE.

------------------------------
How do I run it?
------------------------------
Run the main.bat file if you want to passively check and run test cases from git.
Run the universe.bat file to execute a test case.

The results will be opened with a default web browser and a text document
for the GUI testing and expected value testing respectively.

