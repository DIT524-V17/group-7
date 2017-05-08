
const int FLAME_SENSOR_1_PIN = 14;
const int FLAME_SENSOR_2_PIN = 15;
const int FLAME_SENSOR_3_PIN = 16;
const int FLAME_SENSOR_4_PIN = 17;
const int FLAME_SENSOR_5_PIN = 18;

const int MAX_READING = 800;
const int DONT_READ_ABOVE = 450;

int analogValue_1 = 0;
int analogValue_2 = 0;
int analogValue_3 = 0;
int analogValue_4 = 0;
int analogValue_5 = 0;

void setup() {
    Serial.begin(9600);
}

void loop() {

    analogValue_1 = analogRead(FLAME_SENSOR_1_PIN);
    analogValue_2 = analogRead(FLAME_SENSOR_2_PIN);
    analogValue_3 = analogRead(FLAME_SENSOR_3_PIN);
    analogValue_4 = analogRead(FLAME_SENSOR_4_PIN);
    analogValue_5 = analogRead(FLAME_SENSOR_5_PIN);



    if (analogValue_1 > MAX_READING
        || analogValue_2 > MAX_READING
        || analogValue_3 > MAX_READING
        || analogValue_4 > MAX_READING
        || analogValue_5 > MAX_READING){
    }

    Serial.print(analogValue_1);   
    Serial.print(", ");
    Serial.print(analogValue_2);
    Serial.print(", ");
    Serial.print(analogValue_3);
    Serial.print(", ");
    Serial.print(analogValue_4);
    Serial.print(", ");
    Serial.println(analogValue_5);

}
