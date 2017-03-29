# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
# Maintained since: 29th of March, 2017
from threading import Thread
import socket
import serial

textconverter = 'utf'
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


class Receiver(Thread):

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = ""
    port = 0
    connection = False
    peer = ""

    def __init__(self, host, port):
        assert (host != ""), "Receiver self.host is empty"
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    def run(self):
        self.receiver.bind((self.host, self.port))
        self.receiver.listen(5)

        # Enables the LED on the car
        usbconnection.write("L000?".encode())
        usbconnection.write("L000?".encode())
        self.accept()

    # Accepts the client trying to connect
    def accept(self):
        (client, address) = self.receiver.accept()
        if client.getsockname() != "":
            print("Client: " + client.getpeername() + " connected")
            self.connection = True
        else:
            self.accept()

    def handler(self, client):

        # While you are connected.
        while client.getsockname() != "" and self.connection:
            self.message(client)

        # If the client disconnects
        self.connection = False
        self.disconnected("client offline")
        self.accept()

    # Receive and print commands
    @staticmethod
    def message(client):
        try:
            usbconnection.write(client.recv(1024))
        except:
            raise

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
                # It sends a "confirmation" to the receiver. That reacts once it receives the first command.
                self.transmitter.send("cc".encode(textconverter))
                break
            except:
                attempts += 1
                # If there have been more than 5 attempts. End process.
                assert (attempts <= 5), "Couldn't connect"

        print("Transmitter online")

    # Send one message to the android.
    def send(self):
        while 1:
            try:
                self.transmitter.send(input().encode(textconverter))
            except OSError:
                Receiver.disconnected("couldn't send data, have you tried using AOL?")