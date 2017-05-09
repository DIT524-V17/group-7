
const int FLAME_SENSOR_1_PIN = 15;
const int FLAME_SENSOR_2_PIN = 14;
const int FLAME_SENSOR_3_PIN = 16;
const int FLAME_SENSOR_4_PIN = 17;
const int FLAME_SENSOR_5_PIN = 18;

const int MAX_READING = 600;
const int FLAME_SENSITIVITY = 200;

boolean flame_1_sun = false;
boolean flame_2_sun = false;
boolean flame_3_sun = false;
boolean flame_4_sun = false;
boolean flame_5_sun = false;

int analogValue_1 = 0;
int analogValue_2 = 0;
int analogValue_3 = 0;
int analogValue_4 = 0;
int analogValue_5 = 0;

int temp_name = 1;

void setup() {
    Serial.begin(9600);
}

void loop() {
    switch(temp_name){
      case 1:
          analogValue_1 = analogRead(FLAME_SENSOR_1_PIN);
          readFlame(analogValue_1, flame_1_sun);
          break;
      case 2:
          analogValue_2 = analogRead(FLAME_SENSOR_2_PIN);
          readFlame(analogValue_2, flame_2_sun);
          break;
      case 3:
          analogValue_3 = analogRead(FLAME_SENSOR_3_PIN);
          readFlame(analogValue_3, flame_3_sun);
          break;
      case 4:
          analogValue_4 = analogRead(FLAME_SENSOR_4_PIN);
          readFlame(analogValue_4, flame_4_sun);
          break;
      case 5:
          analogValue_5 = analogRead(FLAME_SENSOR_5_PIN);
          readFlame(analogValue_5, flame_5_sun);
          break;
      default:
             temp_name = 1; 
    
    }
}

void readFlame(int reading, boolean &flame_sun){
    if (reading >= MAX_READING){
         flame_sun = true; 
    } else {
         flame_sun = false; 
    }
          
    if (!flame_sun){
         int medelvarde = analogValue_1 + analogValue_2 + analogValue_3 + analogValue_4 + analogValue_5;
         medelvarde = (medelvarde - reading) / 4;
         if (reading > medelvarde + FLAME_SENSITIVITY){
             Serial.print(temp_name);
             Serial.println(" Flame");
             Serial.print(analogValue_1);   
    
             Serial.print(", ");
             Serial.print(analogValue_2);
             Serial.print(", ");
             Serial.print(analogValue_3);
             Serial.print(", ");
             Serial.print(analogValue_4);
             Serial.print(", ");
             Serial.println(analogValue_5);

         } else {
            Serial.print(temp_name);
            Serial.println(" No flame");
         }
         
         temp_name++;
     }
}
