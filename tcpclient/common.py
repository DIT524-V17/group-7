# Author: Pontus Laestadius.
# Since: 3rd of March, 2017.
from threading import Thread
import socket
import sys
import serial, time

textconverter = 'utf'
usbconnection = serial.Serial('/dev/ttyACM0', 9600, timeout=.1);



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
        usbconnection.write("L000?".encode());
        usbconnection.write("L000?".encode());
        self.reconnect()


    def reconnect(self):
        while 1:
            (client, address) = self.receiver.accept()
            if client.getsockname() != "":
                print("Receiver online")
                self.connection = True
                break
        while client.getsockname() != "" and self.connection:
            msg = client.recv(1024)
            if not msg:
                break
              #      self.disconnected("receivedata(): peer disconnected")
            msg = msg.decode(textconverter)
            print(msg) 

            usbconnection.write(msg.encode());
            #print(usbconnection.readline());
            
        self.connection = False
        self.disconnected("client offline")
        self.reconnect()
        
                
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
                assert attempts < 6
                Receiver.disconnected("Unable to connect transmitter to")
        print("Transmitter online")

        while 1:
            try:
                self.transmitter.send(input().encode(textconverter))
            except OSError:
                Receiver.disconnected(self, "senddata()")
                del self
