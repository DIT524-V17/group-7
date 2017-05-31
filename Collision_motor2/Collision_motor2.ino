/*   SR_04 Pins:
        VCC: +5VDC
        Trig : Trigger (INPUT) - Pin11
        Echo: Echo (OUTPUT) - Pin 12
        GND: GND
 */
 #include <Servo.h>
 int pos=45;
 Servo rotat;
 Servo ESC1;
int trigPin = 4;    //Trig - green Jumper
int echoPin = 2;    //Echo - yellow Jumper
long duration, cm, inches;
 
void setup() {
  pinMode(13, OUTPUT);
  //Serial Port begin
 
  //Define inputs and outputs
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  rotat.attach(5);
  ESC1.attach(7);
   ESC1.write(90);
    Serial.begin (9600);
}
 
void loop()
{ 
 int distancecheck = measure();

 ESC1.write(105);}
if (distancecheck<=10){
  ESC1.write(90);
}
  Serial.println();
 for (pos = 45; pos <= 85; pos += 5) { // goes from 45 degrees to 85 degrees
    // in steps of 1 degree
   rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
   int distance=measure();
   if (distancecheck<=10){
  ESC1.write(90);
}
  }
  for (pos = 85; pos >= 45; pos -= 5) { // goes from 85 degrees to 45 degrees
    rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);  
     int distance=measure();
     if (distancecheck<=10){
  ESC1.write(90);
}
  }
   for (pos = 45; pos >= 5; pos -= 5) { // goes from 45 degrees to 5 degrees
    // in steps of 1 degree
   rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
   int distance=measure();
   if (distancecheck<=10){
  ESC1.write(90);
}
  }
  for (pos = 5; pos <= 45; pos += 5) { // goes from 5 degrees to 45 degrees
    rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);  
     int distance=measure();
     if (distance<=10){
  ESC1.write(90);
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

