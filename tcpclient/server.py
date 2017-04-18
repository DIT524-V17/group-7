# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 17th of April 2017.

import socket
from receiver import Receiver

Receiver(socket.gethostname(), 9005)


