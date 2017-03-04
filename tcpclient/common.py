# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
from threading import Thread
import socket
import sys


class Receiver(Thread):

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = ""
    port = 0
    connection = False

    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    def run(self):
        self.receiver.bind((self.host, self.port))
        self.receiver.listen(5)
        while 1:
            (client, address) = self.receiver.accept()
            if client.getsockname() != "":
                print("Receiver online")
                break
        while 1:
            try:
                msg = client.recv(1024)
                if not msg:
                    self.disconnected("receivedata(): peer disconnected")
                if self.connection == False:
                    self.connection = True
                    continue
                msg = msg.decode('ascii')
                print(msg)
            except:
                self.disconnected("receivedata()")
                del self

    @staticmethod
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
                self.transmitter.connect((self.host, self.port))
                self.transmitter.send("1".encode('ascii'))  # It sends a "confirmation" to the receiver. That reacts once it receives the first command.
                break
            except:
                attempts += 1
                if attempts < 6:
                    print("#%s Attempting to connect. " % attempts)
                else:
                    print("Couldn't connect.")
                    del self
        print("Transmitter online")

        while 1:
            try:
                self.transmitter.send(input().encode('ascii'))
            except OSError:
                Receiver.disconnected(self, "senddata()")
                del self