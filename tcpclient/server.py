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

    # Waits until the receiver is up and running.
    while not receiver.connection:
       pass

    # Uses the address from the first connection to identify who it's connecting to.
    transmitter = Transmitter(receiver.address, port2)

    while 1:
        pass


init()
