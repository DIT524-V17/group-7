# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
import _thread
import socket
import sys
import time

receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object
transmitter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object

host = socket.gethostname()
port = 9005
port2 = 9000

def init():
    print("3")
    _thread.start_new_thread(initreceiver(), ("Thread-ReceiveData", 3,))
    print("4")
    inittransmitter()
    print("Receiver: ", receiver.getsockname(), " Transmitter: ", transmitter.getsockname() )
    _thread.start_new_thread(senddata(), ("Thread-SendData", 2,))


def initreceiver():
    receiver.bind((host, port))  # bind to the port
    receiver.listen(5)  # queue up to 5 requests
    while 1:
        (client, address) = receiver.accept()
        if client.getsockname() != "":
            print("Receiver online")
            break
    while 1:                                            # Infinite loop.
        msg = client.recv(1024)                 # Maximum amount of data to be sent.
        if not msg:
            disconnected("receivedata(): peer disconnected")

        msg = msg.decode('ascii')
        print(msg)



def disconnected(str):
    print("Disconnected at: %s" % str)
    transmitter.close()  # Closes the connection if the client drops.
    receiver.close()
    _thread.exit()
    init()  # most likely doesn't execute since the thread is killed in the last statement.


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
    print("Transmitter online")


def senddata():
    while 1:
        try:
            transmitter.send(input().encode('ascii'))               # Sends input to server.
        except OSError:
            disconnected("senddata()")


init()
