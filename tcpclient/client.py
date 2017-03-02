# Author: Pontus Laestadius.
# Since: 1st of March, 2017.
import _thread
import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
# s.connect(("192.168.0.148", 9010))                      # Connect to the raspberry pi.
s.connect((socket.gethostname(), 9010))                      # Connect to the local machine.


def senddata():
    s.send(("Connected").encode('ascii'))
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
