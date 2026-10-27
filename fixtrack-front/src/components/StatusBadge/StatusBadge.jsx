import { STATUS_LABELS } from '../../utils/labels'
import './StatusBadge.css'

// Etiqueta de color del estado de la orden; el color sale de las variables --status-*
export default function StatusBadge({ status }) {
  const modifier = status.toLowerCase().replaceAll('_', '-')
  return <span className={`status-badge status-badge--${modifier}`}>{STATUS_LABELS[status] ?? status}</span>
}
