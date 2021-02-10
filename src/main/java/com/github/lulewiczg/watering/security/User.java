package com.github.lulewiczg.watering.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO for user configuration.
 */
@Data
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @NotEmpty
    private String name;

    @NotEmpty
    @ToString.Exclude
    private String password;

    @NotEmpty
    private List<Role> roles;

    public String getUsername() {
        return name;
    }
}
