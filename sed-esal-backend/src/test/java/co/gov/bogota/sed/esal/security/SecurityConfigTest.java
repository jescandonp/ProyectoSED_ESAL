package co.gov.bogota.sed.esal.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de seguridad por rol para T4 (actualizado en T9).
 *
 * Verifica las reglas de autorización usando los controladores reales.
 *
 * Usuarios de prueba (perfil local-dev):
 *   admin@educacionbogota.edu.co / admin123       → ADMINISTRADOR
 *   expedidor@educacionbogota.edu.co / expedidor123 → EXPEDIDOR
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-dev")
@Transactional
class SecurityConfigTest {

    private static final String ADMIN_USER  = "admin@educacionbogota.edu.co";
    private static final String ADMIN_PASS  = "admin123";
    private static final String EXPID_USER  = "expedidor@educacionbogota.edu.co";
    private static final String EXPID_PASS  = "expedidor123";

    @Autowired
    private MockMvc mockMvc;

    // =========================================================================
    // 1. Endpoints públicos — sin autenticación
    // =========================================================================

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerIsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // 2. Endpoint admin sin autenticación → 401
    // =========================================================================

    @Test
    void adminEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/auditoria"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // 3. ADMINISTRADOR accede a endpoint admin → 200 (no 401/403)
    // =========================================================================

    @Test
    void adminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/auditoria")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS)))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // 4. EXPEDIDOR intenta acceder a endpoint admin → 403
    // =========================================================================

    @Test
    void expedidorCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/auditoria")
                        .with(httpBasic(EXPID_USER, EXPID_PASS)))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // 5. EXPEDIDOR accede a listado de ESAL → 200 (no 401/403)
    // =========================================================================

    @Test
    void expedidorCanAccessEsalesEndpoint() throws Exception {
        mockMvc.perform(get("/api/esales")
                        .with(httpBasic(EXPID_USER, EXPID_PASS)))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // 6. EXPEDIDOR intenta crear ESAL (POST /api/esales) → 403
    // =========================================================================

    @Test
    void expedidorCannotCreateEsal() throws Exception {
        mockMvc.perform(post("/api/esales")
                        .with(httpBasic(EXPID_USER, EXPID_PASS))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // 7. ADMINISTRADOR puede crear ESAL (POST /api/esales) → 201
    // =========================================================================

    @Test
    void adminCanCreateEsal() throws Exception {
        mockMvc.perform(post("/api/esales")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType("application/json")
                        .content("{\"nombre\":\"ESAL Seguridad Test\"}"))
                .andExpect(status().isCreated());
    }

    // =========================================================================
    // 8. ADMINISTRADOR accede a endpoint admin POST (importaciones) → no 401/403
    //    El endpoint real requiere multipart, pero la seguridad debe pasar.
    //    Enviamos multipart vacío para verificar que la seguridad permite el acceso.
    // =========================================================================

    @Test
    void adminCanPostToAdminEndpoint() throws Exception {
        // El endpoint real de importaciones requiere multipart/form-data con campo "archivo".
        // Enviamos una petición sin el campo para verificar que la seguridad permite el acceso.
        // El resultado esperado es 400 (bad request), no 401 ni 403.
        mockMvc.perform(post("/api/admin/importaciones/esal")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType("multipart/form-data"))
                .andExpect(status().isBadRequest());
    }
}
