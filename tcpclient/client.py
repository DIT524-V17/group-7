# Author: Pontus Laestadius.
# Since: 1st of March, 2017.
import _thread
import socket
import sys
import time

# More sockets.


receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
transmitter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
host = socket.gethostname()
port = 9010
port2 = 9009


def init():
    inittransmitter()
    initreceiver()
    # print("Connected.")


def initreceiver():
    receiver.bind((host, port2))  # bind to the port
    while 1:
        clientsocket, addr = receiver.accept()
        print("Connection from %s" % str(addr))
        if clientsocket.getsockname() != "":
            break


def disconnected():
    print("Disconnected")
    transmitter.close()  # Closes the connection if the client drops.
    receiver.close()
    _thread.exit()


def inittransmitter():
    attempts = 0
    while 1:
        try:
            transmitter.connect((host, port))  # Connect to the local machine.
            break
        except:
            attempts += 1
            if attempts < 6:
                print("#%s Attempting to connect. " % attempts)
            else:
                print("Ending program")
                sys.exit(2)


def senddata():
    while 1:
        try:
            transmitter.send(input().encode('ascii'))               # Sends input to server.
        except:
            disconnected()


def receivedata():
    while 1:                                            # Infinite loop.
        try:
            msg = receiver.recv(1024)                           # Maximum amount of data to be sent.
            msg = msg.decode('ascii')
            print(msg)
        except:
            disconnected()


init()
_thread.start_new_thread(senddata(), ("Thread-SendData", 2, ))
_thread.start_new_thread(receivedata(), ("Thread-ReceiveData", 3, ))
