package com.github.lulewiczg.watering.config.dto;

import com.pi4j.io.gpio.Pin;

/**
 * Interface for configs that support pins.
 */
public interface PinnableConfig {

    String getPinName();

    void setPinName(String name);

    Pin getPin();

    void setPin(Pin pin);

}
