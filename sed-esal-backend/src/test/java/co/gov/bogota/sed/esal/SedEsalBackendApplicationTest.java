package co.gov.bogota.sed.esal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-dev")
class SedEsalBackendApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void exposesHealthEndpointForLocalDev() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void exposesBearerAuthSchemeInOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.BearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.BearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.BearerAuth.bearerFormat").value("JWT"));
    }

    @Test
    void configuresApprovedLocalDevUsers() {
        assertThat(userDetailsService.loadUserByUsername("admin@educacionbogota.edu.co")
                .getAuthorities())
                .extracting(Object::toString)
                .contains("ROLE_ADMINISTRADOR");

        assertThat(userDetailsService.loadUserByUsername("expedidor@educacionbogota.edu.co")
                .getAuthorities())
                .extracting(Object::toString)
                .contains("ROLE_EXPEDIDOR");
    }
}
