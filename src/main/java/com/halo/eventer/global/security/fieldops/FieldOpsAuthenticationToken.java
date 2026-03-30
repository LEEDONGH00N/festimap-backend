package com.halo.eventer.global.security.fieldops;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class FieldOpsAuthenticationToken extends AbstractAuthenticationToken {

    private final FieldOpsUserDetails principal;

    public FieldOpsAuthenticationToken(
            FieldOpsUserDetails principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public FieldOpsUserDetails getPrincipal() {
        return principal;
    }
}
