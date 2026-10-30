import { useState } from 'react'
import FormField from '../../components/FormField/FormField'
import Modal from '../../components/Modal/Modal'
import PartRequestForm from '../../components/PartRequestForm/PartRequestForm'
import SearchSelect from '../../components/SearchSelect/SearchSelect'
import { useRemoteData } from '../../hooks/useRemoteData'
import * as interventionService from '../../services/interventionService'
import { STATUS_LABELS } from '../../utils/labels'
import './NewPartRequestModal.css'

// Nueva solicitud desde «Mis solicitudes»: el técnico elige una de sus órdenes en trabajo y pide el repuesto
export default function NewPartRequestModal({ onClose, onRequested }) {
  const [orderId, setOrderId] = useState('')
  const orders = useRemoteData(() => interventionService.listMyInterventions({ inProgress: true, size: 100 }), [])
  const orderOptions = (orders.data?.content ?? []).map((order) => ({
    value: order.id,
    label: `${order.code} · ${order.device} · ${STATUS_LABELS[order.status]}`,
  }))
  const noOrders = !orders.loading && !orders.error && orderOptions.length === 0

  return (
    <Modal
      open
      title="Nueva solicitud de repuesto"
      onClose={onClose}
      footer={<button type="button" className="btn btn--secondary" onClick={onClose}>Cerrar</button>}
    >
      <div className="new-part-request">
        {noOrders ? (
          <p className="text-sm text-muted">
            No tiene órdenes en diagnóstico, esperando repuestos ni en reparación. Solo se piden repuestos para órdenes en trabajo.
          </p>
        ) : (
          <FormField id="new-part-request-order" label="Orden" error={orders.error} hint="Sus órdenes en diagnóstico, esperando repuestos o en reparación.">
            {(props) => (
              <SearchSelect
                {...props}
                options={orderOptions}
                value={orderId}
                disabled={orders.loading}
                placeholder={orders.loading ? 'Cargando órdenes…' : 'Escriba el código o el equipo'}
                emptyMessage="No hay órdenes con esa búsqueda"
                onChange={setOrderId}
              />
            )}
          </FormField>
        )}
        {orderId && <PartRequestForm key={orderId} orderId={Number(orderId)} onRequested={onRequested} />}
      </div>
    </Modal>
  )
}
