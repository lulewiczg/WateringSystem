package com.github.lulewiczg.watering.service.ina219;

/*
 * INA219.java
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
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.service.ina219.enums.Pga;
import com.github.lulewiczg.watering.service.ina219.enums.VoltageRange;

import java.io.IOException;

/**
 * This class provides a high level interface to the Texas Instruments INA219 current monitor over the Raspberry Pi I2C bus.
 */
public class INA219 extends INA219Base {
    /**
     * Constructs a new INA219 instance.
     *
     * @param address            I2C Address of the INA219.
     * @param shuntResistance    Value in ohms of the current sense shunt resistor.
     * @param maxExpectedCurrent Maximum expected current, in Amps.
     * @param busVoltageRange    Either 16V or 32V.
     * @param pga                Gain range.
     * @param badc               Bus voltage ADC sample size and averaging setting.
     * @param sadc               Shunt resistor voltage ADC sample size and averaging setting.
     * @throws java.io.IOException If the configuration or calibration registers cannot be written.
     */
    public INA219(Address address, double shuntResistance, double maxExpectedCurrent,
                  VoltageRange busVoltageRange, Pga pga, Adc badc, Adc sadc) throws IOException {
        super(new I2CRegisterImpl(address), shuntResistance, maxExpectedCurrent, busVoltageRange, pga, badc, sadc);
    }

}
