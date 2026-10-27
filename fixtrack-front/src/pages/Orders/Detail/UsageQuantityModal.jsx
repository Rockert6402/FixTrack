import { useId, useState } from 'react'
import FormField from '../../../components/FormField/FormField'
import Modal from '../../../components/Modal/Modal'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import './UsageQuantityModal.css'

// Corrige la cantidad usada: el stock se ajusta solo por la diferencia
export default function UsageQuantityModal({ order, usage, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [quantity, setQuantity] = useState(String(usage.quantity))
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)
  const difference = Number(quantity || 0) - usage.quantity

  async function handleSubmit(event) {
    event.preventDefault()
    if (!/^\d+$/.test(quantity) || Number(quantity) < 1) {
      setError('Debe ser un número entero mayor que cero. Para quitarlo del todo use «Quitar».')
      return
    }
    setSaving(true)
    try {
      await interventionService.correctUsedPart(order.id, usage.id, Number(quantity))
      showSuccess(difference > 0
        ? `Se descontaron ${difference} unidades más del inventario.`
        : `Volvieron ${-difference} unidades al inventario.`)
      onSaved()
    } catch (requestError) {
      // Si no alcanza el stock, el backend dice cuántas hay
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      open
      title="Corregir cantidad"
      size="sm"
      onClose={onClose}
      isDirty={quantity !== String(usage.quantity)}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving || difference === 0}>
            {saving ? 'Guardando…' : 'Guardar'}
          </button>
        </>
      }
    >
      <form id={formId} className="usage-quantity" onSubmit={handleSubmit} noValidate>
        <p className="usage-quantity__part">{usage.sparePartName} · registrada: {usage.quantity}</p>
        <FormField id={`${formId}-quantity`} label="Cantidad correcta" error={error}>
          {(props) => <input {...props} className="form-input" inputMode="numeric" value={quantity} onChange={(event) => { setQuantity(event.target.value.replace(/\D/g, '')); setError(null) }} />}
        </FormField>
        {difference !== 0 && quantity !== '' && (
          <p className="form-hint">
            {difference > 0 ? `Se descontarán ${difference} unidades más del inventario.` : `Volverán ${-difference} unidades al inventario.`}
          </p>
        )}
      </form>
    </Modal>
  )
}
