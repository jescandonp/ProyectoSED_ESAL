package co.gov.bogota.sed.esal.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Implementación de {@link AlmacenamientoService} para el perfil test.
 *
 * No escribe al disco — retorna una ruta ficticia para que los tests
 * no dependan del filesystem ni de permisos de escritura.
 */
@Service
@Profile("test")
public class TestAlmacenamientoService implements AlmacenamientoService {

    /**
     * Retorna una ruta ficticia sin escribir al disco.
     *
     * @param esalId        ID de la ESAL
     * @param nombreArchivo nombre original del archivo
     * @param contenido     stream (ignorado en tests)
     * @param tamanoBytes   tamaño en bytes (ignorado en tests)
     * @return ruta ficticia con formato {@code /test/docs/{esalId}/{nombreArchivo}}
     */
    @Override
    public String guardar(Long esalId, String nombreArchivo, InputStream contenido, long tamanoBytes) {
        return "/test/docs/" + esalId + "/" + nombreArchivo;
    }

    /**
     * No-op en tests — no hay archivos reales que eliminar.
     *
     * @param rutaAlmacenamiento ruta (ignorada en tests)
     */
    @Override
    public void eliminar(String rutaAlmacenamiento) {
        // no-op en tests
    }
}
