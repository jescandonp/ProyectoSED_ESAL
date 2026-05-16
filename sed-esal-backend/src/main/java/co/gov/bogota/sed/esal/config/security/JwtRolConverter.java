package co.gov.bogota.sed.esal.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convierte un JWT de Azure AD a un Authentication de Spring Security,
 * mapeando el claim de roles institucionales a ROLE_ADMINISTRADOR / ROLE_EXPEDIDOR.
 *
 * Solo activo en el perfil weblogic (I4).
 */
@Component
@Profile("weblogic")
public class JwtRolConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${esal.security.jwt.rol-claim:roles}")
    private String rolClaim;

    @Value("${esal.security.jwt.admin-claim-value:ADMINISTRADOR}")
    private String adminValue;

    @Value("${esal.security.jwt.expedidor-claim-value:EXPEDIDOR}")
    private String expedidorValue;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList(rolClaim);
        Collection<GrantedAuthority> authorities = resolverAuthorities(roles);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> resolverAuthorities(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(r -> r.equals(adminValue) || r.equals(expedidorValue))
                .map(r -> r.equals(adminValue) ? "ROLE_ADMINISTRADOR" : "ROLE_EXPEDIDOR")
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
