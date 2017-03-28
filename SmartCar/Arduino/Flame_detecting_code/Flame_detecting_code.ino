//int led_pin = 13 ;                                               // initializing the pin 13 as the led pin

int flame_sensor_pin = 6 ;                           // initializing pin 7 as the sensor output pin

int flame_pin = HIGH ;                                   // state of sensor




void setup ( )  {

 // pinMode ( led_pin , OUTPUT ) ;                               // declaring led pin as output pin

  pinMode ( flame_sensor_pin , INPUT ) ;             // declaring sensor pin as input pin for Arduino

  Serial.begin ( 9600 ) ;                                    // setting baud rate at 9600

}




void loop ( ) {

   flame_pin = digitalRead ( flame_sensor_pin ) ;                // reading from the sensor

  if (flame_pin == HIGH )                                        // applying condition

  {

    Serial.println ( " FLAME " ) ;

   // digitalWrite ( led_pin  , HIGH ) ;            // if state is high, then turn high the led

  }

  else

  {

    Serial.println ( " no flame " ) ;

  //  digitalWrite ( led_pin , LOW ) ;             // otherwise turn it low

  } 

}
