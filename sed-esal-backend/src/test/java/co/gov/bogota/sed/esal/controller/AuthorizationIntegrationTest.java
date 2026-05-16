package co.gov.bogota.sed.esal.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de autorizacion: verifica que la matriz de roles del backend
 * bloquea o permite correctamente segun el rol del usuario.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    // ── EXPEDIDOR no puede acceder a admin ────────────────────────────────

    @Test
    @WithMockUser(roles = "EXPEDIDOR")
    void expedidorNoAccedeAdminCargaInicial() throws Exception {
        mockMvc.perform(post("/api/admin/importacion/historica")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EXPEDIDOR")
    void expedidorNoAccedeAdminAuditoria() throws Exception {
        mockMvc.perform(get("/api/admin/auditoria"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EXPEDIDOR")
    void expedidorNoAccedeAdminFirmantes() throws Exception {
        mockMvc.perform(get("/api/admin/firmantes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EXPEDIDOR")
    void expedidorNoCrearEsal() throws Exception {
        mockMvc.perform(post("/api/esales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    // ── EXPEDIDOR puede acceder a busqueda y certificados ─────────────────

    @Test
    @WithMockUser(roles = "EXPEDIDOR")
    void expedidorPuedeBuscarEsal() throws Exception {
        mockMvc.perform(get("/api/esales"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EXPEDIDOR")
    void expedidorPuedeVerCertificados() throws Exception {
        mockMvc.perform(get("/api/certificados/esales/1/historial"))
                .andExpect(status().isOk());
    }

    // ── ADMINISTRADOR puede acceder a todo ────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminAccedeAdminAuditoria() throws Exception {
        mockMvc.perform(get("/api/admin/auditoria"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminAccedeAdminFirmantes() throws Exception {
        mockMvc.perform(get("/api/admin/firmantes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminPuedeBuscarEsal() throws Exception {
        mockMvc.perform(get("/api/esales"))
                .andExpect(status().isOk());
    }

    // ── Sin autenticacion — 401 ───────────────────────────────────────────

    @Test
    void anonimoBuscarEsalRetorna401() throws Exception {
        mockMvc.perform(get("/api/esales"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonimoAdminRetorna401() throws Exception {
        mockMvc.perform(get("/api/admin/auditoria"))
                .andExpect(status().isUnauthorized());
    }
}
