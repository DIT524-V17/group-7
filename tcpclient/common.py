# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
# Maintained since: 7th of April 2017.
from threading import Thread
import socket
import serial, time
import select

textconverter = 'utf'
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


# Side-note: I'm not sure why this is still a threaded application, And at this point i'm not changing it.
class Receiver:

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None
    port = None
    connection = False
    address = None
    msg = None

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
        print("0. Awaiting connection.")
        try:
            # Always accepts the client.
            (client, address) = self.receiver.accept()
            print("1. Client connected. Name: " + client.getsockname())
            client.setblocking(0)
            self.connection = True
        except TimeoutError:
            raise

        # Only breaks when/if the client disconnects from the server.
        while self.connection:

            try:
                # This follows this example of how to use select in python:
                # This will give me a none blocking message receiver.
                # http://stackoverflow.com/questions/2719017/how-to-set-timeout-on-pythons-socket-recv-method
                ready = select.select([client], [], [], 1)
                if ready[0]:
                    self.msg = client.recv(4096)

                # If there does not exist a message due to a connection issue, End loop.
                if self.msg:
                    # Decodes the message received from bytes to text using either utf or ascii.
                    self.msg = self.msg.decode(textconverter)

                    print("2. To Arduino: " + self.msg)

                    # Writes the message to the serial port on the arduino.
                    usbconnection.write(self.msg.encode())
                    usbconnection.flush()

                # Found this solution here:
                # http://stackoverflow.com/questions/38645060/what-is-the-equivalent-of-serial-available-in-pyserial
                while usbconnection.inWaiting():  # Or: while ser.inWaiting():
                    info = usbconnection.readline().decode()
                    print("3. To Android: " + info)
                    client.send(info.encode(textconverter))
            except socket.error:
                # If a client disconnects. Open the port again so a new client can connect.
                self.connection = False
                self.disconnected("------------- \n" +
                                  client.getsockname() + " offline. " +
                                  "\n Re-establishing socket. "
                                  "\n ------------- ")
            self.reconnect()


    # Used for logs. Can be extended to support more features but is not important.
    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)
