package co.gov.bogota.sed.esal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Implementación de {@link AlmacenamientoService} para el perfil local-dev.
 *
 * Guarda los archivos en el filesystem local bajo un directorio configurable.
 * Por defecto usa {@code ${java.io.tmpdir}/sed-esal-docs/}.
 *
 * En I3 esta clase será reemplazada por una implementación de almacenamiento
 * definitivo (S3, NFS, etc.) sin modificar la interfaz.
 */
@Service
@Profile("local-dev")
public class LocalDevAlmacenamientoService implements AlmacenamientoService {

    /** Directorio base donde se almacenan los documentos. */
    private final Path directorioBase;

    public LocalDevAlmacenamientoService(
            @Value("${sed.esal.almacenamiento.directorio:#{systemProperties['java.io.tmpdir']}/sed-esal-docs}")
            String directorio) {
        this.directorioBase = Paths.get(directorio);
    }

    /**
     * Guarda el archivo en un subdirectorio por esalId con nombre único basado en timestamp.
     *
     * @param esalId        ID de la ESAL
     * @param nombreArchivo nombre original del archivo
     * @param contenido     stream con el contenido
     * @param tamanoBytes   tamaño en bytes (informativo)
     * @return ruta absoluta del archivo guardado
     * @throws IOException si no se puede crear el directorio o escribir el archivo
     */
    @Override
    public String guardar(Long esalId, String nombreArchivo, InputStream contenido, long tamanoBytes)
            throws IOException {
        // Crear subdirectorio por esalId
        Path dirEsal = directorioBase.resolve(String.valueOf(esalId));
        Files.createDirectories(dirEsal);

        // Nombre único: timestamp + nombre original para evitar colisiones
        String nombreUnico = System.currentTimeMillis() + "_" + nombreArchivo;
        Path destino = dirEsal.resolve(nombreUnico);

        Files.copy(contenido, destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toString();
    }

    @Override
    public byte[] leer(String rutaAlmacenamiento) throws IOException {
        return Files.readAllBytes(Paths.get(rutaAlmacenamiento));
    }

    /**
     * Elimina el archivo en la ruta indicada si existe.
     *
     * @param rutaAlmacenamiento ruta absoluta del archivo a eliminar
     * @throws IOException si ocurre un error de I/O al eliminar
     */
    @Override
    public void eliminar(String rutaAlmacenamiento) throws IOException {
        Files.deleteIfExists(Paths.get(rutaAlmacenamiento));
    }
}
