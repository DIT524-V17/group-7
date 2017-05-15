//
// Created by tim on 2017-05-12.
//

#include <sstream>
#include "Flame_array.h"

Flame_array::Flame_array(int flame_pins[], int size) {
    for(int i = 0; i< size; i++){
        addFlame(flame_pins[i]);
    }
}

void Flame_array::addFlame(int flame_pin) {
    flame_vector.push_back(Flame(flame_pin));
    flame_vector.at(flame_vector.size() - 1).setFlame();
}

void Flame_array::removeFlame() {
    flame_vector.pop_back();
}
string Flame_array::read_flame_array() {
    if (flame_vector.size() <= index){
        return "Error: Index out of bounds";
    }
    //flame_vector.at(index).setFlame();

    flame_vector.at(index).getFlameValue() >= MAX_READING ? flame_sun = true : flame_sun = false;


    if(!flame_sun) {
        std::ostringstream index_string;
        index_string << "f0" << index;
        string command = index_string.str();
        index_string.str("");
        index_string.clear();
        int flame_value = flame_vector.at(index).getFlameValue();
        if (compareToAverage(flame_value)) {
            command += "1";
            return command;
        }
        command += "0";
        return command;
    }
    return "f100";
}

bool Flame_array::compareToAverage(int &value){
    average_value = 0;
    int tempValue = value;
    for(Flame vector: flame_vector){
        average_value += vector.getFlameValue();
    }
    average_value -= tempValue;
    average_value = average_value/(flame_vector.size() - 1);

    if(tempValue + FLAME_SENSITIVITY <= average_value){
        return true;
    }
    return false;
}