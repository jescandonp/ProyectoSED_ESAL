package co.gov.bogota.sed.esal.service;

/**
 * Constantes de acciones y entidades para el registro de auditoría.
 *
 * Uso:
 * <pre>
 *   auditoriaService.registrar(usuario, rol,
 *       AuditoriaAcciones.CREAR_ESAL,
 *       AuditoriaAcciones.ENTIDAD_ESAL,
 *       esal.getId(), esal.getIdSipej(),
 *       AuditoriaAcciones.RESULTADO_EXITO, null);
 * </pre>
 */
public final class AuditoriaAcciones {

    // -------------------------------------------------------------------------
    // Acciones
    // -------------------------------------------------------------------------

    public static final String CREAR_ESAL             = "CREAR_ESAL";
    public static final String EDITAR_ESAL             = "EDITAR_ESAL";
    public static final String CAMBIAR_ESTADO_ESAL     = "CAMBIAR_ESTADO_ESAL";
    public static final String CONSULTAR_ESAL          = "CONSULTAR_ESAL";
    public static final String IMPORTAR_ESAL           = "IMPORTAR_ESAL";
    public static final String IMPORTAR_DICCIONARIO    = "IMPORTAR_DICCIONARIO";
    public static final String REGISTRAR_DOCUMENTO     = "REGISTRAR_DOCUMENTO";
    public static final String CONSULTAR_COMPLETITUD   = "CONSULTAR_COMPLETITUD";
    public static final String RECALCULAR_COMPLETITUD  = "RECALCULAR_COMPLETITUD";
    public static final String CONSULTAR_AUDITORIA     = "CONSULTAR_AUDITORIA";

    // -------------------------------------------------------------------------
    // Resultados
    // -------------------------------------------------------------------------

    public static final String RESULTADO_EXITO = "EXITO";
    public static final String RESULTADO_ERROR = "ERROR";

    // -------------------------------------------------------------------------
    // Entidades
    // -------------------------------------------------------------------------

    public static final String ENTIDAD_ESAL        = "ESAL";
    public static final String ENTIDAD_IMPORTACION = "IMPORTACION";
    public static final String ENTIDAD_DOCUMENTO   = "DOCUMENTO";
    public static final String ENTIDAD_DICCIONARIO = "DICCIONARIO";

    private AuditoriaAcciones() {
        // clase de constantes — no instanciar
    }
}
