package com.github.lulewiczg.watering.config.dto;

import com.pi4j.io.gpio.Pin;

/**
 * Interface for devices that can be controlled through pins
 */
public interface Steerable {

    /**
     * Get pin name
     *
     * @return pin name
     */
    String getPinName();

    /**
     * Gets pin.
     *
     * @return pin
     */
    Pin getPin();

    /**
     * Sets pin.
     *
     * @param pin pin
     */
    void setPin(Pin pin);
}
