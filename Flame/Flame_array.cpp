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
    changeArraySize();
    ptrFlame_array[flame_array_size - 1] = Flame(flame_pin);
    ptrFlame_array[flame_array_size - 1].setFlame();
    int i = 0;
    for(i; i < flame_array_size; i++){
        std::cout << (*(ptrFlame_array + i)).getPin() << ", ";
        std::cout << (*(ptrFlame_array + i)).getFlameValue() << ", ";
        std::cout << (*(ptrFlame_array + i)).getFlameBool() << "     hi"<< std::endl;

    }
}

void Flame_array::changeArraySize() {
    Flame tempArray[this->flame_array_size];
    int i = 0;
    for(i; i < this->flame_array_size; i++){
        tempArray[i].setFlameBool(ptrFlame_array[i].getFlameBool());
        tempArray[i].setFlame(ptrFlame_array[i].getFlameValue());
        tempArray[i].setPin(ptrFlame_array[i].getPin());
    }
    Flame* ptrTempArray = tempArray;
    this->ptrFlame_array = ptrTempArray;
}

void Flame_array::removeFlame() {
    this->flame_array_size--;
    changeArraySize();
}
string Flame_array::read_flame_array() {
    ptrFlame_array[flame_array_size - 1].setFlame();

    ptrFlame_array[flame_array_size - 1].getFlameValue() >= MAX_READING ? flame_sun = true : flame_sun = false;


    if(!flame_sun) {
        std::ostringstream index_string;
        index_string << "f0" << index;
        string command = index_string.str();
        index_string.str("");
        index_string.clear();
        int flame_value = ptrFlame_array[flame_array_size - 1].getFlameValue();
        if (compareToAverage(flame_value)) {
            ptrFlame_array[flame_array_size - 1].setFlameBool(true);
            command += "1";
            return command;
        }
        ptrFlame_array[flame_array_size - 1].setFlameBool(false);
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