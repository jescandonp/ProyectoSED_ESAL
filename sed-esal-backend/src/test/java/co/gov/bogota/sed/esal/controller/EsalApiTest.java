package co.gov.bogota.sed.esal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración para la API REST de ESALes (T9).
 *
 * Verifica los endpoints CRUD de ESALes con autenticación HTTP Basic.
 * Usa perfil local-dev para la configuración de seguridad.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-dev")
@Transactional
class EsalApiTest {

    private static final String ADMIN_USER = "admin@educacionbogota.edu.co";
    private static final String ADMIN_PASS = "admin123";
    private static final String EXPID_USER = "expedidor@educacionbogota.edu.co";
    private static final String EXPID_PASS = "expedidor123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // 1. Listar ESALes devuelve página vacía
    // =========================================================================

    @Test
    void listarEsalesDevuelvePageVacia() throws Exception {
        mockMvc.perform(get("/api/esales")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));
    }

    // =========================================================================
    // 2. Crear ESAL como ADMINISTRADOR → 201
    // =========================================================================

    @Test
    void crearEsalComoAdmin() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("nombre", "ESAL Test T9");
        body.put("idSipej", "SIPEJ-T9-001");
        body.put("domicilio", "Bogotá D.C.");

        mockMvc.perform(post("/api/esales")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("ESAL Test T9"));
    }

    // =========================================================================
    // 3. Crear ESAL como EXPEDIDOR → 403
    // =========================================================================

    @Test
    void crearEsalComoExpedidorFalla() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("nombre", "ESAL No Permitida");

        mockMvc.perform(post("/api/esales")
                        .with(httpBasic(EXPID_USER, EXPID_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // 4. Obtener ESAL por ID → 200
    // =========================================================================

    @Test
    void obtenerEsalPorId() throws Exception {
        // Crear primero
        Map<String, String> body = new HashMap<>();
        body.put("nombre", "ESAL Detalle T9");
        body.put("idSipej", "SIPEJ-T9-002");

        MvcResult createResult = mockMvc.perform(post("/api/esales")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        // Obtener detalle
        mockMvc.perform(get("/api/esales/" + id)
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("ESAL Detalle T9"));
    }

    // =========================================================================
    // 5. Actualizar ESAL → 200
    // =========================================================================

    @Test
    void actualizarEsal() throws Exception {
        // Crear primero
        Map<String, String> createBody = new HashMap<>();
        createBody.put("nombre", "ESAL Original T9");
        createBody.put("idSipej", "SIPEJ-T9-003");

        MvcResult createResult = mockMvc.perform(post("/api/esales")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Actualizar
        Map<String, String> updateBody = new HashMap<>();
        updateBody.put("nombre", "ESAL Actualizada T9");
        updateBody.put("domicilio", "Bogotá D.C. Actualizado");

        mockMvc.perform(put("/api/esales/" + id)
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ESAL Actualizada T9"));
    }

    // =========================================================================
    // 6. Cambiar estado de ESAL → 200
    // =========================================================================

    @Test
    void cambiarEstadoEsal() throws Exception {
        // Crear primero
        Map<String, String> createBody = new HashMap<>();
        createBody.put("nombre", "ESAL Estado T9");
        createBody.put("idSipej", "SIPEJ-T9-004");

        MvcResult createResult = mockMvc.perform(post("/api/esales")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Cambiar estado
        Map<String, String> estadoBody = new HashMap<>();
        estadoBody.put("estado", "SUSPENDIDO");

        mockMvc.perform(put("/api/esales/" + id + "/estado")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estadoBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SUSPENDIDO"));
    }

    @Test
    void mantenimientoPermiteGuardarInformacionPrincipalYPersoneriaComoAdmin() throws Exception {
        Map<String, String> createBody = new HashMap<>();
        createBody.put("nombre", "ESAL Mantenimiento I5");
        createBody.put("idSipej", "SIPEJ-I5-API-001");

        MvcResult createResult = mockMvc.perform(post("/api/esales/mantenimiento")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.informacionPrincipal.nombre").value("ESAL Mantenimiento I5"))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        Map<String, String> updateBody = new HashMap<>();
        updateBody.put("nombre", "ESAL Mantenimiento I5 Actualizada");
        updateBody.put("domicilio", "Bogota D.C.");

        mockMvc.perform(put("/api/esales/" + id + "/informacion-principal")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.informacionPrincipal.nombre").value("ESAL Mantenimiento I5 Actualizada"));

        Map<String, String> personeriaBody = new HashMap<>();
        personeriaBody.put("reconocimientoPersoneriaJuridica", "Resolucion API 001");
        personeriaBody.put("fechaReconocimientoPersoneriaJuridica", "2025-01-20");
        personeriaBody.put("entidadQueExpide", "SED");

        mockMvc.perform(put("/api/esales/" + id + "/personeria-juridica")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personeriaBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personeriaJuridica.reconocimientoPersoneriaJuridica").value("Resolucion API 001"));
    }

    @Test
    void expedidorNoPuedeUsarEndpointsDeMantenimiento() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("nombre", "ESAL Mantenimiento No Permitida");

        mockMvc.perform(post("/api/esales/mantenimiento")
                        .with(httpBasic(EXPID_USER, EXPID_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}
