# Author: Pontus Laestadius.
# Since: 1st of March, 2017.
import _thread
import socket
import sys
import time

receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
transmitter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   # Define web socket.
host = socket.gethostname()
port = 9005
port2 = 9000


def init():
    print("3")
    inittransmitter()
    _thread.start_new_thread(senddata(), ("Thread-SendData", 2,))

    print("4")
    initreceiver()
    print("5")

    print("Receiver: ", receiver.getsockname(), " Transmitter: ", transmitter.getsockname())
    _thread.start_new_thread(receivedata(), ("Thread-ReceiveData", 3,))


def initreceiver():
    receiver.bind((host, port2))  # bind to the port
    receiver.listen(5)  # queue up to 5 requests
    while 1:
        clientsocket, addr = receiver.accept()
        print("Connection from %s" % str(addr))
        if clientsocket.getsockname() != "":
            break


def disconnected(str):
    print("Disconnected at: %s" % str)
    transmitter.close()  # Closes the connection if the client drops.
    receiver.close()
    _thread.exit()


def inittransmitter():
    attempts = 1
    while 1:
        try:
            transmitter.connect((host, port))  # Connect to the local machine.
            print("Transmitter online")
            break
        except:
            attempts += 1
            if attempts < 6:
                print("#%s Attempt to connect. " % attempts)
            else:
                print("Ending program")
                sys.exit(2)


def senddata():
    while 1:
        try:
            print("Send input:")
            transmitter.send(input().encode('ascii'))               # Sends input to server.
        except:
            disconnected("senddata()")


def receivedata():
    while 1:                                            # Infinite loop.
        try:
            msg = receiver.recv(1024)                           # Maximum amount of data to be sent.
            msg = msg.decode('ascii')
            print(msg)
        except:
            print("Receive message disconnect")

            disconnected("receivedata()")


init()
