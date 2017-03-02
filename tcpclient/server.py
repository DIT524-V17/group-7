import socket

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create a socket object

host = socket.gethostname()

serversocket.bind((host, 9010)) # bind to the port

# queue up to 5 requests
serversocket.listen(5)

while 1:
    clientsocket, addr = serversocket.accept()
    print("Got a connection from %s" % str(addr))

    while (clientsocket.getsockname() != ""):
        msg = 'Thank you for connecting' + "\r\n"
        msg = clientsocket.recv(1024)  # Maximum amount of data to be sent.
        print(msg.decode('ascii'))  # Messages received formatting.


