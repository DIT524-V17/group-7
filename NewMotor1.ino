
#include <Servo.h>
#include <Smartcar.h>
  Servo sr1;
//Car car;
 int pos = 0; //Position variable

void arm(){

setSpeed(0); //Speed variable delay;
}
void setSpeed(int speed){

int angle = map(speed, 0,90, 0, 180); //Sets servo positions to different speeds 
sr1.write(angle);
}


void setup() {
 
sr1.attach(4);
 //car.begin();
}

void loop() {

int speed; //declare seed

for(speed = 0; speed <= 10; speed += 2) { //Cycles speed up to 10% power

setSpeed(speed); 

delay(1000);

}

delay(3000); //Stays on for 3 seconds

for(speed = 10; speed > 0; speed -= 2) { // Cycles speed down to 0% power

setSpeed(speed); 
delay(1000);

}

setSpeed(0); //Sets speed to zero 

delay(1000); 

}
