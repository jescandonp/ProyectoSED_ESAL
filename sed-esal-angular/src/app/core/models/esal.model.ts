export type EstadoEsal = 'ACTIVO' | 'SUSPENDIDO' | 'EN_LIQUIDACION' | 'CANCELADO';
export type EstadoCompletitud =
  | 'LISTO_PARA_CERTIFICAR'
  | 'INCOMPLETO_NO_BLOQUEANTE'
  | 'INCOMPLETO_BLOQUEANTE';

export interface EsalResumen {
  id: number;
  nombre: string;
  idSipej: string | null;
  nit: string | null;
  domicilio: string | null;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  createdAt: string;
}

export interface EsalDetalle extends EsalResumen {
  correoElectronico: string | null;
  terminoDuracion: string | null;
  objetoSocial: string | null;
  createdBy: string | null;
  updatedAt: string | null;
  updatedBy: string | null;
}

export interface EsalInformacionPrincipalDto {
  id?: number | null;
  nombre: string | null;
  idSipej: string | null;
  nit: string | null;
  domicilio: string | null;
  correoElectronico: string | null;
  terminoDuracion: string | null;
  objetoSocial: string | null;
  estado: EstadoEsal | null;
}

export interface PersoneriaJuridicaDto {
  id?: number | null;
  reconocimientoPersoneriaJuridica: string | null;
  fechaReconocimientoPersoneriaJuridica: string | null;
  entidadQueExpide: string | null;
  inscripcion: string | null;
  fechaInscripcion: string | null;
  entidadQueInscribio: string | null;
}

export type TipoNombramiento = 'REPRESENTANTE_LEGAL' | 'REPRESENTANTE_LEGAL_SUPLENTE';

export interface NombramientoDto {
  id?: number | null;
  tipoNombramiento: TipoNombramiento | null;
  nombre: string | null;
  tipoDocumento: string | null;
  numeroDocumento: string | null;
  cargo: string | null;
  actaAprueba: string | null;
  fechaActa: string | null;
  facultadesLimitaciones: string | null;
  vigente: boolean | null;
}

export interface OrganoAdministracionDto {
  id?: number | null;
  organo: string | null;
  miembro: string | null;
  cargo: string | null;
  tipoDocumento: string | null;
  numeroDocumento: string | null;
  actaAprueba: string | null;
  fechaActa: string | null;
  actaAclaratoria: string | null;
  fechaActaAclaratoria: string | null;
  facultadesLimitaciones: string | null;
}

