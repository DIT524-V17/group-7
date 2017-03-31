#include <Servo.h>

int flame_sensor_pin = 6 ;                           // initializing pin 7 as the sensor output pin
int flame_pin = HIGH ;                                   // state of sensor
int motor_pin = 7;
int steer_pin = 5;
int collisionFront_pin = 8;

int trigPin = 4;    //Trig - green Jumper
int echoPin = 2;    //Echo - yellow Jumper

long duration;

int collisionFrontPosition = 45;
boolean collisionFrontDirection = false;

boolean flame;

Servo motor, steer, collisionFront;
String input;
boolean completeCommand;
char data;

void setup() {
    //Sets the motor and steering servos to the default positions
    motor.attach(motor_pin); 
    motor.write(90); 
    
    steer.attach(steer_pin);
    steer.write(45);

    collisionFront.attach(collisionFront_pin);
    collisionFront.write(45);

    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);
    
    pinMode(12, OUTPUT);
    pinMode ( flame_sensor_pin , INPUT ) ;             // declaring sensor pin as input pin for Arduino
    
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
      
        //Controls speed
        case 'd':
            motor.write(input.substring(1, 4).toInt());
            break;
      
        //Turns a LED on when the app connects
        case 'L':
            digitalWrite(12, HIGH);
            break;
          
        default:
            motor.write(90);
            steer.write(45);
            break;
        }
    
        //Resets the it so the next command can be recorded
        input = "";
        completeCommand = false;
    }

  flame_pin = digitalRead ( flame_sensor_pin ) ;                // reading from the sensor

  if (flame_pin == HIGH && !flame){
    Serial.write("f001");
    flame = true;
    digitalWrite(12, HIGH);

  }else if(flame && flame_pin == LOW){
    Serial.write("f000");
    flame = false;
    digitalWrite(12, LOW);
  }

  if (collisionFrontPosition >= 85){
    collisionFrontDirection = false;
  }else if (collisionFrontPosition <= 5){
    collisionFrontDirection = true;
  }

  if (collisionFrontDirection){
    collisionFrontPosition += 1;
    collisionFront.write(collisionFrontPosition);
  } else {
    collisionFrontPosition -= 1;
    collisionFront.write(collisionFrontPosition);
  }
 int distance=measure();
  if (distance<=10){
      
    digitalWrite(12, HIGH);
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

int measure(){
  
  digitalWrite(trigPin, LOW); 
  delayMicroseconds(2);
  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH); 
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH); // Reads the echoPin, returns the sound wave travel time in microseconds
  int distance= duration*0.034/2;
 
  return distance;
  
}
