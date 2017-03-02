# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
import socket
import _thread
import time

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object

def senddata(cs):
    cs.send(("Connected").encode('ascii'))
    while 1:
        try:
            cs.send(input().encode('ascii'))               # Sends input to server.
        except:
            cs.close()  # Closes the connection if the client drops.
            _thread.exit()


def receivedata(cs):
    while 1:                                            # Infinite loop.
        try:
            msg = cs.recv(1024)                           # Maximum amount of data to be sent.
            msg = msg.decode('ascii')
            print(msg)
        except:
            cs.close()                                        # Closes the connection if the client drops.
            _thread.exit()

host = socket.gethostname()

s.bind((host, 9010))                                     # bind to the port

s.listen(5)                                              # queue up to 5 requests

while 1:
    clientsocket, addr = s.accept()
    print("Got a connection from %s" % str(addr))
    _thread.start_new_thread(senddata, (clientsocket,))
    _thread.start_new_thread(receivedata, (clientsocket,))
    time.sleep(3)
