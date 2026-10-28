import { useId, useState } from 'react'
import { MdAdd, MdDeleteOutline, MdOutlineEdit } from 'react-icons/md'
import { useRemoteData } from '../../hooks/useRemoteData'
import { useToast } from '../../hooks/useToast'
import { getErrorMessage, getFieldErrors } from '../../utils/errors'
import ConfirmDialog from '../ConfirmDialog/ConfirmDialog'
import DataTable from '../DataTable/DataTable'
import FormField from '../FormField/FormField'
import Modal from '../Modal/Modal'
import './NameCatalog.css'

// Catálogo que solo tiene nombre (marcas, tipos de repuesto, especialidades): tabla, modal de alta y edición y borrado
export default function NameCatalog({ title, itemLabel, createLabel, service }) {
  const formId = useId()
  const { showError, showSuccess } = useToast()
  const { data, loading, error, reload } = useRemoteData(service.list, [])
  const [editing, setEditing] = useState(null)
  const [name, setName] = useState('')
  const [fieldError, setFieldError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [toDelete, setToDelete] = useState(null)

  function openForm(item) {
    setEditing(item ?? {})
    setName(item?.name ?? '')
    setFieldError(null)
  }

  async function handleSubmit(event) {
    event.preventDefault()
    if (!name.trim()) {
      setFieldError('Escriba el nombre.')
      return
    }
    setSaving(true)
    try {
      if (editing.id) {
        await service.update(editing.id, { name: name.trim() })
      } else {
        await service.create({ name: name.trim() })
      }
      showSuccess(`Se guardó «${name.trim()}».`)
      setEditing(null)
      reload()
    } catch (requestError) {
      setFieldError(getFieldErrors(requestError).name ?? null)
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete() {
    try {
      await service.remove(toDelete.id)
      showSuccess(`Se eliminó «${toDelete.name}» del catálogo.`)
      setToDelete(null)
      reload()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
      setToDelete(null)
    }
  }

  const columns = [
    { key: 'name', header: 'Nombre' },
    {
      key: 'actions',
      header: 'Acciones',
      align: 'right',
      render: (item) => (
        <span className="row-actions">
          <button type="button" className="action-btn action-btn--edit" aria-label={`Editar ${item.name}`} onClick={() => openForm(item)}>
            <MdOutlineEdit aria-hidden="true" />
          </button>
          <button type="button" className="action-btn action-btn--delete" aria-label={`Eliminar ${item.name}`} onClick={() => setToDelete(item)}>
            <MdDeleteOutline aria-hidden="true" />
          </button>
        </span>
      ),
    },
  ]

  const isDirty = editing !== null && name !== (editing.name ?? '')

  return (
    <section className="name-catalog" aria-label={title}>
      <div className="name-catalog__toolbar">
        <p className="text-sm text-muted">{data ? `${data.length} registros` : ''}</p>
        <button type="button" className="btn btn--primary" onClick={() => openForm(null)}>
          <MdAdd aria-hidden="true" />
          {createLabel}
        </button>
      </div>

      <DataTable
        caption={title}
        columns={columns}
        rows={data ?? []}
        loading={loading}
        error={error}
        onRetry={reload}
        emptyMessage="Todavía no hay registros."
      />

      <Modal
        open={editing !== null}
        title={editing?.id ? `Editar ${itemLabel.toLowerCase()}` : createLabel}
        onClose={() => setEditing(null)}
        size="sm"
        isDirty={isDirty}
        footer={
          <>
            <button type="button" className="btn btn--secondary" onClick={() => setEditing(null)} disabled={saving}>
              Cancelar
            </button>
            <button type="submit" form={formId} className="btn btn--primary" disabled={saving}>
              {saving ? 'Guardando…' : 'Guardar'}
            </button>
          </>
        }
      >
        <form id={formId} onSubmit={handleSubmit} noValidate>
          <FormField id={`${formId}-name`} label="Nombre" error={fieldError} required>
            {(props) => (
              <input {...props} className="form-input" maxLength={100} value={name} onChange={(event) => { setName(event.target.value); setFieldError(null) }} />
            )}
          </FormField>
        </form>
      </Modal>

      <ConfirmDialog
        open={toDelete !== null}
        title={`Eliminar ${itemLabel.toLowerCase()}`}
        message={`Se eliminará «${toDelete?.name}» del catálogo. Si tiene registros asociados no se podrá eliminar.`}
        confirmLabel="Eliminar"
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </section>
  )
}
