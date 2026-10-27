import DataTable from '../../components/DataTable/DataTable'
import { STOCK_STATUS_LABELS } from '../../utils/labels'
import './StockAlertList.css'

const COLUMNS = [
  {
    key: 'name',
    header: 'Repuesto',
    render: (part) => (
      <span className="stock-alert-list__part">
        {part.name}
        <span className="stock-alert-list__sku">{part.sku}</span>
      </span>
    ),
  },
  { key: 'deviceModel', header: 'Modelo' },
  { key: 'stock', header: 'Stock', align: 'right', render: (part) => `${part.stock} (mín. ${part.minStock})` },
  {
    key: 'stockStatus',
    header: 'Estado',
    render: (part) => (
      <span className={`tag ${part.stockStatus === 'OUT' ? 'tag--danger' : 'tag--warning'}`}>
        {STOCK_STATUS_LABELS[part.stockStatus]}
      </span>
    ),
  },
]

// Repuestos agotados o en el mínimo, primero los agotados
export default function StockAlertList({ parts, loading, error, onRetry }) {
  return (
    <DataTable
      caption="Repuestos en alerta de stock"
      columns={COLUMNS}
      rows={parts ?? []}
      loading={loading}
      error={error}
      onRetry={onRetry}
      emptyMessage="Ningún repuesto está en el mínimo ni agotado."
    />
  )
}
