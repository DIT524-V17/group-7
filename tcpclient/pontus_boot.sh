#! bin/bash

# Pings google, to check for internet connection,
ping -q -c2 google.com > /dev/null
 
# If there exists a network connection run rest of the script.
# If there does not, kill the script and let the service auto-restart.
if [ $? -eq 0 ]
then


# The Address can be in use. This clears the port for TCP use.
sudo fuser -k 9005/tcp

# Locate the Git repository
cd /home/pi/GitRasp/group-7

# Fuck it.
sudo git reset --hard origin/TCPclient

sudo git fetch
sudo git pull
# sudo go kill yourself

# Run TCP server
python tcpclient/server.py

# In case of emergency. Call 911.
cd

fi

