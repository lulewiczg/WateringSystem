package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.pi4j.io.gpio.Pin;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * Service for GPIO communication.
 */
@Log4j2
@Service
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnExpression("!${com.github.lulewiczg.watering.mockedIO:false}")
public class IOServiceImpl implements IOService {

    private static final String ERR = "Not yet implemented!";

    @Override
    public void toggleOn(Pin pin) {
        throw new IllegalStateException(ERR);
    }

    @Override
    public void toggleOff(Pin pin) {
        throw new IllegalStateException(ERR);
    }

    @Override
    public boolean readPin(Pin pin) {
        throw new IllegalStateException(ERR);
    }

    @Override
    public double analogRead(Pin pin) {
        throw new IllegalStateException(ERR);
    }
}
