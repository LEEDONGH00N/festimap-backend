package com.halo.eventer.global.security.fieldops;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import lombok.Getter;

@Getter
public class FieldOpsUserDetails implements UserDetails {

    private final Long sessionId;
    private final String token;
    private final Long categoryId;
    private final Long festivalId;
    private final Collection<? extends GrantedAuthority> authorities;

    public FieldOpsUserDetails(FieldOpsSession session) {
        this.sessionId = session.getId();
        this.token = session.getToken();
        this.categoryId = session.getCategory().getId();
        this.festivalId =
                session.getCategory().getFestivalModule().getFestival().getId();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_FIELD_STAFF"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return token;
    }

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
}
