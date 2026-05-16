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

    // I2 — Búsqueda y Vista Previa
    public static final String BUSQUEDA_ESAL                  = "BUSQUEDA_ESAL";
    public static final String DETALLE_ESAL_CONSULTADO        = "DETALLE_ESAL_CONSULTADO";
    public static final String PREVIEW_CERTIFICADO_CONSULTADO = "PREVIEW_CERTIFICADO_CONSULTADO";
    public static final String PREVIEW_CERTIFICADO_BLOQUEADO  = "PREVIEW_CERTIFICADO_BLOQUEADO";
    public static final String ERROR_VALIDACION_PREVIEW       = "ERROR_VALIDACION_PREVIEW";

    // I3 — Expedición de Certificado
    public static final String CERTIFICADO_GENERACION_SOLICITADA = "CERTIFICADO_GENERACION_SOLICITADA";
    public static final String CERTIFICADO_BLOQUEADO             = "CERTIFICADO_BLOQUEADO";
    public static final String CERTIFICADO_GENERADO              = "CERTIFICADO_GENERADO";
    public static final String CERTIFICADO_GENERACION_FALLIDA    = "CERTIFICADO_GENERACION_FALLIDA";
    public static final String CERTIFICADO_DESCARGADO            = "CERTIFICADO_DESCARGADO";
    public static final String NUMERACION_ACTUALIZADA            = "NUMERACION_ACTUALIZADA";
    public static final String FIRMANTE_CREADO                   = "FIRMANTE_CREADO";
    public static final String FIRMANTE_ACTUALIZADO              = "FIRMANTE_ACTUALIZADO";
    public static final String FIRMANTE_ACTIVADO                 = "FIRMANTE_ACTIVADO";
    public static final String FIRMANTE_INACTIVADO               = "FIRMANTE_INACTIVADO";

    // -------------------------------------------------------------------------
    // Resultados
    // -------------------------------------------------------------------------

    public static final String RESULTADO_EXITO = "EXITO";
    public static final String RESULTADO_ERROR = "ERROR";

    // -------------------------------------------------------------------------
    // Entidades
    // -------------------------------------------------------------------------

    public static final String ENTIDAD_ESAL         = "ESAL";
    public static final String ENTIDAD_IMPORTACION  = "IMPORTACION";
    public static final String ENTIDAD_DOCUMENTO    = "DOCUMENTO";
    public static final String ENTIDAD_DICCIONARIO  = "DICCIONARIO";
    public static final String ENTIDAD_CERTIFICADO  = "CERTIFICADO";
    public static final String ENTIDAD_FIRMANTE     = "FIRMANTE";
    public static final String ENTIDAD_NUMERACION   = "NUMERACION";

    private AuditoriaAcciones() {
        // clase de constantes — no instanciar
    }
}
