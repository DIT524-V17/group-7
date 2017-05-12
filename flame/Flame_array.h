//
// Created by tim on 2017-05-12.
//

#ifndef FLAME_FLAME_H
#define FLAME_FLAME_H

#include <Arduino.h>
#include <vector>
#include <string>
using namespace std;

class Flame_array {
public:
    Flame_array();
    Flame_array(int flame_pin);
    Flame_array(int flame_pin, int flame_pin_2);
    Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3);
    Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4);
    Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4, int flame_pin_5);

    ~Flame_array();

    string read_flame_array();

private:
    bool flame_sun;

    int average_value;
    int index = 0;

    const int MAX_READING = 650;
    const int FLAME_SENSITIVITY = 200;

    bool compareToAverage(int value);


    vector<Flame> flame_vector;

};
class Flame{
public:
    bool getFlameBool(){return flame_bool;}
    int getFlameValue(){return flame_reading;}
    void readFlame(){flame_reading = analogRead(FLAME_SENSOR_PIN);}
    void setFlameBool(bool value){flame_bool = value;}
    Flame();
    Flame(const int pin){FLAME_SENSOR_PIN = pin;};
    ~Flame();

private:
    bool flame_bool = false;
    int flame_reading = 500;
    int FLAME_SENSOR_PIN;
};

#endif //FLAME_FLAME_H
