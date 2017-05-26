#include <iostream>
#include "Flame_array.h"

int main() {

    Flame_array flame (1, 2, 3, 4, 5);

    int i =1;
    int x;
    int temp;
    while(true) {
        i =0;
        for(i; i < 5; i++) {
            std::cin >> temp;
            flame.getArray()[i].setFlame();
        }
         x = 0;
        for (x; x < 5; x++) {
            std::cout << flame.getArray()[x].getPin() << ", ";
            std::cout << flame.getArray()[x].getFlameValue() << ", ";
            std::cout << flame.read() << ", ";
            std::cout << flame.getArray()[x].getFlameBool() << std::endl;
        }
    }
    return 0;
}