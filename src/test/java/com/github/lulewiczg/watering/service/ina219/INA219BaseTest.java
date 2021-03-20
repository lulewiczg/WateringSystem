package com.github.lulewiczg.watering.service.ina219;

/*
 * INA219BaseTest.java
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
import com.github.lulewiczg.watering.service.ina219.enums.VoltageRange;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the INA219Base class using an INA219 Simulator.
 */
public class INA219BaseTest {

    /**
     * Performs a very basic test of the INA219Base class using the simulator.
     *
     * @throws java.io.IOException Not thrown due to use of the simulator.
     */
    @Test
    public void basic() throws IOException {
        INA219Simulator s = new INA219Simulator(20e-3, 12.0);
        INA219Base i = new INA219Base(s, 0.1, 3.0, VoltageRange.V32, Pga.GAIN_8, Adc.BITS_12,
                Adc.BITS_12);

        assertEquals(20e-3, i.getShuntVoltage(), 1E-9);
        assertEquals(12.0, i.getBusVoltage(), 1E-9);
        assertEquals(200E-3 * 12.0, i.getPower(), 0.01);
        assertEquals(200E-3, i.getCurrent(), 0.001);
    }
}
