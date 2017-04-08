# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 7th of April 2017.
from common import Receiver
import socket

print(socket.gethostbyname(socket.gethostname()));
Receiver(socket.gethostbyname(socket.gethostname()), 9005)
