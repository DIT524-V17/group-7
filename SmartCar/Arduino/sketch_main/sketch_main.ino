#include <NewPing.h>
#include <Servo.h>

#define ECHO_PIN 2
#define TRIG_PIN 4
#define STEER_PIN 5
#define FLAME_SENSOR_PIN 6
#define MOTOR_PIN 7
#define COLLISION_SERVO_PIN 8
#define LED_PIN 12

#define START_POSITION_MOTOR_SERVO 90
#define START_POSITION_STEER_SERVO 45
#define START_POSITION_COLLISION_SERVO 50

#define MAX_DISTANCE_ULTRASONIC 400

int flame_pin;
int velocity;
int range;

int collisionServoPosition = START_POSITION_STEER_SERVO;
int collision_delay = 250;
int flame_delay = 500;
int lastDistance = 0;
int distance_Delay = 0;

boolean collisionDirection;
boolean obstacle;
boolean completeCommand;
boolean flame;

Servo motor;
Servo steer;
Servo collisionServo;

String input;
String range_string;

char data;

NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE_ULTRASONIC);



// Camera servos and all related code.
// Author: Pontus Laestadius.
Servo camera_servo_x;
Servo camera_servo_y;
const int Y_MAX_ANGLE = 100;
const int Y_MIN_ANGLE = 30;

const int X_MAX_ANGLE = 180;
const int X_MIN_ANGLE = 0;

int cameraX = 90;
int cameraY = 90;

// See this picture as to why they are numbered this way:
// http://pighixxx.com/megapdf.pdf
const int analog0 = 54; // Analog pin 0
const int analog1 = 55; // Analog pin 1
int cameraX_value = 0;
int cameraY_value = 0;

void setup() {
  //Sets the motor and steering servos to the default positions
  motor.attach(MOTOR_PIN);
  motor.write(START_POSITION_MOTOR_SERVO);

  steer.attach(STEER_PIN);
  steer.write(START_POSITION_STEER_SERVO);

  collisionServo.attach(COLLISION_SERVO_PIN);
  collisionServo.write(START_POSITION_COLLISION_SERVO);

  camera_servo_x.attach(analog0); //analog pin 0
  camera_servo_y.attach(analog1);  //analog pin 1

  // Resets the camera to a stationary position.
  camera_servo_x.write(X_MAX_ANGLE / 2);
  camera_servo_y.write(Y_MAX_ANGLE / 2);

  pinMode(LED_PIN, OUTPUT);
  pinMode(FLAME_SENSOR_PIN, INPUT);

  Serial.begin(9600);
}

void loop() {



  //Is true when a command with a ? in the end is given or if the input reaches a length atleast 4
  if (completeCommand) {

    // Improves readability.
    int value = input.substring(1, 4).toInt();

    switch (input.charAt(0)) {

      //Controls the steering
      case 'a':
        steer.write(value);
        break;

      //Controls the speed
      case 'd':
        velocity = value;
        if ((!flame && !obstacle) || velocity >= 90) {
          motor.write(velocity);
        }
        break;

      // Camera servos,
      // Their input should fit this expression: (x||y)(0001||0002)
      case 'x': // Sets the command to be used in the update function.
        cameraX_value = value;
        break;
      case 'y':
        cameraY_value = value;
        break;

      default:
        motor.write(START_POSITION_MOTOR_SERVO);
        steer.write(START_POSITION_STEER_SERVO);
        break;
    }
    //Resets the it so the next command can be recorded
    input = "";
    completeCommand = false;
  }

  // Updates the camera servos angle.
  cameraUpdate(cameraX_value, cameraY_value);

  //Reads the value from the flame sensor
  flame_pin = digitalRead (FLAME_SENSOR_PIN) ;

  //If the flame sensor sees a flame it writes a command that says it sees a flame
  if (flame_pin == HIGH && !flame) {
    if (++flame_delay >= 500) {
      Serial.write("f001\n");
    }
    flame = true;
    digitalWrite(LED_PIN, HIGH);

    //when the flame disapears it sends a command that it stopped seing a flame
  } else if (flame && flame_pin == LOW) {
    Serial.write("f000\n");
    flame = false;
    digitalWrite(LED_PIN, LOW);
    flame_delay = 0;
  }
  //Pings the ultrasonic sensor
  range_string = sonar.ping_cm();
  range = range_string.toInt();
  range_string = "c" + range_string + '\n';
  if (lastDistance != range && ++distance_Delay >= 100) {
    // Serial.write(range);
    Serial.print(range_string);
    lastDistance = range;
    distance_Delay = 0;
  }

  //Allows the car to drive backwards if an obstacle is in from of the car
  if (velocity < 90) {
    //Is true if the range is less than 50 cm or if the flame sensor has set the flame variable to true
    if (((range != 0 && range <= 50) && ++collision_delay >= 250) || flame) {
      if (range > 20) {
        motor.write(velocity + (50 - range) / 2);
      } else {
        obstacle = true;
        collision_delay = 0;

        motor.write(START_POSITION_MOTOR_SERVO + 30);
        delay(300);
        motor.write(START_POSITION_MOTOR_SERVO);
      }
      //Sets obstacle to false if the obstacle in front of the car disapeared
    } else if (obstacle) {
      obstacle = false;
      motor.write(START_POSITION_MOTOR_SERVO);
    }
  }
}



