import { useId, useState } from 'react'
import ContactField from '../../components/ContactField/ContactField'
import Modal from '../../components/Modal/Modal'
import { useToast } from '../../hooks/useToast'
import * as personService from '../../services/personService'
import { getErrorMessage, getFieldErrors } from '../../utils/errors'
import './CustomerContactModal.css'

const PHONE_PATTERN = /^\d{7,10}$/
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function initialForm(customer) {
  return {
    phone: customer.phone ?? '',
    noPhone: !customer.phone,
    email: customer.email ?? '',
    noEmail: !customer.email,
  }
}

function validate(form) {
  const errors = {}
  if (!form.noPhone && !PHONE_PATTERN.test(form.phone)) errors.phone = 'Entre 7 y 10 dígitos, o marque «No tiene».'
  if (!form.noEmail && !EMAIL_PATTERN.test(form.email.trim())) errors.email = 'Escriba un correo válido o marque «No tiene».'
  return errors
}

// Corrección del celular y el correo del cliente; cada uno se llena o se marca «No tiene»
export default function CustomerContactModal({ customer, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const [initial] = useState(() => initialForm(customer))
  const [form, setForm] = useState(initial)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [{ noPhone: 'phone', noEmail: 'email' }[field] ?? field]: undefined }))
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
      const updated = await personService.updateCustomerContact(customer.id, {
        phone: form.noPhone ? null : form.phone,
        noPhone: form.noPhone,
        email: form.noEmail ? null : form.email.trim(),
        noEmail: form.noEmail,
      })
      showSuccess(`Se actualizó el contacto de ${customer.fullName}.`)
      onSaved(updated)
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
      title={`Contacto de ${customer.fullName}`}
      onClose={onClose}
      isDirty={JSON.stringify(form) !== JSON.stringify(initial)}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving}>{saving ? 'Guardando…' : 'Guardar'}</button>
        </>
      }
    >
      <form id={formId} className="customer-contact-form" onSubmit={handleSubmit} noValidate>
        <ContactField
          id={`${formId}-phone`}
          label="Celular"
          error={errors.phone}
          value={form.phone}
          noValue={form.noPhone}
          noValueLabel="No tiene celular"
          inputProps={{ inputMode: 'numeric', maxLength: 10, autoComplete: 'off' }}
          onValueChange={(value) => update('phone', value.replace(/\D/g, ''))}
          onNoValueChange={(value) => update('noPhone', value)}
        />
        <ContactField
          id={`${formId}-email`}
          label="Correo"
          error={errors.email}
          value={form.email}
          noValue={form.noEmail}
          noValueLabel="No tiene correo"
          inputProps={{ type: 'email', maxLength: 150, autoComplete: 'off' }}
          onValueChange={(value) => update('email', value)}
          onNoValueChange={(value) => update('noEmail', value)}
        />
        <p className="form-hint">La clave de consulta de sus órdenes no cambia aunque cambie el celular.</p>
      </form>
    </Modal>
  )
}
