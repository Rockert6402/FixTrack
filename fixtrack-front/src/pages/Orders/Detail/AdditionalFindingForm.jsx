import { useId, useState } from 'react'
import { MdAdd } from 'react-icons/md'
import FormField from '../../../components/FormField/FormField'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import { SERVICE_TYPE_LABELS, toOptions } from '../../../utils/labels'
import './AdditionalFindingForm.css'

const EMPTY = { serviceType: '', description: '' }

// Daño adicional que encuentra el técnico; el backend decide si queda aprobado o pendiente del cliente
export default function AdditionalFindingForm({ order, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [form, setForm] = useState(EMPTY)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validation = {}
    if (!form.serviceType) validation.serviceType = 'Elija el tipo de daño.'
    if (!form.description.trim()) validation.description = 'Describa el daño.'
    setErrors(validation)
    if (Object.keys(validation).length > 0) {
      return
    }
    setSaving(true)
    try {
      const updated = await interventionService.addFinding(order.id, { serviceType: form.serviceType, description: form.description.trim() })
      const added = updated.findings.filter((finding) => finding.origin === 'ADDITIONAL').at(-1)
      showSuccess(added?.approvalStatus === 'PENDING'
        ? 'Daño agregado. El total supera el presupuesto: queda pendiente de la respuesta del cliente.'
        : 'Daño agregado y aprobado: cabe en el presupuesto.')
      setForm(EMPTY)
      onSaved()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <form id={formId} className="additional-finding" onSubmit={handleSubmit} noValidate>
      <h3 className="order-info__title">Agregar daño adicional</h3>
      <FormField id={`${formId}-type`} label="Tipo de daño" error={errors.serviceType} required>
        {(props) => (
          <select {...props} className="form-input" value={form.serviceType} onChange={(event) => update('serviceType', event.target.value)}>
            <option value="">Elija el tipo</option>
            {toOptions(SERVICE_TYPE_LABELS).map(({ value, label }) => <option key={value} value={value}>{label}</option>)}
          </select>
        )}
      </FormField>
      <FormField id={`${formId}-description`} label="Descripción" error={errors.description} required>
        {(props) => <textarea {...props} className="form-input" maxLength={1000} value={form.description} onChange={(event) => update('description', event.target.value)} />}
      </FormField>
      <div className="additional-finding__actions">
        <button type="submit" className="btn btn--primary" disabled={saving}>
          <MdAdd aria-hidden="true" />
          {saving ? 'Agregando…' : 'Agregar daño'}
        </button>
      </div>
    </form>
  )
}
