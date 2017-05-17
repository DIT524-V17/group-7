//
// Created by tim on 2017-05-12.
//

#include <sstream>
#include "Flame_array.h"

Flame_array::Flame_array(int flame_pin) {
    flame_array[0] = *new Flame(flame_pin);
    flame_array[1] = *new Flame(99);
    flame_array[2] = *new Flame(99);
    flame_array[3] = *new Flame(99);
    flame_array[4] = *new Flame(99);
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2) {
    flame_array[0] = *new Flame(flame_pin);
    flame_array[1] = *new Flame(flame_pin_2);
    flame_array[2] = *new Flame(99);
    flame_array[3] = *new Flame(99);
    flame_array[4] = *new Flame(99);
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3) {
    flame_array[0] = *new Flame(flame_pin);
    flame_array[1] = *new Flame(flame_pin_2);
    flame_array[2] = *new Flame(flame_pin_3);
    flame_array[3] = *new Flame(99);
    flame_array[4] = *new Flame(99);
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4) {
    flame_array[0] = *new Flame(flame_pin);
    flame_array[1] = *new Flame(flame_pin_2);
    flame_array[2] = *new Flame(flame_pin_3);
    flame_array[3] = *new Flame(flame_pin_4);
    flame_array[4] = *new Flame(99);
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4, int flame_pin_5) {
    flame_array[0] = *new Flame(flame_pin);
    flame_array[1] = *new Flame(flame_pin_2);
    flame_array[2] = *new Flame(flame_pin_3);
    flame_array[3] = *new Flame(flame_pin_4);
    flame_array[4] = *new Flame(flame_pin_5);
}

string Flame_array::read_flame_array() {
    //(*(flame_array + index)).setFlame();

    (*(flame_array + index)).getFlameValue() >= MAX_READING ? flame_sun = true : flame_sun = false;
    if(!flame_sun) {
        std::ostringstream index_string;
        index_string << "f0" << index;
        string command = index_string.str();
        index_string.str("");
        index_string.clear();
        int flame_value = (*(flame_array + index)).getFlameValue();
        if (compareToAverage(flame_value)) {
            (*(flame_array + index)).setFlameBool(true);
            command += "1";
            return command;
        }
        (*(flame_array + index)).setFlameBool(false);
        command += "0";
        return command;
    }
    return "f100";
}

bool Flame_array::compareToAverage(int &value){
    average_value = 0;
    int tempValue = value;
    for(Flame vector: flame_array){
        average_value += vector.getFlameValue();
    }
    average_value -= tempValue;
    average_value = average_value/4;

    if(tempValue > FLAME_SENSITIVITY + average_value){
        return true;
    }
    return false;
}

string Flame_array::read() {
    if (flame_array[index].getPin() == 99 || index == 5){
        this->index = 0;
    }
    string temp = read_flame_array();
    if(temp.compare("f100")) {
        this->index++;
    }
    return temp;
}