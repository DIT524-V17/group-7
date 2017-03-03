#include <Smartcar.h>
#include <Servo.h>

Servo ESC1, steer;
int value=90;//throttle
int pos=30;
int stop=0;//has to be boolean, but who cares?

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
  if (stop!=2){
 /* for (value=105;value<110;value+=1){
  ESC1.write(value);
  delay(1000);
  }
  delay(1000);
  value=90;
  ESC1.write(value);
  /*for (pos = 0; pos <= 55; pos += 5) { // goes from 0 degrees to 55 degrees
    // in steps of 1 degree
    steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  
   delay(1000);
  for (value=50;value>40;value-=0){
  ESC1.write(value);
  delay(1000);
  }
   value=90;
  ESC1.write(value);
 // delay(2000);
  /*for (pos = 55; pos >= 0; pos -= 5) { // goes from 180 degrees to 0 degrees
    steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }*/

value=70;
 ESC1.write(value);
  delay(1000);
   
  for (pos = 45; pos <= 90; pos += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
   steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  delay(1000);
  for (pos = 90; pos >= 45; pos -= 1) { // goes from 180 degrees to 0 degrees
    steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  delay(1000);
    for (pos = 45; pos >=0; pos -= 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
   steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  delay(1000);
  for (pos = 0; pos <= 45; pos += 1) { // goes from 180 degrees to 0 degrees
    steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
   delay(1000);
  //ESC1.write(value);
 
 value=90;
  ESC1.write(value);
  delay(1500);
  


value=100;
 ESC1.write(value);
  delay(1000);
  for (pos = 45; pos <= 90; pos += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
   steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  delay(1000);
  for (pos = 90; pos >= 45; pos -= 1) { // goes from 180 degrees to 0 degrees
    steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  delay(1000);
    for (pos = 45; pos >=0; pos -= 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
   steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  delay(1000);
  for (pos = 0; pos <= 45; pos += 1) { // goes from 180 degrees to 0 degrees
    steer.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
   delay(1000);





  value=90;
  ESC1.write(value);
  delay(1500);
  
  /*value=60;
 ESC1.write(value);
  delay(3000);
   value=90;
  ESC1.write(value);
  delay(1500);*/
stop=2;

  } 
}

