import socket
import select
import time
import traceback
from server import abspath

# This import will throw a syntax error if the module is not installed.
# import serial

# The encoding and decoding procedure to follow.
coding = 'utf'

# Arduino connected via a specific USB port.
# usb = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


class Receiver:

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None # host, an adress.
    port = None # port, integer please.
    connection = False # Connection, a boolean.
    address = None # address, the clients address.
    msg = None # msg, The message to be sent.

    # Constructor
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.daemon = True
        self.bind()

    # Binds the socket to the dedicated TCP port.
    # If the port is in use
    def bind(self):

        # Makes sure the port is not in use by clearing it in the terminal.
        # In previous iterations this was located in the Shell script.
        # But it was moved here so that reconnections or manual launches of
        # the python script will be a more seemless experience.
        # os.system("sudo fuser -k {}/tcp".format(self.port))

        # Binds the server to the socket.
        self.receiver.bind((self.host, self.port))

        # The server can listen up to 5 backlog connections.
        self.receiver.listen(5)

        # Initalize read and write loop.
        self.reconnect()

    # Handles connecting and reconnecting.
    def reconnect(self):

        # Reads and writes from the buffered socket forever.
        while 1:
            print("Address: {}".format(self.host))
            print("Port: {}".format(self.port))
            print("0. Awaiting connection.")

            # Grabs the tuples for the socket and address of the client.
            (client, address) = self.receiver.accept()
            print("1. Client connected.")

            # Make the new opened socket use a none-blocking flag, so it can read and write.
            client.setblocking(0)

            # Used to decide if the client is able to reconnect or not.
            self.connection = True

            read_stop = True
            try:
                read = [lines for lines in open(abspath + "_testcase/testcase_read.txt", "r")]
                read_index = 0
                start_time = time.time()
                read_time = int(read[read_index][0:read[read_index].find(" ")])
                read_stop = False
            except:
                traceback.print_exc()

            looptime = time.time()
            oldloop = time.time()

            # Only breaks when/if the client disconnects from the server.
            while self.connection:

                this_loop = (time.time() - looptime)
                if this_loop > 0.3:
                    print("Slow loop: {}".format((time.time() - looptime)))
                looptime = time.time()

                try:
                    if not read_stop:
                        if read_time < (time.time() - start_time):
                            command = "{}\n".format(
                                read[read_index][read[read_index].find(" ")+1::])
                            print("Reading: {}".format(command))
                            client.send(command.encode(coding))

                            read_index += 1
                            if len(read) <= read_index:
                                read_stop = True
                                print("Finished reading")
                            else:
                               read_time = int(read[read_index][0:read[read_index].find(" ")])
                except:
                    traceback.print_exc()
                    self.connection = False

                try:
                    if (oldloop + 0.3) < time.time():
                        client.send("\n".encode(coding))
                        oldloop = time.time()


                    # This follows this example of how to use select in python:
                    # http://stackoverflow.com/questions/2719017/how-to-set-timeout-on-pythons-socket-recv-method
                    # This will give me a none blocking message receiver.
                    ready = select.select([client], [], [], 0.1)

                    # Times out according to previous declaration.
                    if ready[0]:

                        # Able to receive up to 4096 bytes in one package.
                        self.msg = client.recv(4096)

                    # If there does not exist a message due to a connection issue, End loop.
                    if self.msg:

                        # Decodes the message received from bytes to text using either utf or ascii.
                        # self.msg = self.msg.decode(coding)
                        # print("2. To Arduino: " + self.msg.decode(coding))
                        self.msg = self.msg.decode(coding)
                        # print(self.msg)

                        self.msg = self.msg[2:6]

                        if not self.msg == "":
                            self.eval()

                        # Writes the message to the serial port on the Arduino.
                    #    usb.write(self.msg.encode())

                        # Flush the stream to force it to write to the buffer.
                    #    usb.flush()

                    # Found this clever solution here:
                    # http://stackoverflow.com/questions/38645060/what-is-the-equivalent-of-serial-available-in-pyserial

                # Due to socket issues or if the client disconnects.
                except socket.error:

                    # If a client disconnects. This will end the while self.connection loop.
                    self.connection = False
                    print("4. Client disconnected ")

    # Used for logs. Can be extended to support more features but is not important.
    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)

    def eval(self):
        v = self.msg[0:1:]
        a = int(self.msg[1::])
        testcase(self.msg)
        if v == "a":
            straight = 45
            t = ""
            if a < straight:
                t = "left"
            elif a == straight:
                t = "straight"
            else:
                t = "right"

            print("Turning {}° {}".format(a -straight, t))

        elif v == "d":
            default = 90
            t = a
            print("Driving {}".format(t))
            pass

        self.msg = None


def testcase(value):
    file = open(abspath + '_testcase/testcase_generated.txt','a')
    file.write(value + "\n")
    file.close()
