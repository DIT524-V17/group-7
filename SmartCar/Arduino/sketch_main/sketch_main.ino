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
Servo camera_servo_x; Servo camera_servo_y; 
const int Y_MAX_ANGLE = 100;
const int Y_MIN_ANGLE = 30;

const int X_MAX_ANGLE = 180;
const int X_MIN_ANGLE = 0;

int cameraX = 90;
int cameraY = 90;

const int analog0 = 54; // Analog pin 0
const int analog1 = 55; // Analog pin 1


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
 
    camera_servo_x.write(X_MAX_ANGLE/2);
    camera_servo_y.write(Y_MAX_ANGLE/2);
    
    pinMode(LED_PIN, OUTPUT);
    pinMode(FLAME_SENSOR_PIN, INPUT);
    
    Serial.begin(9600);  
}

void loop() { 
    //Is true when a command with a ? in the end is given or if the input reaches a length atleast 4
    if(completeCommand){
        switch(input.charAt(0)){
      
        //Controls the steering
        case 'a':
            steer.write(input.substring(1, 4).toInt());
            break;
      
        //Controls the speed
        case 'd':
            velocity = input.substring(1, 4).toInt();
            if ((!flame && !obstacle) || velocity >= 90){
                motor.write(velocity);
            }
            break;
            /*
        case 'x':
          setCameraServo(camera_servo_x, input.substring(1, 4).toInt(), 'x');
        break;
        case 'y':
        setCameraServo(camera_servo_y, input.substring(1, 4).toInt(), 'y');
        break;
     */
        default:
            motor.write(START_POSITION_MOTOR_SERVO);
            steer.write(START_POSITION_STEER_SERVO);
            break;
        }
        //Resets the it so the next command can be recorded
        input = "";
        completeCommand = false;
    }

    //Reads the value from the flame sensor
    flame_pin = digitalRead (FLAME_SENSOR_PIN) ;

    //If the flame sensor sees a flame it writes a command that says it sees a flame
    if (flame_pin == HIGH && !flame){
      if (++flame_delay >= 500){
          Serial.write("f001\n");
      }
      flame = true;
      digitalWrite(LED_PIN, HIGH);

    //when the flame disapears it sends a command that it stopped seing a flame
    }else if(flame && flame_pin == LOW){
      Serial.write("f000\n");
      flame = false;
      digitalWrite(LED_PIN, LOW);
      flame_delay = 0;
    }
    //Pings the ultrasonic sensor
    range_string = sonar.ping_cm();
    range = range_string.toInt();
    range_string = "c" + range_string + '\n';
    if (lastDistance != range && ++distance_Delay >= 100){
       // Serial.write(range);
        Serial.print(range_string);
        lastDistance = range;
        distance_Delay = 0;
    }

    //Allows the car to drive backwards if an obstacle is in from of the car
    if (velocity < 90){
      //Is true if the range is less than 50 cm or if the flame sensor has set the flame variable to true
        if (((range != 0 && range <= 50) && ++collision_delay >= 250) || flame){
            if (range > 20){
                motor.write(velocity +(50 - range)/2);
            } else {
                obstacle = true;
                collision_delay = 0;
    
                motor.write(START_POSITION_MOTOR_SERVO +30);
                delay(300);
                motor.write(START_POSITION_MOTOR_SERVO);
            }
        //Sets obstacle to false if the obstacle in front of the car disapeared
        } else if (obstacle){
            obstacle = false;
            motor.write(START_POSITION_MOTOR_SERVO);
        }
    }
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

void serialEvent() {
    //Records one command at a time when availible
    while(Serial.available()){
        data = Serial.read();
      
        //Makes sure no artifacts are in the command
        if(data != '?' && isAlphaNumeric(data)){
            input += data;
        }else if (input.length() >= 4){
        
            //Makes the loop do the command when the input is the end character ?
            completeCommand = true;
            break;
        }
    }
}

