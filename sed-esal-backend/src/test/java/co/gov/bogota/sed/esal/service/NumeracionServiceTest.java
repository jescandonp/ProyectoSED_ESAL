package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.dto.NumeracionDto;
import co.gov.bogota.sed.esal.dto.NumeracionUpdateDto;
import co.gov.bogota.sed.esal.repository.NumeracionCertificadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NumeracionServiceTest {

    @Autowired
    private NumeracionService numeracionService;

    @Autowired
    private NumeracionCertificadoRepository numeracionRepository;

    @BeforeEach
    void limpiar() {
        numeracionRepository.deleteAll();
    }

    @Test
    void obtenerActual_sinRegistro_retornaDefecto() {
        NumeracionDto dto = numeracionService.obtenerActual();
        assertThat(dto.getPrefijo()).isEqualTo("ESAL");
        assertThat(dto.getAnio()).isEqualTo(Year.now().getValue());
        assertThat(dto.getUltimoConsecutivo()).isEqualTo(0L);
    }

    @Test
    void reservarSiguienteNumero_primerNumero_generaFormato() {
        String numero = numeracionService.reservarSiguienteNumero("testuser");
        assertThat(numero).matches("ESAL-\\d{4}-\\d{6}");
        assertThat(numero).startsWith("ESAL-" + Year.now().getValue() + "-");
        assertThat(numero).endsWith("000001");
    }

    @Test
    void reservarSiguienteNumero_dosLlamadas_incrementaConsecutivo() {
        String n1 = numeracionService.reservarSiguienteNumero("testuser");
        String n2 = numeracionService.reservarSiguienteNumero("testuser");
        assertThat(n1).endsWith("000001");
        assertThat(n2).endsWith("000002");
    }

    @Test
    void actualizarPrefijo_cambiaPrefijo_reflejaEnConsultaActual() {
        NumeracionUpdateDto update = new NumeracionUpdateDto();
        update.setPrefijo("CERT");
        NumeracionDto dto = numeracionService.actualizarPrefijo(update, "admin");
        assertThat(dto.getPrefijo()).isEqualTo("CERT");
    }
}
