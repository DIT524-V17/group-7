//
// Created by tim on 2017-05-12.
//

#include "Flame_array.h"

Flame_array::Flame_array(int flame_pin) {
    flame_array.push_back(Flame(flame_pin));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2) {
    this ->(flame_pin);
    flame_array.push_back(Flame(flame_pin_2));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3) {
    this ->(flame_pin, flame_pin_2);
    flame_array.push_back(Flame(flame_pin_3));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4) {
    this ->(flame_pin, flame_pin_2, flame_pin_3);
    flame_array.push_back(Flame(flame_pin_4));
}
Flame_array::Flame_array(int flame_pin, int flame_pin_2, int flame_pin_3, int flame_pin_4, int flame_pin_5) {
    this ->(flame_pin, flame_pin_2, flame_pin_3, flame_pin_4);
    flame_array.push_back(Flame(flame_pin_5));
}