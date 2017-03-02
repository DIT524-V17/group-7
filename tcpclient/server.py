# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
import socket
import _thread
import time

receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object
transmitter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object

host = socket.gethostname()
port = 9010
port2 = 9009

def init():
    initreceiver()
    inittransmitter()


def initreceiver():
    receiver.bind((host, port))  # bind to the port
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
            transmitter.connect((host, port2))  # Connect to the local machine.
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

receiver.bind((host, port))                                     # bind to the port

receiver.listen(5)                                              # queue up to 5 requests

init()
_thread.start_new_thread(senddata(), ("Thread-SendData", 2, ))
_thread.start_new_thread(receivedata(), ("Thread-ReceiveData", 3, ))
