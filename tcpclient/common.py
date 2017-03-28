# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
from threading import Thread
import socket
import sys
import serial, time

textconverter = 'utf'

# Defines the Arduino Serial for writing the commands.
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1);


class Receiver(Thread):

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = ""
    port = 0
    connection = False

    # Constructor that defines the host and port of the receiver.
    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    def run(self):

        # binds the server to the port.
        self.receiver.bind((self.host, self.port))

        # Listens for packages of size 5. Any other size causes artifacts.
        self.receiver.listen(5)

        while 1:
            (client, address) = self.receiver.accept()
            print(client.getsockname)
            if client.getsockname() != "":
                print("Receiver online")
                break
        while self.host != "":
            msg = client.recv(1024)
            if not msg:
                self.disconnected("because peer disconnected")
            if not self.connection:
                self.connection = True
                continue
            msg = msg.decode(textconverter)
            print(msg)

            # Tim's Added line for sending the commands to the Arduino
            usbconnection.write(msg.encode())


    # Handles errors.
    def disconnected(self, s):
        print("Disconnected %s" % s)
        sys.exit(1)


class Transmitter(Thread):

    transmitter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = ""
    port = 0

    # Constructor
    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    # On start
    def run(self):
        attempts = 0
        while 1:
            try:

                # Connects the transmitter to the phone.
                self.transmitter.connect((self.host, self.port))

                # It sends a "confirmation" to the receiver. That reacts once it receives the first command.
                self.transmitter.send("cc".encode(textconverter))
                break
            except:

                # Tries to connect up to 5 times before giving up.
                attempts += 1
                if attempts < 6:
                    print("#%s Attempting to connect. " % attempts)
                else:
                    self.disconnected("because I couldn't connect")

        print("Transmitter online")

        while 1:
            try:
                self.transmitter.send(input().encode(textconverter))
            except OSError:
                self.disconnected("due to server")

    # Handles errors.
    def disconnected(self, s):
        print("Disconnected %s" % s)
        sys.exit(1)
