#include <Servo.h>

Servo camera_servo_x; Servo camera_servo_y; 

boolean input = false;

void setup() {

  pinMode(1,OUTPUT);
  camera_servo_x.attach(14); //analog pin 0
  camera_servo_y.attach(15); //analog pin 1

  camera_servo_x.write(90);
  camera_servo_x.write(90);

  Serial.begin(9600);
}

void loop() {

} 

void serialEvent() {

    // Reads a line from the Serial port.
    String in = Serial.readStringUntil('\n');

    // Convert it to integer. Hopefully it should already be integer. But who knows..
    int ins = in.toInt();

    // Makes it so that ever other command is for the x angle and the y angle.
    if (input)
    setCameraServo(camera_servo_x, ins);
    else
    setCameraServo(camera_servo_y, ins);

    input = !input;
}

/**
 * Author: Pontus Laestadius
 * Purpose: Handles the camera's servos.
 */
void setCameraServo(Servo servo, int degrees){

  // If the values exceed the angle the servo is able to turn.
  if (degrees < 0 || degrees > 180){

    // Reset to center position.
    servo.write(90);

    // End the method.
    return;
  }

  // Turn the servo to the indicated angle.
  servo.write(degrees);
  
  }


  
  

