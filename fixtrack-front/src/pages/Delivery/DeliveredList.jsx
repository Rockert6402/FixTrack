import { useState } from 'react'
import { MdDownload } from 'react-icons/md'
import { useToast } from '../../hooks/useToast'
import * as interventionService from '../../services/interventionService'
import { getErrorMessage } from '../../utils/errors'
import { downloadFile } from '../../utils/files'
import './DeliveredList.css'

// Órdenes entregadas en esta visita a la página, con la descarga de su comprobante de entrega
export default function DeliveredList({ orders }) {
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
      <h2 id="delivered-title" className="page__section-title">Entregadas ahora</h2>
      <ul className="delivered-list">
        {orders.map((order) => (
          <li key={order.id} className="delivered-list__item">
            <span><strong>{order.code}</strong> · {order.customerName}</span>
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
