# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
# Maintained since: 5th of April 2017.
from threading import Thread
import socket
import sys
import serial, time

textconverter = 'utf'
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


# Side-note: I'm not sure why this is still a threaded application, And at this point i'm not changing it.
class Receiver(Thread):

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None
    port = None
    connection = False
    address = None

    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    # On thread run.
    def run(self):
        self.receiver.bind((self.host, self.port))
        self.receiver.listen(5)
        self.reconnect()

    # Handles connecting and reconnecting.
    def reconnect(self):

        # Breaks once the client is connected.
        while 1:

            # Always accepts the client.
            (client, address) = self.receiver.accept()

            # The getsockname is not an empty string if it has a connected client.
            if client.getsockname() != "":
                self.connection = True
                break

        # Only breas when/if the client disconnects from the server.
        while self.connection:

            # Receives up to 1024 bytes I think. Do some more reserach on teh purpose of this.
            msg = client.recv(1024)

            # If there does not exist a message due to a connection issue, End loop.
            if not msg:
                break

            # Decodes the message received from bytes to text using either utf or ascii.
            msg = msg.decode(textconverter)

            # Writes the message to the serial port on the arduino.
            usbconnection.write(msg.encode())

            # Reads from the Serial and sends it to the client.
            # if usbconnection.readline():
            #    self.client.send(usbconnection.readline().decode().encode(textconverter))
            self.client.send("Reply".encode(textconverter))

        # If a client disconnects. Open the port again so a new client can connect.
        self.connection = False
        self.disconnected(client.getsockname() + "offline. \n Re-establishing socket. ")
        self.reconnect()

    # Used for logs. Can be extended to support more features but is not important.
    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)
