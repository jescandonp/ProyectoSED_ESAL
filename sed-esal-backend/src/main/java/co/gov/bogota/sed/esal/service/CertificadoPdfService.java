package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CertificadoPdfService {

    static final String VERSION_PLANTILLA = "I3-v1";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COLOR_PRIMARIO  = new Color(0x1B, 0x5E, 0x20);
    private static final Color COLOR_HEADER_BG = new Color(0xE8, 0xF5, 0xE9);
    private static final Color COLOR_GRIS      = new Color(0x60, 0x60, 0x60);

    public byte[] generar(PreviewCertificadoDto preview,
                          String numeroCertificado,
                          String firmanteNombre,
                          String firmanteCargo,
                          LocalDateTime fechaExpedicion) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 60, 60, 70, 70);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font fuenteTitulo   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_PRIMARIO);
        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_PRIMARIO);
        Font fuenteLabel    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_GRIS);
        Font fuenteValor    = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font fuenteAlerta   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(0xD9, 0x7F, 0x06));
        Font fuentePie      = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_GRIS);
        Font fuenteFirmante = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_PRIMARIO);

        // Encabezado institucional
        Paragraph encabezado = new Paragraph();
        encabezado.add(new Chunk("ALCALDIA MAYOR DE BOGOTA D.C.", fuenteLabel));
        encabezado.add(Chunk.NEWLINE);
        encabezado.add(new Chunk("Secretaria de Educacion del Distrito - SED", fuenteLabel));
        encabezado.setAlignment(Element.ALIGN_CENTER);
        doc.add(encabezado);
        doc.add(new Paragraph(" "));

        // Titulo
        Paragraph titulo = new Paragraph("CERTIFICADO DE EXISTENCIA Y REPRESENTACION LEGAL", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(4);
        doc.add(titulo);

        // Numero y fecha
        Paragraph numFecha = new Paragraph();
        numFecha.add(new Chunk("No. " + numeroCertificado, fuenteSubtitulo));
        numFecha.add(new Chunk("     Expedido: " + (fechaExpedicion != null ? fechaExpedicion.format(FMT) : "—"),
                fuenteLabel));
        numFecha.setAlignment(Element.ALIGN_CENTER);
        numFecha.setSpacingAfter(12);
        doc.add(numFecha);

        // Alerta de estado si aplica
        if (preview.getAlertaEstado() != null) {
            Paragraph alerta = new Paragraph("⚠ " + preview.getAlertaEstado(), fuenteAlerta);
            alerta.setSpacingAfter(8);
            doc.add(alerta);
        }

        // Nombre ESAL con sufijo de estado
        String nombreEsal = preview.getNombre();
        if (EstadoEsal.EN_LIQUIDACION.name().equals(preview.getEstado() != null ? preview.getEstado().name() : "")) {
            nombreEsal = nombreEsal + " (EN LIQUIDACION)";
        }
        Paragraph nombrePar = new Paragraph(nombreEsal, fuenteSubtitulo);
        nombrePar.setSpacingAfter(8);
        doc.add(nombrePar);

        // Secciones del certificado
        if (preview.getSecciones() != null) {
            for (PreviewCertificadoDto.SeccionPreviewDto seccion : preview.getSecciones()) {
                Paragraph secTitulo = new Paragraph(seccion.getNombre().toUpperCase(), fuenteLabel);
                secTitulo.setSpacingBefore(8);
                secTitulo.setSpacingAfter(4);
                doc.add(secTitulo);

                PdfPTable tabla = new PdfPTable(2);
                tabla.setWidthPercentage(100);
                tabla.setWidths(new float[]{1f, 2f});

                if (seccion.getCampos() != null) {
                    for (PreviewCertificadoDto.CampoPreviewDto campo : seccion.getCampos()) {
                        PdfPCell celLabel = new PdfPCell(new Phrase(campo.getEtiqueta(), fuenteLabel));
                        celLabel.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                        celLabel.setPaddingBottom(4);

                        String valorTexto = Boolean.TRUE.equals(campo.getFaltante()) ? "—" : (campo.getValor() != null ? campo.getValor() : "—");
                        PdfPCell celValor = new PdfPCell(new Phrase(valorTexto, fuenteValor));
                        celValor.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                        celValor.setPaddingBottom(4);

                        tabla.addCell(celLabel);
                        tabla.addCell(celValor);
                    }
                }
                doc.add(tabla);
            }
        }

        // Parrafo legal por estado
        String parrafoEstado = obtenerParrafoEstado(preview);
        if (parrafoEstado != null) {
            doc.add(new Paragraph(" "));
            Paragraph parLegal = new Paragraph(parrafoEstado, fuentePie);
            parLegal.setSpacingBefore(8);
            doc.add(parLegal);
        }

        // Pie de pagina: firmante
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        Paragraph pieFirmante = new Paragraph();
        pieFirmante.add(new Chunk(firmanteNombre, fuenteFirmante));
        pieFirmante.add(Chunk.NEWLINE);
        pieFirmante.add(new Chunk(firmanteCargo, fuenteLabel));
        pieFirmante.add(Chunk.NEWLINE);
        pieFirmante.add(new Chunk("Secretaria de Educacion del Distrito", fuenteLabel));
        pieFirmante.setSpacingBefore(12);
        doc.add(pieFirmante);

        // Hash y version (pie tecnico)
        doc.add(new Paragraph(" "));
        Paragraph pieTecnico = new Paragraph();
        pieTecnico.add(new Chunk("Plantilla: " + VERSION_PLANTILLA + "  |  Generado: " +
                (fechaExpedicion != null ? fechaExpedicion.format(FMT) : "—"), fuentePie));
        pieTecnico.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pieTecnico);

        doc.close();
        return out.toByteArray();
    }

    private String obtenerParrafoEstado(PreviewCertificadoDto preview) {
        if (preview.getEstado() == null) return null;
        switch (preview.getEstado()) {
            case SUSPENDIDO:
                return "La presente entidad se encuentra SUSPENDIDA. Los efectos juridicos de la personeria juridica " +
                       "quedan en suspenso durante el tiempo indicado en la respectiva actuacion administrativa.";
            case EN_LIQUIDACION:
                return "La presente entidad se encuentra EN PROCESO DE DISOLUCION Y LIQUIDACION conforme a las normas " +
                       "aplicables. La personeria juridica subsiste para efectos del proceso liquidatorio.";
            case CANCELADO:
                return "La personeria juridica de la presente entidad ha sido CANCELADA mediante acto administrativo. " +
                       "El presente certificado se expide unicamente para efectos de verificacion historica.";
            default:
                return null;
        }
    }
}
