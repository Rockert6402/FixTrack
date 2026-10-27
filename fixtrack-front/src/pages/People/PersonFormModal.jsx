import { useId, useState } from 'react'
import FormField from '../../components/FormField/FormField'
import Modal from '../../components/Modal/Modal'
import { useToast } from '../../hooks/useToast'
import * as personService from '../../services/personService'
import { getErrorMessage, getFieldErrors } from '../../utils/errors'
import { DOCUMENT_TYPE_LABELS, ROLE_LABELS, ROLES, toOptions } from '../../utils/labels'
import './PersonFormModal.css'

const PHONE_PATTERN = /^\d{7,10}$/
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const DOCUMENT_PATTERN = /^[A-Za-z0-9]+$/

function initialForm(person) {
  return {
    documentType: person?.documentType ?? 'CC',
    documentNumber: person?.documentNumber ?? '',
    fullName: person?.fullName ?? '',
    role: person?.role ?? ROLES.CUSTOMER,
    phone: person?.phone ?? '',
    noPhone: person ? !person.phone : false,
    email: person?.email ?? '',
    noEmail: person ? !person.email : false,
    password: '',
    specialtyIds: person?.specialties?.map((specialty) => specialty.id) ?? [],
  }
}

// Reglas del formulario: documento obligatorio, «No tiene» para clientes, correo y contraseña para internos
function validate(form, isNew) {
  const errors = {}
  const customer = form.role === ROLES.CUSTOMER
  if (!form.documentNumber.trim()) {
    errors.documentNumber = 'Escriba el número de documento.'
  } else if (!DOCUMENT_PATTERN.test(form.documentNumber.trim())) {
    errors.documentNumber = 'Solo puede tener letras y números.'
  }
  if (!form.fullName.trim()) {
    errors.fullName = 'Escriba el nombre completo.'
  }
  const phone = form.phone.trim()
  if (phone && !(customer && form.noPhone) && !PHONE_PATTERN.test(phone)) {
    errors.phone = 'Debe tener entre 7 y 10 dígitos, solo números.'
  } else if (customer && !phone && !form.noPhone) {
    errors.phone = 'Escriba el celular o marque «No tiene».'
  }
  const email = form.email.trim()
  if (email && !(customer && form.noEmail) && !EMAIL_PATTERN.test(email)) {
    errors.email = 'Escriba un correo válido.'
  } else if (customer && !email && !form.noEmail) {
    errors.email = 'Escriba el correo o marque «No tiene».'
  } else if (!customer && !email) {
    errors.email = 'El correo es obligatorio: con él inicia sesión.'
  }
  if (!customer && isNew && !form.password) {
    errors.password = 'Escriba una contraseña.'
  } else if (!customer && form.password && form.password.length < 8) {
    errors.password = 'Debe tener al menos 8 caracteres.'
  }
  return errors
}

function toRequest(form) {
  const customer = form.role === ROLES.CUSTOMER
  return {
    documentType: form.documentType,
    documentNumber: form.documentNumber.trim(),
    fullName: form.fullName.trim(),
    phone: customer && form.noPhone ? null : form.phone.trim() || null,
    noPhone: customer ? form.noPhone : null,
    email: customer && form.noEmail ? null : form.email.trim() || null,
    noEmail: customer ? form.noEmail : null,
    password: customer ? null : form.password || null,
    role: form.role,
    specialtyIds: form.role === ROLES.TECHNICIAN ? form.specialtyIds : null,
  }
}

