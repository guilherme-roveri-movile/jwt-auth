package com.gbr.gateways.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gbr.domains.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(of = {"uid"})
@Builder
public class UserPrincipal implements UserDetails {

    private String uid;
    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserPrincipal fromUser(final User user) {
        return UserPrincipal.builder()
                .uid(user.getUid())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        user.getAuthorities().stream()
                                .map(roleName -> new SimpleGrantedAuthority(roleName)).collect(Collectors.toList()))
                .build();
    }
}
