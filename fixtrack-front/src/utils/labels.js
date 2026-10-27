export const STATUS_LABELS = {
  RECEIVED: 'Recibido',
  IN_DIAGNOSIS: 'En diagnóstico',
  WAITING_PARTS: 'Esperando repuestos',
  IN_REPAIR: 'En reparación',
  READY: 'Listo para entregar',
  DELIVERED: 'Entregado',
  CANCELLED: 'Cancelado',
}

export const ROLE_LABELS = {
  ADMIN: 'Administrador',
  COORDINATOR: 'Coordinador de inventario',
  ADVISOR: 'Asesor',
  TECHNICIAN: 'Técnico',
  CUSTOMER: 'Cliente',
}

export const ROLES = {
  ADMIN: 'ADMIN',
  COORDINATOR: 'COORDINATOR',
  ADVISOR: 'ADVISOR',
  TECHNICIAN: 'TECHNICIAN',
  CUSTOMER: 'CUSTOMER',
}

export const DOCUMENT_TYPE_LABELS = {
  CC: 'Cédula de ciudadanía',
  TI: 'Tarjeta de identidad',
  CE: 'Cédula de extranjería',
  PASSPORT: 'Pasaporte',
  PPT: 'Permiso por protección temporal',
}

export const AVAILABILITY_LABELS = {
  AVAILABLE: 'Disponible',
  BUSY: 'Ocupado',
  ABSENT: 'Ausente',
}

export const DEVICE_TYPE_LABELS = {
  PHONE: 'Teléfono',
  TABLET: 'Tablet',
  SMARTWATCH: 'Reloj inteligente',
  SPEAKER: 'Parlante',
  OTHER: 'Otro',
}

export const QUALITY_LABELS = {
  ORIGINAL: 'Original',
  OEM: 'OEM',
  GENERIC: 'Genérico',
}

export const STOCK_STATUS_LABELS = {
  OK: 'Suficiente',
  LOW: 'Bajo',
  OUT: 'Agotado',
}

export const SERVICE_TYPE_LABELS = {
  SCREEN_REPLACEMENT: 'Cambio de pantalla',
  BATTERY_REPLACEMENT: 'Cambio de batería',
  CHARGING_PORT: 'Pin de carga',
  CAMERA: 'Cámara',
  SOFTWARE_FLASHING: 'Software / flasheo',
  WATER_DAMAGE: 'Daño por humedad',
  AUDIO: 'Audio',
  GENERAL_DIAGNOSIS: 'Diagnóstico general',
  OTHER: 'Otro',
}

export const FINDING_ORIGIN_LABELS = {
  INITIAL: 'Inicial',
  ADDITIONAL: 'Adicional',
}

export const APPROVAL_LABELS = {
  APPROVED: 'Aprobado',
  PENDING: 'Pendiente',
  REJECTED: 'Rechazado',
}

// Convierte un mapa de etiquetas en opciones para un <select>
export function toOptions(labels) {
  return Object.entries(labels).map(([value, label]) => ({ value, label }))
}

export const OUTCOME_LABELS = {
  REPAIRED: 'Reparado',
  NOT_REPAIRABLE: 'No reparable',
  REJECTED_BY_CUSTOMER: 'Rechazado por el cliente',
}

export const PAYMENT_TYPE_LABELS = {
  DEPOSIT: 'Abono',
  FINAL: 'Cobro final',
  REFUND: 'Devolución',
}

export const PAYMENT_METHOD_LABELS = {
  CASH: 'Efectivo',
  TRANSFER: 'Transferencia',
  CARD: 'Tarjeta',
}
