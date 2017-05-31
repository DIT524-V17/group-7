#include <NewPing.h>
#include <Servo.h>

//Multiple ultrasonics
#define ECHO_PIN 2 //front
#define TRIG_PIN 4
#define ECHO_PIN2 A8 // front right
#define TRIG_PIN2 A9
#define ECHO_PIN3 A10 // front left
#define TRIG_PIN3 A11
#define ECHO_PIN4 A12 // back 
#define TRIG_PIN4 A13
#define ULTRASONIC_SENSOR_COUNT 4 //Number of ultrasonic sensors. Update if more sensors are added

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
int steer_angle;

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

NewPing sonars [ULTRASONIC_SENSOR_COUNT] { //Array of NewPing sonars, must be updated if more are added
  NewPing(TRIG_PIN, ECHO_PIN, MAX_DISTANCE_ULTRASONIC),
  NewPing(TRIG_PIN2, ECHO_PIN2, MAX_DISTANCE_ULTRASONIC),
  NewPing(TRIG_PIN3, ECHO_PIN3, MAX_DISTANCE_ULTRASONIC),
  NewPing(TRIG_PIN4, ECHO_PIN4, MAX_DISTANCE_ULTRASONIC)
};


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
}

void loop() { 
    //Is true when a command with a ? in the end is given or if the input reaches a length atleast 4
    if(completeCommand){
        switch(input.charAt(0)){
     
        //Controls the steering
        case 'a':
        steer_angle=input.substring(1, 4).toInt();
            steer.write(input.substring(1, 4).toInt());
            break;
      
        //Controls the speed
        case 'd':
            velocity = input.substring(1, 4).toInt();
            if ((!flame && !obstacle) || velocity >= 90){
                motor.write(velocity);
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
    //Pings all ultrasonic sensors
    range_string = sonars[0].ping_cm();
    range = range_string.toInt();
    int range2=sonars[1].ping_cm();
    int range3=sonars[2].ping_cm();
    int range4=sonars[3].ping_cm();
    range_string = "c" + range_string + '\n';
    
   if (lastDistance != range && ++distance_Delay >= 100){
        Serial.write(range);
        Serial.println(range_string);
        
        lastDistance = range;
        distance_Delay = 0;
    }
        

      
      

        
    //Allows the car to drive backwards if an obstacle is in from of the car
    if (velocity<90){ //velocity<90
      //Is true if the range is less than 50 cm or if the flame sensor has set the flame variable to true
        if (((range != 0 && range <= 50) ) || ((range2 != 0 && range2 <= 30))
        ||((range3 != 0 && range3 <= 30) )) //|| flame
        
        {if (range >= 30 && range2!=0 && range2 <= 20 && steer_angle == 0){
          obstacle=false;
          velocity=70;
        } 
        else if (range >=30 && range3!=0 && range3 <= 20 && steer_angle == 90){
          obstacle=false;
          velocity=70;
        }
 else {
                obstacle = true;
                collision_delay = 0;
                digitalWrite(LED_PIN, HIGH);
                motor.write(START_POSITION_MOTOR_SERVO +30);
                
                motor.write(START_POSITION_MOTOR_SERVO);
            }
        //Sets obstacle to false if the obstacle in front of the car disappeared
        } else if (obstacle){
            obstacle = false;
             digitalWrite(LED_PIN, LOW);
            motor.write(START_POSITION_MOTOR_SERVO);
        }
    }


if (velocity>90){ 
      //Is true if the range is less than 50 cm or if the flame sensor has set the flame variable to true
        if (range4 != 0 && range4 <= 50) //|| flame
        
      
 {
                obstacle = true;
                collision_delay = 0;
                digitalWrite(LED_PIN, HIGH);
                motor.write(START_POSITION_MOTOR_SERVO -30);
               
                motor.write(START_POSITION_MOTOR_SERVO);
            }
        //Sets obstacle to false if the obstacle in front of the car disappeared
         else if (obstacle){
            obstacle = false;
             digitalWrite(LED_PIN, LOW);
            motor.write(START_POSITION_MOTOR_SERVO);
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
