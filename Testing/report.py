import webbrowser
import os

mypath = "C:/EyeAutomate/reports/"
startswith = "defaultTest"
stuff = os.listdir(mypath)

reports = [file for file in os.listdir(mypath) if file.endswith(".htm") and file.startswith(startswith)]
reports = reports[::-1]
webbrowser.open_new_tab(mypath + stuff[0])
