#! bin/bash

# Pings google twice, to check for internet connection.
ping -q -c2 google.com > /dev/null
 
# If there exists a network connection run rest of the script.
# If there does not, kill the script and let the service auto-restart.
if [ $? -eq 0 ]
then


# The Address can be in use. This clears the port for TCP use.
# This is now done in the Python script.
# sudo fuser -k 9005/tcp

# Locate the Git repository
cd /home/pi/GitRasp/group-7

# Fetch any latest git changes.
sudo git fetch

# Pulling can always cause issues.
# By doing this it will always ignore all local changes, since It shouldn't have any anyways.
sudo git reset --hard origin/TCPclient

# Run TCP server
python tcpclient/server.py

# In case of emergency. Call 911.
cd

fi

