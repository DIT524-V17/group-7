import serial, time
arduino = serial.Serial('COM3', 9600, timeout=.1)
time.sleep(1) #give the connection a second to settle
arduino.write("Hello worldQ".encode())
data = arduino.readline()
if data:
	print(data)

time.sleep(5)
arduino.write("Hello world2Q".encode())
data = arduino.readline()
if data:
	print(data)

time.sleep(5)
arduino.write("Hello world3Q".encode())
data = arduino.readline()
if data:
	print(data)

time.sleep(5)
arduino.write("Hello world2Q".encode())
data = arduino.readline()
if data:
	print(data)

