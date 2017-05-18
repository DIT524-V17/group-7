#include <Flame_array.h>
Flame_array flame(14,15,16,17,18);
char* temp;
void setup(){
  Serial.begin(9600);
}

void loop(){
  delay(100);
  temp = flame.read();
  Serial.println(temp);

}


