import { useId, useState } from 'react'
import FormField from '../../components/FormField/FormField'
import Modal from '../../components/Modal/Modal'
import { useToast } from '../../hooks/useToast'
import * as sparePartService from '../../services/sparePartService'
import { getErrorMessage } from '../../utils/errors'
import './MinStockModal.css'

// Stock mínimo de un repuesto: por debajo de él aparece en las alertas
export default function MinStockModal({ part, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [minStock, setMinStock] = useState(String(part.minStock))
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    if (!/^\d+$/.test(minStock)) {
      setError('Debe ser un número entero de 0 en adelante.')
      return
    }
    setSaving(true)
    try {
      const updated = await sparePartService.updateSparePart(part.id, { minStock: Number(minStock) })
      showSuccess(`El stock mínimo de «${part.name}» ahora es ${updated.minStock}.`)
      onSaved(updated)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      open
      title="Stock mínimo"
      size="sm"
      onClose={onClose}
      isDirty={minStock !== String(part.minStock)}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving || minStock === String(part.minStock)}>
            {saving ? 'Guardando…' : 'Guardar'}
          </button>
        </>
      }
    >
      <form id={formId} className="min-stock" onSubmit={handleSubmit} noValidate>
        <p className="min-stock__part">{part.name} · stock actual {part.stock}</p>
        <FormField id={`${formId}-min`} label="Stock mínimo" error={error} hint="Con el stock en este valor o por debajo, el repuesto queda en alerta.">
          {(props) => <input {...props} className="form-input" inputMode="numeric" value={minStock} onChange={(event) => { setMinStock(event.target.value.replace(/\D/g, '')); setError(null) }} />}
        </FormField>
      </form>
    </Modal>
  )
}
