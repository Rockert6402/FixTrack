import { AVAILABILITY_LABELS } from '../../utils/labels'
import './TechnicianAvailabilityList.css'

const AVAILABILITY_TONES = { AVAILABLE: 'tag--success', BUSY: 'tag--warning', ABSENT: 'tag--danger' }

// Técnicos activos con su disponibilidad y cuántas órdenes abiertas tienen
export default function TechnicianAvailabilityList({ technicians, loading }) {
  if (loading) {
    return <p className="technician-list__message">Cargando…</p>
  }
  if (!technicians?.length) {
    return <p className="technician-list__message">No hay técnicos activos.</p>
  }
  return (
    <ul className="technician-list">
      {technicians.map((technician) => (
        <li key={technician.id} className="technician-list__item">
          <span className="technician-list__name">{technician.fullName}</span>
          <span className={`tag ${AVAILABILITY_TONES[technician.availability] ?? ''}`}>
            {AVAILABILITY_LABELS[technician.availability] ?? 'Sin registrar'}
          </span>
          <span className="technician-list__orders">
            {technician.openOrders} {technician.openOrders === 1 ? 'orden abierta' : 'órdenes abiertas'}
          </span>
        </li>
      ))}
    </ul>
  )
}
