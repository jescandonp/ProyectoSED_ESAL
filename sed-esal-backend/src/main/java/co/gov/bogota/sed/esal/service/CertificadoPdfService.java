package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Genera el PDF del certificado de existencia y representación legal.
 *
 * El layout es programático y puede ajustarse a la plantilla oficial
 * cuando sea entregada. Cada sección del preview se mapea a una sección
 * del documento PDF.
 */
@Service
public class CertificadoPdfService {

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
            new java.util.Locale("es", "CO"));
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Colores institucionales SED
    private static final Color COLOR_PRIMARIO   = new Color(0x1B, 0x4F, 0x72);  // azul SED
    private static final Color COLOR_ALERTA     = new Color(0xD6, 0x8A, 0x00);  // amarillo advertencia
    private static final Color COLOR_CANCELADO  = new Color(0x8B, 0x0C, 0x0C);  // rojo cancelado
    private static final Color COLOR_GRIS_CLARO = new Color(0xF2, 0xF3, 0xF4);
    private static final Color COLOR_GRIS_TEXTO = new Color(0x5D, 0x6D, 0x7E);

    /**
     * Genera el PDF completo y retorna los bytes resultantes.
     *
     * @param preview      datos validados del certificado (I2)
     * @param numero       número de certificado asignado
     * @param firmante     firmante vigente seleccionado
     * @param fechaExp     fecha y hora de expedición
     * @return bytes del PDF generado
     */
    public byte[] generar(PreviewCertificadoDto preview, String numero,
                          Firmante firmante, LocalDateTime fechaExp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 60, 60, 70, 60);

        try {
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PiePaginaEvent(numero, fechaExp));
            doc.open();

            agregarEncabezado(doc, numero, fechaExp, preview.getEstado());
            agregarAlertaEstado(doc, preview);
            agregarDatosIdentificacion(doc, preview);
            agregarSecciones(doc, preview.getSecciones());
            agregarFirmante(doc, firmante, fechaExp);
            agregarNota(doc);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF del certificado: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    // -------------------------------------------------------------------------
    // Secciones del documento
    // -------------------------------------------------------------------------

    private void agregarEncabezado(Document doc, String numero, LocalDateTime fechaExp,
                                    EstadoEsal estado) throws DocumentException {
        // Membrete institucional
        Font fInstitucion = new Font(Font.HELVETICA, 9, Font.NORMAL, COLOR_GRIS_TEXTO);
        Font fTitulo      = new Font(Font.HELVETICA, 14, Font.BOLD, COLOR_PRIMARIO);
        Font fSubtitulo   = new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_PRIMARIO);
        Font fNumero      = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);

        Paragraph institucion = new Paragraph("SECRETARÍA DE EDUCACIÓN DEL DISTRITO", fInstitucion);
        institucion.setAlignment(Element.ALIGN_CENTER);
        doc.add(institucion);

        Paragraph div = new Paragraph("Dirección de Inspección y Vigilancia de Entidades Privadas", fInstitucion);
        div.setAlignment(Element.ALIGN_CENTER);
        doc.add(div);

        // Línea separadora
        doc.add(crearLinea(COLOR_PRIMARIO));
        doc.add(Chunk.NEWLINE);

        // Título del certificado
        String tituloPrincipal = "CERTIFICADO DE EXISTENCIA Y REPRESENTACIÓN LEGAL";
        if (estado == EstadoEsal.EN_LIQUIDACION) {
            tituloPrincipal += " — EN LIQUIDACIÓN";
        }
        Paragraph titulo = new Paragraph(tituloPrincipal, fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(4);
        doc.add(titulo);

        if (estado == EstadoEsal.SUSPENDIDO) {
            Font fSuspendido = new Font(Font.HELVETICA, 9, Font.BOLD, COLOR_ALERTA);
            Paragraph susp = new Paragraph("⚠  ENTIDAD ACTUALMENTE SUSPENDIDA", fSuspendido);
            susp.setAlignment(Element.ALIGN_CENTER);
            doc.add(susp);
        }

        // Número y fecha
        Font fEtiqueta = new Font(Font.HELVETICA, 9, Font.NORMAL, COLOR_GRIS_TEXTO);
        PdfPTable tabNum = new PdfPTable(2);
        tabNum.setWidthPercentage(100);
        tabNum.setSpacingBefore(8);
        tabNum.setSpacingAfter(8);
        tabNum.setWidths(new float[]{50, 50});

        agregarCeldaTablaInfo(tabNum, "Número de certificado:", fEtiqueta);
        agregarCeldaTablaInfo(tabNum, numero, fNumero);
        agregarCeldaTablaInfo(tabNum, "Fecha de expedición:", fEtiqueta);
        agregarCeldaTablaInfo(tabNum, fechaExp.format(FMT_FECHA), fNumero);

        doc.add(tabNum);
        doc.add(crearLinea(COLOR_GRIS_CLARO));
        doc.add(Chunk.NEWLINE);
    }

    private void agregarAlertaEstado(Document doc, PreviewCertificadoDto preview)
            throws DocumentException {
        if (preview.getAlertaEstado() == null || preview.getAlertaEstado().isBlank()) return;

        Color colorBg = preview.getEstado() == EstadoEsal.CANCELADO ? new Color(0xF9, 0xEB, 0xEA)
                : preview.getEstado() == EstadoEsal.EN_LIQUIDACION   ? new Color(0xFD, 0xF2, 0xE9)
                : new Color(0xFF, 0xF3, 0xCD); // SUSPENDIDO

        Color colorTexto = preview.getEstado() == EstadoEsal.CANCELADO ? COLOR_CANCELADO
                : preview.getEstado() == EstadoEsal.EN_LIQUIDACION   ? new Color(0x87, 0x42, 0x00)
                : COLOR_ALERTA;

        Font fAlerta = new Font(Font.HELVETICA, 9, Font.BOLD, colorTexto);

        PdfPTable alerta = new PdfPTable(1);
        alerta.setWidthPercentage(100);
        alerta.setSpacingAfter(10);
        PdfPCell celda = new PdfPCell(new Phrase(preview.getAlertaEstado(), fAlerta));
        celda.setBackgroundColor(colorBg);
        celda.setPadding(8);
        celda.setBorderColor(colorTexto);
        alerta.addCell(celda);
        doc.add(alerta);
    }

    private void agregarDatosIdentificacion(Document doc, PreviewCertificadoDto preview)
            throws DocumentException {
        Font fSeccion  = new Font(Font.HELVETICA, 11, Font.BOLD, COLOR_PRIMARIO);
        Font fEtiqueta = new Font(Font.HELVETICA, 9, Font.BOLD, Color.BLACK);
        Font fValor    = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);

        Paragraph secTitulo = new Paragraph("IDENTIFICACIÓN DE LA ENTIDAD", fSeccion);
        secTitulo.setSpacingBefore(4);
        secTitulo.setSpacingAfter(4);
        doc.add(secTitulo);
        doc.add(crearLinea(COLOR_PRIMARIO));

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(6);
        tabla.setSpacingAfter(10);
        tabla.setWidths(new float[]{20, 30, 20, 30});

        agregarParCampo(tabla, "Nombre:", preview.getNombre(), fEtiqueta, fValor, true);
        agregarParCampo(tabla, "NIT:", valorOGuion(preview.getNit()), fEtiqueta, fValor, false);
        agregarParCampo(tabla, "ID SIPEJ:", valorOGuion(preview.getIdSipej()), fEtiqueta, fValor, false);
        agregarParCampo(tabla, "Estado:", formatEstado(preview.getEstado()), fEtiqueta, fValor, false);

        doc.add(tabla);
    }

    private void agregarSecciones(Document doc, List<PreviewCertificadoDto.SeccionPreviewDto> secciones)
            throws DocumentException {
        if (secciones == null) return;

        Font fSeccion  = new Font(Font.HELVETICA, 11, Font.BOLD, COLOR_PRIMARIO);
        Font fEtiqueta = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);
        Font fValor    = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);

        for (PreviewCertificadoDto.SeccionPreviewDto seccion : secciones) {
            // Omite la sección de identificación principal (ya fue agregada)
            if ("INFORMACION PRINCIPAL".equals(seccion.getNombre())) continue;

            boolean tieneDatos = seccion.getCampos() != null &&
                    seccion.getCampos().stream().anyMatch(c -> !Boolean.TRUE.equals(c.getFaltante()));
            if (!tieneDatos) continue;

            Paragraph titulo = new Paragraph(seccion.getNombre(), fSeccion);
            titulo.setSpacingBefore(8);
            titulo.setSpacingAfter(4);
            doc.add(titulo);
            doc.add(crearLinea(COLOR_PRIMARIO));

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(4);
            tabla.setSpacingAfter(8);
            tabla.setWidths(new float[]{25, 25, 25, 25});

            boolean filaPar = false;
            for (PreviewCertificadoDto.CampoPreviewDto campo : seccion.getCampos()) {
                if (Boolean.TRUE.equals(campo.getFaltante()) && !Boolean.TRUE.equals(campo.getObligatorio())) {
                    continue;
                }
                String valor = Boolean.TRUE.equals(campo.getFaltante()) ? "(sin datos)" : campo.getValor();
                Font fv = Boolean.TRUE.equals(campo.getFaltante())
                        ? new Font(Font.HELVETICA, 8, Font.ITALIC, COLOR_GRIS_TEXTO)
                        : fValor;
                agregarParCampo(tabla, campo.getEtiqueta() + ":", valor, fEtiqueta, fv, filaPar);
                filaPar = !filaPar;
            }

            // Si la tabla quedó vacía (todos opcionales faltantes) no agregar
            if (tabla.getRows() != null && !tabla.getRows().isEmpty()) {
                doc.add(tabla);
            }
        }
    }

    private void agregarFirmante(Document doc, Firmante firmante, LocalDateTime fechaExp)
            throws DocumentException {
        Font fEtiqueta = new Font(Font.HELVETICA, 9, Font.NORMAL, COLOR_GRIS_TEXTO);
        Font fNombre   = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        Font fCargo    = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);

        doc.add(Chunk.NEWLINE);
        doc.add(crearLinea(COLOR_GRIS_CLARO));
        doc.add(Chunk.NEWLINE);

        Paragraph expidio = new Paragraph("EXPIDIÓ:", fEtiqueta);
        expidio.setAlignment(Element.ALIGN_CENTER);
        doc.add(expidio);

        Paragraph nombre = new Paragraph(firmante.getNombre(), fNombre);
        nombre.setAlignment(Element.ALIGN_CENTER);
        nombre.setSpacingBefore(4);
        doc.add(nombre);

        Paragraph cargo = new Paragraph(firmante.getCargo(), fCargo);
        cargo.setAlignment(Element.ALIGN_CENTER);
        doc.add(cargo);

        if (firmante.getDependencia() != null && !firmante.getDependencia().isBlank()) {
            Paragraph dep = new Paragraph(firmante.getDependencia(), fCargo);
            dep.setAlignment(Element.ALIGN_CENTER);
            doc.add(dep);
        }

        Font fFecha = new Font(Font.HELVETICA, 8, Font.NORMAL, COLOR_GRIS_TEXTO);
        Paragraph fecha = new Paragraph("Bogotá D.C., " + fechaExp.format(FMT_FECHA), fFecha);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingBefore(4);
        doc.add(fecha);
    }

    private void agregarNota(Document doc) throws DocumentException {
        Font fNota = new Font(Font.HELVETICA, 7, Font.ITALIC, COLOR_GRIS_TEXTO);
        Paragraph nota = new Paragraph(
                "Este certificado es válido únicamente para los fines legales contemplados en la normatividad vigente. " +
                "La veracidad de la información aquí contenida puede ser verificada ante la Secretaría de Educación del Distrito.",
                fNota);
        nota.setAlignment(Element.ALIGN_CENTER);
        nota.setSpacingBefore(16);
        doc.add(nota);
    }

    // -------------------------------------------------------------------------
    // Utilidades de layout
    // -------------------------------------------------------------------------

    private Paragraph crearLinea(Color color) {
        Chunk linea = new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1f, 100f, color, Element.ALIGN_CENTER, -2));
        return new Paragraph(linea);
    }

    private void agregarCeldaTablaInfo(PdfPTable tabla, String texto, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(2);
        tabla.addCell(celda);
    }

    private void agregarParCampo(PdfPTable tabla, String etiqueta, String valor,
                                  Font fEtiqueta, Font fValor, boolean filaPar) {
        Color bgColor = filaPar ? COLOR_GRIS_CLARO : Color.WHITE;

        PdfPCell cEtiqueta = new PdfPCell(new Phrase(etiqueta, fEtiqueta));
        cEtiqueta.setBorder(Rectangle.NO_BORDER);
        cEtiqueta.setPadding(3);
        cEtiqueta.setBackgroundColor(bgColor);

        PdfPCell cValor = new PdfPCell(new Phrase(valor != null ? valor : "—", fValor));
        cValor.setBorder(Rectangle.NO_BORDER);
        cValor.setPadding(3);
        cValor.setBackgroundColor(bgColor);

        tabla.addCell(cEtiqueta);
        tabla.addCell(cValor);
    }

    private String valorOGuion(String v) {
        return (v == null || v.isBlank()) ? "—" : v;
    }

    private String formatEstado(EstadoEsal estado) {
        if (estado == null) return "—";
        switch (estado) {
            case ACTIVO:         return "Activo";
            case SUSPENDIDO:     return "Suspendido";
            case EN_LIQUIDACION: return "En Liquidación";
            case CANCELADO:      return "Cancelado";
            default:             return estado.name();
        }
    }

    // -------------------------------------------------------------------------
    // Pie de página con número de certificado y hash (si disponible)
    // -------------------------------------------------------------------------

    private static class PiePaginaEvent extends PdfPageEventHelper {
        private final String numero;
        private final String fechaStr;

        PiePaginaEvent(String numero, LocalDateTime fechaExp) {
            this.numero = numero;
            this.fechaStr = fechaExp.format(FMT_DATETIME);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Font f = new Font(Font.HELVETICA, 7, Font.NORMAL, COLOR_GRIS_TEXTO);
            Phrase pie = new Phrase("Certificado N.° " + numero + "  |  Expedido: " + fechaStr
                    + "  |  Página " + writer.getPageNumber(), f);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, pie,
                    (document.left() + document.right()) / 2f, document.bottom() - 10, 0);
        }
    }
}
