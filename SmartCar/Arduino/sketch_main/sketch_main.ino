#include <Servo.h>

Servo motor, steer;
String input;
boolean completeCommand;
char data;

void setup() {
    //Sets the motor and steering servos to the default positions
    motor.attach(7); 
    motor.write(90); 
    steer.attach(5);
    steer.write(45);
    pinMode(12, OUTPUT);
    
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
