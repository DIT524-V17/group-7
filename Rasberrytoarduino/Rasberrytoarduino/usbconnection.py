import serial
arduino = serial.Serial('COM5', 9600, timeout=.1)
while True:
    byte = "kej"
    arduino.write(byte.encode())
    data = arduino.readline()[:-2] #the last bit gets rid of the new-line chars
    if data:
        print(data)