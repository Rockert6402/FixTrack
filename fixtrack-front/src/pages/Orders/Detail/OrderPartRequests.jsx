import { useId, useState } from 'react'
import { MdSend } from 'react-icons/md'
import FormField from '../../../components/FormField/FormField'
import PartRequestStatusTag from '../../../components/PartRequestStatusTag/PartRequestStatusTag'
import { useRemoteData } from '../../../hooks/useRemoteData'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import * as partRequestService from '../../../services/partRequestService'
import { getErrorMessage } from '../../../utils/errors'
import { formatDateTime } from '../../../utils/format'
import './OrderPartRequests.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/
const EMPTY = { mode: 'catalog', sparePartId: '', description: '', quantity: '1', estimatedCost: '' }

// Repuestos que no hay en inventario: el técnico los solicita (del catálogo o descritos) y ve en qué van
export default function OrderPartRequests({ order, canRequest, onRequested }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)
  const requests = useRemoteData(() => partRequestService.listByIntervention(order.id), [order.id])
  const candidates = useRemoteData(() => (canRequest ? interventionService.listPartCandidates(order.id) : Promise.resolve([])), [order.id, canRequest])

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    if (form.mode === 'catalog' && !form.sparePartId) return showError('Elija el repuesto del catálogo.')
    if (form.mode === 'manual' && !form.description.trim()) return showError('Describa el repuesto que necesita.')
    if (!/^\d+$/.test(form.quantity) || Number(form.quantity) < 1) return showError('La cantidad debe ser un entero mayor que cero.')
    if (form.estimatedCost && !MONEY_PATTERN.test(form.estimatedCost)) return showError('El costo estimado admite máximo 2 decimales.')
    setSaving(true)
    try {
      const created = await partRequestService.createPartRequest(order.id, {
        sparePartId: form.mode === 'catalog' ? Number(form.sparePartId) : null,
        description: form.mode === 'manual' ? form.description.trim() : null,
        quantity: Number(form.quantity),
        estimatedCost: form.estimatedCost ? Number(form.estimatedCost) : null,
      })
      showSuccess(`Solicitud #${created.id} enviada al coordinador; la orden pasó a «Esperando repuestos».`)
      setForm(EMPTY)
      requests.reload()
      onRequested()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="order-part-requests" aria-labelledby={`${formId}-title`}>
      <h3 id={`${formId}-title`} className="order-info__title">Solicitudes de repuestos</h3>
      {requests.data?.length === 0 && <p className="text-sm text-muted">La orden no tiene solicitudes.</p>}
      {requests.data?.length > 0 && (
        <ul className="order-part-requests__list">
          {requests.data.map((request) => (
            <li key={request.id} className="order-part-requests__item">
              <span className="order-part-requests__name">
                #{request.id} · {request.description} × {request.quantity}
                <span>{formatDateTime(request.requestedAt)}{request.supplierName ? ` · ${request.supplierName}` : ''}{request.rejectionReason ? ` · ${request.rejectionReason}` : ''}</span>
              </span>
              <PartRequestStatusTag status={request.status} />
            </li>
          ))}
        </ul>
      )}

      {canRequest && (
        <form id={formId} className="order-part-requests__form" onSubmit={handleSubmit} noValidate>
          <div className="order-part-requests__modes" role="radiogroup" aria-label="Origen del repuesto">
            <label><input type="radio" checked={form.mode === 'catalog'} onChange={() => update('mode', 'catalog')} /> Del catálogo</label>
            <label><input type="radio" checked={form.mode === 'manual'} onChange={() => update('mode', 'manual')} /> Descrito a mano</label>
          </div>
          {form.mode === 'catalog' ? (
            <FormField id={`${formId}-part`} label="Repuesto" hint="Solo si no alcanza el stock; si hay, regístrelo como usado.">
              {(props) => (
                <select {...props} className="form-input" value={form.sparePartId} onChange={(event) => update('sparePartId', event.target.value)}>
                  <option value="">Elija el repuesto</option>
                  {(candidates.data ?? []).map((part) => <option key={part.id} value={part.id}>{part.name} · stock {part.stock}{part.compatible ? ' · compatible' : ''}</option>)}
                </select>
              )}
            </FormField>
          ) : (
            <FormField id={`${formId}-description`} label="Descripción">
              {(props) => <input {...props} className="form-input" maxLength={500} placeholder="Repuesto, modelo, referencia…" value={form.description} onChange={(event) => update('description', event.target.value)} />}
            </FormField>
          )}
          <div className="order-part-requests__row">
            <FormField id={`${formId}-quantity`} label="Cantidad">
              {(props) => <input {...props} className="form-input" inputMode="numeric" value={form.quantity} onChange={(event) => update('quantity', event.target.value.replace(/\D/g, ''))} />}
            </FormField>
            <FormField id={`${formId}-cost`} label="Costo estimado" hint="Opcional, por unidad.">
              {(props) => <input {...props} className="form-input" inputMode="decimal" value={form.estimatedCost} onChange={(event) => update('estimatedCost', event.target.value.replace(/[^\d.]/g, ''))} />}
            </FormField>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              <MdSend aria-hidden="true" />
              {saving ? 'Enviando…' : 'Solicitar'}
            </button>
          </div>
        </form>
      )}
    </section>
  )
}
