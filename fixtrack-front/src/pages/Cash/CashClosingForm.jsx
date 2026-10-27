import { useId, useState } from 'react'
import ConfirmDialog from '../../components/ConfirmDialog/ConfirmDialog'
import FormField from '../../components/FormField/FormField'
import { useToast } from '../../hooks/useToast'
import * as cashService from '../../services/cashService'
import { getErrorMessage } from '../../utils/errors'
import { formatCurrency } from '../../utils/format'
import './CashClosingForm.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/

// Cierre del día: efectivo contado contra el esperado; un cierre por día que no se edita
export default function CashClosingForm({ today, onClosed }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [countedCash, setCountedCash] = useState('')
  const [notes, setNotes] = useState('')
  const [error, setError] = useState(null)
  const [confirming, setConfirming] = useState(false)
  const difference = MONEY_PATTERN.test(countedCash) ? Number(countedCash) - Number(today.expectedCash) : null

  function handleSubmit(event) {
    event.preventDefault()
    if (!MONEY_PATTERN.test(countedCash)) {
      setError('Escriba el efectivo contado, con máximo 2 decimales.')
      return
    }
    setConfirming(true)
  }

  async function handleClose() {
    try {
      const closing = await cashService.closeCash({ countedCash: Number(countedCash), notes: notes.trim() || null })
      showSuccess(`Caja cerrada con ${closing.payments} ${closing.payments === 1 ? 'pago' : 'pagos'}. Diferencia en efectivo: ${formatCurrency(closing.cashDifference)}.`)
      onClosed(closing)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setConfirming(false)
    }
  }

  return (
    <form id={formId} className="cash-closing-form" onSubmit={handleSubmit} noValidate>
      <h2 className="cash-closing-form__title">Cerrar la caja de hoy</h2>
      <FormField id={`${formId}-counted`} label="Efectivo contado" error={error} required>
        {(props) => <input {...props} className="form-input" inputMode="decimal" value={countedCash} onChange={(event) => { setCountedCash(event.target.value.replace(/[^\d.]/g, '')); setError(null) }} />}
      </FormField>
      {difference !== null && (
        <p className={`cash-closing-form__difference ${difference < 0 ? 'cash-closing-form__difference--short' : ''}`} role="status">
          {difference === 0 ? 'El efectivo cuadra.' : difference < 0 ? `Faltan ${formatCurrency(-difference)}.` : `Sobran ${formatCurrency(difference)}.`}
        </p>
      )}
      <FormField id={`${formId}-notes`} label="Notas" hint="Opcional; por ejemplo, el motivo de una diferencia.">
        {(props) => <textarea {...props} className="form-input" maxLength={500} value={notes} onChange={(event) => setNotes(event.target.value)} />}
      </FormField>
      <button type="submit" className="btn btn--primary">Cerrar caja</button>
      <ConfirmDialog
        open={confirming}
        title="Cerrar caja"
        message={`Se cerrará la caja de hoy con ${formatCurrency(Number(countedCash || 0))} en efectivo. El cierre no se puede editar.`}
        confirmLabel="Cerrar caja"
        danger={false}
        onConfirm={handleClose}
        onCancel={() => setConfirming(false)}
      />
    </form>
  )
}
