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
