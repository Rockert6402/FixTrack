import { useId, useState } from 'react'
import FormField from '../../components/FormField/FormField'
import Modal from '../../components/Modal/Modal'
import { useToast } from '../../hooks/useToast'
import * as paymentService from '../../services/paymentService'
import { getErrorMessage } from '../../utils/errors'
import { formatCurrency } from '../../utils/format'
import { PAYMENT_METHOD_LABELS, PAYMENT_TYPE_LABELS, toOptions } from '../../utils/labels'
import './PaymentModal.css'

const MONEY_PATTERN = /^\d{1,10}(\.\d{1,2})?$/

// Cobro final del saldo o devolución del saldo a favor, con su medio de pago
export default function PaymentModal({ row, type, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const max = Number(type === 'REFUND' ? row.refundDue : row.balance)
  const [form, setForm] = useState({ method: 'CASH', amount: String(max) })
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validation = {}
    if (!MONEY_PATTERN.test(form.amount) || Number(form.amount) <= 0) validation.amount = 'Escriba un valor mayor que cero, con máximo 2 decimales.'
    else if (Number(form.amount) > max) validation.amount = `No puede superar ${formatCurrency(max)}.`
    setErrors(validation)
    if (Object.keys(validation).length > 0) {
      return
    }
    setSaving(true)
    try {
      const summary = await paymentService.registerPayment(row.order.id, { type, method: form.method, amount: Number(form.amount) })
      showSuccess(`${PAYMENT_TYPE_LABELS[type]} registrado. Saldo: ${formatCurrency(summary.balance)}.`)
      onSaved()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      open
      title={type === 'REFUND' ? `Devolución · ${row.order.code}` : `Cobro · ${row.order.code}`}
      onClose={onClose}
      isDirty={form.amount !== String(max)}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving}>
            {saving ? 'Registrando…' : `Registrar ${PAYMENT_TYPE_LABELS[type].toLowerCase()}`}
          </button>
        </>
      }
    >
      <form id={formId} className="payment-modal" onSubmit={handleSubmit} noValidate>
        <dl className="payment-modal__summary">
          <div><dt>Total</dt><dd>{formatCurrency(row.total)}</dd></div>
          <div><dt>Abonado</dt><dd>{formatCurrency(row.paid)}</dd></div>
          <div><dt>{type === 'REFUND' ? 'A favor del cliente' : 'Saldo'}</dt><dd>{formatCurrency(max)}</dd></div>
        </dl>
        <div className="form-grid">
          <FormField id={`${formId}-method`} label="Medio de pago" required>
            {(props) => (
              <select {...props} className="form-input" value={form.method} onChange={(event) => update('method', event.target.value)}>
                {toOptions(PAYMENT_METHOD_LABELS).map(({ value, label }) => <option key={value} value={value}>{label}</option>)}
              </select>
            )}
          </FormField>
          <FormField id={`${formId}-amount`} label="Valor" error={errors.amount} required>
            {(props) => <input {...props} className="form-input" inputMode="decimal" value={form.amount} onChange={(event) => update('amount', event.target.value.replace(/[^\d.]/g, ''))} />}
          </FormField>
        </div>
      </form>
    </Modal>
  )
}
