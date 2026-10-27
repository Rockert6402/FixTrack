import { useId, useState } from 'react'
import FormField from '../../components/FormField/FormField'
import Modal from '../../components/Modal/Modal'
import { useToast } from '../../hooks/useToast'
import * as sparePartService from '../../services/sparePartService'
import { getErrorMessage, getFieldErrors } from '../../utils/errors'
import { formatCurrency } from '../../utils/format'
import './StockEntryModal.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/

function validate(form) {
  const errors = {}
  if (!/^\d+$/.test(form.quantity) || Number(form.quantity) < 1) errors.quantity = 'Debe ser un número entero mayor que cero.'
  else if (Number(form.quantity) > 100000) errors.quantity = 'Máximo 100.000 unidades por entrada.'
  if (!MONEY_PATTERN.test(form.unitCost)) errors.unitCost = 'Escriba el costo unitario, con máximo 2 decimales.'
  if (!form.reason.trim()) errors.reason = 'Escriba el motivo (por ejemplo, la factura del proveedor).'
  return errors
}

// Entrada de mercancía: suma stock y deja el movimiento con fecha, motivo, costo y responsable
export default function StockEntryModal({ part, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const initial = { quantity: '', unitCost: String(Number(part.costPrice)), reason: '' }
  const [form, setForm] = useState(initial)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const isDirty = JSON.stringify(form) !== JSON.stringify(initial)

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validation = validate(form)
    setErrors(validation)
    if (Object.keys(validation).length > 0) {
      return
    }
    setSaving(true)
    try {
      const { sparePart } = await sparePartService.registerEntry(part.id, {
        quantity: Number(form.quantity),
        unitCost: Number(form.unitCost),
        reason: form.reason.trim(),
      })
      showSuccess(`Entraron ${form.quantity} unidades de «${part.name}». Stock actual: ${sparePart.stock}.`)
      onSaved(sparePart)
    } catch (requestError) {
      setErrors(getFieldErrors(requestError))
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      open
      title="Registrar entrada"
      onClose={onClose}
      isDirty={isDirty}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving}>
            {saving ? 'Registrando…' : 'Registrar entrada'}
          </button>
        </>
      }
    >
      <form id={formId} className="stock-entry" onSubmit={handleSubmit} noValidate>
        <p className="stock-entry__part">
          <strong>{part.name}</strong>
          <span>{part.sku} · stock actual {part.stock} · mínimo {part.minStock}</span>
        </p>
        <div className="form-grid">
          <FormField id={`${formId}-quantity`} label="Cantidad" error={errors.quantity} required>
            {(props) => <input {...props} className="form-input" inputMode="numeric" value={form.quantity} onChange={(event) => update('quantity', event.target.value.replace(/\D/g, ''))} />}
          </FormField>
          <FormField id={`${formId}-cost`} label="Costo unitario" error={errors.unitCost} hint={`Costo actual del repuesto: ${formatCurrency(part.costPrice)}.`} required>
            {(props) => <input {...props} className="form-input" inputMode="decimal" value={form.unitCost} onChange={(event) => update('unitCost', event.target.value.replace(/[^\d.]/g, ''))} />}
          </FormField>
          <FormField id={`${formId}-reason`} label="Motivo" error={errors.reason} className="form-grid__full" required>
            {(props) => <input {...props} className="form-input" maxLength={500} placeholder="Compra a proveedor, factura…" value={form.reason} onChange={(event) => update('reason', event.target.value)} />}
          </FormField>
        </div>
      </form>
    </Modal>
  )
}
