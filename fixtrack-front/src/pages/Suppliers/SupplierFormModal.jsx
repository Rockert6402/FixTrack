import { useId, useState } from 'react'
import FormField from '../../components/FormField/FormField'
import Modal from '../../components/Modal/Modal'
import { useToast } from '../../hooks/useToast'
import * as supplierService from '../../services/supplierService'
import { getErrorMessage, getFieldErrors } from '../../utils/errors'
import './SupplierFormModal.css'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function initialForm(supplier) {
  return {
    name: supplier?.name ?? '',
    taxId: supplier?.taxId ?? '',
    contactName: supplier?.contactName ?? '',
    phone: supplier?.phone ?? '',
    email: supplier?.email ?? '',
  }
}

function validate(form) {
  const errors = {}
  if (!form.name.trim()) errors.name = 'Escriba el nombre del proveedor.'
  if (!/^\d{5,15}(-\d)?$/.test(form.taxId.replace(/[.\s]/g, ''))) errors.taxId = 'Entre 5 y 15 dígitos y, si lo lleva, el dígito de verificación (900123456-7).'
  if (form.phone && !/^[0-9+()\s-]{7,20}$/.test(form.phone)) errors.phone = 'Solo números, espacios, +, ( ) y -.'
  if (form.email && !EMAIL_PATTERN.test(form.email.trim())) errors.email = 'Escriba un correo válido.'
  return errors
}

// Registro y edición de un proveedor; el NIT se guarda sin puntos ni espacios y es único
export default function SupplierFormModal({ supplier, onClose, onSaved }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const isNew = !supplier
  const [initial] = useState(() => initialForm(supplier))
  const [form, setForm] = useState(initial)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validation = validate(form)
    setErrors(validation)
    if (Object.keys(validation).length > 0) {
      return
    }
    setSaving(true)
    const request = {
      name: form.name.trim(),
      taxId: form.taxId.trim(),
      contactName: form.contactName.trim() || null,
      phone: form.phone.trim() || null,
      email: form.email.trim() || null,
    }
    try {
      const saved = isNew ? await supplierService.createSupplier(request) : await supplierService.updateSupplier(supplier.id, request)
      showSuccess(isNew ? `Se registró el proveedor «${saved.name}».` : `Se guardaron los cambios de «${saved.name}».`)
      onSaved(saved)
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
      title={isNew ? 'Nuevo proveedor' : 'Editar proveedor'}
      onClose={onClose}
      isDirty={JSON.stringify(form) !== JSON.stringify(initial)}
      footer={
        <>
          <button type="button" className="btn btn--secondary" onClick={onClose} disabled={saving}>Cancelar</button>
          <button type="submit" form={formId} className="btn btn--primary" disabled={saving}>{saving ? 'Guardando…' : 'Guardar'}</button>
        </>
      }
    >
      <form id={formId} className="supplier-form form-grid" onSubmit={handleSubmit} noValidate>
        <FormField id={`${formId}-name`} label="Nombre" error={errors.name} required className="form-grid__full">
          {(props) => <input {...props} className="form-input" maxLength={150} value={form.name} onChange={(event) => update('name', event.target.value)} />}
        </FormField>
        <FormField id={`${formId}-tax`} label="NIT" error={errors.taxId} hint="Con o sin puntos: 900.123.456-7" required>
          {(props) => <input {...props} className="form-input" maxLength={20} value={form.taxId} onChange={(event) => update('taxId', event.target.value)} />}
        </FormField>
        <FormField id={`${formId}-contact`} label="Persona de contacto" error={errors.contactName}>
          {(props) => <input {...props} className="form-input" maxLength={150} value={form.contactName} onChange={(event) => update('contactName', event.target.value)} />}
        </FormField>
        <FormField id={`${formId}-phone`} label="Teléfono" error={errors.phone}>
          {(props) => <input {...props} className="form-input" inputMode="tel" maxLength={20} value={form.phone} onChange={(event) => update('phone', event.target.value)} />}
        </FormField>
        <FormField id={`${formId}-email`} label="Correo" error={errors.email}>
          {(props) => <input {...props} type="email" className="form-input" maxLength={150} value={form.email} onChange={(event) => update('email', event.target.value)} />}
        </FormField>
      </form>
    </Modal>
  )
}
