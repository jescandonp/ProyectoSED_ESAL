package co.gov.bogota.sed.esal.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interfaz de almacenamiento de documentos.
 *
 * En I1 se implementa con filesystem local-dev ({@link LocalDevAlmacenamientoService}).
 * En I3 se reemplazará por almacenamiento definitivo (S3, NFS, etc.)
 * sin cambiar esta interfaz.
 */
public interface AlmacenamientoService {

    /**
     * Guarda el contenido del archivo y retorna la ruta de almacenamiento.
     *
     * @param esalId        ID de la ESAL propietaria del documento
     * @param nombreArchivo nombre original del archivo
     * @param contenido     stream con el contenido del archivo
     * @param tamanoBytes   tamaño del archivo en bytes
     * @return ruta de almacenamiento donde quedó guardado el archivo
     * @throws IOException si ocurre un error de I/O al guardar
     */
    String guardar(Long esalId, String nombreArchivo, InputStream contenido, long tamanoBytes) throws IOException;

    /**
     * Lee el contenido de un archivo almacenado.
     *
     * @param rutaAlmacenamiento ruta o identificador interno del archivo
     * @return bytes del archivo
     * @throws IOException si ocurre un error de I/O al leer
     */
    byte[] leer(String rutaAlmacenamiento) throws IOException;

    /**
     * Elimina un archivo por su ruta de almacenamiento.
     *
     * @param rutaAlmacenamiento ruta del archivo a eliminar
     * @throws IOException si ocurre un error de I/O al eliminar
     */
    void eliminar(String rutaAlmacenamiento) throws IOException;
}
