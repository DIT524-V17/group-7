# Author: Pontus Laestadius.
# Since: 1st March 2017.
import _thread
import socket
import sys
import time


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
# s.connect(("192.168.0.148", 9010))                      # Connect to the raspberry pi.
s.connect((socket.gethostname(), 9010))                      # Connect to the raspberry pi.


def senddata():
    while 1:
        userinput = input()
        s.send(userinput.encode('ascii'))               # Sends input to server.

def receivedata():
    while 1:                                            # Infinite loop.
        msg = s.recv(1024)                              # Maximum amount of data to be sent.
        print(msg.decode('ascii'))                      # Messages received formatting.

def pingserver():                                       # Pings the server every 5 seconds.
    while 1:
        s.send("!ping")                                 # The returning data will be handled by receivedata()
        time.sleep(5)

senddata()
# _thread.start_new_thread(senddata(), ("Thread-SendData", 2, ))
# _thread.start_new_thread(senddata(), ("Thread-ReceiveData", 3, ))
# _thread.start_new_thread(pingserver(), ("Thread-pingServer", 3, ))
