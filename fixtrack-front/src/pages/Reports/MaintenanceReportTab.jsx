import { useState } from 'react'
import { MdAssignment, MdBuild, MdTaskAlt, MdVerifiedUser } from 'react-icons/md'
import { Bar, BarChart, CartesianGrid, Cell, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import ChartCard from '../../components/ChartCard/ChartCard'
import DataTable from '../../components/DataTable/DataTable'
import DateRangeFilter from '../../components/DateRangeFilter/DateRangeFilter'
import StatCard from '../../components/StatCard/StatCard'
import { useRemoteData } from '../../hooks/useRemoteData'
import * as reportService from '../../services/reportService'
import { dateRangePresets } from '../../utils/dates'
import { STATUS_LABELS } from '../../utils/labels'
import './MaintenanceReportTab.css'

const STATUS_COLORS = {
  RECEIVED: 'var(--status-received)',
  IN_DIAGNOSIS: 'var(--status-in-diagnosis)',
  WAITING_PARTS: 'var(--status-waiting-parts)',
  IN_REPAIR: 'var(--status-in-repair)',
  READY: 'var(--status-ready)',
  DELIVERED: 'var(--status-delivered)',
  CANCELLED: 'var(--status-cancelled)',
}

// Órdenes recibidas en el rango: totales por estado y por técnico
export default function MaintenanceReportTab() {
  const [range, setRange] = useState(() => {
    const { from, to } = dateRangePresets()[0]
    return { from, to }
  })
  const report = useRemoteData(() => reportService.maintenanceReport(range), [range.from, range.to])
  const data = report.data
  const count = (status) => data?.byStatus.find((entry) => entry.status === status)?.count ?? 0
  const statusData = (data?.byStatus ?? []).map((entry) => ({ ...entry, name: STATUS_LABELS[entry.status] }))
  const technicianData = (data?.byTechnician ?? []).map((row) => ({
    name: row.technicianName,
    'En proceso': row.inProgress,
    Listas: row.ready,
    Entregadas: row.delivered,
    Canceladas: row.cancelled,
  }))

  const columns = [
    { key: 'technicianName', header: 'Técnico' },
    { key: 'total', header: 'Órdenes', align: 'right' },
    { key: 'inProgress', header: 'En proceso', align: 'right' },
    { key: 'ready', header: 'Listas', align: 'right' },
    { key: 'delivered', header: 'Entregadas', align: 'right' },
    { key: 'cancelled', header: 'Canceladas', align: 'right' },
    { key: 'warranties', header: 'Garantías', align: 'right' },
  ]

  return (
    <div className="report-tab">
      <DateRangeFilter value={range} onChange={setRange} />
      {report.error && <p className="form-error" role="alert">{report.error}</p>}
      <section className="grid-cards" aria-label="Totales de mantenimientos">
        <StatCard icon={MdAssignment} title="Órdenes recibidas" value={data?.totalOrders ?? null} />
        <StatCard icon={MdBuild} title="En proceso" value={data ? count('RECEIVED') + count('IN_DIAGNOSIS') + count('WAITING_PARTS') + count('IN_REPAIR') : null} />
        <StatCard icon={MdTaskAlt} title="Entregadas" tone="success" value={data ? count('DELIVERED') : null} />
        <StatCard icon={MdVerifiedUser} title="Garantías" tone="warning" value={data?.warranties ?? null} />
      </section>
      {data && data.totalOrders > 0 && (
        <div className="report-tab__charts">
          <ChartCard title="Órdenes por estado" summary={statusData.map((entry) => `${entry.name}: ${entry.count}`).join(', ')}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={statusData}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                <XAxis dataKey="name" tick={{ fontSize: 11 }} interval={0} angle={-20} textAnchor="end" height={60} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="count" name="Órdenes" radius={[4, 4, 0, 0]}>
                  {statusData.map((entry) => <Cell key={entry.status} fill={STATUS_COLORS[entry.status]} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </ChartCard>
          <ChartCard title="Órdenes por técnico" summary={technicianData.map((row) => `${row.name}: ${row.Entregadas} entregadas`).join(', ')}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={technicianData}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Bar dataKey="En proceso" stackId="orders" fill="var(--status-in-repair)" />
                <Bar dataKey="Listas" stackId="orders" fill="var(--status-ready)" />
                <Bar dataKey="Entregadas" stackId="orders" fill="var(--status-delivered)" />
                <Bar dataKey="Canceladas" stackId="orders" fill="var(--status-cancelled)" />
              </BarChart>
            </ResponsiveContainer>
          </ChartCard>
        </div>
      )}
      <DataTable
        caption="Órdenes por técnico"
        columns={columns}
        rows={(data?.byTechnician ?? []).map((row) => ({ ...row, id: row.technicianId ?? 'none' }))}
        loading={report.loading}
        emptyMessage="No hay órdenes recibidas en ese rango."
      />
    </div>
  )
}
