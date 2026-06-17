package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
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
        assertThat(texto).contains("Decretos Distritales 479 de 2024");
        assertThat(texto).contains("650 de");
        assertThat(texto).contains("2025");
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
        assertThat(texto).contains("Se expide en Bogota D.C., a los veintisiete (27) dias del mes de mayo de dos mil");
        assertThat(texto).contains("veintiseis (2026).");
        assertThat(texto).contains("Directora Test");
        assertThat(texto).contains("Plantilla: I8-EYRL-v1");
        assertThat(texto).contains("NOTA 1: Este certificado de existencia y representacion legal NO hace las veces");
    }

    @Test
    void generar_conContratoI8_reproduceEstructuraBasePlantillaEyrl() throws Exception {
        CertificadoNarrativoDto dto = new CertificadoNarrativoDto();
        dto.setNombre("Fundacion Plantilla Exacta");
        dto.setIdSipej("EYRL-I8-001");
        dto.setNit("901222333-4");
        dto.setDomicilio("Bogota D.C.");
        dto.setCorreoElectronico("eyrl@test.com");
        dto.setTerminoDuracion("INDEFINIDA");
        dto.setObjetoSocial("Objeto social estatutario de prueba.");
        dto.setEstado(EstadoEsal.ACTIVO);
        dto.setResolucionPersoneria("Resolucion 777");
        dto.setFechaResolucion(LocalDate.of(2021, 4, 20));
        dto.setEntidadQueExpide("Secretaria de Educacion del Distrito");
        dto.setInscripcion("S-EYRL-001");
        dto.setFechaInscripcion(LocalDate.of(2021, 5, 21));
        dto.setFacultadesRepresentante("Facultades estatutarias de representacion.");

        MiembroDto representante = new MiembroDto();
        representante.setNombre("ANA REPRESENTANTE");
        representante.setTipoDocumento("CC");
        representante.setNumeroDocumento("10000001");
        representante.setCargo("Representante Legal Principal");
        representante.setActaNombramiento("ACT-REP-001");
        dto.setRepresentantesLegales(Arrays.asList(representante));

        MiembroDto junta = new MiembroDto();
        junta.setNombre("LUIS JUNTA");
        junta.setTipoDocumento("CC");
        junta.setNumeroDocumento("10000002");
        junta.setCargo("Presidente Junta Directiva");
        junta.setActaNombramiento("ACT-JUN-001");
        dto.setMiembrosJunta(Arrays.asList(junta));

        MiembroDto asamblea = new MiembroDto();
        asamblea.setNombre("MIEMBRO ASAMBLEA");
        asamblea.setCargo("Miembro Asamblea");
        dto.setMiembrosAsamblea(Arrays.asList(asamblea));

        MiembroDto revisor = new MiembroDto();
        revisor.setNombre("ROSA REVISORA");
        revisor.setTipoDocumento("CC");
        revisor.setNumeroDocumento("10000003");
        revisor.setCargo("Revisor Fiscal Principal");
        dto.setRevisoresFiscales(Arrays.asList(revisor));

        byte[] pdf = service.generar(dto, "ESAL-2026-000008", "LIDA DIAZ VELANDIA",
                "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 6, 17, 9, 15));

        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
        Rectangle pageSize = reader.getPageSize(1);
        PdfDictionary resources = reader.getPageN(1).getAsDict(PdfName.RESOURCES);
        PdfDictionary xobjects = resources.getAsDict(PdfName.XOBJECT);
        reader.close();
        assertThat(pageSize.getWidth()).isEqualTo(612.0f);
        assertThat(pageSize.getHeight()).isEqualTo(792.0f);
        assertThat(xobjects)
                .as("El encabezado debe incluir el logo institucional como imagen PDF")
                .isNotNull();
        assertThat(xobjects.getKeys()).isNotEmpty();

        String texto = extraerTexto(pdf);
        assertThat(texto).contains("Plantilla: I8-EYRL-v1");
        assertThat(texto).contains("Av. El Dorado No. 66 - 63");
        assertThat(texto).contains("PBX: 324 1000 - Fax: 315 34 48");
        assertThat(texto).contains("Codigo postal: 111321");
        assertThat(texto).contains("www.educacionbogota.edu.co");
        assertThat(texto).contains("Info: Linea 195");
        assertThat(texto).contains("Atentamente,");

        assertThat(texto).contains("FUNCIONES DE LA REPRESENTACION LEGAL:");
        assertThat(texto).contains("FUNCIONES DE LA ASAMBLEA GENERAL:");
        assertThat(texto).contains("FUNCIONES DE LA JUNTA DIRECTIVA:");
        assertThat(texto).contains("REVISORIA FISCAL:");
        assertThat(texto).doesNotContain("RADICADO SED\nROSA REVISORA");

        assertOrden(texto,
                "CERTIFICA",
                "Que, la entidad sin animo de lucro denominada",
                "REPRESENTACION LEGAL:",
                "FUNCIONES DE LA REPRESENTACION LEGAL:",
                "ASAMBLEA GENERAL",
                "FUNCIONES DE LA ASAMBLEA GENERAL:",
                "JUNTA DIRECTIVA:",
                "FUNCIONES DE LA JUNTA DIRECTIVA:",
                "REVISORIA FISCAL:",
                "DURACION:",
                "Se expide en Bogota D.C.",
                "Atentamente,",
                "LIDA DIAZ VELANDIA");
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

    private void assertOrden(String texto, String... fragmentos) {
        int posicion = -1;
        for (String fragmento : fragmentos) {
            int siguiente = texto.indexOf(fragmento);
            assertThat(siguiente)
                    .as("Fragmento no encontrado: %s", fragmento)
                    .isGreaterThanOrEqualTo(0);
            assertThat(siguiente)
                    .as("Fragmento fuera de orden: %s", fragmento)
                    .isGreaterThan(posicion);
            posicion = siguiente;
        }
    }
}
