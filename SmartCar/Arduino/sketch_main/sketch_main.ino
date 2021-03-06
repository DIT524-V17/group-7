#include <Flame_array.h>

#include <NewPing.h> // https://bitbucket.org/teckel12/arduino-new-ping/downloads/
#include <Servo.h>
//Library. Callibrates digital temperature readings.
#include <OneWire.h> //https://github.com/PaulStoffregen/OneWire  
//Library derived from OneWire. Used to do calculations regarding the temperature sensor. 
#include <DallasTemperature.h> //https://github.com/milesburton/Arduino-Temperature-Control-Library 


/*
@Contributors
Isabelle Tornqvist
Tim Jonasson
Pontus Laestadius
Anthony Path

@Version 1.4
2017-05-27
Tim Jonasson: Integrated the library for the 5 channel flame sensor
2017-05-27
Tim Jonasson: Commented out collision control cause it broke everything and increased the temperature delay

2017-05-24
Anthony Path: Added support of modified collision control system
2017-05-11
Anthony Path: Added battery voltage measurement feature to determine and display battery level.
2017-05-10
Tim Jonasson: Removed the delay that made it so it doesnt send commands when the value doesnt change and that caused issues since we didnt 
get the temperature to the app when the temperature didnt change
Fixed an issue with the flame commands not being sent properly
2017-05-08
Isabelle Törnqvist: Added code to allow for deactivation of camera servos, X and Y. Refined temperature code. 
*/

const int ONE_WIRE_BUS_PIN = 3; //for temperature sensor
const int ECHO_PIN_FRONT_MID = 2; //front
const int TRIG_PIN_FRONT_MID = 4;
const int ECHO_PIN_FRONT_RIGHT = A8; // front right
const int TRIG_PIN_FRONT_RIGHT  = A9;
const int ECHO_PIN_FRONT_LEFT  = A10; // front left
const int TRIG_PIN_FRONT_LEFT  = A11;
const int ECHO_PIN_BACK = A12; // back 
const int TRIG_PIN_BACK = A13;
const int ULTRASONIC_SENSOR_COUNT = 4; //Number of ultrasonic sensors
const int STEER_PIN = 5;
const int MOTOR_PIN = 7;
const int COLLISION_SERVO_PIN = 8;
const int LED_PIN = 12;
const int VOLT_PIN = A5;
const int FLAME_PIN_WEST = A2;
const int FLAME_PIN_NORTH_WEST = A3;
const int FLAME_PIN_NORTH = A4;
const int FLAME_PIN_NORTH_EAST = A6;
const int FLAME_PIN_EAST = A7;

const int START_POSITION_MOTOR_SERVO = 90;
const int START_POSITION_STEER_SERVO = 45;
const int START_POSITION_COLLISION_SERVO = 50;

const int MAX_DISTANCE_ULTRASONIC = 400;

int flame_reading;

int velocity;
int ultrasonic_range_front_mid;
int ultrasonic_range_front_right;
int ultrasonic_range_front_left;
int ultrasonic_range_back;
float voltage_Now=150;

//Temperature variables
int temperature;

int voltage_delay = 500;
int collision_delay = 250;
int flame_delay = 500;
int last_distance_read = 0;
int distance_delay = 0;
int temp_delay = 0;

boolean obstacle_detected_front;
boolean obstacle_detected_back;
boolean complete_command;

//For turning modules on and off
boolean flame_activation = true;
boolean temperature_activation = true;
boolean ultrasonic_activation = true; 
boolean servo_activation = true;     
boolean motor_activation = true;
boolean all_activation = true; //Used for de-_activation of all sensors/modules 
boolean camera_servo_horizontal_activation = true; //X is horizontal  
boolean camera_servo_vertical_activation = true; //Y is vertical

Servo motor;
Servo steer;

String input;
String ultrasonic_distance_string;


NewPing sonars [ULTRASONIC_SENSOR_COUNT] { //Array of NewPing sonars, must be updated if more are added
  NewPing(TRIG_PIN_FRONT_MID, ECHO_PIN_FRONT_MID, MAX_DISTANCE_ULTRASONIC),
  NewPing(TRIG_PIN_FRONT_RIGHT, ECHO_PIN_FRONT_RIGHT, MAX_DISTANCE_ULTRASONIC),
  NewPing(TRIG_PIN_FRONT_LEFT, ECHO_PIN_FRONT_LEFT, MAX_DISTANCE_ULTRASONIC),
  NewPing(TRIG_PIN_BACK, ECHO_PIN_BACK, MAX_DISTANCE_ULTRASONIC)
};


