# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
import socket
from common import Receiver
from common import Transmitter
import time

host = socket.gethostname()
port = 9005
port2 = 9000


def init():
    receiver = Receiver(host, port)
    while not receiver.connection:
        pass
    transmitter = Transmitter(host, port2)


init()
while 1:
    pass

