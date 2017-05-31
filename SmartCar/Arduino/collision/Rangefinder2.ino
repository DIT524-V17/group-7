/*   SR_04 Pins:
        VCC: +5VDC
        Trig : Trigger (INPUT) - Pin11
        Echo: Echo (OUTPUT) - Pin 12
        GND: GND
 */
 #include <Servo.h>
 int pos=45;
 Servo rotat;
int trigPin = 4;    //Trig - green Jumper
int echoPin = 2;    //Echo - yellow Jumper
long duration, cm, inches;
 
void setup() {
  pinMode(13, OUTPUT);
  //Serial Port begin
  Serial.begin (9600);
  //Define inputs and outputs
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  rotat.attach(5);
}
 
void loop()
{
 
 int distancecheck = measure();
 while (distancecheck>=12){
  Serial.println();
 for (pos = 45; pos <= 85; pos += 5) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
   rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
   int distance=measure();
    Serial.println();
     Serial.println(distance);
      Serial.println();
  }
 
  for (pos = 85; pos >= 45; pos -= 5) { // goes from 180 degrees to 0 degrees
    rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);  
     int distance=measure();
    Serial.println();
     Serial.println(distance);
      Serial.println();// waits 15ms for the servo to reach the position
  }
   for (pos = 45; pos >= 5; pos -= 5) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
   rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
   int distance=measure();
    Serial.println();
     Serial.println(distance);
      Serial.println();
  }
 
  for (pos = 5; pos <= 45; pos += 5) { // goes from 180 degrees to 0 degrees
    rotat.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);  
     int distance=measure();
    Serial.println();
     Serial.println(distance);
      Serial.println();// waits 15ms for the servo to reach the position
  }

  
 }
 // delay(50);
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
  if (distance<=12){
     digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(100);                       // wait for a second
  digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
  delay(100);
  }
  return distance;
  
}

