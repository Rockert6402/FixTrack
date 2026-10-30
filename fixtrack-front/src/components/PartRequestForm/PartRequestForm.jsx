import { useId, useState } from 'react'
import { MdSend } from 'react-icons/md'
import { useRemoteData } from '../../hooks/useRemoteData'
import { useToast } from '../../hooks/useToast'
import * as interventionService from '../../services/interventionService'
import * as partRequestService from '../../services/partRequestService'
import { getErrorMessage } from '../../utils/errors'
import FormField from '../FormField/FormField'
import SearchSelect from '../SearchSelect/SearchSelect'
import './PartRequestForm.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/
const EMPTY = { mode: 'catalog', sparePartId: '', description: '', quantity: '1', estimatedCost: '' }

// Solicitud de un repuesto que no hay en inventario para una orden: del catálogo (con búsqueda) o descrito a mano
// Al enviarla, la orden pasa a «Esperando repuestos»; la usan el detalle de la orden y «Mis solicitudes»
export default function PartRequestForm({ orderId, onRequested }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)
  const candidates = useRemoteData(() => interventionService.listPartCandidates(orderId), [orderId])
  const partOptions = (candidates.data ?? []).map((part) => ({
    value: part.id,
    label: `${part.name} · stock ${part.stock}${part.compatible ? ' · compatible' : ''}`,
  }))

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
      const created = await partRequestService.createPartRequest(orderId, {
        sparePartId: form.mode === 'catalog' ? Number(form.sparePartId) : null,
        description: form.mode === 'manual' ? form.description.trim() : null,
        quantity: Number(form.quantity),
        estimatedCost: form.estimatedCost ? Number(form.estimatedCost) : null,
      })
      showSuccess(`Solicitud #${created.id} enviada al coordinador; la orden pasó a «Esperando repuestos».`)
      setForm(EMPTY)
      onRequested(created)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <form id={formId} className="part-request-form" onSubmit={handleSubmit} noValidate>
      <div className="part-request-form__modes" role="radiogroup" aria-label="Origen del repuesto">
        <label><input type="radio" name={`${formId}-mode`} checked={form.mode === 'catalog'} onChange={() => update('mode', 'catalog')} /> Del catálogo</label>
        <label><input type="radio" name={`${formId}-mode`} checked={form.mode === 'manual'} onChange={() => update('mode', 'manual')} /> Descrito a mano</label>
      </div>
      {form.mode === 'catalog' ? (
        <FormField id={`${formId}-part`} label="Repuesto" hint="Solo si no alcanza el stock; si hay, regístrelo como usado. Si no está en el catálogo, elija «Descrito a mano».">
          {(props) => (
            <SearchSelect
              {...props}
              options={partOptions}
              value={form.sparePartId}
              disabled={candidates.loading}
              placeholder={candidates.loading ? 'Cargando repuestos…' : 'Escriba o elija el repuesto'}
              emptyMessage="No hay repuestos con ese nombre"
              onChange={(sparePartId) => update('sparePartId', sparePartId)}
            />
          )}
        </FormField>
      ) : (
        <FormField id={`${formId}-description`} label="Descripción">
          {(props) => <input {...props} className="form-input" maxLength={500} placeholder="Repuesto, modelo, referencia…" value={form.description} onChange={(event) => update('description', event.target.value)} />}
        </FormField>
      )}
      <div className="part-request-form__row">
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
  )
}
