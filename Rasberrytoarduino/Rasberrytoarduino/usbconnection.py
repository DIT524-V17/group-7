import serial
arduino = serial.Serial('COM3', 9600, timeout=.1)
while True:
    byte = "kejQ"
    arduino.write(byte.encode())
    data = arduino.readline()[:-2] #the last bit gets rid of the new-line chars
    if data:
        print(data)
