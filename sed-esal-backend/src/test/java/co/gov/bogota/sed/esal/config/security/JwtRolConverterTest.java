package co.gov.bogota.sed.esal.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JwtRolConverterTest {

    private JwtRolConverter converter;

    @BeforeEach
    void setUp() {
        converter = new JwtRolConverter();
        ReflectionTestUtils.setField(converter, "rolClaim", "roles");
        ReflectionTestUtils.setField(converter, "adminValue", "ADMINISTRADOR");
        ReflectionTestUtils.setField(converter, "expedidorValue", "EXPEDIDOR");
    }

    @Test
    void mapeoAdministrador() {
        Jwt jwt = buildJwt(Collections.singletonList("ADMINISTRADOR"));
        AbstractAuthenticationToken token = converter.convert(jwt);
        assertNotNull(token);
        List<String> authorities = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertTrue(authorities.contains("ROLE_ADMINISTRADOR"));
        assertFalse(authorities.contains("ROLE_EXPEDIDOR"));
    }

    @Test
    void mapeoExpedidor() {
        Jwt jwt = buildJwt(Collections.singletonList("EXPEDIDOR"));
        AbstractAuthenticationToken token = converter.convert(jwt);
        List<String> authorities = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertTrue(authorities.contains("ROLE_EXPEDIDOR"));
        assertFalse(authorities.contains("ROLE_ADMINISTRADOR"));
    }

    @Test
    void rolDesconocidoNoGeneraAuthority() {
        Jwt jwt = buildJwt(Collections.singletonList("ROL_DESCONOCIDO"));
        AbstractAuthenticationToken token = converter.convert(jwt);
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void sinRolesEnClaimRetornaVacio() {
        Jwt jwt = buildJwt(Collections.emptyList());
        AbstractAuthenticationToken token = converter.convert(jwt);
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void multipleRolesFiltraCorrectamente() {
        Jwt jwt = buildJwt(Arrays.asList("ADMINISTRADOR", "ROL_EXTERNO", "EXPEDIDOR"));
        AbstractAuthenticationToken token = converter.convert(jwt);
        Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
        List<String> names = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertTrue(names.contains("ROLE_ADMINISTRADOR"));
        assertTrue(names.contains("ROLE_EXPEDIDOR"));
        assertEquals(2, authorities.size());
    }

    private Jwt buildJwt(List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "usuario-test");
        claims.put("roles", roles);
        claims.put("iss", "https://login.microsoftonline.com/test/v2.0");
        claims.put("aud", Collections.singletonList("client-id-test"));

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");

        return Jwt.withTokenValue("token-test")
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
