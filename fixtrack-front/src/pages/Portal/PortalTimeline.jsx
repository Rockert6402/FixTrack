import { formatDateTime } from '../../utils/format'
import { STATUS_LABELS } from '../../utils/labels'
import './PortalTimeline.css'

// Línea de tiempo de la orden: cada cambio de estado con su fecha; el último es el estado actual
export default function PortalTimeline({ entries }) {
  if (!entries?.length) {
    return null
  }
  return (
    <ol className="portal-timeline">
      {entries.map((entry, index) => (
        <li
          key={`${entry.status}-${entry.date}`}
          className={`portal-timeline__item${index === entries.length - 1 ? ' portal-timeline__item--current' : ''}`}
        >
          <span className="portal-timeline__status">{STATUS_LABELS[entry.status]}</span>
          <span className="portal-timeline__date">{formatDateTime(entry.date)}</span>
        </li>
      ))}
    </ol>
  )
}
