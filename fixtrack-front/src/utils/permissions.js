import { ROLES } from './labels'

const FRONT_DESK = [ROLES.ADVISOR, ROLES.COORDINATOR, ROLES.ADMIN]

// Recepción: asesor, coordinador y administrador (reciben, asignan técnico y entregan)
export function isFrontDesk(user) {
  return Boolean(user) && FRONT_DESK.includes(user.role)
}

export function isAssignedTechnician(order, user) {
  return Boolean(user && order?.technician) && order.technician.id === user.id
}

// Mismas reglas del backend; ocultar un botón no reemplaza su validación
export function canEditRepair(order, user) {
  return isAssignedTechnician(order, user) || user?.role === ROLES.ADMIN
}

export function canChangeTo(order, user, status) {
  return status === 'DELIVERED' ? isFrontDesk(user) : canEditRepair(order, user)
}

const IN_PROGRESS = ['IN_DIAGNOSIS', 'WAITING_PARTS', 'IN_REPAIR']

// No reparable o rechazada: ya no acepta repuestos, mano de obra ni daños, y no se reabre
export function endsWithoutRepair(order) {
  return order.repairOutcome === 'NOT_REPAIRABLE' || order.repairOutcome === 'REJECTED_BY_CUSTOMER'
}

// Daños, mano de obra y resultado: técnico asignado (o administrador) mientras la orden está en trabajo
export function canWorkOnRepair(order, user) {
  return canEditRepair(order, user) && IN_PROGRESS.includes(order.status) && !endsWithoutRepair(order)
}

// Los repuestos usados los registra y corrige solo el técnico asignado
export function canEditUsedParts(order, user) {
  return isAssignedTechnician(order, user) && IN_PROGRESS.includes(order.status) && !endsWithoutRepair(order)
}

export function isOpen(order) {
  return order.status !== 'DELIVERED' && order.status !== 'CANCELLED'
}
