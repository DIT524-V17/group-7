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
        self.exit()

