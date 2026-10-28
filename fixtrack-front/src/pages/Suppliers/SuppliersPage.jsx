import { useState } from 'react'
import { MdAdd, MdDeleteOutline, MdOutlineEdit } from 'react-icons/md'
import ConfirmDialog from '../../components/ConfirmDialog/ConfirmDialog'
import DataTable from '../../components/DataTable/DataTable'
import FormField from '../../components/FormField/FormField'
import PageHeader from '../../components/PageHeader/PageHeader'
import { useDebouncedValue } from '../../hooks/useDebouncedValue'
import { useRemoteData } from '../../hooks/useRemoteData'
import { useToast } from '../../hooks/useToast'
import * as supplierService from '../../services/supplierService'
import { getErrorMessage } from '../../utils/errors'
import SupplierFormModal from './SupplierFormModal'
import './SuppliersPage.css'

// Proveedores: búsqueda por nombre, NIT o contacto, registro, edición y baja lógica
export default function SuppliersPage() {
  const { showError, showSuccess } = useToast()
  const [search, setSearch] = useState('')
  const [active, setActive] = useState(true)
  const [page, setPage] = useState(0)
  const [editing, setEditing] = useState(null)
  const [toDeactivate, setToDeactivate] = useState(null)
  const debouncedSearch = useDebouncedValue(search)
  const suppliers = useRemoteData(() => supplierService.searchSuppliers({ search: debouncedSearch, active, page }), [debouncedSearch, active, page])

  async function handleDeactivate() {
    try {
      await supplierService.deactivateSupplier(toDeactivate.id)
      showSuccess(`«${toDeactivate.name}» quedó dado de baja; su historial se conserva.`)
      suppliers.reload()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setToDeactivate(null)
    }
  }

  const columns = [
    { key: 'name', header: 'Proveedor' },
    { key: 'taxId', header: 'NIT' },
    { key: 'contactName', header: 'Contacto', render: (supplier) => supplier.contactName ?? '—' },
    { key: 'phone', header: 'Teléfono', render: (supplier) => supplier.phone ?? '—' },
    { key: 'email', header: 'Correo', render: (supplier) => supplier.email ?? '—' },
    ...(active
      ? [{
        key: 'actions',
        header: 'Acciones',
        align: 'right',
        render: (supplier) => (
          <span className="row-actions">
            <button type="button" className="action-btn action-btn--edit" aria-label={`Editar ${supplier.name}`} onClick={() => setEditing(supplier)}>
              <MdOutlineEdit aria-hidden="true" />
            </button>
            <button type="button" className="action-btn action-btn--delete" aria-label={`Dar de baja ${supplier.name}`} onClick={() => setToDeactivate(supplier)}>
              <MdDeleteOutline aria-hidden="true" />
            </button>
          </span>
        ),
      }]
      : []),
  ]

  return (
    <div className="page">
      <PageHeader
        title="Proveedores"
        description="Proveedores de repuestos con su NIT y su contacto."
        actions={
          <button type="button" className="btn btn--primary" onClick={() => setEditing({})}>
            <MdAdd aria-hidden="true" />
            Nuevo proveedor
          </button>
        }
      />
      <div className="filters">
        <FormField id="suppliers-search" label="Buscar">
          {(props) => <input {...props} type="search" className="form-input" placeholder="Nombre, NIT o contacto" value={search} onChange={(event) => { setSearch(event.target.value); setPage(0) }} />}
        </FormField>
        <FormField id="suppliers-active" label="Mostrar">
          {(props) => (
            <select {...props} className="form-input" value={String(active)} onChange={(event) => { setActive(event.target.value === 'true'); setPage(0) }}>
              <option value="true">Activos</option>
              <option value="false">Dados de baja</option>
            </select>
          )}
        </FormField>
      </div>
      <DataTable
        caption="Proveedores"
        columns={columns}
        rows={suppliers.data?.content ?? []}
        loading={suppliers.loading}
        error={suppliers.error}
        onRetry={suppliers.reload}
        emptyMessage={active ? 'No hay proveedores con esa búsqueda.' : 'No hay proveedores dados de baja.'}
        page={suppliers.data?.page ?? 0}
        totalPages={suppliers.data?.totalPages ?? 1}
        totalElements={suppliers.data?.totalElements}
        onPageChange={setPage}
      />
      {editing && (
        <SupplierFormModal
          key={editing.id ?? 'new'}
          supplier={editing.id ? editing : null}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null)
            suppliers.reload()
          }}
        />
      )}
      <ConfirmDialog
        open={toDeactivate !== null}
        title="Dar de baja proveedor"
        message={`«${toDeactivate?.name}» dejará de ofrecerse para los repuestos y las solicitudes. Su NIT queda reservado y su historial se conserva.`}
        confirmLabel="Dar de baja"
        onConfirm={handleDeactivate}
        onCancel={() => setToDeactivate(null)}
      />
    </div>
  )
}
