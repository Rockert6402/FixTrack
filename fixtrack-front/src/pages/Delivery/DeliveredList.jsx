import { useState } from 'react'
import { MdDownload } from 'react-icons/md'
import { useToast } from '../../hooks/useToast'
import * as interventionService from '../../services/interventionService'
import { getErrorMessage } from '../../utils/errors'
import { formatDateTime } from '../../utils/format'
import { downloadFile } from '../../utils/files'
import './DeliveredList.css'

// Órdenes entregadas hoy (se cargan del backend), con la descarga de su comprobante de entrega
export default function DeliveredList({ state }) {
  const orders = state.data ?? []
  const { showError } = useToast()
  const [downloading, setDownloading] = useState(null)

  async function handleDownload(order) {
    setDownloading(order.id)
    try {
      downloadFile(await interventionService.getDeliveryReceipt(order.id), `entrega-${order.code}.pdf`)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setDownloading(null)
    }
  }

  return (
    <section className="page__section" aria-labelledby="delivered-title">
      <h2 id="delivered-title" className="page__section-title">
        Entregadas hoy{state.data && ` (${orders.length})`}
      </h2>
      {state.loading && <p className="text-sm text-muted">Cargando…</p>}
      {state.error && (
        <p className="text-sm text-muted">
          {state.error}{' '}
          <button type="button" className="btn btn--ghost" onClick={state.reload}>Reintentar</button>
        </p>
      )}
      {state.data && orders.length === 0 && <p className="text-sm text-muted">Aún no se ha entregado ninguna orden hoy.</p>}
      <ul className="delivered-list">
        {orders.map((order) => (
          <li key={order.id} className="delivered-list__item">
            <span>
              <strong>{order.code}</strong> · {order.customerName}
              <span className="delivered-list__meta">{order.device} · {formatDateTime(order.deliveredAt)}</span>
            </span>
            <button type="button" className="btn btn--secondary" disabled={downloading === order.id} onClick={() => handleDownload(order)}>
              <MdDownload aria-hidden="true" />
              {downloading === order.id ? 'Descargando…' : 'Comprobante de entrega'}
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}
