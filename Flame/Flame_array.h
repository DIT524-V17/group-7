//
// Created by tim on 2017-05-12.
//

#ifndef FLAME_FLAME_H
#define FLAME_FLAME_H

//#include <Arduino.h>
#include <string>
#include <iostream>

using namespace std;

class Flame{
public:
    bool getFlameBool(){return flame_bool;}
    int  getFlameValue(){return flame_reading;}
    int  getPin(){return FLAME_SENSOR_PIN;}
    void setFlame(){flame_reading = 500;}//analogRead(FLAME_SENSOR_PIN);}
    void setFlame(int input){this->flame_reading = input;}
    void setFlameBool(bool value){flame_bool = value;}
    void setPin(const int pin){FLAME_SENSOR_PIN = pin;}

    Flame(){};
    Flame(const int pin, int reading, bool flame){this->FLAME_SENSOR_PIN = pin;this->flame_reading = reading;this->flame_bool = flame;}
    Flame(const int pin){this->FLAME_SENSOR_PIN = pin;}
    ~Flame(){ cout << "deleted";}

private:
    bool flame_bool = false;
    int flame_reading = 0;
    int FLAME_SENSOR_PIN = 0;
};

class Flame_array {
public:
    Flame_array();
    Flame_array(int flame_pin);
    Flame_array(int flame_pin, int flame_pin_2);
    Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3);
    Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4);
    Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4, int flame_pin_5);
    ~Flame_array(){};

    void removeFlame();
    void addFlame(int flame_pin);
    Flame* getArray(){return flame_array;}

    string read();
    void setIndex(int x){index = x;}
    int getArraySize(){return flame_array_size;};

private:
    bool flame_sun = false;

    int average_value = 500;
    int index = 0;

    const int MAX_READING = 650;
    const int FLAME_SENSITIVITY = 200;

    bool compareToAverage(int &value);

    Flame flame_array[5];
    int flame_array_size = 5;
    string read_flame_array();
};

#endif //FLAME_FLAME_H