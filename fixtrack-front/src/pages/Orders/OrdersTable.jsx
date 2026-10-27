import DataTable from '../../components/DataTable/DataTable'
import StatusBadge from '../../components/StatusBadge/StatusBadge'
import { formatDate, formatDateTime } from '../../utils/format'
import './OrdersTable.css'

const COLUMNS = [
  { key: 'code', header: 'Orden', render: (order) => <span className="orders-table__code">{order.code}</span> },
  {
    key: 'customer',
    header: 'Cliente',
    render: (order) => (
      <span className="orders-table__customer">
        {order.customerName}
        <span className="orders-table__document">{order.customerDocument}</span>
      </span>
    ),
  },
  { key: 'device', header: 'Equipo' },
  { key: 'technicianName', header: 'Técnico', render: (order) => order.technicianName ?? 'Sin asignar' },
  { key: 'status', header: 'Estado', render: (order) => <StatusBadge status={order.status} /> },
  {
    key: 'estimatedDeliveryDate',
    header: 'Entrega estimada',
    render: (order) => (
      <span className="orders-table__delivery">
        {formatDate(order.estimatedDeliveryDate)}
        {order.overdue && <span className="orders-table__overdue">Atrasada</span>}
      </span>
    ),
  },
  { key: 'receivedAt', header: 'Ingreso', render: (order) => formatDateTime(order.receivedAt) },
]

// Tabla de órdenes: las atrasadas se resaltan en rojo y además llevan la etiqueta «Atrasada»
export default function OrdersTable({ page, loading, error, onRetry, onPageChange, onSelect, emptyMessage, caption = 'Órdenes' }) {
  return (
    <DataTable
      caption={caption}
      columns={COLUMNS}
      rows={page?.content ?? []}
      loading={loading}
      error={error}
      onRetry={onRetry}
      emptyMessage={emptyMessage}
      page={page?.page ?? 0}
      totalPages={page?.totalPages ?? 1}
      totalElements={page?.totalElements}
      onPageChange={onPageChange}
      onRowClick={onSelect ? (order) => onSelect(order.id) : undefined}
      rowClassName={(order) => (order.overdue ? 'data-table__row--danger' : undefined)}
    />
  )
}