void serialEvent() {
  //Records one command at a time when availible
  while (Serial.available()) {
    data = Serial.read();

    //Makes sure no artifacts are in the command
    if (data != '?' && isAlphaNumeric(data)) {
      input += data;
    } else if (input.length() >= 4) {

      //Makes the loop do the command when the input is the end character ?
      completeCommand = true;
      break;
    }
  }
}

/**
   Updates the camera servos on each iteration, until a 0 is given.
   Checks both x and y individually so that they both can be moved at once.
*/
void cameraUpdate(int &x, int &y) {

  if (x != 0) { 
    setCameraServoRelative(camera_servo_x, X_MAX_ANGLE, X_MIN_ANGLE, x, cameraX);
  }
  if (y != 0) {
    setCameraServoRelative(camera_servo_y, Y_MAX_ANGLE, Y_MIN_ANGLE, y, cameraY);
  }

}

/**
   Author: Pontus Laestadius
   Purpose: Handles the camera's servos with constraits.
   Do not call this method directly, Rather use setCameraServoRelative.
*/
int moveCamera(Servo &servo, const int &max, const int &min, int degrees) {

  // If the values exceed the angle the servo is able to turn.
  if (degrees < min || degrees > max) {

    // Reset to either the minimum or maximum depending on which one it exceeds.
    servo.write(degrees < min ? min : max);

    // End the method. And return the value the camera angle is now set to.
    return degrees < min ? min : max;
  }

  // Turn the servo to the indicated angle.
  servo.write(degrees);
  return degrees;
}

/**
   Author: Pontus Laestadius
   Converts a value for the servo relative method.
   Param: v is an integer. either 1 or 2 (everything else returns 0), that gets converted to -1 or 1.
*/
int setCameraConverter(int v) {
  // If the method is called without any changed values. End.
  if (v == 1) {
    return -v;
  } else if (v == 2) {
    return  --v;
  }
  return 0;
}

/**
   Author: Pontus Laestadius
   Moves the camera relative to the current position. Such as left, right, up and down.
   Param: Servo to be moved, max and min angles the servo can move. Value, either 1 for lowing the angle and 2 for increasing it.
   Amplifier amplifies the input coordinates. Add functionality so this value can be changed remotely to change speed.
*/

void setCameraServoRelative(Servo &servo, const int &max, const int &min, int value, int &currentAngle) {
  // The servo will move in 2 degree increments.
  int amplifier = 2;

  // Change the value for cameraY to keep track of it.
  currentAngle += setCameraConverter(value) * amplifier;

  // Call the method to set the angle of the servo.
  currentAngle = moveCamera(servo, max, min, cameraX);
}

