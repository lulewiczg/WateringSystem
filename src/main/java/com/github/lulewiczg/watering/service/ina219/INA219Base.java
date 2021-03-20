package com.github.lulewiczg.watering.service.ina219;

/*
 * INA219Base.java
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

import com.github.lulewiczg.watering.service.ina219.enums.Adc;
import com.github.lulewiczg.watering.service.ina219.enums.Pga;
import com.github.lulewiczg.watering.service.ina219.enums.RegisterAddress;
import com.github.lulewiczg.watering.service.ina219.enums.VoltageRange;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * Base class for the INA219 driver implementation. Register read/writes are abstracted using the INA219RegisterIF to allow testing without an I2C device present.
 */
class INA219Base {
    private static final double SHUNT_VOLTAGE_LSB = 10e-6;
    private static final double BUS_VOLTAGE_LSB = 4e-3;
    private static final int POWER_LSB_SCALE = 20;
    private final double currentLSB;
    private final INA219Register register;

    /**
     * Constructs an INA219Base with the specified values.
     *
     * @param reg                Register interface to use for read/write access.
     * @param shuntResistance    Value in ohms of the current sense shunt resistor.
     * @param maxExpectedCurrent Maximum expected current, in Amps.
     * @param busVoltageRange    Either 16V or 32V.
     * @param pga                Gain range.
     * @param badc               Bus voltage ADC sample size and averaging setting.
     * @param sadc               Shunt resistor voltage ADC sample size and averaging setting.
     */
    @SneakyThrows
    public INA219Base(INA219Register reg, double shuntResistance, double maxExpectedCurrent,
                      VoltageRange busVoltageRange, Pga pga, Adc badc, Adc sadc) {
        register = reg;

        currentLSB = (maxExpectedCurrent / 32768);
        int cal = (int) (((0.04096 * 32768) / (maxExpectedCurrent * shuntResistance)));

        configure(busVoltageRange, pga, badc, sadc);
        register.writeRegister(RegisterAddress.CALIBRATION, cal);
    }

    /**
     * Reads and returns the shunt voltage.
     *
     * @return The shunt voltage.
     */
    @SneakyThrows
    public double getShuntVoltage() {
        int rval = register.readSignedRegister(RegisterAddress.SHUNT_VOLTAGE);
        return rval * SHUNT_VOLTAGE_LSB;
    }

    /**
     * Reads and returns the bus voltage.
     *
     * @return The bus voltage.
     */
    @SneakyThrows
    public double getBusVoltage() {
        int rval = register.readRegister(RegisterAddress.BUS_VOLTAGE);
        return (rval >> 3) * BUS_VOLTAGE_LSB;
    }

    /**
     * Reads and returns the power.
     *
     * @return The power value.
     */
    @SneakyThrows
    public double getPower() {
        int rval = register.readRegister(RegisterAddress.POWER);
        return rval * POWER_LSB_SCALE * currentLSB;
    }

    /**
     * Reads and returns the current.
     *
     * @return The current value.
     */
    @SneakyThrows
    public double getCurrent() {
        int rval = register.readSignedRegister(RegisterAddress.CURRENT);
        return rval * currentLSB;
    }

    /**
     * Configures the INA219 with the specified parameters.
     *
     * @param busVoltageRange Bus voltage range to write to the configuration register.
     * @param pga             Gain range to write to the configuration register.
     * @param badc            Bus voltage ADC setting to write to the configuration register.
     * @param sadc            Shunt voltage ADC setting to write to the configuration register.
     * @throws java.io.IOException If the configuration register could not be written.
     */
    private void configure(VoltageRange busVoltageRange, Pga pga, Adc badc, Adc sadc) throws IOException {
        int regValue = (busVoltageRange.getValue() << 13) | (pga.getValue() << 11) | (badc.getValue() << 7)
                | (sadc.getValue() << 3) | 0x7;

        register.writeRegister(RegisterAddress.CONFIGURATION, regValue);
    }
}
