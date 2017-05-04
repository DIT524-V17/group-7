# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 17th of April 2017.

from receiver import Receiver
import socket

file = open('testcase_generated.txt', 'w')
file.close()
Receiver("localhost", 9005)
