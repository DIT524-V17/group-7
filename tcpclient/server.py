# Author: Pontus Laestadius.
# Since: 1st March 2017.
import socket
import _thread
import time

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object
def senddata(cs):
    while 1:
        userinput = input()
        cs.send(userinput.encode('ascii'))               # Sends input to server.
    _thread.exit()

def receivedata(cs):
    while 1:                                            # Infinite loop.
        try:
            msg = clientsocket.recv(1024)                           # Maximum amount of data to be sent.
            msg = msg.decode('ascii')
            print(msg)
        except:
            clientsocket.close()                                        # Closes the connection if the client drops.
            c = 0
            _thread.exit()



host = socket.gethostname()

s.bind((host, 9010))                                     # bind to the port

s.listen(5)                                              # queue up to 5 requests
c = 0
while 1:
    clientsocket, addr = s.accept()
    print("Got a connection from %s" % str(addr))

    if c == 0:
        _thread.start_new_thread(senddata, (clientsocket,))
        _thread.start_new_thread(receivedata, (clientsocket,))
        c = 1
    else:
        time.sleep(3)


