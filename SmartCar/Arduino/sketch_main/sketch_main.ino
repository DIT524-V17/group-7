#include <NewPing.h> // https://bitbucket.org/teckel12/arduino-new-ping/downloads/
#include <Servo.h>
//Library. Callibrates digital temperature readings.
#include <OneWire.h> //https://github.com/PaulStoffregen/OneWire  
//Library derived from OneWire. Used to do calculations regarding the temperature sensor. 
#include <DallasTemperature.h> //https://github.com/milesburton/Arduino-Temperature-Control-Library 

#define ONE_WIRE_BUS 3 //for temperature sensor
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
//Temperature variables
int tempC;
int oldTemp;

int collisionServoPosition = START_POSITION_STEER_SERVO;
int collision_delay = 250;
int flame_delay = 500;
int lastDistance = 0;
int distance_Delay = 0;
int tempDelay = 0;

boolean collisionDirection;
boolean obstacle;
boolean completeCommand;
boolean flame;

//For turning modules on and off
boolean flameActivation = true;
boolean temperatureActivation = true;
boolean ultrasonicActivation = true; 
boolean servoActivation = true;     
boolean motorActivation = true;
boolean allActivation = true; //Used for de-activation of all sensors/modules 

Servo motor;
Servo steer;
Servo collisionServo;

String input;
String range_string;

char data;

NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE_ULTRASONIC);

//Setup a oneWire, used for temperature sensor
OneWire oneWire(ONE_WIRE_BUS);

//Library used for temperature sensor 
DallasTemperature sensors(&oneWire);

// array to hold device readings
DeviceAddress thermometer;

void setup() {
    //Sets the motor and steering servos to the default positions
    motor.attach(MOTOR_PIN); 
    motor.write(START_POSITION_MOTOR_SERVO); 
    
    steer.attach(STEER_PIN);
    steer.write(START_POSITION_STEER_SERVO);
    
    collisionServo.attach(COLLISION_SERVO_PIN);
    collisionServo.write(START_POSITION_COLLISION_SERVO);
    
    pinMode(LED_PIN, OUTPUT);
    pinMode(FLAME_SENSOR_PIN, INPUT);
    
    Serial.begin(9600);  

  // Starting up <DallasTemperature.h>
    sensors.begin();    
    if (!sensors.getAddress(thermometer, 0));  
    
}

//Added by Isabelle Törnqvist 2017-04-18
// function to print the temperature for a device
void printTemperature(DeviceAddress deviceAddress)
{  
  oldTemp = tempC;
  tempC = (int)sensors.getTempC(deviceAddress);
  String tempString;

//Handling temperature: turning into a string of the appropriate length for sending to the Android app 
  if(tempC < 10){
    tempString = "t00" + (String)tempC;
  }else if (tempC >= 100){
    tempString = "t" + (String)tempC;
  }
  else{
    tempString = "t0" + (String)tempC;
  }

//delay for sending the temperature: if the temperature has changed, send the temperature 
  if(oldTemp != tempC){
      Serial.println(tempString);   
  }  
}

void loop() { 
    //Is true when a command with a ? in the end is given or if the input reaches a length atleast 4
    if(completeCommand){
      
        switch(input.charAt(0)){
      
        //Controls the steering
        case 'a':
            if (servoActivation){
                steer.write(input.substring(1, 4).toInt());
            }
            break;
      
        //Controls the speed
        case 'd':
            velocity = input.substring(1, 4).toInt();
            if ((!flame && !obstacle) || velocity >= 90){
                motor.write(velocity);
            }
            break;
        //case: '*' - switching sensors on or off, by setting bool variables to true or false. 
        //input is sent from Android app. Checks first character - cases for all sensors/modules.   
        case 'F':
         flameActivation = !flameActivation;
         break;

        case 'T':
          temperatureActivation = !temperatureActivation;
          break;

        case 'U':
          ultrasonicActivation = !ultrasonicActivation;
          break;

        case 'S':
          servoActivation =!servoActivation;
          break;

        case 'M':
          motorActivation = !motorActivation; 
          break;

        //switch off all sensors
        case 'E':
          allActivation = !allActivation;
          motorActivation = allActivation;
          servoActivation = allActivation;
          ultrasonicActivation = allActivation;
          temperatureActivation = allActivation;
          flameActivation = allActivation;        
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

//Activation of the flame-sensor: if flameActivation is false, this if-statement won't be executed. 
//This is were the detection of flame is done
    if(flameActivation){
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
  }

//Activation of ultrasonic sensor
  if(ultrasonicActivation){
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
  }

//Activation of motor
//Allows the car to drive backwards if an obstacle is in from of the car
if (motorActivation && velocity < 90 ){
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

//Activation for temperature sensor
if(temperatureActivation){
    sensors.requestTemperatures(); // Get temperatures
    printTemperature(thermometer); //Call 
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
