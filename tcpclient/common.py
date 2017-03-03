from threading import Thread
import socket
import sys
import time


class Receiver(Thread):

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = ""
    port = 0

    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    def run(self):
        self.receiver.bind((self.host, self.port))  # bind to the port
        self.receiver.listen(5)  # queue up to 5 requests
        while 1:
            (client, address) = self.receiver.accept()
            if client.getsockname() != "":
                print("Receiver online")
                break
        while 1:  # Infinite loop.
            msg = client.recv(1024)  # Maximum amount of data to be sent.
            if not msg:
                self.disconnected("receivedata(): peer disconnected")

            msg = msg.decode('ascii')
            print(msg)

    def disconnected(self, str):
        print("Disconnected at: %s" % str)


class Transmitter(Thread):

    transmitter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = ""
    port = 0

    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    def run(self):
        attempts = 0
        while 1:
            try:
                self.transmitter.connect((self.host, self.port))  # Connect to the local machine.
                break
            except:
                attempts += 1
                if attempts < 6:
                    print("#%s Attempting to connect. " % attempts)
                else:
                    print("Ending program")
                    sys.exit(2)
        print("Transmitter online")

        while 1:
            try:
                self.transmitter.send(input().encode('ascii'))  # Sends input to server.
            except OSError:
                self.disconnected("senddata()")

    def disconnected(self, str):
        print("Disconnected at: %s" % str)
