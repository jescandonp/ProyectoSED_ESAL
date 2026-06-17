package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CertificadoPdfService {

    static final String VERSION_PLANTILLA = "I8-EYRL-v1";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COLOR_PRIMARIO = new Color(0x00, 0x33, 0x66);
    private static final Color COLOR_TABLA = new Color(0xC8, 0xE6, 0xC9);
    private static final Color COLOR_GRIS = new Color(0x60, 0x60, 0x60);
    private static final float MARGEN_HORIZONTAL = 85f;
    private static final float MARGEN_VERTICAL = 106f;
    private static final String FUENTE_BASE = "Arial";
    private static final String LOGO_HEADER_CLASSPATH = "/certificado/logo-sed-header.png";
    static final float LOGO_HEADER_ANCHO = 154.49f; // 5.45 cm
    static final float LOGO_HEADER_ALTO = 57.54f;   // 2.03 cm

    public byte[] generar(CertificadoNarrativoDto narrativo,
                          String numeroCertificado,
                          String firmanteNombre,
                          String firmanteCargo,
                          LocalDateTime fechaExpedicion) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.LETTER, MARGEN_HORIZONTAL, MARGEN_HORIZONTAL,
                MARGEN_VERTICAL, MARGEN_VERTICAL);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        writer.setPageEvent(new FooterInstitucional());
        doc.open();

        Font titulo = FontFactory.getFont(FUENTE_BASE, 11, Font.BOLD, COLOR_PRIMARIO);
        Font subtitulo = FontFactory.getFont(FUENTE_BASE, 11, Font.BOLD, COLOR_PRIMARIO);
        Font normal = FontFactory.getFont(FUENTE_BASE, 11, Color.BLACK);
        Font bold = FontFactory.getFont(FUENTE_BASE, 11, Font.BOLD, Color.BLACK);
        Font italica = FontFactory.getFont(FUENTE_BASE, 11, Font.ITALIC, Color.BLACK);
        Font label = FontFactory.getFont(FUENTE_BASE, 9, Font.BOLD, COLOR_GRIS);
        Font tablaHeader = FontFactory.getFont(FUENTE_BASE, 8, Font.BOLD, Color.BLACK);
        Font tablaValor = FontFactory.getFont(FUENTE_BASE, 8, Color.BLACK);
        Font alerta = FontFactory.getFont(FUENTE_BASE, 9, Font.BOLD, new Color(0xD9, 0x7F, 0x06));
        Font pie = FontFactory.getFont(FUENTE_BASE, 8, COLOR_GRIS);
        Font firmante = FontFactory.getFont(FUENTE_BASE, 11, Font.BOLD, COLOR_PRIMARIO);

        Image logoHeader = cargarLogoHeader();
        logoHeader.scaleAbsolute(LOGO_HEADER_ANCHO, LOGO_HEADER_ALTO);
        logoHeader.setAlignment(Element.ALIGN_CENTER);
        logoHeader.setSpacingAfter(16f);
        doc.add(logoHeader);

        Paragraph pTitulo = new Paragraph("CERTIFICADO DE EXISTENCIA Y REPRESENTACION LEGAL", titulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        pTitulo.setSpacingAfter(4);
        doc.add(pTitulo);

        Paragraph numeroFecha = new Paragraph();
        numeroFecha.add(new Chunk("No. " + numeroCertificado, subtitulo));
        numeroFecha.add(new Chunk("     Expedido: " + (fechaExpedicion != null ? fechaExpedicion.format(FMT) : "-"), label));
        numeroFecha.setAlignment(Element.ALIGN_CENTER);
        numeroFecha.setSpacingAfter(12);
        doc.add(numeroFecha);

        Paragraph preambulo = new Paragraph();
        preambulo.add(new Chunk("LA SUSCRITA DIRECTORA DE INSPECCION Y VIGILANCIA", bold));
        preambulo.add(Chunk.NEWLINE);
        preambulo.add(new Chunk("En uso de las facultades concedidas por los Decretos Distritales 479 de 2024 y 650 de 2025", normal));
        preambulo.add(Chunk.NEWLINE);
        preambulo.add(new Chunk("CERTIFICA", bold));
        preambulo.setAlignment(Element.ALIGN_CENTER);
        preambulo.setSpacingAfter(12);
        doc.add(preambulo);

        Paragraph datos = parrafoNarrativo(narrativo, normal, bold);
        datos.setSpacingAfter(8);
        doc.add(datos);

        if (texto(narrativo.getObjetoSocial())) {
            Paragraph objetoIntro = new Paragraph();
            objetoIntro.setAlignment(Element.ALIGN_JUSTIFIED);
            objetoIntro.add(new Chunk("Que, revisados los estatutos de la entidad se encuentra en el articulo ", normal));
            objetoIntro.add(new Chunk("no registrado", bold));
            objetoIntro.add(new Chunk(", que su objeto social es el siguiente: ", normal));
            objetoIntro.setSpacingAfter(8);
            doc.add(objetoIntro);

            Paragraph objeto = new Paragraph();
            objeto.setAlignment(Element.ALIGN_JUSTIFIED);
            objeto.add(new Chunk("\"" + narrativo.getObjetoSocial() + "\".", italica));
            objeto.setSpacingAfter(8);
            doc.add(objeto);
        }

        agregarParrafoJustificado(doc,
                "REPRESENTACION LEGAL:",
                bold,
                8,
                4);
        agregarParrafoJustificado(doc,
                "Que, una vez revisados los estatutos de la ESAL, respecto de la representacion legal se encuentra en el articulo no registrado que:",
                normal,
                0,
                8);
        agregarParrafoJustificado(doc,
                "\"Informacion tomada de las facultades y limitaciones registradas para la representacion legal.\".",
                italica,
                0,
                8);
        agregarParrafoJustificado(doc,
                "Que, a la fecha de expedicion del presente Certificado, la representacion legal de la ESAL, esta conformada como se expresa a continuacion:",
                normal,
                0,
                6);
        agregarTablaSiExiste(doc, narrativo.getRepresentantesLegales(), tablaHeader, tablaValor);
        if (texto(narrativo.getFacultadesRepresentante())) {
            agregarParrafoJustificado(doc, "FUNCIONES DE LA REPRESENTACION LEGAL:", bold, 8, 4);
            agregarParrafoJustificado(doc,
                    "Dispone el articulo no registrado de los estatutos que las facultades de la representacion legal son:",
                    normal,
                    0,
                    4);
            agregarParrafoJustificado(doc, "\"" + narrativo.getFacultadesRepresentante() + "\".", italica, 0, 8);
        }

        agregarParrafoJustificado(doc, "ASAMBLEA GENERAL", bold, 8, 4);
        agregarParrafoJustificado(doc,
                "Respecto de la Asamblea General, consagra el articulo no registrado de los estatutos que sus funciones son:",
                normal,
                0,
                4);
        agregarParrafoJustificado(doc,
                "\"Informacion estatutaria no registrada como campo independiente en el sistema.\".",
                italica,
                0,
                8);
        agregarParrafoJustificado(doc, "FUNCIONES DE LA ASAMBLEA GENERAL:", bold, 8, 4);
        agregarParrafoJustificado(doc,
                "Dispone el articulo no registrado de los estatutos que las facultades de la ASAMBLEA GENERAL son:",
                normal,
                0,
                4);
        agregarParrafoJustificado(doc,
                "\"Informacion estatutaria no registrada como campo independiente en el sistema.\".",
                italica,
                0,
                8);

        agregarParrafoJustificado(doc, "JUNTA DIRECTIVA:", bold, 8, 4);
        agregarParrafoJustificado(doc,
                "Respecto de la Junta Directiva, consagra el articulo no registrado de los estatutos que sus funciones son:",
                normal,
                0,
                4);
        agregarParrafoJustificado(doc,
                "\"Informacion estatutaria no registrada como campo independiente en el sistema.\".",
                italica,
                0,
                8);
        agregarParrafoJustificado(doc,
                "Que, a la fecha de expedicion del presente Certificado, la Junta Directiva de la ESAL, esta conformada como se expresa a continuacion:",
                normal,
                0,
                6);
        agregarTablaSiExiste(doc, narrativo.getMiembrosJunta(), tablaHeader, tablaValor);
        agregarParrafoJustificado(doc, "FUNCIONES DE LA JUNTA DIRECTIVA:", bold, 8, 4);
        agregarParrafoJustificado(doc,
                "Dispone el articulo no registrado de los estatutos que las facultades de la JUNTA DIRECTIVA son:",
                normal,
                0,
                4);
        agregarParrafoJustificado(doc,
                "\"Informacion estatutaria no registrada como campo independiente en el sistema.\".",
                italica,
                0,
                8);

        agregarParrafoJustificado(doc, "REVISORIA FISCAL:", bold, 8, 4);
        agregarParrafoJustificado(doc,
                "Que, una vez revisados los estatutos de la ESAL, respecto de la revisoria fiscal se encuentra en el articulo no registrado que:",
                normal,
                0,
                4);
        agregarParrafoJustificado(doc,
                "\"Informacion estatutaria no registrada como campo independiente en el sistema.\".",
                italica,
                0,
                8);
        agregarParrafoJustificado(doc,
                "Que, a la fecha de expedicion del presente Certificado, la revisoria fiscal de la ESAL, esta conformada como se expresa a continuacion:",
                normal,
                0,
                6);
        agregarTablaRevisoriaSiExiste(doc, narrativo.getRevisoresFiscales(), tablaHeader, tablaValor);

        if (texto(narrativo.getTerminoDuracion())) {
            Paragraph duracion = new Paragraph("DURACION: Que, de acuerdo con lo definido en el articulo no registrado de los estatutos, la entidad tendra una duracion "
                    + narrativo.getTerminoDuracion() + ".", normal);
            duracion.setAlignment(Element.ALIGN_JUSTIFIED);
            duracion.setSpacingAfter(8);
            doc.add(duracion);
        }

        if (texto(narrativo.getAlertaEstado())) {
            Paragraph pAlerta = new Paragraph(narrativo.getAlertaEstado(), alerta);
            pAlerta.setSpacingAfter(4);
            doc.add(pAlerta);
            Paragraph legalEstado = parrafoLegalEstado(narrativo.getEstado(), normal);
            if (legalEstado != null) {
                legalEstado.setSpacingAfter(8);
                doc.add(legalEstado);
            }
        }

        if (fechaExpedicion != null) {
            Paragraph cierre = new Paragraph(FechaEnLetras.formatear(fechaExpedicion.toLocalDate()), normal);
            cierre.setAlignment(Element.ALIGN_JUSTIFIED);
            cierre.setSpacingBefore(8);
            cierre.setSpacingAfter(12);
            doc.add(cierre);
        }

        doc.add(new Paragraph(" "));
        Paragraph atentamente = new Paragraph("Atentamente,", normal);
        atentamente.setAlignment(Element.ALIGN_JUSTIFIED);
        atentamente.setSpacingAfter(18);
        doc.add(atentamente);

        Paragraph pFirmante = new Paragraph();
        pFirmante.add(new Chunk(firmanteNombre, firmante));
        pFirmante.add(Chunk.NEWLINE);
        pFirmante.add(new Chunk(firmanteCargo, label));
        pFirmante.add(Chunk.NEWLINE);
        pFirmante.add(new Chunk("Secretaria de Educacion del Distrito", label));
        pFirmante.setSpacingBefore(12);
        doc.add(pFirmante);

        Paragraph tecnico = new Paragraph("Plantilla: " + VERSION_PLANTILLA + "  |  Generado: "
                + (fechaExpedicion != null ? fechaExpedicion.format(FMT) : "-"), pie);
        tecnico.setAlignment(Element.ALIGN_RIGHT);
        tecnico.setSpacingBefore(10);
        doc.add(tecnico);

        doc.add(new Chunk(new LineSeparator(0.5f, 100, COLOR_GRIS, Element.ALIGN_CENTER, -2)));
        Paragraph nota = new Paragraph("NOTA 1: Este certificado de existencia y representacion legal NO hace las veces de "
                + "autorizacion o licencia de funcionamiento de los establecimientos educativos presentes y futuros de "
                + "propiedad de la entidad. " + nvl(narrativo.getIdSipej()) + " - " + nvl(narrativo.getNombre()), pie);
        nota.setAlignment(Element.ALIGN_JUSTIFIED);
        nota.setSpacingBefore(4);
        doc.add(nota);

        doc.close();
        return out.toByteArray();
    }

    private Paragraph parrafoNarrativo(CertificadoNarrativoDto n, Font normal, Font bold) {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.add(new Chunk("Que, la entidad sin animo de lucro denominada ", normal));
        p.add(new Chunk(nvl(n.getNombre()), bold));
        agregarClausula(p, ", cuenta con domicilio en ", n.getDomicilio(), normal, bold);
        agregarClausula(p, ", correo electronico ", n.getCorreoElectronico(), normal, bold);
        p.add(new Chunk(", se encuentra registrada en el Sistema de Informacion de Personas Juridicas SIPEJ e identificada con ID. ", normal));
        p.add(new Chunk(nvl(n.getIdSipej()), bold));
        p.add(new Chunk(", NIT ", normal));
        p.add(new Chunk(nvl(n.getNit()), bold));
        p.add(new Chunk(", tiene personeria juridica vigente", normal));
        if (texto(n.getResolucionPersoneria())) {
            p.add(new Chunk(" reconocida mediante la Resolucion No. ", normal));
            p.add(new Chunk(n.getResolucionPersoneria(), bold));
            if (n.getFechaResolucion() != null) {
                p.add(new Chunk(" del " + n.getFechaResolucion().format(FMT_FECHA), normal));
            }
            agregarClausula(p, " expedida por ", n.getEntidadQueExpide(), normal, bold);
        }
        if (texto(n.getInscripcion())) {
            p.add(new Chunk(". Inscripcion ", normal));
            p.add(new Chunk(n.getInscripcion(), bold));
            if (n.getFechaInscripcion() != null) {
                p.add(new Chunk(" del " + n.getFechaInscripcion().format(FMT_FECHA), normal));
            }
        }
        p.add(new Chunk(".", normal));
        return p;
    }

    private Image cargarLogoHeader() throws Exception {
        try (InputStream in = CertificadoPdfService.class.getResourceAsStream(LOGO_HEADER_CLASSPATH)) {
            if (in == null) {
                throw new IllegalStateException("No se encontro el logo institucional: " + LOGO_HEADER_CLASSPATH);
            }
            return Image.getInstance(StreamUtils.copyToByteArray(in));
        }
    }

    private void agregarClausula(Paragraph p, String prefijo, String valor, Font normal, Font bold) {
        if (texto(valor)) {
            p.add(new Chunk(prefijo, normal));
            p.add(new Chunk(valor, bold));
        }
    }

    private void agregarTablaSiExiste(Document doc, List<MiembroDto> miembros,
                                      Font header, Font valor) throws Exception {
        if (miembros == null || miembros.isEmpty()) {
            return;
        }
        doc.add(tablaOrgano(miembros, header, valor));
    }

    private void agregarParrafoJustificado(Document doc, String texto, Font fuente,
                                           int spacingBefore, int spacingAfter) throws Exception {
        Paragraph parrafo = new Paragraph(texto, fuente);
        parrafo.setAlignment(Element.ALIGN_JUSTIFIED);
        parrafo.setLeading(0, 1.15f);
        parrafo.setSpacingBefore(spacingBefore);
        parrafo.setSpacingAfter(spacingAfter);
        doc.add(parrafo);
    }

    private PdfPTable tablaOrgano(List<MiembroDto> miembros, Font header, Font valor) throws Exception {
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 2f, 2f, 2f, 2f});
        tabla.setSpacingAfter(8);

        String[] cabeceras = {"NOMBRE", "IDENTIFICACION", "CARGO", "ACTA NOMBRAMIENTO", "RADICADO SED"};
        for (String cabecera : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cabecera, header));
            celda.setBackgroundColor(COLOR_TABLA);
            celda.setPadding(4);
            tabla.addCell(celda);
        }

        for (MiembroDto miembro : miembros) {
            tabla.addCell(celda(nvl(miembro.getNombre()), valor));
            tabla.addCell(celda(((texto(miembro.getTipoDocumento()) ? miembro.getTipoDocumento() + " " : "")
                    + nvl(miembro.getNumeroDocumento())).trim(), valor));
            tabla.addCell(celda(nvl(miembro.getCargo()), valor));
            tabla.addCell(celda(nvl(miembro.getActaNombramiento()), valor));
            tabla.addCell(celda("", valor));
        }
        return tabla;
    }

    private void agregarTablaRevisoriaSiExiste(Document doc, List<MiembroDto> revisores,
                                               Font header, Font valor) throws Exception {
        if (revisores == null || revisores.isEmpty()) {
            return;
        }
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 2f, 2f});
        tabla.setSpacingAfter(8);

        String[] cabeceras = {"NOMBRE", "IDENTIFICACION", "CARGO"};
        for (String cabecera : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cabecera, header));
            celda.setBackgroundColor(COLOR_TABLA);
            celda.setPadding(4);
            tabla.addCell(celda);
        }

        for (MiembroDto revisor : revisores) {
            tabla.addCell(celda(nvl(revisor.getNombre()), valor));
            tabla.addCell(celda(((texto(revisor.getTipoDocumento()) ? revisor.getTipoDocumento() + " " : "")
                    + nvl(revisor.getNumeroDocumento())).trim(), valor));
            tabla.addCell(celda(nvl(revisor.getCargo()), valor));
        }
        doc.add(tabla);
    }

    private PdfPCell celda(String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto(texto) ? texto : "-", fuente));
        celda.setPadding(4);
        celda.setBorder(Rectangle.BOX);
        return celda;
    }

    private Paragraph parrafoLegalEstado(EstadoEsal estado, Font fuente) {
        String texto = null;
        if (EstadoEsal.SUSPENDIDO.equals(estado)) {
            texto = "La presente entidad se encuentra SUSPENDIDA. Los efectos juridicos de la personeria juridica "
                    + "quedan en suspenso durante el tiempo indicado en la respectiva actuacion administrativa.";
        } else if (EstadoEsal.EN_LIQUIDACION.equals(estado)) {
            texto = "La presente entidad se encuentra EN PROCESO DE DISOLUCION Y LIQUIDACION conforme a las normas "
                    + "aplicables. La personeria juridica subsiste para efectos del proceso liquidatorio.";
        } else if (EstadoEsal.CANCELADO.equals(estado)) {
            texto = "La personeria juridica de la presente entidad ha sido CANCELADA mediante acto administrativo. "
                    + "El presente certificado se expide unicamente para efectos de verificacion historica.";
        }
        if (texto == null) {
            return null;
        }
        Paragraph p = new Paragraph(texto, fuente);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        return p;
    }

    private boolean texto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private String nvl(String valor) {
        return texto(valor) ? valor : "-";
    }

    private static final class FooterInstitucional extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Font footer = FontFactory.getFont(FUENTE_BASE, 7, COLOR_GRIS);
                PdfPTable tabla = new PdfPTable(1);
                tabla.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
                tabla.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                tabla.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                tabla.addCell(new Phrase("Av. El Dorado No. 66 - 63", footer));
                tabla.addCell(new Phrase("PBX: 324 1000 - Fax: 315 34 48", footer));
                tabla.addCell(new Phrase("Codigo postal: 111321", footer));
                tabla.addCell(new Phrase("www.educacionbogota.edu.co", footer));
                tabla.addCell(new Phrase("Info: Linea 195", footer));
                tabla.writeSelectedRows(0, -1, document.leftMargin(), 54, writer.getDirectContent());
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IllegalStateException("No fue posible escribir el footer institucional", ex);
            }
        }
    }

    static final class FechaEnLetras {

        private static final String[] DIAS = {
                "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
                "diez", "once", "doce", "trece", "catorce", "quince", "dieciseis", "diecisiete",
                "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidos", "veintitres",
                "veinticuatro", "veinticinco", "veintiseis", "veintisiete", "veintiocho",
                "veintinueve", "treinta", "treinta y uno"
        };

        private static final String[] MESES = {
                "enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        };

        static String formatear(LocalDate fecha) {
            return String.format("Se expide en Bogota D.C., a los %s (%d) dias del mes de %s de %s (%d).",
                    DIAS[fecha.getDayOfMonth()],
                    fecha.getDayOfMonth(),
                    MESES[fecha.getMonthValue() - 1],
                    anioEnLetras(fecha.getYear()),
                    fecha.getYear());
        }

        private static String anioEnLetras(int anio) {
            if (anio == 2000) {
                return "dos mil";
            }
            if (anio < 2001 || anio > 2099) {
                return String.valueOf(anio);
            }
            int resto = anio - 2000;
            if (resto < 30) {
                return "dos mil " + DIAS[resto];
            }
            if (resto == 30) {
                return "dos mil treinta";
            }
            return "dos mil treinta y " + DIAS[resto - 30];
        }
    }
}
