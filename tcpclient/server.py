# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 7th of April 2017.
from common import Receiver
import socket

print(socket.getfqdn())
Receiver("192.168.43.249", 9005)
