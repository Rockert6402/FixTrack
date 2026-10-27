import { useId, useState } from 'react'
import FormField from '../../../components/FormField/FormField'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import { formatCurrency } from '../../../utils/format'
import './OrderLaborForm.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/

// Mano de obra que define el técnico; si el total supera el presupuesto, los daños adicionales quedan pendientes
export default function OrderLaborForm({ order, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const isWarranty = Boolean(order.warrantyOfCode)
  const current = String(Number(order.laborCost ?? 0))
  const [laborCost, setLaborCost] = useState(current)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    if (!MONEY_PATTERN.test(laborCost)) {
      setError('Escriba el valor, con máximo 2 decimales.')
      return
    }
    setSaving(true)
    try {
      const updated = await interventionService.setLaborCost(order.id, Number(laborCost))
      showSuccess(`Mano de obra guardada: ${formatCurrency(updated.laborCost)}. Total actual ${formatCurrency(updated.charges.total)}.`)
      onSaved()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <form id={formId} className="order-labor" onSubmit={handleSubmit} noValidate>
      <FormField id={`${formId}-labor`} label="Mano de obra" error={error} hint={isWarranty ? 'En una garantía la mano de obra es 0.' : `Presupuesto inicial: ${formatCurrency(order.estimatedPrice)}.`}>
        {(props) => <input {...props} className="form-input" inputMode="decimal" disabled={isWarranty} value={laborCost} onChange={(event) => { setLaborCost(event.target.value.replace(/[^\d.]/g, '')); setError(null) }} />}
      </FormField>
      <button type="submit" className="btn btn--primary" disabled={saving || isWarranty || laborCost === current}>
        {saving ? 'Guardando…' : 'Guardar mano de obra'}
      </button>
    </form>
  )
}
