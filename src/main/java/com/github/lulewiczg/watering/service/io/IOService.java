package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.state.dto.Sensor;

/**
 * Interface for IO communication.
 */
public interface IOService {

    /**
     * Turns pin on.
     *
     * @param pin pin
     */
    void toggleOn(int pin);

    /**
     * Turns pin off.
     *
     * @param pin pin
     */
    void toggleOff(int pin);

    /**
     * Reads pin state.
     *
     * @param pin pin
     * @return state
     */
    boolean readPin(int pin);

    /**
     * Reads analog raw value from sensor.
     *
     * @param sensor sensor
     * @return analog value
     */
    double analogRead(Sensor sensor);

}
