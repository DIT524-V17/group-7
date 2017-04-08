# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 7th of April 2017.
from common import Receiver
import socket

print(socket.gethostname())
Receiver(socket.gethostname(), 9005)
