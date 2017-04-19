#include <NewPing.h>
#include <Servo.h>
#include <OneWire.h>
#include <DallasTemperature.h>

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
boolean ultrasonicActivation = true; //Needs to be added but I don't know about these three thigs
boolean servoActivation = true;     //Needs to be added but I don't know about these three thigs
boolean motorActivation = true;     //Needs to be added but I don't know about these three thigs
boolean everythingActivation = true;

Servo motor;
Servo steer;
Servo collisionServo;

String input;
String range_string;

char data;

NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE_ULTRASONIC);

//Setup a oneWire instance to communicate with any OneWire devices
OneWire oneWire(ONE_WIRE_BUS);

// Pass our oneWire reference to Dallas Temperature. 
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

  // Start the sensor
  //  sensors.begin();    
  //  if (!sensors.getAddress(thermometer, 0)) Serial.println("Unable to find address for Device 0");  
  // set the resolution to 9 bit
  //  sensors.setResolution(thermometer, 9);
  //  sensors.getResolution(thermometer);

}

//Added by Isabelle Törnqvist 2017-04-18
// function to print the temperature for a device
void printTemperature(DeviceAddress deviceAddress)
{  
  oldTemp = tempC;
  tempC = (int)sensors.getTempC(deviceAddress);
  String tempString;

//Check temperature 
  if(tempC < 10){
    tempString = "t00" + (String)tempC;
  }else if (tempC >= 100){
    tempString = "t" + (String)tempC;
  }
  else{
    tempString = "t0" + (String)tempC;
  }

//delay for sending the temperature: wait 300 incrementations, and if the temperature has changed, send the temperature 
  if(++tempDelay >= 300 && oldTemp != tempC){
      Serial.println(tempString); 
      tempDelay = 0;  
  }  
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

        case 'F':
         if(input.charAt(3) == '0' ){
          flameActivation = false;
         }
         else {
          flameActivation = true;
         }
         break;

      case 'T':
        if(input.charAt(3) == '0' ){
          temperatureActivation = false;
         }
         else {
          temperatureActivation = true;
         }
         break;

      case 'U':
        if(input.charAt(3) == '0' ){
          ultrasonicActivation = false;
         }
         else {
          ultrasonicActivation = true;
         }
         break;

      case 'S':
        if(input.charAt(3) == '0' ){
          servoActivation = false;
         }
         else {
          servoActivation = true;
         }
         break;

      case 'M':
        if(input.charAt(3) == '0' ){
          motorActivation = false;
         }
         else {
          motorActivation = true;
         }
         break;

    case 'E':
        if(input.charAt(3) == '0' ){
          everythingActivation = false;
         }
         else {
          everythingActivation = true;
         }
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

  if(everythingActivation){
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

  if(temperatureActivation){
  //for temperature sensor
    sensors.requestTemperatures(); // Get temperatures
    printTemperature(thermometer); //Call 
    }   
  
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
