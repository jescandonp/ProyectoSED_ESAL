package co.gov.bogota.sed.esal.service;

public final class AuditoriaAcciones {

    private AuditoriaAcciones() {}

    // ── Entidades ─────────────────────────────────────────────────────────────
    public static final String ENTIDAD_ESAL        = "ESAL";
    public static final String ENTIDAD_CERTIFICADO = "CERTIFICADO";
    public static final String ENTIDAD_FIRMANTE    = "FIRMANTE";
    public static final String ENTIDAD_NUMERACION  = "NUMERACION";

    // ── Resultados ────────────────────────────────────────────────────────────
    public static final String RESULTADO_EXITO = "EXITO";
    public static final String RESULTADO_ERROR = "ERROR";

    // ── I1 ───────────────────────────────────────────────────────────────────
    public static final String ESAL_CREADA             = "ESAL_CREADA";
    public static final String ESAL_ACTUALIZADA        = "ESAL_ACTUALIZADA";
    public static final String ESTADO_CAMBIADO         = "ESTADO_CAMBIADO";
    public static final String IMPORTACION_HISTORICA   = "IMPORTACION_HISTORICA";
    public static final String IMPORTACION_DICCIONARIO = "IMPORTACION_DICCIONARIO";
    public static final String DOCUMENTO_CARGADO       = "DOCUMENTO_CARGADO";

    // ── I2 ───────────────────────────────────────────────────────────────────
    public static final String BUSQUEDA_ESAL              = "BUSQUEDA_ESAL";
    public static final String DETALLE_ESAL_CONSULTADO    = "DETALLE_ESAL_CONSULTADO";
    public static final String PREVIEW_CERTIFICADO_GENERADO = "PREVIEW_CERTIFICADO_GENERADO";

    // ── I3 ───────────────────────────────────────────────────────────────────
    public static final String CERTIFICADO_GENERACION_SOLICITADA = "CERTIFICADO_GENERACION_SOLICITADA";
    public static final String CERTIFICADO_GENERADO              = "CERTIFICADO_GENERADO";
    public static final String CERTIFICADO_GENERACION_FALLIDA    = "CERTIFICADO_GENERACION_FALLIDA";
    public static final String CERTIFICADO_BLOQUEADO             = "CERTIFICADO_BLOQUEADO";
    public static final String CERTIFICADO_DESCARGADO            = "CERTIFICADO_DESCARGADO";
    public static final String NUMERACION_ACTUALIZADA            = "NUMERACION_ACTUALIZADA";
    public static final String FIRMANTE_CREADO                   = "FIRMANTE_CREADO";
    public static final String FIRMANTE_ACTUALIZADO              = "FIRMANTE_ACTUALIZADO";
    public static final String FIRMANTE_ACTIVADO                 = "FIRMANTE_ACTIVADO";
    public static final String FIRMANTE_INACTIVADO               = "FIRMANTE_INACTIVADO";

    // ── I4 ───────────────────────────────────────────────────────────────────
    public static final String ENTIDAD_SEGURIDAD            = "SEGURIDAD";
    public static final String ACCESO_DENEGADO              = "ACCESO_DENEGADO";
    public static final String TOKEN_INVALIDO_O_AUSENTE     = "TOKEN_INVALIDO_O_AUSENTE";
    public static final String DOCUMENTO_DESCARGADO         = "DOCUMENTO_DESCARGADO";
}
