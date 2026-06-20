package co.gov.bogota.sed.esal.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación de {@link AlmacenamientoService} para el perfil test.
 *
 * No escribe al disco — retorna una ruta ficticia para que los tests
 * no dependan del filesystem ni de permisos de escritura.
 */
@Service
@Profile("test")
public class TestAlmacenamientoService implements AlmacenamientoService {

    private final Map<String, byte[]> archivos = new ConcurrentHashMap<>();

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
    public String guardar(Long esalId, String nombreArchivo, InputStream contenido, long tamanoBytes)
            throws IOException {
        String ruta = "/test/docs/" + esalId + "/" + nombreArchivo;
        archivos.put(ruta, StreamUtils.copyToByteArray(contenido));
        return ruta;
    }

    @Override
    public byte[] leer(String rutaAlmacenamiento) {
        byte[] contenido = archivos.get(rutaAlmacenamiento);
        if (contenido == null) {
            throw new IllegalArgumentException("Archivo de prueba no encontrado: " + rutaAlmacenamiento);
        }
        return contenido;
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
