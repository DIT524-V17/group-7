//
// Created by tim on 2017-05-12.
//

#include "Flame_array.h"

Flame_array::Flame_array(unsigned short flame_pin) {
    flame_array[0].setPin(flame_pin);
    amountOfSensors = 1;
}
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    amountOfSensors = 2;
}
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    flame_array[2].setPin(flame_pin_3);
    amountOfSensors = 3;
}
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3, unsigned short flame_pin_4) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    flame_array[2].setPin(flame_pin_3);
    flame_array[3].setPin(flame_pin_4);
    amountOfSensors = 4;
}
Flame_array::Flame_array(unsigned short flame_pin, unsigned short flame_pin_2, unsigned short flame_pin_3, unsigned short flame_pin_4, unsigned short flame_pin_5) {
    flame_array[0].setPin(flame_pin);
    flame_array[1].setPin(flame_pin_2);
    flame_array[2].setPin(flame_pin_3);
    flame_array[3].setPin(flame_pin_4);
    flame_array[4].setPin(flame_pin_5);
    amountOfSensors = 5;
}

char* Flame_array::read_flame_array() {
    (*(flame_array + index)).setFlame();

    (*(flame_array + index)).getFlameValue() >= MAX_READING ? flame_sun = true : flame_sun = false;
    if(!flame_sun) {
        command[0] = 'f';
        command[1] = '0';
        command[2] =  '0' + index;
        int flame_value = (*(flame_array + index)).getFlameValue();
        if (compareToAverage(flame_value)) {
            (*(flame_array + index)).setFlameBool(true);
            command[3] = '1';
            return command;
        }
        (*(flame_array + index)).setFlameBool(false);
        command[3] = '0';
        return command;
    }
    return sun;
}

bool Flame_array::compareToAverage(int &value){
    average_value = 0;
    int tempValue = value;
    int i = 0;
    for(i;i < amountOfSensors; i++){
        average_value += flame_array[i].getFlameValue();
    }
    if(amountOfSensors > 1) {
        average_value -= tempValue;
        average_value = average_value / (amountOfSensors - 1);

        if (tempValue > FLAME_SENSITIVITY + average_value) {
            return true;
        }
    } else if (tempValue > FLAME_SENSITIVITY && tempValue < MAX_READING) {
        return true;
    }
    return false;
}

char* Flame_array::read() {
    if (flame_array[index].getPin() == 0 || index == 5){
        this->index = 0;
    }
    flame_status = read_flame_array();
    if(flame_status[1] != '1') {
        this->index++;
    }
    return flame_status;
}