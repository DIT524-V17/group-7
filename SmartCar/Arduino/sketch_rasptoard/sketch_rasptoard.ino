#include <Smartcar.h>
#include <Servo.h>

Servo ESC1, steer;
int value=90;//throttle
int pos=45;
int stop=0;//has to be boolean, but who cares?
String input = "";
int switchInput;

void arm(){
  value=90;
}

void setup() {
  ESC1.attach(7);  
  steer.attach(5);
  ESC1.write(90);
    
  Serial.begin(9600);  
  arm();  
}

void loop() { 
  if(Serial.available() > 0) {
      char data = Serial.read();
      if(data != 'Q'){
          input += data;
      }else {
          if(input == "Hello world"){
            switchInput = 1;
          } else if(input == "Hello world2"){
            switchInput = 2;
          } else if(input == "Hello world3"){
            switchInput = 3;
          } 
          switch(switchInput){
              case 1: 
                //Steer right
                  for (pos = 45; pos <= 90; pos += 1) { // goes from 0 degrees to 180 degrees
                  // in steps of 1 degree
                  steer.write(pos);              // tell servo to go to position in variable 'pos'
                  delay(15);                       // waits 15ms for the servo to reach the position
                  }
                  input = "";
                  break;
              case 2: 
                //Reset steering
                  for (pos = 90; pos >= 45; pos -= 1) { // goes from 0 degrees to 180 degrees
                  // in steps of 1 degree
                  steer.write(pos);              // tell servo to go to position in variable 'pos'
                  delay(15);                       // waits 15ms for the servo to reach the position
                  }
                  input = "";
                  break;
              case 3:
                  //steer left
                  for (pos = 45; pos >=0; pos -= 1) { // goes from 0 degrees to 180 degrees
                      // in steps of 1 degree
                      steer.write(pos);              // tell servo to go to position in variable 'pos'
                      delay(15);                       // waits 15ms for the servo to reach the position
                  }

              default:
                  input = "";
                  break;
              }
      }
  }
}