export interface MantenimientoEsalDto {
  id: number;
  informacionPrincipal: EsalInformacionPrincipalDto;
  personeriaJuridica: PersoneriaJuridicaDto | null;
  representantes: NombramientoDto[];
  organosAdministracion: OrganoAdministracionDto[];
  completitud: CompletitudResponse | null;
  advertencias: AdvertenciaItem[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AdvertenciaItem {
  seccion: string;
  campo: string;
  tipo: string;
  bloqueante: boolean;
  mensaje: string;
}

export interface CompletitudResponse {
  esalId: number;
  idSipej: string | null;
  nombre: string;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  totalAdvertencias: number;
  advertenciasBloqueantes: number;
  advertenciasNoBloqueantes: number;
  advertencias: AdvertenciaItem[];
}

export interface DocumentoSoporte {
  id: number;
  esalId: number;
  tipoProceso: string | null;
  tipoDocumento: string | null;
  tipoDocumental: string | null;
  subtipoDocumental: string | null;
  referenciaActo: string | null;
  fechaActo: string | null;
  observacion: string | null;
  vigente: boolean;
  nombreArchivo: string;
  contentType: string;
  tamanoBytes: number;
  estadoValidacion: string;
  createdAt: string;
  createdBy: string;
}

export interface ImportResultDto {
  totalLeidos: number;
  totalImportados: number;
  totalRechazados: number;
  totalAdvertencias: number;
  totalReformas: number;
  fechaImportacion: string;
  importadoPor: string;
}

// ── I2 ─────────────────────────────────────────────────────────────────────
export interface BusquedaResultado {
  id: number;
  nombre: string;
  idSipej: string | null;
  nit: string | null;
  domicilio: string | null;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  updatedAt: string | null;
}

export interface BloqueoItem {
  seccion: string;
  campo: string;
  tipo: string;
  mensaje: string;
  origenHistorico: boolean;
}

export interface CampoPreview {
  etiqueta: string;
  valor: string | null;
  faltante: boolean;
  obligatorio: boolean;
  origenHistorico: boolean;
}

export interface SeccionPreview {
  nombre: string;
  campos: CampoPreview[];
}

export interface PreviewCertificado {
  esalId: number;
  idSipej: string | null;
  nit: string | null;
  nombre: string;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  versionDatos: string | null;
  generacionHabilitada: boolean;
  alertaEstado: string | null;
  secciones: SeccionPreview[];
  advertencias: string[];
  bloqueos: BloqueoItem[];
}

// ── I3 ─────────────────────────────────────────────────────────────────────
export type EstadoCertificado = 'GENERADO' | 'FALLIDO' | 'BLOQUEADO';

export interface CertificadoDto {
  certificadoId: number;
  esalId: number;
  idSipej: string | null;
  nit: string | null;
  numeroCertificado: string | null;
  estadoCertificado: EstadoCertificado;
  fechaExpedicion: string | null;
  versionDatos: string | null;
  firmanteNombre: string | null;
  firmanteCargo: string | null;
  plantillaVersion: string | null;
  hashSha256: string | null;
  nombreArchivo: string | null;
  tamanoBytes: number | null;
  errorDetalle: string | null;
  createdAt: string | null;
  createdBy: string | null;
}

export interface NumeracionDto {
  id: number | null;
  prefijo: string;
  anio: number;
  ultimoConsecutivo: number;
  activo: boolean;
  updatedAt: string | null;
}

export interface FirmanteDto {
  id: number;
  nombre: string;
  cargo: string;
  dependencia: string | null;
  fechaInicioVigencia: string;
  fechaFinVigencia: string | null;
  activo: boolean;
  createdAt: string | null;
  createdBy: string | null;
}

export interface AuditoriaItem {
  id: number;
  usuario: string;
  rol: string;
  accion: string;
  entidad: string | null;
  entidadId: number | null;
  idSipej: string | null;
  resultado: string | null;
  detalle: string | null;
  createdAt: string;
}

// ── I2: Búsqueda operativa ────────────────────────────────────────────────────

export interface BusquedaResultado {
  id: number;
  nombre: string;
  idSipej: string | null;
  nit: string | null;
  domicilio: string | null;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  updatedAt: string | null;
}

export interface PersoneriaSeccion {
  reconocimiento: string | null;
  fechaReconocimiento: string | null;
  entidadQueExpide: string | null;
  inscripcion: string | null;
  fechaInscripcion: string | null;
  entidadQueInscribio: string | null;
}

export interface ReformaItem {
  orden: number | null;
  tipoActo: string | null;
  numeroActo: string | null;
  fechaActo: string | null;
  entidadQueExpide: string | null;
  descripcion: string | null;
}

export interface NombramientoItem {
  tipoNombramiento: string | null;
  nombre: string | null;
  tipoDocumento: string | null;
  numeroDocumento: string | null;
  cargo: string | null;
  actaAprueba: string | null;
  fechaActa: string | null;
  facultadesLimitaciones: string | null;
}

export interface OrganoItem {
  organo: string | null;
  miembro: string | null;
  cargo: string | null;
  actaAprueba: string | null;
  fechaActa: string | null;
}

export interface ActuacionItem {
  tipoActuacion: string | null;
  acta: string | null;
  fechaActa: string | null;
  resolucion: string | null;
  fechaResolucion: string | null;
  motivo: string | null;
  tiempoSuspension: string | null;
  fechaInicio: string | null;
}

export interface BusquedaDetalle {
  esalId: number;
  nombre: string;
  idSipej: string | null;
  nit: string | null;
  domicilio: string | null;
  correoElectronico: string | null;
  terminoDuracion: string | null;
  objetoSocial: string | null;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  updatedAt: string | null;
  personeria: PersoneriaSeccion | null;
  reformas: ReformaItem[];
  nombramientos: NombramientoItem[];
  organos: OrganoItem[];
  actuaciones: ActuacionItem[];
  documentos: DocumentoSoporte[];
  completitud: CompletitudResponse | null;
}

// ── I2: Vista previa certificado ─────────────────────────────────────────────

export interface BloqueoItem {
  seccion: string;
  campo: string;
  tipo: string;
  mensaje: string;
  origenHistorico: boolean;
}

export interface CampoPreview {
  etiqueta: string;
  valor: string | null;
  faltante: boolean;
  obligatorio: boolean;
  origenHistorico: boolean;
}

export interface SeccionPreview {
  nombre: string;
  campos: CampoPreview[];
}

export interface PreviewCertificado {
  esalId: number;
  idSipej: string | null;
  nit: string | null;
  nombre: string;
  estado: EstadoEsal;
  estadoCompletitud: EstadoCompletitud;
  versionDatos: string | null;
  generacionHabilitada: boolean;
  alertaEstado: string | null;
  secciones: SeccionPreview[];
  advertencias: string[];
  bloqueos: BloqueoItem[];
}
