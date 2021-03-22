package com.github.lulewiczg.watering.service.ina219.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of the valid I2C bus addresses to use with the INA219.
 * <p>
 * Note: this implementation does not include support addresses that have the A1 or A0 pins connected to SDA or SCL.
 */
@Getter
@RequiredArgsConstructor
public enum Address {
    ADDR_40(0x40), ADDR_41(0x41), ADDR_44(0x44), ADDR_45(0x45);

    private final int value;

    public static Address getAddress(int addr) {
        switch (addr) {
            case 0x40:
                return ADDR_40;
            case 0x41:
                return ADDR_41;
            case 0x44:
                return ADDR_44;
            case 0x45:
                return ADDR_45;
            default:
                return null;
        }
    }
}
