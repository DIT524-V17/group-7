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
    print("3")
    receiver = Receiver(host, port)
    time.sleep(5)
    transmitter = Transmitter(host, port)


init()
while 1:
    pass

