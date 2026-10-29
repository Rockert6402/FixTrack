import { useAccessibility } from '../../hooks/useAccessibility'
import { STATUS_LABELS } from '../../utils/labels'
import { STATUS_ICONS } from '../../utils/statusIcons'
import './PortalStatusHero.css'

const MESSAGES = {
  RECEIVED: 'Recibimos su equipo y pronto empezará el diagnóstico.',
  IN_DIAGNOSIS: 'Un técnico está revisando su equipo.',
  WAITING_PARTS: 'Estamos esperando un repuesto para continuar.',
  IN_REPAIR: 'Su equipo está en reparación.',
  READY: 'Su equipo está listo. Puede pasar a recogerlo.',
  DELIVERED: 'Su equipo ya fue entregado.',
  CANCELLED: 'La orden fue cancelada.',
}

// Estado actual en grande, con el color del estado (y su ícono en alto contraste) y una frase que lo explica
export default function PortalStatusHero({ status }) {
  const { settings } = useAccessibility()
  const modifier = status.toLowerCase().replaceAll('_', '-')
  const Icon = settings.highContrast ? STATUS_ICONS[status] : null
  return (
    <div className={`portal-status portal-status--${modifier}`} role="status">
      <span className="portal-status__label">Estado actual</span>
      <strong className="portal-status__value">
        {Icon && <Icon className="portal-status__icon" aria-hidden="true" />}
        {STATUS_LABELS[status]}
      </strong>
      <span className="portal-status__message">{MESSAGES[status]}</span>
    </div>
  )
}
