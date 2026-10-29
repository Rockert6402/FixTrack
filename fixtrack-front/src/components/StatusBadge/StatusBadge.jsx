import { useAccessibility } from '../../hooks/useAccessibility'
import { STATUS_LABELS } from '../../utils/labels'
import { STATUS_ICONS } from '../../utils/statusIcons'
import './StatusBadge.css'

// Etiqueta de color del estado de la orden; el color sale de las variables --status-* y en alto contraste lleva ícono
export default function StatusBadge({ status }) {
  const { settings } = useAccessibility()
  const modifier = status.toLowerCase().replaceAll('_', '-')
  const Icon = settings.highContrast ? STATUS_ICONS[status] : null
  return (
    <span className={`status-badge status-badge--${modifier} ${Icon ? 'status-badge--with-icon' : ''}`}>
      {Icon && <Icon className="status-badge__icon" aria-hidden="true" />}
      {STATUS_LABELS[status] ?? status}
    </span>
  )
}
