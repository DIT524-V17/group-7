# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
from threading import Thread
import socket
import sys

# What type of format will the text be encoded/decoded with.
textconverter = 'utf'

class Receiver(Thread):

    # Declare default socket
    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Declare new host and port.
    host = ""
    port = 0

    # Used for if the socket is still active or not.
    connection = False

    # Constructor that defines the host and port of the receiver.
    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    # On run
    def run(self):

        # binds the server to the port.
        self.receiver.bind((self.host, self.port))

        # Listens for packages of size 5. Any other size causes artifacts.
        self.receiver.listen(5)


        while 1:
            (client, address) = self.receiver.accept()
            if client.getsockname() != "":
                print("Receiver online")
                break
        while self.host != "":
            try:
                msg = client.recv(1024)
                if not msg:
                    self.disconnected("receivedata(): peer disconnected")
                if self.connection == False:
                    self.connection = True
                    continue
                msg = msg.decode(textconverter)
                print(msg) # ---------------------------------------------------------------- Redirect your stuff here.
            except:
                print("Disconnected")
                self.host = ""
                self.connection = False
                self.receiver = ""


    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)


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
                self.transmitter.send("cc".encode(textconverter))  # It sends a "confirmation" to the receiver. That reacts once it receives the first command.
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
                self.transmitter.send(input().encode(textconverter))
            except OSError:
                Receiver.disconnected(self, "senddata()")
                del self