// Alta y edición de una persona en un modal sobre el listado
export default function PersonFormModal({ person, specialties, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const isNew = !person
  const [initial] = useState(() => initialForm(person))
  const [form, setForm] = useState(initial)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  const customer = form.role === ROLES.CUSTOMER
  const isDirty = JSON.stringify(form) !== JSON.stringify(initial)

  // Al editar un campo se borra su error; las marcas «No tiene» borran el del dato que reemplazan
  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    const errorField = { noPhone: 'phone', noEmail: 'email' }[field] ?? field
    setErrors((current) => (current[errorField] ? { ...current, [errorField]: undefined } : current))
  }

  function toggleSpecialty(id) {
    setForm((current) => ({
      ...current,
      specialtyIds: current.specialtyIds.includes(id)
        ? current.specialtyIds.filter((specialtyId) => specialtyId !== id)
        : [...current.specialtyIds, id],
    }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validation = validate(form, isNew)
    setErrors(validation)
    if (Object.keys(validation).length > 0) {
      return
    }
    setSaving(true)
    try {
      const request = toRequest(form)
      const saved = isNew ? await personService.createPerson(request) : await personService.updatePerson(person.id, request)
      showSuccess(isNew ? `Se registró a ${saved.fullName}.` : `Se guardaron los cambios de ${saved.fullName}.`)
      onSaved()
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
      title={isNew ? 'Nueva persona' : `Editar a ${person.fullName}`}
      onClose={onClose}
      isDirty={isDirty}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving}>
            {saving ? 'Guardando…' : 'Guardar'}
          </button>
        </>
      }
    >
      <form id={formId} className="form-grid" onSubmit={handleSubmit} noValidate>
        <FormField id={`${formId}-documentType`} label="Tipo de documento" required>
          {(props) => (
            <select {...props} className="form-input" value={form.documentType} onChange={(event) => update('documentType', event.target.value)}>
              {toOptions(DOCUMENT_TYPE_LABELS).map(({ value, label }) => <option key={value} value={value}>{label}</option>)}
            </select>
          )}
        </FormField>
        <FormField id={`${formId}-documentNumber`} label="Número de documento" error={errors.documentNumber} required>
          {(props) => (
            <input {...props} className="form-input" maxLength={20} autoComplete="off" value={form.documentNumber} onChange={(event) => update('documentNumber', event.target.value)} />
          )}
        </FormField>
        <FormField id={`${formId}-fullName`} label="Nombre completo" error={errors.fullName} required className="form-grid__full">
          {(props) => (
            <input {...props} className="form-input" maxLength={150} value={form.fullName} onChange={(event) => update('fullName', event.target.value)} />
          )}
        </FormField>
        <FormField id={`${formId}-role`} label="Rol" required className="form-grid__full">
          {(props) => (
            <select {...props} className="form-input" value={form.role} onChange={(event) => update('role', event.target.value)}>
              {toOptions(ROLE_LABELS).map(({ value, label }) => <option key={value} value={value}>{label}</option>)}
            </select>
          )}
        </FormField>

        <div className="person-form__contact">
          <FormField id={`${formId}-phone`} label="Celular" error={errors.phone} required={customer && !form.noPhone}>
            {(props) => (
              <input {...props} className="form-input" inputMode="numeric" maxLength={10} disabled={customer && form.noPhone} value={customer && form.noPhone ? '' : form.phone} onChange={(event) => update('phone', event.target.value.replace(/\D/g, ''))} />
            )}
          </FormField>
          {customer && (
            <label className="person-form__no-data">
              <input type="checkbox" checked={form.noPhone} onChange={(event) => update('noPhone', event.target.checked)} />
              No tiene celular
            </label>
          )}
        </div>
        <div className="person-form__contact">
          <FormField id={`${formId}-email`} label="Correo" error={errors.email} required={!customer || !form.noEmail}>
            {(props) => (
              <input {...props} type="email" className="form-input" maxLength={150} disabled={customer && form.noEmail} value={customer && form.noEmail ? '' : form.email} onChange={(event) => update('email', event.target.value)} />
            )}
          </FormField>
          {customer && (
            <label className="person-form__no-data">
              <input type="checkbox" checked={form.noEmail} onChange={(event) => update('noEmail', event.target.checked)} />
              No tiene correo
            </label>
          )}
        </div>

        {!customer && (
          <FormField
            id={`${formId}-password`}
            label="Contraseña"
            error={errors.password}
            required={isNew}
            hint={isNew ? 'Mínimo 8 caracteres.' : 'Déjela vacía para conservar la actual.'}
            className="form-grid__full"
          >
            {(props) => (
              <input {...props} type="password" className="form-input" autoComplete="new-password" maxLength={72} value={form.password} onChange={(event) => update('password', event.target.value)} />
            )}
          </FormField>
        )}

        {form.role === ROLES.TECHNICIAN && (
          <fieldset className="person-form__specialties form-grid__full">
            <legend className="form-label">Especialidades</legend>
            {specialties.length === 0 && <p className="form-hint">No hay especialidades en el catálogo.</p>}
            <div className="person-form__specialty-list">
              {specialties.map((specialty) => (
                <label key={specialty.id} className="person-form__specialty">
                  <input type="checkbox" checked={form.specialtyIds.includes(specialty.id)} onChange={() => toggleSpecialty(specialty.id)} />
                  {specialty.name}
                </label>
              ))}
            </div>
          </fieldset>
        )}
      </form>
    </Modal>
  )
}
