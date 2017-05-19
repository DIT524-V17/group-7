//
// Created by tim on 2017-05-12.
//

#ifndef FLAME_FLAME_H
#define FLAME_FLAME_H

#include <Arduino.h>

class Flame{
public:
    //Returns true when the flame sensor sees a flame and false when it doesnt
    bool getFlameBool(){return flame_bool;}
    //Returns the current value of the output from the flame sensor
    int  getFlameValue(){return flame_reading;}
    //Returns the pin of the flame sensor
    int  getPin(){return FLAME_SENSOR_PIN;}

    //Reads from the flame sensor and updates the current value of the output
    void setFlame(){flame_reading = analogRead(FLAME_SENSOR_PIN);}
    //Sets the bool value of the flame sensor to the inputed value
    void setFlameBool(bool value){flame_bool = value;}
    //Sets the pin of the flame sensor
    void setPin(const int pin){FLAME_SENSOR_PIN = pin;}

    Flame(){};
    Flame(const int pin){this->FLAME_SENSOR_PIN = pin;}
    //Destructor
    ~Flame(){};

private:
    //False for when it isn't seeing a flame
    //True when it sees a flame
    bool flame_bool = false;
    //The saved output from the flame sensor between approximately 0 and 1000
    int flame_reading = 0;
    //The pin number
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

    //Returns a pointer to the output from the flame sensors
    char* read();
    //Returns the array containing the flame sensors
    Flame* getArray(){return flame_array;}

private:
    //The char array that is going to contain the output from the library
    char command[4];
    //Is true while a flame sensor thinks it sees the sun
    bool flame_sun = false;

    //The output for when the flame sensor sees the sun
    char sun[4]{'f', '1', '0', '0'};
    //A variable for saving the pointer to the char array that is the output from the read() method
    char* flame_status;

    //Initialized the values to this
    int average_value = 500;
    int index = 0;
    int amountOfSensors = 0;

    //Every reading higher than this will be interpreted as the sun
    const int MAX_READING = 650;
    //The number that is used to decide whether the flame sensor sees a flame or not
    const int FLAME_SENSITIVITY = 200;
    //Compares the inputed value to the average of all the other flame sensors
    bool compareToAverage(int &value);

    //The array of flame sensors
    Flame flame_array[5];
    //The size of the array
    char* read_flame_array();
};

#endif //FLAME_FLAME_H