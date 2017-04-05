# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
# Maintained since: 4th of April 2017.
from threading import Thread
import socket
import sys
import serial, time

textconverter = 'utf'
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


# noinspection PyInterpreter
class Receiver(Thread):

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None
    port = None
    connection = False
    address = None
    clientIP = None

    def __init__(self, host, port):
        Thread.__init__(self)
        self.host = host
        self.port = port
        self.daemon = True
        self.start()

    def run(self):
        self.receiver.bind((self.host, self.port))
        self.receiver.listen(5)
        self.reconnect()


    def reconnect(self):
        while 1:
            (client, address) = self.receiver.accept()
            clientIP = client.getsockname()
            if client.getsockname() != "":
                self.connection = True
                break
        while client.getsockname() != "" and self.connection:
            msg = client.recv(1024)
            if not msg:
                break
            msg = msg.decode(textconverter)
            print(msg)
            usbconnection.write(msg.encode());

            if usbconnection.readline():
                self.client.send(usbconnection.readline().encode(textconverter))

        # If a client disconnects. Make it reconnectable.
        self.connection = False
        self.disconnected(client.getsockname() + "offline. \n Re-establishing socket. ")
        self.reconnect()
        
                
    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)
