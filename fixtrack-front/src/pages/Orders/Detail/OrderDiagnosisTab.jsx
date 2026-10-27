import { useId, useState } from 'react'
import FormField from '../../../components/FormField/FormField'
import { useAuth } from '../../../hooks/useAuth'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import { OUTCOME_LABELS } from '../../../utils/labels'
import { canEditRepair, canWorkOnRepair, isOpen } from '../../../utils/permissions'
import OrderOutcomeForm from './OrderOutcomeForm'
import OrderStatusActions from './OrderStatusActions'
import './OrderDiagnosisTab.css'

// Pestaña de diagnóstico: el técnico asignado registra diagnóstico y solución, y avanza el estado
export default function OrderDiagnosisTab({ order, onUpdated }) {
  const formId = useId()
  const { user } = useAuth()
  const { showError, showSuccess } = useToast()
  const [diagnosis, setDiagnosis] = useState(order.diagnosis ?? '')
  const [solution, setSolution] = useState(order.solution ?? '')
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  const editable = isOpen(order) && canEditRepair(order, user)
  const changed = diagnosis !== (order.diagnosis ?? '') || solution !== (order.solution ?? '')

  async function handleSubmit(event) {
    event.preventDefault()
    if (!diagnosis.trim() && !solution.trim()) {
      setError('Escriba el diagnóstico, la solución o ambos.')
      return
    }
    setSaving(true)
    try {
      await interventionService.registerDiagnosis(order.id, {
        diagnosis: diagnosis.trim() || null,
        solution: solution.trim() || null,
      })
      showSuccess('Diagnóstico guardado.')
      onUpdated()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="order-diagnosis">
      <form id={formId} className="order-diagnosis__form" onSubmit={handleSubmit} noValidate>
        <FormField id={`${formId}-diagnosis`} label="Diagnóstico" error={error}>
          {(props) => (
            <textarea {...props} className="form-input" maxLength={2000} readOnly={!editable} placeholder={editable ? 'Qué encontró el técnico' : 'Sin diagnóstico'} value={diagnosis} onChange={(event) => { setDiagnosis(event.target.value); setError(null) }} />
          )}
        </FormField>
        <FormField id={`${formId}-solution`} label="Solución aplicada">
          {(props) => (
            <textarea {...props} className="form-input" maxLength={2000} readOnly={!editable} placeholder={editable ? 'Qué se hizo para repararlo' : 'Sin solución registrada'} value={solution} onChange={(event) => { setSolution(event.target.value); setError(null) }} />
          )}
        </FormField>
        {editable && (
          <div className="order-diagnosis__actions">
            <button type="submit" className="btn btn--primary" disabled={saving || !changed}>
              {saving ? 'Guardando…' : 'Guardar diagnóstico'}
            </button>
          </div>
        )}
        {!editable && isOpen(order) && (
          <p className="form-hint">Solo el técnico asignado registra el diagnóstico.</p>
        )}
      </form>

      <section className="order-diagnosis__outcome" aria-labelledby="order-outcome-title">
        <h3 id="order-outcome-title" className="order-info__title">Resultado y garantía</h3>
        {order.repairOutcome ? (
          <p>
            <strong>{OUTCOME_LABELS[order.repairOutcome]}</strong>
            {order.outcomeReason && ` · ${order.outcomeReason}`}
            {' · '}
            {order.warrantyDays > 0 ? `Garantía de ${order.warrantyDays} días desde la entrega` : 'Sin garantía'}
          </p>
        ) : (
          <p className="text-sm text-muted">Aún no se marca el resultado.</p>
        )}
        {canWorkOnRepair(order, user) && <OrderOutcomeForm order={order} onSaved={onUpdated} />}
      </section>

      <OrderStatusActions order={order} onUpdated={onUpdated} />
    </div>
  )
}
