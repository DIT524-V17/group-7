#! bin/bash

# The Address can be in use. This clears the port for TCP use.
sudo fuser -k 9005/tcp

# Locate the Git repository
cd /home/pi/GitRasp/group-7

# Fetch any updated files
git fetch

# Pull new files
git pull

# Run TCP server
python tcpclient/server.py

# In case of emergency. Call 911.
cd