//Setup a oneWire, used for temperature sensor
OneWire oneWire(ONE_WIRE_BUS_PIN);

//Library used for temperature sensor 
DallasTemperature temperature_sensor(&oneWire);

// array to hold device readings
DeviceAddress thermometer;

//The array of flame sensors
Flame_array flame_array(FLAME_PIN_WEST, 
                        FLAME_PIN_NORTH_WEST, 
                        FLAME_PIN_NORTH, 
                        FLAME_PIN_NORTH_EAST, 
                        FLAME_PIN_EAST);

/*
  * Camera servos.
  * Author: Pontus Laestadius.
  * Since: 25th of April 2017
  */
  Servo camera_servo_x_axis;
  Servo camera_servo_y_axis;
  const int Y_AXIS_MAX_ANGLE = 100;
  const int Y_MIN_MAX_ANGLE = 30;
  
  const int X_AXIS_MAX_ANGLE = 180;
  const int X_MIN_MAX_ANGLE = 0;
  
  int camera_x = 90; // Angle of the camera X
  int camera_y = 90; // Angle of the camera Y
  
  // See this picture as to why they are numbered this way:
  // http://pighixxx.com/megapdf.pdf
  const int CAMERA_PIN_X = 54; // Analog pin 0
  const int CAMERA_PIN_Y = 55; // Analog pin 1
  int camera_x_value_received_from_the_raspberry = 0; // Last X input.
  int camera_y_value_received_from_the_raspberry = 0; // Last Y input.

void setup() {
    //Sets the motor and steering servos to the default positions
    motor.attach(MOTOR_PIN); 
    motor.write(START_POSITION_MOTOR_SERVO); 
    
    steer.attach(STEER_PIN);
    steer.write(START_POSITION_STEER_SERVO);

    pinMode(LED_PIN, OUTPUT);

    camera_servo_x_axis.attach(CAMERA_PIN_X); //analog pin 0
    camera_servo_y_axis.attach(CAMERA_PIN_Y);  //analog pin 1

    // Resets the camera to a stationary position.
    camera_servo_x_axis.write(X_AXIS_MAX_ANGLE/2);
    camera_servo_y_axis.write(Y_AXIS_MAX_ANGLE/2);
    
    Serial.begin(9600);  

  // Starting up <DallasTemperature.h>
    temperature_sensor.begin();    
    if (!temperature_sensor.getAddress(thermometer, 0));  
    
}
/*
  * Temperature sensor.
  * Author: Isabelle Törnqvist.
  * Since: 18th of April 2017
  */

//function to print the temperature 
void printTemperature(DeviceAddress deviceAddress){  
  temperature = (int)temperature_sensor.getTempC(deviceAddress);
  String temp_string = "t";

//Handling temperature: turning into a string of the appropriate length for sending to the Android app 
  if(temperature < 10){
    temp_string += "00";
  }else if( temperature < 100){
    temp_string += "0";
  }
  temp_string += temperature; 

//delay for sending the temperature: if the temperature has changed, send the temperature 
  Serial.println(temp_string);  

}

  /*
  * Camera servos and all related code. 
  * Author: Pontus Laestadius.
  * Since: 25th of April 2017
  */
namespace camera {

  #define CAMERA_BOUNDRY(x,y,z) (x) < (y) ? (y) : (z)
  
  /*
     Purpose: Handles the camera's servos with constraits.
     Do not call this method directly, Rather use setServoRelative.
  */
  int moveCamera(Servo &servo, const int &max, const int &min, int degrees) {
  
    // If the value_received_from_the_raspberrys exceed the angle the servo is able to turn.
    if (degrees < min || degrees > max) {
  
      // Which boundry does the camera exceed.
      int boundry = CAMERA_BOUNDRY(degrees, min, max);
  
      // Reset to either the minimum or maximum depending on which one it exceeds.
      servo.write(boundry);
  
      // End the method. And return the value_received_from_the_raspberry the camera angle is now set to.
      return boundry;
    }
  
    // Turn the servo to the indicated angle.
    servo.write(degrees);
    return degrees;
  }
  
  /**
     Converts a value_received_from_the_raspberry for the servo relative method.
     Param: v is an integer. either 1 or 2 (everything else returns 0), that gets converted to -1 or 1.
  */
  int valueConverter(int v) {
    if (v == 1) { 
      return -v; // v = -1;
    } else if (v == 2) {
      return  --v; // v = 1;
    }
    return 0;
  }
  
