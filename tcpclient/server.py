# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 29th of March, 2017

import socket
from common import Receiver
from common import Transmitter
import time
import os

host = "192.168.0.120"
port = 9005
port2 = 9000


def init():
    while not os.system("ping -c 1 google.com") == 0:
        pass

    receiver = Receiver(host, port)
    while 1:
        pass

    # Let the user enable the transmitter from the server.
    # print("Enable Transmitter? Y/N")
    # x = input()
    # if x.lower() != "y":
    #    while 1:
    #        pass

    # Waits for the receiver to be connected.
    # while not receiver.connection:
    #    pass

    # Using the peername from the first connection it can find the client and connect to it's socket.
    transmitter = Transmitter(receiver.receiver.getpeername(), port2)

    while 1:
        pass


init()
