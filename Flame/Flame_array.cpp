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
    this->flame_array_size++;
    Flame tempArray[flame_array_size];
    Flame* ptrTempArray = tempArray;
    int i = 0;
    for(i; i < flame_array_size - 1; i++){
        ptrTempArray[i] = ptrFlame_array[i];
    }
    ptrFlame_array = ptrTempArray;
    *(ptrFlame_array + flame_array_size -1) = Flame(flame_pin);
    (*(ptrFlame_array + flame_array_size -1)).setFlame();
    std::cout << (*(ptrFlame_array + flame_array_size -1)).getPin() << std::endl;
}
/*
void Flame_array::changeArraySize() {
    Flame tempArray[flame_array_size];
    Flame* ptrTempArray = tempArray;
    int i = 0;
    for(i; i < flame_array_size - 1; i++){
        ptrTempArray[i] = ptrFlame_array[i];
    }
    ptrFlame_array = ptrTempArray;
}*/

void Flame_array::removeFlame() {
    this->flame_array_size--;
    Flame tempArray[flame_array_size];
    Flame* ptrTempArray = tempArray;
    int i = 0;
    for(i; i < flame_array_size - 1; i++){
        ptrTempArray[i] = ptrFlame_array[i];
    }
    ptrFlame_array = ptrTempArray;
}
string Flame_array::read_flame_array() {
    (*(ptrFlame_array + index)).setFlame();

    (*(ptrFlame_array + index)).getFlameValue() >= MAX_READING ? flame_sun = true : flame_sun = false;


    if(!flame_sun) {
        std::ostringstream index_string;
        index_string << "f0" << index;
        string command = index_string.str();
        index_string.str("");
        index_string.clear();
        int flame_value = (*(ptrFlame_array + index)).getFlameValue();
        if (compareToAverage(flame_value)) {
            (*(ptrFlame_array + index)).setFlameBool(true);
            command += "1";
            return command;
        }
        (*(ptrFlame_array + index)).setFlameBool(false);
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
    if (flame_array_size <= this->index){
        this->index = 0;
    }
    string temp = read_flame_array();
    if(temp.compare("f100")) {
        this->index++;
    }
    return temp;
}