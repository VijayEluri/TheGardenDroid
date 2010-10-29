/*
  Sensor.h - Library for modeling Sensors attached to the Arduino.
  Created by Lee Clarke, Oct 20, 2010.
  Released into the public domain.
*/
#ifndef Sensor_h
#define Sensor_h

#include "PollEvent.h"
#include "WProgram.h"

class Sensor
{
  public:
    Sensor();
    Sensor(int sensorId, String name);
    Sensor(int sensorId, String name, unsigned long pollInterval);
    
    void setLowThreshold(int value);
    void setHighThreshold(int value);
    void setPollInterval(unsigned long mills);
//void setInterupt(?);
    unsigned long getPollInterval();
    int getSensorValue();
    int getSensorState();
    int getSensorID();
    String getSensorName();
    PollEvent poll;
  protected:
    int _lowThresh;
    int _highThresh;
    int _id;
    String _name;
    unsigned long _pollInterval;
    byte sendStatus;
};
#endif
