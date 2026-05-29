package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CertificadoPdfServiceTest {

    private final CertificadoPdfService service = new CertificadoPdfService();

    @Test
    void generar_conDtoNarrativo_incluyeBloquesInstitucionalesI6() throws Exception {
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
        dto.setRepresentantesLegales(Arrays.asList(representante));

        MiembroDto junta = new MiembroDto();
        junta.setNombre("MARIA JUNTA");
        junta.setCargo("Presidenta");
        dto.setMiembrosJunta(Arrays.asList(junta));

        MiembroDto asamblea = new MiembroDto();
        asamblea.setNombre("CARLOS ASAMBLEA");
        asamblea.setCargo("Miembro");
        dto.setMiembrosAsamblea(Arrays.asList(asamblea));

        MiembroDto revisor = new MiembroDto();
        revisor.setNombre("PEDRO REVISOR");
        revisor.setCargo("Revisor Fiscal");
        dto.setRevisoresFiscales(Arrays.asList(revisor));

        byte[] pdf = service.generar(dto, "ESAL-2026-000001", "Directora Test",
                "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 5, 27, 10, 30));

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");

        String texto = extraerTexto(pdf);
        assertThat(texto).contains("LA SUSCRITA DIRECTORA DE INSPECCION Y VIGILANCIA");
        assertThat(texto).contains("Decretos Distritales 479 de 2024 y 650 de 2025");
        assertThat(texto).contains("CERTIFICA");
        assertThat(texto).contains("Fundacion PDF I6");
        assertThat(texto).contains("PDF-I6-001");
        assertThat(texto).contains("900123456-1");
        assertThat(texto).contains("REPRESENTACION LEGAL:");
        assertThat(texto).contains("NOMBRE");
        assertThat(texto).contains("IDENTIFICACION");
        assertThat(texto).contains("ACTA");
        assertThat(texto).contains("NOMBRAMIENTO");
        assertThat(texto).contains("RADICADO SED");
        assertThat(texto).contains("JUNTA DIRECTIVA:");
        assertThat(texto).contains("ASAMBLEA GENERAL:");
        assertThat(texto).contains("REVISORIA FISCAL:");
        assertThat(texto).contains("Se expide en Bogota D.C., a los veintisiete (27) dias del mes de mayo de dos mil veintiseis (2026).");
        assertThat(texto).contains("Directora Test");
        assertThat(texto).contains("Plantilla: I6-v1");
        assertThat(texto).contains("NOTA 1: Este certificado de existencia y representacion legal NO hace las veces");
    }

    private String extraerTexto(byte[] pdf) throws Exception {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
        PdfTextExtractor extractor = new PdfTextExtractor(reader);
        StringBuilder texto = new StringBuilder();
        for (int page = 1; page <= reader.getNumberOfPages(); page++) {
            texto.append(extractor.getTextFromPage(page)).append('\n');
        }
        reader.close();
        return texto.toString();
    }
}
