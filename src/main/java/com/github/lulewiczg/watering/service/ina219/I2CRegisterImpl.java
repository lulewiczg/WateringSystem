package com.github.lulewiczg.watering.service.ina219;

/*
 * I2CRegisterImpl.java
 *
 * Copyright 2017 Greg Steckman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.service.ina219.enums.RegisterAddress;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * An implementation of the INA219RegisterIf that uses the Raspberry Pi I2C bus.
 */
@Log4j2
class I2CRegisterImpl implements INA219Register {
    private I2CDevice device;

    /**
     * Create a new I2CRegisterImple using the specified device address.
     *
     * @param address Address of the device with which this instance communications.
     * @throws java.io.IOException If the I2C device could not be created.
     */
    I2CRegisterImpl(Address address) throws IOException {
        try {
            device = I2CFactory.getInstance(I2CBus.BUS_1).getDevice(address.getValue());
        } catch (UnsupportedBusNumberException e) {
            log.error("BUS_1 no supported", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeRegister(RegisterAddress ra, int value) throws IOException {
        device.write(ra.getValue(), new byte[]{(byte) ((value >> 8) & 0xFF), (byte) (value & 0xFF)});
    }

    /**
     * {@inheritDoc}
     */
    public int readRegister(RegisterAddress ra) throws IOException {
        byte[] buf = new byte[2];
        device.read(ra.getValue(), buf, 0, buf.length);
        return ((buf[0] & 0xFF) << 8) | (buf[1] & 0xFF);
    }

    /**
     * {@inheritDoc}
     */
    public short readSignedRegister(RegisterAddress ra) throws IOException {
        byte[] buf = new byte[2];
        device.read(ra.getValue(), buf, 0, buf.length);
        return (short) ((buf[0] << 8) | (buf[1] & 0xFF));
    }

}
