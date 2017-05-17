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
    Flame(const int pin){this->FLAME_SENSOR_PIN = pin;}
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
    Flame* getArray(){return ptrFlame_array;}


    string read();
    void setIndex(int x){index = x;}
    int getArraySize(){return flame_array_size;};

private:

    void changeArraySize();
    bool flame_sun;

    int average_value = 500;
    int index = 0;

    const int MAX_READING = 650;
    const int FLAME_SENSITIVITY = 200;

    bool compareToAverage(int &value);

    Flame flame_array[];
    Flame* ptrFlame_array = flame_array;
    int flame_array_size = 0;
    string read_flame_array();
};

#endif //FLAME_FLAME_H