  /**
     Moves the camera relative to the current position. Such as left, right, up and down.
     Param: Servo to be moved, max and min angles the servo can move. value_received_from_the_raspberry, either 1 for lowing the angle and 2 for increasing it.
     Amplifier amplifies the input coordinates.
  */
  
  void setServoRelative(Servo &servo, const int &max, const int &min, int value_received_from_the_raspberry, int &currentAngle) {
    // The servo will move in 2 degree increments.
    int amplifier = 2;
  
    // Change the value_received_from_the_raspberry for the camera angle to keep track of it.
    currentAngle += valueConverter(value_received_from_the_raspberry) * amplifier;
  
    // Call the method to set the angle of the servo.
    currentAngle = moveCamera(servo, max, min, currentAngle);
  } 

  /*
   * Updates the camera servos on each iteration, until a 0 is given.
   * Checks both x and y individually so that they both can be moved at once.
  */
  void updateServo(int &x, int &y) {
  
    if (camera_servo_horizontal_activation && x != 0) { 
      setServoRelative(camera_servo_x_axis, X_AXIS_MAX_ANGLE, X_MIN_MAX_ANGLE, x, camera_x);
    }
    if (camera_servo_vertical_activation && y != 0) {
      setServoRelative(camera_servo_y_axis, Y_AXIS_MAX_ANGLE, Y_MIN_MAX_ANGLE, y, camera_y);
    }
  
  }
}

/*
  * Method for using the commands
  * Author: Tim Jonasson
  * Comment: Several people contributed to the switch case
  */

void command() {
      // Improves readability.
      int value_received_from_the_raspberry = input.substring(1, 4).toInt();
      
        switch(input.charAt(0)){
      
        //Controls the steering
        case 'a':
            if (servo_activation){
                steer.write(input.substring(1, 4).toInt());
            }
            break;
      
        //Controls the speed
        case 'd':
            velocity = input.substring(1, 4).toInt();
            //Allows car to drive forward if no obstacle or flame is in front of it, drive backward if there is no obstacle behind the car, or stand still
            if ((!obstacle_detected_front && velocity < 90) || (velocity > 90 && !obstacle_detected_back) || velocity == 90){
                motor.write(velocity);
            }
            break;
        //case: '*' - switching sensors on or off, by setting bool variables to true or false. 
        //input is sent from Android app. Checks first character - cases for all sensors/modules.   
        case 'F':
          flame_activation = !flame_activation;
          break;

        case 'T':
          temperature_activation = !temperature_activation;
          break;

        case 'U':
          ultrasonic_activation = !ultrasonic_activation;
          break;

        case 'S':
          servo_activation =!servo_activation;
          break;

        case 'M':
          motor_activation = !motor_activation; 
          break; 

        case 'X':
          camera_servo_horizontal_activation = !camera_servo_horizontal_activation;
          break;

        case 'Y':
          camera_servo_vertical_activation = !camera_servo_vertical_activation;
          break; 

        //switch off all sensors
        case 'E':
          all_activation = !all_activation;
          motor_activation = all_activation;
          servo_activation = all_activation;
          ultrasonic_activation = all_activation;
          temperature_activation = all_activation;
          flame_activation = all_activation; 
          camera_servo_horizontal_activation = all_activation; 
          camera_servo_vertical_activation = all_activation;      
          break;     

      // Camera servos,
      // Their input should fit this expression: (x||y)(0001||0002)
      case 'x': // Sets the command to be used in the update function for the camera.
        camera_x_value_received_from_the_raspberry = value_received_from_the_raspberry;
        break;
      case 'y':
        camera_y_value_received_from_the_raspberry = value_received_from_the_raspberry;
        break;
      
        default:
            motor.write(START_POSITION_MOTOR_SERVO);
            steer.write(START_POSITION_STEER_SERVO);
            break;
        }
        //Resets the it so the next command can be recorded 
        input = "";
        complete_command = false;
    
}


/*
  * Method for reading the 5 channel flame sensor
  * Author: Tim Jonasson
  */


void readFlame(){
//Reads the value_received_from_the_raspberry from the flame sensor
          char* flame_reading = flame_array.read();
          if((*(flame_reading + 1)) != '2'){
            String temp = "";
            temp += (*(flame_reading + 0));
            temp += (*(flame_reading + 1));
            temp += (*(flame_reading + 2));
            temp += (*(flame_reading + 3));
            Serial.println(temp);
        }

}

