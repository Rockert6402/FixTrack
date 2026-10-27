import { useState } from 'react'
import { MdAttachMoney, MdDownload, MdShoppingCart, MdTrendingUp, MdVerifiedUser } from 'react-icons/md'
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import ChartCard from '../../components/ChartCard/ChartCard'
import DataTable from '../../components/DataTable/DataTable'
import DateRangeFilter from '../../components/DateRangeFilter/DateRangeFilter'
import FormField from '../../components/FormField/FormField'
import StatCard from '../../components/StatCard/StatCard'
import { useRemoteData } from '../../hooks/useRemoteData'
import { useToast } from '../../hooks/useToast'
import * as personService from '../../services/personService'
import * as reportService from '../../services/reportService'
import { dateRangePresets } from '../../utils/dates'
import { getErrorMessage } from '../../utils/errors'
import { downloadFile } from '../../utils/files'
import { formatCurrency, formatDateTime } from '../../utils/format'
import { ROLES } from '../../utils/labels'
import './ProfitReportTab.css'

// Ganancias de las órdenes entregadas: total, por técnico y por orden, con el costo de garantías y exportación a Excel.
// El técnico solo ve las suyas
export default function ProfitReportTab({ role }) {
  const { showError } = useToast()
  const isTechnician = role === ROLES.TECHNICIAN
  const [range, setRange] = useState(() => {
    const { from, to } = dateRangePresets()[0]
    return { from, to }
  })
  const [technicianId, setTechnicianId] = useState('')
  const [exporting, setExporting] = useState(false)
  const query = { ...range, technicianId: isTechnician ? '' : technicianId }
  const report = useRemoteData(() => reportService.profitReport(query), [range.from, range.to, query.technicianId])
  const technicians = useRemoteData(() => (isTechnician ? Promise.resolve({ content: [] }) : personService.searchPeople({ role: ROLES.TECHNICIAN, size: 100 })), [isTechnician])
  const data = report.data
  const totals = data?.totals

  async function handleExport() {
    setExporting(true)
    try {
      downloadFile(await reportService.exportProfit(query), `ganancias-${range.from}-a-${range.to}.xlsx`)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setExporting(false)
    }
  }

  const chartData = (data?.byTechnician ?? []).map((row) => ({
    name: row.technicianName,
    Cobrado: Number(row.revenue),
    'Costo de repuestos': Number(row.partsCost),
    Ganancia: Number(row.profit),
  }))
  const technicianColumns = [
    { key: 'technicianName', header: 'Técnico' },
    { key: 'orders', header: 'Órdenes', align: 'right' },
    { key: 'warranties', header: 'Garantías', align: 'right' },
    { key: 'revenue', header: 'Cobrado', align: 'right', render: (row) => formatCurrency(row.revenue) },
    { key: 'partsCost', header: 'Costo de repuestos', align: 'right', render: (row) => formatCurrency(row.partsCost) },
    { key: 'warrantyCost', header: 'Costo de garantías', align: 'right', render: (row) => formatCurrency(row.warrantyCost) },
    { key: 'profit', header: 'Ganancia', align: 'right', render: (row) => <span className={row.profit < 0 ? 'profit-report__loss' : 'profit-report__gain'}>{formatCurrency(row.profit)}</span> },
  ]
  const orderColumns = [
    { key: 'code', header: 'Orden', render: (order) => <span>{order.code}{order.warranty && <span className="tag profit-report__warranty">Garantía</span>}</span> },
    { key: 'deliveredAt', header: 'Entregada', render: (order) => formatDateTime(order.deliveredAt) },
    { key: 'customerName', header: 'Cliente' },
    ...(isTechnician ? [] : [{ key: 'technicianName', header: 'Técnico' }]),
    { key: 'revenue', header: 'Cobrado', align: 'right', render: (order) => formatCurrency(order.revenue) },
    { key: 'partsCost', header: 'Costo de repuestos', align: 'right', render: (order) => formatCurrency(order.partsCost) },
    { key: 'profit', header: 'Ganancia', align: 'right', render: (order) => <span className={order.profit < 0 ? 'profit-report__loss' : 'profit-report__gain'}>{formatCurrency(order.profit)}</span> },
  ]

  return (
    <div className="report-tab">
      <div className="profit-report__filters">
        <DateRangeFilter value={range} onChange={setRange} />
        {!isTechnician && (
          <FormField id="profit-technician" label="Técnico">
            {(props) => (
              <select {...props} className="form-input" value={technicianId} onChange={(event) => setTechnicianId(event.target.value)}>
                <option value="">Todos</option>
                {(technicians.data?.content ?? []).map((technician) => <option key={technician.id} value={technician.id}>{technician.fullName}</option>)}
              </select>
            )}
          </FormField>
        )}
        <button type="button" className="btn btn--secondary" disabled={exporting || !data} onClick={handleExport}>
          <MdDownload aria-hidden="true" />
          {exporting ? 'Exportando…' : 'Exportar a Excel'}
        </button>
      </div>
      {report.error && <p className="form-error" role="alert">{report.error}</p>}
      <section className="grid-cards" aria-label="Totales de ganancias">
        <StatCard icon={MdAttachMoney} title="Total cobrado" value={totals ? formatCurrency(totals.revenue) : null} hint={totals ? `${totals.orders} órdenes entregadas` : undefined} />
        <StatCard icon={MdShoppingCart} title="Costo de repuestos" tone="warning" value={totals ? formatCurrency(totals.partsCost) : null} />
        <StatCard icon={MdTrendingUp} title="Ganancia" tone="success" value={totals ? formatCurrency(totals.profit) : null} />
        <StatCard icon={MdVerifiedUser} title="Costo de garantías" tone="danger" value={totals ? formatCurrency(totals.warrantyCost) : null} hint={totals ? `${totals.warranties} garantías (incluido en el costo)` : undefined} />
      </section>
      {chartData.length > 0 && (
        <ChartCard title={isTechnician ? 'Mis valores' : 'Cobrado, costo y ganancia por técnico'} summary={chartData.map((row) => `${row.name}: ganancia ${formatCurrency(row.Ganancia)}`).join(', ')}>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData} margin={{ left: 24 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
              <XAxis dataKey="name" tick={{ fontSize: 12 }} />
              <YAxis tickFormatter={(value) => formatCurrency(value)} tick={{ fontSize: 11 }} width={90} />
              <Tooltip formatter={(value) => formatCurrency(value)} />
              <Legend />
              <Bar dataKey="Cobrado" fill="var(--color-primary)" radius={[4, 4, 0, 0]} />
              <Bar dataKey="Costo de repuestos" fill="var(--color-warning)" radius={[4, 4, 0, 0]} />
              <Bar dataKey="Ganancia" fill="var(--color-success)" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>
      )}
      {!isTechnician && (
        <DataTable caption="Ganancia por técnico" columns={technicianColumns} rows={(data?.byTechnician ?? []).map((row) => ({ ...row, id: row.technicianId ?? 'none' }))} loading={report.loading} emptyMessage="No hay órdenes entregadas en ese rango." />
      )}
      <section className="page__section" aria-labelledby="profit-orders-title">
        <h2 id="profit-orders-title" className="page__section-title">Por orden</h2>
        <DataTable caption="Ganancia por orden" columns={orderColumns} rows={data?.orders ?? []} loading={report.loading} emptyMessage="No hay órdenes entregadas en ese rango." />
      </section>
    </div>
  )
}
