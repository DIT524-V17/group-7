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

# @ Version 5.4.1

# The encoding and decoding procedure to follow.
coding = 'utf-8' # 'ISO8859-1' if encoding is an issue try this.

# Arduino connected via a specific USB port.
usb = serial.Serial('/dev/ttyACM0', 9600, timeout=.1)


class Receiver:

    
    command_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    camera_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = None
    port = None
    connection = False
    address = None
    msg = None
    msg_last = None
    prv = 0
    b = None
    client = None
    camera_feed = None
    fps = time.time()
    ite = 0
    

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

        # Binds the server to the sockets.
        self.command_sock.bind((self.host, 9006))
        self.camera_sock.bind((self.host, self.port))

        # The server can listen up to 5 backlog connections.
        self.command_sock.listen(5)
        self.camera_sock.listen(5)


        # Initalize read and write loop.
        self.reconnect()

    # Handles connecting and reconnecting.
    def reconnect(self):

        # Reads and writes from the buffered socket forever.
        while 1:
            print("Address: {}".format(self.host))
            print("Port: {}".format(self.port))
            print("0. Awaiting connection.")
   
            # Grabs the tuples for the sockets and address of the client and camera_feed.
            (self.camera_feed, address) = self.camera_sock.accept()
            (self.client, address) = self.command_sock.accept()
            print("1. Client connected.")

            # Runs the image capturing in a seperate thread.
            threading.Thread(target=self.drawme).start()
            
            # Make the new opened socket use a none-blocking flag, so it can read and write.
            self.client.setblocking(0)
            # Used to decide if the client is able to reconnect or not.
            self.connection = True

            # Only breaks when/if the client disconnects from the server.
            while self.connection:
                try:

                    # This follows this example of how to use select in python:
                    # http://stackoverflow.com/questions/2719017/how-to-set-timeout-on-pythons-socket-recv-method
                    # This will give me a none blocking message receiver.
                    ready = select.select([self.client], [], [], 0.1)

                    # Times out according to previous declaration.
                    if ready[0]:

                        # Able to receive up to 4096 bytes in one package.
                        self.msg = self.client.recv(40000)

                    # If there does not exist a message due to a connection issue, End loop.
			

                    if self.msg:

                            # Writes the message to the serial port on the Arduino.
                            if (self.msg_last != self.msg):
                                print("2. To Arduino: ") 
                                usb.write(self.msg)
                                self.msg_last = self.msg

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
                       	    self.client.send(info.encode(coding))
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

    def openImg(self):
        with open('img.jpg',"rb") as imageFile:
            self.b = bytearray(imageFile.read()) # Create a byte array of the saved image file.
            threading.Thread(target=self.sendFrame, args=(self.b,)).start()

    def drawme(self):
        # Sets the camera object and resolution.
        with picamera.PiCamera() as camera:
            camera.framerate = 10
            camera.resolution = (320, 191)  # Sets the resolution to 320x191.
            camera.brightness = 45
            
            time.sleep(2) # Let the camera warm up for 2 seconds.

            #Loops through indefinetly for capturing sequence
            while self.connection:
                # Capture an image
                # Set the video port to True for an increase in sending speed while sacrificing quality.
                camera.capture('img.jpg', use_video_port=True)
     
                threading.Thread(target=self.openImg).start()
                
            
    
    # Method for sending the frames through the Java Client.
    # b is the byte array.
    # camera_feed is the used socket .                                                                                                                           
    def sendFrame(self, b):
        try:
            index = 1
            div = 1024*8
            # Chop up the image and send it in smaller packages.
            for i in range (int(len(b)/div)):
                self.sendInBG(b[(index-1)*div:index*div])
                index += 1

            # Sends the remaining bytes which is smaller than a package.
            self.sendInBG(b[(index-1)*div::])
        except:
            traceback.print_exc()
            return

    # Send the images and check for occurrances of IOErrors.
    def sendInBG(self, b):
        try:
            self.camera_feed.send(b) # Sends the image through the socket to the Java client.
        # Catch error and try to resend the image.
        except BlockingIOError: 
            time.sleep(0.1)
            self.camera_feed.send(b)
        except:
            traceback.print_exc()
            self.connection = False # Closes connection.
        
        
            














        
        
