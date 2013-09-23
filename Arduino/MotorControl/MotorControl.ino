#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("Marc Tan",
		     "Motor Control",
		     "Motor Control on Android",
		     "1.0",
		     "http://www.marctan.com",
		     "0000000012345678");

 
int basePin = 9;

void setup(){
  pinMode(basePin, OUTPUT);
  Serial.begin(9600);
  acc.powerOn();
}

void loop(){
  byte msg[1];
  
  if (acc.isConnected()) {
       int len = acc.read(msg, sizeof(msg), 1);

       if(len > 0){
           analogWrite(basePin, msg[0]);
       }
   }else{
     analogWrite(basePin, 0);
   }
}





