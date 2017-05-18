//
// Created by tim on 2017-05-12.
//

#ifndef FLAME_FLAME_H
#define FLAME_FLAME_H

#include <Arduino.h>

class Flame{
public:
    bool getFlameBool(){return flame_bool;}
    int  getFlameValue(){return flame_reading;}
    int  getPin(){return FLAME_SENSOR_PIN;}
    void setFlame(){flame_reading = analogRead(FLAME_SENSOR_PIN);}
    void setFlame(int input){this->flame_reading = input;}
    void setFlameBool(bool value){flame_bool = value;}
    void setPin(const int pin){FLAME_SENSOR_PIN = pin;}

    Flame(){};
    Flame(const int pin){this->FLAME_SENSOR_PIN = pin;}
    ~Flame(){};

private:
    bool flame_bool = false;
    int flame_reading = 0;
    int FLAME_SENSOR_PIN = 0;
};

class Flame_array {
public:
    Flame_array();
    Flame_array(unsigned short flame_pin);
    Flame_array(unsigned short flame_pin, unsigned short flame_pin_2);
    Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3);
    Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3, unsigned short flame_pin_4);
    Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3, unsigned short flame_pin_4, unsigned short flame_pin_5);
    ~Flame_array(){};

    void removeFlame();
    void addFlame(int flame_pin);
    Flame* getArray(){return flame_array;}

    char* read();
    void setIndex(int x){index = x;}
    int getArraySize(){return flame_array_size;};

private:
    char command[4];
    bool flame_sun = false;

    char sun[4]{'f', '1', '0', '0'};
    char* flame_status;

    int average_value = 500;
    int index = 0;
    int amountOfSensors;

    const int MAX_READING = 650;
    const int FLAME_SENSITIVITY = 200;

    bool compareToAverage(int &value);

    Flame flame_array[5];
    int flame_array_size = 5;
    char* read_flame_array();
};

#endif //FLAME_FLAME_H