package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CertificadoPdfServiceTest {

    private final CertificadoPdfService service = new CertificadoPdfService();

    @Test
    void generar_conDtoNarrativo_retornaPdfNoVacio() throws Exception {
        CertificadoNarrativoDto dto = new CertificadoNarrativoDto();
        dto.setNombre("Fundacion PDF I6");
        dto.setIdSipej("PDF-I6-001");
        dto.setNit("900123456-1");
        dto.setDomicilio("Bogota D.C.");
        dto.setCorreoElectronico("pdf@test.com");
        dto.setTerminoDuracion("INDEFINIDA");
        dto.setObjetoSocial("Objeto social de prueba para certificado narrativo.");
        dto.setEstado(EstadoEsal.ACTIVO);
        dto.setResolucionPersoneria("Resolucion 001");
        dto.setFechaResolucion(LocalDate.of(2020, 1, 15));
        dto.setEntidadQueExpide("Secretaria de Educacion del Distrito");
        dto.setInscripcion("S100001");
        dto.setFechaInscripcion(LocalDate.of(2020, 2, 10));

        MiembroDto representante = new MiembroDto();
        representante.setNombre("JUAN PEREZ");
        representante.setTipoDocumento("CC");
        representante.setNumeroDocumento("12345678");
        representante.setCargo("Representante Legal");
        representante.setActaNombramiento("ACT-001");
        dto.setRepresentantesLegales(Collections.singletonList(representante));

        byte[] pdf = service.generar(dto, "ESAL-2026-000001", "Directora Test",
                "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 5, 27, 10, 30));

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }
}
