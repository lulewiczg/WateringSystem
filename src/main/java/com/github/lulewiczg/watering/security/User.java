package com.github.lulewiczg.watering.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

/**
 * DTO for user configuration.
 */
@Data
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @NotEmpty
    private String name;

    @NotEmpty
    @ToString.Exclude
    private String password;

    @NotEmpty
    private List<Role> roles;

    private List<SimpleGrantedAuthority> authorities;

    public User(@NotEmpty String name, @NotEmpty String password, @NotEmpty List<Role> roles) {
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
