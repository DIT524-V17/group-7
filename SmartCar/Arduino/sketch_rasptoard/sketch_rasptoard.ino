#include <Smartcar.h>
#include <Servo.h>

Servo ESC1, steer;
int value=90;//throttle
int pos=45;
String input = "";

void arm(){
  value=90;
}

void setup() {
  ESC1.attach(7);  
  steer.attach(5);
  ESC1.write(90);
  pinMode(12, OUTPUT);
  steer.write(45);
    
  Serial.begin(9600);  
  arm();  
}

void loop() { 
}

void serialEvent() {
      char data = Serial.read();
      if(data != '?' && isAlphaNumeric(data)){
          input += data;
      }else {
            if (input.charAt(0) == 'a'){
                //Steers
                  steer.write(input.substring(1, 4).toInt());              // tell servo to go to position in variable 'pos'
                  digitalWrite(12, LOW);
                  input = "";
            }else if (input.charAt(0) == 'd'){
                //Controls speed
                  ESC1.write(input.substring(1, 4).toInt());              // tell servo to go to position in variable 'pos'
                  digitalWrite(12, HIGH);
                  input = "";
            }
             
      }
  
  }
