# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
# Maintained since: 7th of April 2017.
from threading import Thread
import socket
import serial, time

textconverter = 'utf'
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


# Side-note: I'm not sure why this is still a threaded application, And at this point i'm not changing it.
class Receiver:

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None
    port = None
    connection = False
    address = None

    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.daemon = True
        self.run()

    # On thread run.
    def run(self):
        self.receiver.bind((self.host, self.port))
        self.receiver.listen(5)
        self.reconnect()

    # Handles connecting and reconnecting.
    def reconnect(self):

        try:
            # Always accepts the client.
            (client, address) = self.receiver.accept()
            self.connection = True
            Transmitter(client)
            client.setblocking(0)
        except TimeoutError:
            raise
        finally:
            self.reconnect()

        # Only breaks when/if the client disconnects from the server.
        while self.connection:

            # Receives up to 1024 bytes I think. Do some more reserach on teh purpose of this.
            msg = client.recv(1024)

            # If there does not exist a message due to a connection issue, End loop.
            if msg:

                print("Sending to Arduino: " + msg)

                # Decodes the message received from bytes to text using either utf or ascii.
                msg = msg.decode(textconverter)

                # Writes the message to the serial port on the arduino.
                usbconnection.write(msg.encode())


        # If a client disconnects. Open the port again so a new client can connect.
        self.connection = False
        self.disconnected(client.getsockname() + "offline. \n Re-establishing socket. ")
        self.reconnect()

    # Used for logs. Can be extended to support more features but is not important.
    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)


class Transmitter(Thread):

    s = socket

    def __init__(self, ssocket):
        Thread.__init__(self)
        self.s = ssocket
        self.daemon = True
        self.start()

    # On thread run.
    def run(self):
        while 1:

            # Reads from the Serial and sends it to the client.
            try:
                self.s.send(usbconnection.readline().decode().encode(textconverter))
            except BrokenPipeError:
                raise