//collision control system  - getting range from all ultrasonic sensors, and printing range of middle one to serial for display
void collisionControl() {
    ultrasonic_range_front_mid = sonars[0].ping_cm();
    ultrasonic_distance_string = "c" + (String)ultrasonic_range_front_mid + '\n';
    ultrasonic_range_front_right=sonars[1].ping_cm();
    ultrasonic_range_front_left=sonars[2].ping_cm();
    ultrasonic_range_back=sonars[3].ping_cm();
   if (last_distance_read != ultrasonic_range_front_mid && ++distance_delay >= 100){
       // Serial.write(range);
        Serial.print(ultrasonic_distance_string);
        last_distance_read = ultrasonic_range_front_mid;
        distance_delay = 0;
    }
    //Collision control - front
    if (motor_activation && velocity < 90 ){
      //Is true if the range in front is less than 50 cm, front left and front right - less than 25 cm or if the flame sensor has set the flame variable to true
        if (((ultrasonic_range_front_mid != 0 && ultrasonic_range_front_mid <= 50 )  ||
        (ultrasonic_range_front_right != 0 && ultrasonic_range_front_right <= 25 ) ||
        (ultrasonic_range_front_left != 0 && ultrasonic_range_front_left <= 25 ))){            
                obstacle_detected_front = true;
                collision_delay = 0;
                motor.write(START_POSITION_MOTOR_SERVO);           
        //Sets obstacle to false if the obstacle in front of the car disapeared
        } else if (obstacle_detected_front){
            obstacle_detected_front = false;
            motor.write(START_POSITION_MOTOR_SERVO);
        }
    }
    //Collision control - back
    if (motor_activation && velocity > 90){ 
      //Is true if the range is less than 50 cm in the back
        if (ultrasonic_range_back != 0 && ultrasonic_range_back <= 50)   {
                obstacle_detected_back = true;
                collision_delay = 0;
             
               
                motor.write(START_POSITION_MOTOR_SERVO);
            }
        //Sets obstacle to false if the obstacle in front of the car disappeared
         else if (obstacle_detected_back){
            obstacle_detected_back = false;
            motor.write(START_POSITION_MOTOR_SERVO);
        }
    } 
}

/*
  * Author: Isabelle Tornqvist
  */

void readTemp(){
    temperature_sensor.requestTemperatures(); // Get temperatures
    printTemperature(thermometer); //Call  
}

/*
@Author Anthony Path
Measure voltage of each cell of motor's battery and print it to serial. Works for both 1.5 V batteries and 1.2 V NIMHs
*/
void sendVoltage(){ 

 int value = analogRead(VOLT_PIN); 
float voltage = value * (5.00 / 1023.00*3.13/8); 
//Formula taken from here: https://www.arduino.cc/en/Tutorial/ReadAnalogVoltage
 //Multiplied by 3.13 because 47 and 100 K Ohm resistors lower voltage about 3.13 times, then divided by 8 to get approximate voltage per cell.
  if (voltage<voltage_Now && voltage != 0.00){ //change global variable and send voltage to phone only when voltage drops
   voltage_Now=voltage;
   float voltage_send = voltage_Now*1000;
String voltage_string = "v" + String(voltage_send,2);
//Serial.println(voltage_Now);
String voltage_string_to_send=voltage_string.substring(0, 4) + '\n';
 Serial.println(voltage_string_to_send);
  //output examples: v1.39, v1.31 etc
 } 
}



 

void loop() { 
     //Is true when a command with a ? in the end is given or if the input reaches a length atleast 4
    if(complete_command){
      command();
    }
    // Updates the camera servos angle.
  camera::updateServo(camera_x_value_received_from_the_raspberry, camera_y_value_received_from_the_raspberry);

//_activation of the flame-sensor: if flame_activation is false, this if-statement won't be executed. 
//This is were the detection of flame is done
    if(flame_activation && ++flame_delay >= 500){
        readFlame();  

    }
  if (++voltage_delay >= 1000){
sendVoltage();
  }  
//_activation of ultrasonic sensor
  if(ultrasonic_activation && ++collision_delay >= 10){
   //Pings the ultrasonic sensors
  collisionControl();
    }   
  


//_activation for temperature sensor
if(temperature_activation && ++temp_delay >= 10000){
    readTemp();   
    temp_delay = 0;
}
}

/*
  * Reads the input from the serial
  * Author: Tim Jonasson
  */

void serialEvent() {
    //Records one command at a time when availible
    
    // String in = Serial.readStringUntil('?');
    
    while(Serial.available()){
        char data = Serial.read();
      
        //Makes sure no artifacts are in the command
        if(data != '?' && isAlphaNumeric(data)){
            input += data;
        }else if (input.length() >= 4){
        
            //Makes the loop do the command when the input is the end character ?
            complete_command = true;
            break;
        }
    }
}
