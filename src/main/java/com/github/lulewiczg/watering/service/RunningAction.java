package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.service.actions.Action;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
public class RunningAction {

    private final Action<?, ?> action;
    private final Object param;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude()
    private final Runnable killJob;
}
