#! bin/bash
sudo fuser -k 9005/tcp
cd /home/pi/GitRasp/group-7
git fetch
git pull
python tcpclient/server.py
cd
# python /home/pi/server.py

