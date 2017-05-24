import socket
import select
import os
import traceback
import picamera
import time
import threading
import __future__
import io

# This import will throw a syntax error if the module is not installed.
import serial

# The encoding and decoding procedure to follow.
coding = 'utf-8' # 'ISO8859-1'

# Arduino connected via a specific USB port.
usb = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


class Receiver:

    receiver = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None
    port = None
    connection = False
    address = None
    msg = None
    prv = 0
    b = None
    client = None
    

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
        os.system("sudo fuser -k {}/tcp".format(self.port))

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

            # Runs the image capturing in a seperate thread.
            
            threading.Thread(target=self.drawme).start()
            #threading.Thread(target=self.bgFrame).start()
            
            # Make the new opened socket use a none-blocking flag, so it can read and write.
            client.setblocking(0)
            self.client = client
            # Used to decide if the client is able to reconnect or not.
            self.connection = True

            # Only breaks when/if the client disconnects from the server.
            while self.connection:
                try:

                    # This follows this example of how to use select in python:
                    # http://stackoverflow.com/questions/2719017/how-to-set-timeout-on-pythons-socket-recv-method
                    # This will give me a none blocking message receiver.
                    ready = select.select([client], [], [], 0.1)

                    # Times out according to previous declaration.
                    if ready[0]:

                        # Able to receive up to 4096 bytes in one package.
                        self.msg = client.recv(40000)

                    # If there does not exist a message due to a connection issue, End loop.
			

                    if self.msg:

			
                            # Decodes the message received from bytes to text using either utf or ascii.
                            #self.msg = self.msg.decode(coding)
                            print("2. To Arduino: ")

                            # Writes the message to the serial port on the Arduino.
                            usb.write(self.msg)

                            # Flush the stream to force it to write to the buffer.
                            usb.flush()

                    # Found this clever solution here:
                    # http://stackoverflow.com/questions/38645060/what-is-the-equivalent-of-serial-available-in-pyserial
                    # If the serial is waiting to send a message through the socket.
                    while usb.inWaiting():

                        # Grabs an entire line (until a newline character exists). And decode in to readable text.
                        try:
                            pass
                            info = usb.readline().decode()
                            print("3. To Android: " + info)
                       	    # client.send(info.encode(coding))
                       	except:
                            print("Unable to read")
                            # Send the message to the client.

                # Due to socket issues or if the client disconnects.
                except socket.error:
                    traceback.print_exc()
                    # If a client disconnects. This will end the while self.connection loop.
                    self.connection = False
                    print("4. Client disconnected ")

    # Used for logs. Can be extended to support more features but is not important.
    @staticmethod
    def disconnected(s):
        print("Disconnected at: %s" % s)

    def drawme(self):
        #stream = io.BytesIO()
        #imgsent = 0
        with picamera.PiCamera() as camera:
            res_width = 1280
            res_height = 720
            camera.resolution = (res_width, res_height)  # Sets the resolution to 640x480.
            # camera.zoom = (0, 0, 0.2, 0.2)
            
            time.sleep(2) # Let the camera warm up for 2 seconds.

            #Loops through indefinetly for capturing sequence
            while self.connection:
                camera.capture('img.jpg', resize=(int(res_width/3), int(res_height/3)),use_video_port=True)
                with open('img.jpg',"rb") as imageFile:
                    self.b = bytearray(imageFile.read())
                    
                    # Start a new thread where the sending of frames will be done          
                    try:
                        threading.Thread(target=self.sendFrame, args=(self.b,)).start()
                        # time.sleep(0.05)      
                    except:
                        traceback.print_exc()
                        self.connection = False
                        break # set the camera object as an instance of the picamera lib
    
    # Method for sending the frames through the Java Client
    # Params:
    # b is the byte array
    # client is the socket                                                                                                                            
    def sendFrame(self, b):
        try:
            index = 1
            div = 1024*8
            # for i in range (int(len(b)/div)):
                # threading.Thread(target=self.sendInBG, args= (b[index-1*div:index*div],)).start()
                # self.sendInBG(b[(index-1)*div:index*div])
                # index += 1
                # time.sleep(1/int(len(b)/div)) # Reduces unavailable resources issue.

            # Sends the remaining bytes which is smaller than a package.
            # self.sendInBG(b[(index-1)*div::])
            self.sendInBG(b)
            # threading.Thread(target=self.sendInBG, args= (b[index*div::],)).start()
            # threading.Thread(target=self.sendInBG, args= (b[index*div::],)).start()

            print("Frame sent with length: {}".format(len(b)) )
        except:
            traceback.print_exc()
            return

    
    def sendInBG(self, b):
        try:
            self.client.send(b)
        except BlockingIOError:
            time.sleep(0.05)
            self.client.send(b)
        except:
            traceback.print_exc()
            self.connection = False
        
        
            














        
        
