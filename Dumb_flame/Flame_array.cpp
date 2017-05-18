//
// Created by tim on 2017-05-12.
//

#include "Flame_array.h"

//sets the pin of the flame pin and puts it at the first index of the array
Flame_array::Flame_array(unsigned short flame_pin) {
    flame_array[0].setPin(flame_pin);
    amountOfSensors = 1;
}
//sets the pin of the flame pins and puts them into the array
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    amountOfSensors = 2;
}
//sets the pin of the flame pins and puts them into the array
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    flame_array[2].setPin(flame_pin_3);
    amountOfSensors = 3;
}
//sets the pin of the flame pins and puts them into the array
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3, unsigned short flame_pin_4) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    flame_array[2].setPin(flame_pin_3);
    flame_array[3].setPin(flame_pin_4);
    amountOfSensors = 4;
}
//sets the pin of the flame pins and puts them into the array
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3, unsigned short flame_pin_4, unsigned short flame_pin_5) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    flame_array[2].setPin(flame_pin_3);
    flame_array[3].setPin(flame_pin_4);
    flame_array[4].setPin(flame_pin_5);
    amountOfSensors = 5;
}
//Returns a pointer to the char array that is the output of the library
char* Flame_array::read_flame_array() {

    //Reads the current value of the flame sensor
    (*(flame_array + index)).setFlame();

    //Checks if the reading could be the sun
    flame_sun = (*(flame_array + index)).getFlameValue() >= MAX_READING;
    if(!flame_sun) {
        //Starts creating the array
        command[0] = 'f';
        command[1] = '0';
        command[2] = '0' + index;

        //Gets the value of the flame sensor
        int flame_value = (*(flame_array + index)).getFlameValue();
        //Compares the reading to the readings of the other flame sensors
        if (compareToAverage(flame_value)) {
            //If its a flame it sets the flame bool to true and returns a char array that says it sees a flame
            (*(flame_array + index)).setFlameBool(true);
            command[3] = '1';
            return command;
        }
        //If it isnt a flame it sets the flame bool to false and then returns a char array that says that it doesnt see flame
        (*(flame_array + index)).setFlameBool(false);
        command[3] = '0';
        return command;
    }
    //If the reading might be from sunlight then this will be returned
    return sun;
}

//Compares the value with the values of the rest of the flame sensors and returns true if its at least bigger than the average
//value + the flame sensitivity
bool Flame_array::compareToAverage(int &value){
    average_value = 0;
    int tempValue = value;
    int i = 0;
    //Loops through all the sensors and saves the values
    for(i;i < amountOfSensors; i++){
        average_value += flame_array[i].getFlameValue();
    }
    //Calculates the average value of the other sensors
    if(amountOfSensors > 1) {
        average_value -= tempValue;
        average_value = average_value / (amountOfSensors - 1);

        //checks if the value is bigger than the average value + the flame sensitivity
        if (tempValue > FLAME_SENSITIVITY + average_value) {
            return true;
        }
        //Checks if the sensor sees a flame when its only one sensor
    } else if (tempValue > FLAME_SENSITIVITY && tempValue < MAX_READING) {
        return true;
    }
    return false;
}

//Reads all the sensors and then returns their status
char* Flame_array::read() {
    //Checks if the next index in the char array contains an active flame sensor
    if (flame_array[index].getPin() == 0 || index == 5){
        this->index = 0;
    }
    //reads the current flame sensor
    flame_status = read_flame_array();
    //moves the index to the next sensor if the sensor is seeing something that isnt the sun
    //the array {'f', '1', '0', ''0} is sent if think that its the sun
    if(flame_status[1] != '1') {
        this->index++;
    }
    //returns the array
    return flame_status;
}