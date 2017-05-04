# Author: Pontus Laestadius
# Since: 04/05/2017
# Version: 1.0

# Loads the latest report file generated by EyeAutomate in the web browser.

import webbrowser
import os

mypath = "C:/EyeAutomate/reports/"
startswith = "defaultTest"
stuff = os.listdir(mypath)

# Lists all the reports in the directory with the requirements set.
reports = [file for file in os.listdir(mypath) if file.endswith(".htm") and file.startswith(startswith)]

# Reverses the list.
reports = reports[::-1]

print(mypath + reports[0])

# Gets the first item in the list. And open a web browser tab with it.
webbrowser.open_new_tab(mypath + reports[0])

# This currently only opens the test document and not in a web browser. Functional but not optimal.
webbrowser.open_new_tab("file:///C:/Users/pontu/IdeaProjects/group-7/Testing/testcase_result.txt")
