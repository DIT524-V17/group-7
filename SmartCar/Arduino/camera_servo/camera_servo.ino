#include <Servo.h>

Servo camera_servo_x; Servo camera_servo_y; 
const int Y_MAX_ANGLE = 100;
const int Y_MIN_ANGLE = 30;

const int X_MAX_ANGLE = 180;
const int X_MIN_ANGLE = 0;

boolean input = false;

int cameraX = 90;
int cameraY = 90;

void setup() {

  camera_servo_x.attach(14); //analog pin 0
  camera_servo_y.attach(15); //analog pin 1

  camera_servo_x.write(X_MAX_ANGLE/2);
  camera_servo_y.write(Y_MAX_ANGLE/2);

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
    setCameraServo(camera_servo_x, ins, 'x');
    else
    setCameraServo(camera_servo_y, ins, 'y');

    input ? Serial.println("X"):Serial.println("Y");

    input = !input;
}

/**
 * Author: Pontus Laestadius
 * Purpose: Handles the camera's servos. Do not call this method directly, Rather use setCameraServoRelative.
 */
void setCameraServo(Servo servo, int degrees, char dir){
  int max, min;

  // Declares the maximum and minimum based on which servo was entered.
  if ((int) dir == (int) 'y'){
    max = Y_MAX_ANGLE;
    min = Y_MIN_ANGLE;
  } else {
    max = X_MAX_ANGLE;
    min = X_MIN_ANGLE;
  }

  // If the values exceed the angle the servo is able to turn.
  if (degrees < min || degrees > max){

    // Reset to either the minimum or maximum depending on which one it exceeds.
    servo.write(degrees < min ? min : max);

    // End the method.
    return;
  }

  // Turn the servo to the indicated angle.
  servo.write(degrees);
  
}

/**
 * Author: Pontus Laestadius
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
    setCameraServo(servo, cameraX, 'x');
  }

  // If a y value has been given.
  if (y != 0){

    // Change the value for cameraY to keep track of it.
    cameraY += y*amplifier;

    // Call the method to set the angle of the servo.
    setCameraServo(servo, cameraY, 'y');
    
  }
  
}

  
  

