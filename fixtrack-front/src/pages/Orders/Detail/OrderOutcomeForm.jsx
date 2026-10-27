import { useId, useState } from 'react'
import FormField from '../../../components/FormField/FormField'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import { OUTCOME_LABELS, toOptions } from '../../../utils/labels'
import './OrderOutcomeForm.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/

function validate(form, order) {
  const errors = {}
  if (!form.outcome) errors.outcome = 'Elija el resultado.'
  if (form.outcome === 'REPAIRED') {
    if (order.status !== 'IN_REPAIR') errors.outcome = 'Pase la orden a «En reparación» antes de marcarla como reparada.'
    if (form.warrantyDays && !/^\d{1,4}$/.test(form.warrantyDays)) errors.warrantyDays = 'Escriba los días como número entero.'
  } else if (form.outcome) {
    if (!form.reason.trim()) errors.reason = 'Explique por qué no se repara.'
    if (!MONEY_PATTERN.test(form.diagnosisFee)) errors.diagnosisFee = 'Escriba el valor del diagnóstico (puede ser 0).'
  }
  return errors
}

// Resultado de la orden: reparada con su garantía, o sin reparación con motivo y valor del diagnóstico
export default function OrderOutcomeForm({ order, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const isWarranty = Boolean(order.warrantyOfCode)
  const [form, setForm] = useState({
    outcome: order.repairOutcome ?? '',
    warrantyDays: order.repairOutcome === 'REPAIRED' && order.warrantyDays != null ? String(order.warrantyDays) : '',
    reason: '',
    diagnosisFee: isWarranty ? '0' : '',
  })
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const withoutRepair = form.outcome && form.outcome !== 'REPAIRED'

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validation = validate(form, order)
    setErrors(validation)
    if (Object.keys(validation).length > 0) {
      return
    }
    setSaving(true)
    try {
      const request = withoutRepair
        ? { outcome: form.outcome, reason: form.reason.trim(), diagnosisFee: Number(form.diagnosisFee), warrantyDays: 0 }
        : { outcome: form.outcome, warrantyDays: form.warrantyDays === '' ? null : Number(form.warrantyDays) }
      const updated = await interventionService.registerOutcome(order.id, request)
      showSuccess(withoutRepair
        ? 'Resultado registrado: los repuestos volvieron al inventario y la orden ya puede pasar a lista para entregar.'
        : `Orden marcada como reparada con ${updated.warrantyDays} días de garantía.`)
      onSaved()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <form id={formId} className="order-outcome" onSubmit={handleSubmit} noValidate>
      <div className="form-grid">
        <FormField id={`${formId}-outcome`} label="Resultado" error={errors.outcome} required>
          {(props) => (
            <select {...props} className="form-input" value={form.outcome} onChange={(event) => update('outcome', event.target.value)}>
              <option value="">Elija el resultado</option>
              {toOptions(OUTCOME_LABELS).map(({ value, label }) => <option key={value} value={value}>{label}</option>)}
            </select>
          )}
        </FormField>
        {form.outcome === 'REPAIRED' && (
          <FormField
            id={`${formId}-warranty`}
            label="Días de garantía"
            error={errors.warrantyDays}
            hint={isWarranty ? 'Una garantía no extiende la garantía.' : 'Vacío usa la garantía de la tienda; 0 si no aplica.'}
          >
            {(props) => <input {...props} className="form-input" inputMode="numeric" disabled={isWarranty} value={isWarranty ? '0' : form.warrantyDays} onChange={(event) => update('warrantyDays', event.target.value.replace(/\D/g, ''))} />}
          </FormField>
        )}
        {withoutRepair && (
          <>
            <FormField id={`${formId}-fee`} label="Valor del diagnóstico" error={errors.diagnosisFee} hint="Es lo único que se cobra; puede ser 0." required>
              {(props) => <input {...props} className="form-input" inputMode="decimal" disabled={isWarranty} value={form.diagnosisFee} onChange={(event) => update('diagnosisFee', event.target.value.replace(/[^\d.]/g, ''))} />}
            </FormField>
            <FormField id={`${formId}-reason`} label="Motivo" error={errors.reason} className="form-grid__full" required>
              {(props) => <textarea {...props} className="form-input" maxLength={500} value={form.reason} onChange={(event) => update('reason', event.target.value)} />}
            </FormField>
            <p className="order-outcome__warning form-grid__full" role="note">
              Los repuestos registrados vuelven al inventario, los daños sin respuesta quedan rechazados, no hay garantía y la orden no se puede reabrir.
            </p>
          </>
        )}
      </div>
      <div className="order-outcome__actions">
        <button type="submit" className="btn btn--primary" disabled={saving || !form.outcome}>
          {saving ? 'Guardando…' : 'Guardar resultado'}
        </button>
      </div>
    </form>
  )
}
