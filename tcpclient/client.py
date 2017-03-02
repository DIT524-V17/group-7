# Author: Pontus Laestadius.
# Since: 1st of March, 2017.
import _thread
import socket
import sys

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
# s.connect(("192.168.0.148", 9010))                      # Connect to the raspberry pi.

attempts = 0
while 1:
    try:
        s.connect((socket.gethostname(), 9010))                      # Connect to the local machine.
        break
    except:
        attempts = attempts +1
        if attempts < 6:
            print("No device found. Attempting to connect. # %s" % attempts)
        else:
            print("Ending program")
            sys.exit(2)

print("Connected.")


def senddata():
    while 1:
        try:
            s.send(input().encode('ascii'))               # Sends input to server.
        except:
            s.close()  # Closes the connection if the client drops.
            _thread.exit()


def receivedata():
    while 1:                                            # Infinite loop.
        try:
            msg = s.recv(1024)                           # Maximum amount of data to be sent.
            msg = msg.decode('ascii')
            print(msg)
        except:
            s.close()                                        # Closes the connection if the client drops.
            _thread.exit()

_thread.start_new_thread(senddata(), ("Thread-SendData", 2, ))
_thread.start_new_thread(receivedata(), ("Thread-ReceiveData", 3, ))
