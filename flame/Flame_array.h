//
// Created by tim on 2017-05-12.
//

#ifndef FLAME_FLAME_H
#define FLAME_FLAME_H

//#include <Arduino.h>
#include <vector>
#include <string>
#include <iostream>

using namespace std;

class Flame{
public:
    bool getFlameBool(){return flame_bool;}
    int getFlameValue(){return flame_reading;}
    void setFlame(){flame_reading = 700;}//analogRead(FLAME_SENSOR_PIN);}
    void setFlameBool(bool value){flame_bool = value;}
    int getPin(){return FLAME_SENSOR_PIN;}
    Flame();
    Flame(const int pin){FLAME_SENSOR_PIN = pin;};
    ~Flame(){};

private:
    bool flame_bool = false;
    int flame_reading;
    int FLAME_SENSOR_PIN;
};

class Flame_array {
public:
    Flame_array();
    Flame_array(int flame_pin[], int size);

    ~Flame_array(){};

    void removeFlame();
    void addFlame(int flame_pin);
    vector <Flame>* getVector(){return &flame_vector;}


    string read();
    void setIndex(int x){index = x;}

private:
    bool flame_sun;

    int average_value = 500;
    int index = 0;

    const int MAX_READING = 650;
    const int FLAME_SENSITIVITY = 200;

    bool compareToAverage(int &value);

    vector<Flame> flame_vector;
    string read_flame_array();

};

#endif //FLAME_FLAME_H
