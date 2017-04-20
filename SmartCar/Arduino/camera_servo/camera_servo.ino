#include <Servo.h>

Servo camera_servo_x; Servo camera_servo_y; 

boolean input = false;

int cameraX = 90;
int cameraY = 90;

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
 * Purpose: Handles the camera's servos. Do not call this method directly, Rather use setCameraServoRelative.
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

/**
 * Moves the camera relative to the current position. Such as left, right, up and down.
 * Param: Servo to be moved. Coordinates either 1,0 or -1. Amplifier amplifies the input coordinates.
 */
void setCameraServoRelative(Servo servo, int x=0, int y=0, int amplifier=10){
  
  // If the method is called without any changed values. End.
  if (x == 0 && y == 0) return;

  // If a x value has been given.
  if (x != 0){

    // Change the value for camera_x to keep track of it.
    cameraX += x*amplifier;

    // Call the method to set the angle of the servo.
    setCameraServo(servo, cameraX);
  }

  // If a y value has been given.
  if (y != 0){

    // Change the value for cameraY to keep track of it.
    cameraY += y*amplifier;

    // Call the method to set the angle of the servo.
    setCameraServo(servo, cameraY);
    
  }
  
}
/*
void setCameraReset(Servo servo,...){
    for (int i = 0; i < servo.length(); i++){
      servo[i].write(90);
    }
  }
*/

  
  

