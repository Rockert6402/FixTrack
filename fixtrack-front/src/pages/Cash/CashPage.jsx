import { useState } from 'react'
import DataTable from '../../components/DataTable/DataTable'
import FormField from '../../components/FormField/FormField'
import PageHeader from '../../components/PageHeader/PageHeader'
import { useAuth } from '../../hooks/useAuth'
import { useRemoteData } from '../../hooks/useRemoteData'
import * as cashService from '../../services/cashService'
import { formatCurrency, formatDate, formatDateTime } from '../../utils/format'
import { ROLES } from '../../utils/labels'
import CashClosingForm from './CashClosingForm'
import CashTodayCard from './CashTodayCard'
import './CashPage.css'

// Caja: lo esperado del día, el cierre con el efectivo contado y el historial (el administrador ve el de todos)
export default function CashPage() {
  const { user } = useAuth()
  const isAdmin = user.role === ROLES.ADMIN
  const [range, setRange] = useState({ from: '', to: '' })
  const [page, setPage] = useState(0)
  const today = useRemoteData(cashService.getToday, [])
  const closings = useRemoteData(() => cashService.listClosings({ ...range, page }), [range.from, range.to, page])

  function refresh() {
    today.reload()
    closings.reload()
  }

  const closedToday = today.data?.closing
  const columns = [
    { key: 'closingDate', header: 'Fecha', render: (closing) => formatDate(closing.closingDate) },
    ...(isAdmin ? [{ key: 'closedByName', header: 'Usuario' }] : []),
    { key: 'expectedCash', header: 'Efectivo esperado', align: 'right', render: (closing) => formatCurrency(closing.expectedCash) },
    { key: 'countedCash', header: 'Efectivo contado', align: 'right', render: (closing) => formatCurrency(closing.countedCash) },
    {
      key: 'cashDifference',
      header: 'Diferencia',
      align: 'right',
      render: (closing) => <span className={closing.cashDifference < 0 ? 'cash-page__short' : undefined}>{formatCurrency(closing.cashDifference)}</span>,
    },
    { key: 'expectedTransfer', header: 'Transferencias', align: 'right', render: (closing) => formatCurrency(closing.expectedTransfer) },
    { key: 'expectedCard', header: 'Tarjetas', align: 'right', render: (closing) => formatCurrency(closing.expectedCard) },
    { key: 'payments', header: 'Pagos', align: 'right' },
    { key: 'notes', header: 'Notas', render: (closing) => closing.notes ?? '—' },
  ]

  return (
    <div className="page">
      <PageHeader title="Caja" description="Lo recibido hoy por medio de pago y el cierre diario de su caja." />

      {today.error && <p className="form-error">{today.error}</p>}
      {today.data && (
        <div className="cash-page__today">
          <CashTodayCard today={today.data} />
          {closedToday ? (
            <section className="cash-page__closed" aria-labelledby="cash-closed-title">
              <h2 id="cash-closed-title" className="cash-page__closed-title">Caja de hoy cerrada</h2>
              <p>
                Cerrada {formatDateTime(closedToday.createdAt)} con {formatCurrency(closedToday.countedCash)} contados
                (diferencia {formatCurrency(closedToday.cashDifference)}).
              </p>
              {today.data.payments > 0 && (
                <p className="text-sm text-muted">Los pagos recibidos después del cierre quedan para el cierre de mañana.</p>
              )}
            </section>
          ) : (
            <CashClosingForm today={today.data} onClosed={refresh} />
          )}
        </div>
      )}

      <section className="page__section" aria-labelledby="cash-history-title">
        <h2 id="cash-history-title" className="page__section-title">{isAdmin ? 'Cierres de todos los usuarios' : 'Mis cierres'}</h2>
        <div className="filters">
          <FormField id="cash-from" label="Desde">
            {(props) => <input {...props} type="date" className="form-input" max={range.to || undefined} value={range.from} onChange={(event) => { setRange((current) => ({ ...current, from: event.target.value })); setPage(0) }} />}
          </FormField>
          <FormField id="cash-to" label="Hasta">
            {(props) => <input {...props} type="date" className="form-input" min={range.from || undefined} value={range.to} onChange={(event) => { setRange((current) => ({ ...current, to: event.target.value })); setPage(0) }} />}
          </FormField>
        </div>
        <DataTable
          caption="Cierres de caja"
          columns={columns}
          rows={closings.data?.content ?? []}
          loading={closings.loading}
          error={closings.error}
          onRetry={closings.reload}
          emptyMessage="No hay cierres en ese rango."
          page={closings.data?.page ?? 0}
          totalPages={closings.data?.totalPages ?? 1}
          totalElements={closings.data?.totalElements}
          onPageChange={setPage}
          rowClassName={(closing) => (closing.cashDifference < 0 ? 'data-table__row--danger' : undefined)}
        />
      </section>
    </div>
  )
}
