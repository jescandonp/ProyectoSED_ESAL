package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.dto.NumeracionDto;
import co.gov.bogota.sed.esal.dto.NumeracionUpdateDto;
import co.gov.bogota.sed.esal.repository.NumeracionCertificadoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para NumeracionService (I3).
 *
 * 1.  consultar_creaConfiguracionInicial_prefijoesal
 * 2.  consultar_devuelveProximoNumeroFormateado
 * 3.  reservar_primerNumero_esalAnioConsecutivo1
 * 4.  reservar_segundoNumero_consecutivoIncrementa
 * 5.  reservar_dosLlamadas_noDuplicados
 * 6.  actualizar_cambiaPrefijo
 * 7.  actualizar_prefijoNulo_mantieneActual
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NumeracionServiceTest {

    @Autowired
    private NumeracionService numeracionService;

    @Autowired
    private NumeracionCertificadoRepository numeracionRepository;

    @Test
    void consultar_creaConfiguracionInicial_prefijoEsal() {
        NumeracionDto dto = numeracionService.consultar();
        assertThat(dto.getPrefijo()).isEqualTo("ESAL");
        assertThat(dto.getAnio()).isEqualTo(LocalDateTime.now().getYear());
        assertThat(dto.getUltimoConsecutivo()).isEqualTo(0L);
        assertThat(dto.getActivo()).isTrue();
    }

    @Test
    void consultar_devuelveProximoNumeroFormateado() {
        NumeracionDto dto = numeracionService.consultar();
        int anio = LocalDateTime.now().getYear();
        assertThat(dto.getProximoNumero()).isEqualTo("ESAL-" + anio + "-000001");
    }

    @Test
    void reservar_primerNumero_formatoCorrecto() {
        int anio = LocalDateTime.now().getYear();
        String numero = numeracionService.reservarSiguienteNumero();
        assertThat(numero).isEqualTo("ESAL-" + anio + "-000001");
    }

    @Test
    void reservar_segundoNumero_consecutivoIncrementa() {
        numeracionService.reservarSiguienteNumero();
        String segundo = numeracionService.reservarSiguienteNumero();
        int anio = LocalDateTime.now().getYear();
        assertThat(segundo).isEqualTo("ESAL-" + anio + "-000002");
    }

    @Test
    void reservar_tresLlamadas_consecutivoAlcanza3() {
        numeracionService.reservarSiguienteNumero();
        numeracionService.reservarSiguienteNumero();
        String tercero = numeracionService.reservarSiguienteNumero();
        int anio = LocalDateTime.now().getYear();
        assertThat(tercero).isEqualTo("ESAL-" + anio + "-000003");
    }

    @Test
    void actualizar_cambiaPrefijo() {
        NumeracionUpdateDto upd = new NumeracionUpdateDto();
        upd.setPrefijo("CER");
        NumeracionDto dto = numeracionService.actualizar(upd);
        assertThat(dto.getPrefijo()).isEqualTo("CER");
    }

    @Test
    void actualizar_prefijoNuloMantienePrefijo() {
        NumeracionUpdateDto upd = new NumeracionUpdateDto();
        upd.setPrefijo(null);
        NumeracionDto dto = numeracionService.actualizar(upd);
        assertThat(dto.getPrefijo()).isEqualTo("ESAL");
    }
}
