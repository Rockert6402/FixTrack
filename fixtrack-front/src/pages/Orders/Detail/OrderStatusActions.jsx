import { useState } from 'react'
import { MdArrowForward } from 'react-icons/md'
import ConfirmDialog from '../../../components/ConfirmDialog/ConfirmDialog'
import FormField from '../../../components/FormField/FormField'
import StatusBadge from '../../../components/StatusBadge/StatusBadge'
import { useAuth } from '../../../hooks/useAuth'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import { formatCurrency } from '../../../utils/format'
import { STATUS_LABELS } from '../../../utils/labels'
import { canChangeTo, canEditRepair } from '../../../utils/permissions'
import './OrderStatusActions.css'

// Cambio de estado: solo ofrece las transiciones válidas que el usuario puede hacer; la entrega pide confirmación
export default function OrderStatusActions({ order, onUpdated }) {
  const { user } = useAuth()
  const { showError, showSuccess } = useToast()
  const [note, setNote] = useState('')
  const [saving, setSaving] = useState(false)
  const [confirmDelivery, setConfirmDelivery] = useState(false)

  const allowed = order.nextStatuses.filter((status) => canChangeTo(order, user, status))
  const needsTechnician = !order.technician && order.status === 'RECEIVED'
  const pendingFindings = order.findings.some((finding) => finding.approvalStatus === 'PENDING')
  const readyBlocked = ['IN_DIAGNOSIS', 'WAITING_PARTS', 'IN_REPAIR'].includes(order.status)
    && !order.nextStatuses.includes('READY') && canEditRepair(order, user)

  async function applyStatus(status) {
    setSaving(true)
    try {
      // La entrega tiene su propia acción: el backend verifica que los pagos cubran el total
      if (status === 'DELIVERED') {
        await interventionService.deliver(order.id, { notes: note.trim() || null })
      } else {
        await interventionService.changeStatus(order.id, { status, note: note.trim() || null })
      }
      showSuccess(`La orden pasó a «${STATUS_LABELS[status]}».`)
      setNote('')
      onUpdated()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
      setConfirmDelivery(false)
    }
  }

  function emptyMessage() {
    if (order.nextStatuses.length === 0) return 'La orden está cerrada; su estado ya no cambia.'
    if (needsTechnician) return 'Asigne un técnico para empezar el diagnóstico.'
    return 'El siguiente cambio de estado lo hace otra persona (el técnico asignado o el asesor en la entrega).'
  }

  return (
    <section className="order-status" aria-labelledby="order-status-title">
      <h3 id="order-status-title" className="order-info__title">Estado</h3>
      <p className="order-status__current">Estado actual: <StatusBadge status={order.status} /></p>

      {allowed.length > 0 && !needsTechnician ? (
        <>
          <FormField id="order-status-note" label="Nota del cambio" hint="Opcional; queda en el historial.">
            {(props) => <input {...props} className="form-input" maxLength={500} value={note} onChange={(event) => setNote(event.target.value)} />}
          </FormField>
          <div className="order-status__buttons">
            {allowed.map((status) => (
              <button
                key={status}
                type="button"
                className={`btn ${status === 'DELIVERED' ? 'btn--primary' : 'btn--secondary'}`}
                disabled={saving || (status === 'DELIVERED' && order.charges.balance > 0)}
                onClick={() => (status === 'DELIVERED' ? setConfirmDelivery(true) : applyStatus(status))}
              >
                <MdArrowForward aria-hidden="true" />
                {STATUS_LABELS[status]}
              </button>
            ))}
          </div>
        </>
      ) : (
        <p className="text-sm text-muted">{emptyMessage()}</p>
      )}
      {allowed.includes('DELIVERED') && order.charges.balance > 0 && (
        <p className="form-hint">Falta cobrar {formatCurrency(order.charges.balance)}; regístrelo en «Salida» para poder entregar.</p>
      )}
      {readyBlocked && (
        <p className="form-hint">
          {pendingFindings
            ? 'Para pasar a «Listo para entregar» falta la respuesta del cliente a los daños adicionales pendientes.'
            : 'Para pasar a «Listo para entregar» marque primero el resultado de la orden.'}
        </p>
      )}

      <ConfirmDialog
        open={confirmDelivery}
        title="Entregar equipo"
        message={`Se entregará la orden ${order.code} a ${order.customer.fullName}. Se guarda el total y se borra la contraseña de desbloqueo. Esta acción no se puede deshacer.`}
        confirmLabel="Entregar"
        danger={false}
        onConfirm={() => applyStatus('DELIVERED')}
        onCancel={() => setConfirmDelivery(false)}
      />
    </section>
  )
}
