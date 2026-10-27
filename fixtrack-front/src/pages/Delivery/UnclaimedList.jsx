import { MdPhone, MdEmail, MdWarningAmber } from 'react-icons/md'
import { formatDateTime } from '../../utils/format'
import './UnclaimedList.css'

// Equipos listos que el cliente no ha reclamado, con su contacto (o «Sin contacto») para llamarlo
export default function UnclaimedList({ orders, onOpen }) {
  return (
    <section className="page__section" aria-labelledby="unclaimed-title">
      <h2 id="unclaimed-title" className="page__section-title unclaimed__title">
        <MdWarningAmber aria-hidden="true" />
        Equipos no reclamados ({orders.length})
      </h2>
      <ul className="unclaimed">
        {orders.map((order) => (
          <li key={order.id} className="unclaimed__item">
            <button type="button" className="unclaimed__code" onClick={() => onOpen(order.id)}>{order.code}</button>
            <span className="unclaimed__customer">
              {order.customerName}
              <span>{order.device} · listo desde {formatDateTime(order.readyAt)} ({order.daysWaiting} días)</span>
            </span>
            <span className="unclaimed__contact">
              {order.noContact ? (
                <span className="tag tag--danger">Sin contacto</span>
              ) : (
                <>
                  {order.phone && <a href={`tel:${order.phone}`}><MdPhone aria-hidden="true" /> {order.phone}</a>}
                  {order.email && <a href={`mailto:${order.email}`}><MdEmail aria-hidden="true" /> {order.email}</a>}
                </>
              )}
            </span>
          </li>
        ))}
      </ul>
    </section>
  )
}
