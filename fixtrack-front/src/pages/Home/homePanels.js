import {
  MdAssignment,
  MdBuild,
  MdEngineering,
  MdHourglassEmpty,
  MdInventory,
  MdMoveToInbox,
  MdSchedule,
  MdTaskAlt,
} from 'react-icons/md'

const READY = { key: 'ready', title: 'Listas para entregar', icon: MdTaskAlt, tone: 'success', to: '/ordenes?status=READY' }
const RECEIVED_TODAY = {
  key: 'receivedToday',
  title: 'Ingresadas hoy',
  icon: MdMoveToInbox,
  to: (today) => `/ordenes?receivedFrom=${today}&receivedTo=${today}`,
}
const OVERDUE = { key: 'overdue', title: 'Atrasadas', icon: MdSchedule, tone: 'danger', to: '/ordenes?overdue=true' }
const IN_PROGRESS = { key: 'inProgress', title: 'En proceso', icon: MdBuild, to: '/ordenes?inProgress=true' }
const UNCLAIMED = { key: 'unclaimed', title: 'Sin reclamar', icon: MdHourglassEmpty, tone: 'warning', to: '/ordenes?unclaimed=true' }
const STOCK_ALERTS = { key: 'stockAlerts', title: 'Repuestos en alerta', icon: MdInventory, tone: 'warning', to: '/inventario?alertas=1' }

// Indicadores del panel de Inicio de cada rol; key es el campo de counters que devuelve /api/dashboard
// (stockAlerts se cuenta de la lista de alertas) y to, el listado filtrado al que lleva la tarjeta
export const HOME_PANELS = {
  ADMIN: {
    description: 'Resumen general del taller.',
    stats: [RECEIVED_TODAY, OVERDUE, IN_PROGRESS, READY, STOCK_ALERTS],
  },
  COORDINATOR: {
    description: 'Órdenes del taller, técnicos y estado del inventario.',
    stats: [READY, RECEIVED_TODAY, OVERDUE, IN_PROGRESS, UNCLAIMED, STOCK_ALERTS],
  },
  ADVISOR: {
    description: 'Órdenes por recibir, entregar y cobrar.',
    stats: [READY, RECEIVED_TODAY, OVERDUE, IN_PROGRESS, UNCLAIMED],
  },
  TECHNICIAN: {
    description: 'Sus órdenes asignadas y su avance.',
    stats: [
      { key: 'assigned', title: 'Mis órdenes asignadas', icon: MdEngineering, to: '/ordenes' },
      OVERDUE,
      IN_PROGRESS,
      { key: 'waitingParts', title: 'Esperando repuestos', icon: MdAssignment, tone: 'warning', to: '/ordenes?status=WAITING_PARTS' },
    ],
  },
}
