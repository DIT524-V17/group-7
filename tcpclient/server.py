# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 4th of April 2017.
from common import Receiver
import socket

def init():
    # Receiver("192.168.0.120", 9005)
    print(socket.gethostname())
    Receiver(socket.gethostname(), 9005)


    while 1:
        pass


init()
