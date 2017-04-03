# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
import socket
from common import Receiver
from common import Transmitter
import time
import os

host = "192.168.0.120"
port = 9005
port2 = 9000


def init():
    print(host)
    receiver = Receiver(host, port)
    
    while not receiver.connection:
       pass
    transmitter = Transmitter(receiver.receiver.getpeername(), port2)

    while 1:
        pass


init()
