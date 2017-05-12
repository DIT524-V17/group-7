//
// Created by tim on 2017-05-12.
//

#include <sstream>
#include "Flame_array.h"

Flame_array::Flame_array(int flame_pin) {
    flame_vector.push_back(Flame(flame_pin));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2) {
    this ->(flame_pin);
    flame_vector.push_back(Flame(flame_pin_2));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3) {
    this ->(flame_pin, flame_pin_2);
    flame_vector.push_back(Flame(flame_pin_3));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4) {
    this ->(flame_pin, flame_pin_2, flame_pin_3);
    flame_vector.push_back(Flame(flame_pin_4));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4, int flame_pin_5) {
    this ->(flame_pin, flame_pin_2, flame_pin_3, flame_pin_4);
    flame_vector.push_back(Flame(flame_pin_5));
}

string Flame_array::read_flame_array() {
    if (flame_vector.size() >= index){
        return "Error: Index out of bounds";
    }
    flame_vector.at(index).readFlame();

    flame_vector.at(index).getFlameValue() >= MAX_READING ? flame_sun = true : flame_sun = false;

    if(!flame_sun) {
        std::ostringstream index_string;
        index_string << "f0" << index;
        string command = index_string.str();
        index_string.str("");
        index_string.clear();
        if (compareToAverage(flame_vector.at(index).getFlameValue())) {
            command += "1";
            return command;
        }
        command += "0";
        return command;
    }
    return "f100";
}

bool Flame_array::compareToAverage(int value){
    average_value = 0;
    for(Flame vector: flame_vector){
        average_value += vector.getFlameValue();
    }
    average_value -= value;
    average_value = average_value/4;

    if(value + FLAME_SENSITIVITY >= average_value){
        return true;
    }
    return false;
}