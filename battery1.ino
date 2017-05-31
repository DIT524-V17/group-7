#define VOLT_PIN A5

void setup() {
 Serial.begin(9600); 

}

void loop() {
 int value = analogRead(VOLT_PIN);
float voltage = value * (5.00 / 1023.00); //*3.125 because 47 and 100 K Ohm resistors lower voltage about 3.125 times, then divided by 8 to get voltage per cell
Serial.println(voltage);
//Serial.write(voltage);

// Values for display 
//>1.23 - 75%-100%
//1.23 - 50%-75%
//1.21 - 25%-50%
//1.18 - <25%
//1.00< - critical
}
