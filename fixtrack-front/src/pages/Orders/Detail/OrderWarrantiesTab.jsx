import { useState } from 'react'
import { MdAdd, MdChevronRight } from 'react-icons/md'
import StatusBadge from '../../../components/StatusBadge/StatusBadge'
import { useAuth } from '../../../hooks/useAuth'
import { formatDate, formatDateTime, todayInBogota } from '../../../utils/format'
import { isFrontDesk } from '../../../utils/permissions'
import WarrantyModal from './WarrantyModal'
import './OrderWarrantiesTab.css'

// Garantías anexadas a la orden original; el asesor abre una nueva mientras la garantía esté vigente
export default function OrderWarrantiesTab({ order, technicians, onOpenOrder, onUpdated }) {
  const { user } = useAuth()
  const [opening, setOpening] = useState(false)
  const active = order.status === 'DELIVERED' && order.warrantyUntil && order.warrantyUntil >= todayInBogota()
  const canOpen = isFrontDesk(user) && active

  function warrantyNote() {
    if (order.status !== 'DELIVERED') return 'La garantía empieza cuando se entrega el equipo.'
    if (!order.warrantyUntil) return 'La orden se entregó sin garantía.'
    return active ? `Garantía vigente hasta el ${formatDate(order.warrantyUntil)}.` : `La garantía venció el ${formatDate(order.warrantyUntil)}.`
  }

  return (
    <div className="order-warranties">
      <div className="order-warranties__header">
        <p className="text-sm text-muted">{warrantyNote()}</p>
        {canOpen && (
          <button type="button" className="btn btn--primary" onClick={() => setOpening(true)}>
            <MdAdd aria-hidden="true" />
            Abrir garantía
          </button>
        )}
      </div>
      {order.warranties.length === 0 ? (
        <p className="text-sm text-muted">La orden no tiene garantías.</p>
      ) : (
        <ul className="order-warranties__list">
          {order.warranties.map((warranty) => (
            <li key={warranty.id}>
              <button type="button" className="order-warranties__item" onClick={() => onOpenOrder?.(warranty.id)} disabled={!onOpenOrder}>
                <span className="order-warranties__code">{warranty.code}</span>
                <StatusBadge status={warranty.status} />
                <span className="order-warranties__dates">
                  Ingreso {formatDateTime(warranty.receivedAt)}{warranty.deliveredAt ? ` · entregada ${formatDateTime(warranty.deliveredAt)}` : ''}
                </span>
                {onOpenOrder && <MdChevronRight aria-hidden="true" />}
              </button>
            </li>
          ))}
        </ul>
      )}
      {opening && (
        <WarrantyModal
          order={order}
          technicians={technicians}
          onClose={() => setOpening(false)}
          onCreated={(warranty) => {
            setOpening(false)
            onUpdated()
            onOpenOrder?.(warranty.id)
          }}
        />
      )}
    </div>
  )
}
