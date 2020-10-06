package com.github.lulewiczg.watering.service.io;

import com.pi4j.io.gpio.Pin;

/**
 * Interface for IO communication.
 */
public interface IOService {

    /**
     * Turns pin on.
     *
     * @param pin pin
     */
    void toggleOn(Pin pin);


    /**
     * Turns pin off.
     *
     * @param pin pin
     */
    void toggleOff(Pin pin);

    /**
     * Reads pin state.
     *
     * @param pin pin
     * @return state
     */
    boolean readPin(Pin pin);

    /**
     * Reads analog value from pin
     *
     * @param pin pin
     * @return analog value
     */
    double analogRead(Pin pin);
}
