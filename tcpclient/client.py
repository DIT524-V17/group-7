# Author: Pontus Laestadius.
# Since: 1st of March, 2017.
import socket
from common import Receiver
from common import Transmitter

host = socket.gethostname()
port = 9005
port2 = 9000


def init():
    Transmitter(host, port)
    Receiver(host, port2)


init()
while 1:
    pass
