package com.github.lulewiczg.watering.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Valve {

    private String name;

    private ValveType type;

    private boolean open;

}
