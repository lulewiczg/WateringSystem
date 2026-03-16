package com.github.lulewiczg.watering.config.dto;

/**
 * Interface for configs that support pins.
 */
public interface PinnableConfig {

    Integer getPin();

    void setPin(Integer pin);

}
