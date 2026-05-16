package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.PermisosDto;
import co.gov.bogota.sed.esal.dto.UsuarioContextoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@educacionbogota.edu.co", roles = {"ADMINISTRADOR"})
    void meRetornaUsuarioAdmin() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        UsuarioContextoDto dto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UsuarioContextoDto.class);

        assertEquals("admin@educacionbogota.edu.co", dto.getUsuario());
        assertEquals("ADMINISTRADOR", dto.getRol());
    }

    @Test
    @WithMockUser(username = "expedidor@educacionbogota.edu.co", roles = {"EXPEDIDOR"})
    void meRetornaUsuarioExpedidor() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UsuarioContextoDto dto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UsuarioContextoDto.class);

        assertEquals("EXPEDIDOR", dto.getRol());
    }

    @Test
    void meSinAutenticacionRetorna401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@educacionbogota.edu.co", roles = {"ADMINISTRADOR"})
    void permisosAdminTieneAccesoCompleto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/permisos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PermisosDto dto = objectMapper.readValue(
                result.getResponse().getContentAsString(), PermisosDto.class);

        assertEquals("ADMINISTRADOR", dto.getRol());
        assertTrue(dto.isPuedeAdministrar());
        assertTrue(dto.isPuedeBuscar());
        assertTrue(dto.isPuedeConsultarAuditoria());
    }

    @Test
    @WithMockUser(username = "expedidor@educacionbogota.edu.co", roles = {"EXPEDIDOR"})
    void permisosExpedidorSinAccesoAdmin() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/permisos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PermisosDto dto = objectMapper.readValue(
                result.getResponse().getContentAsString(), PermisosDto.class);

        assertEquals("EXPEDIDOR", dto.getRol());
        assertFalse(dto.isPuedeAdministrar());
        assertTrue(dto.isPuedeBuscar());
        assertFalse(dto.isPuedeConsultarAuditoria());
    }
}
