import { formatCurrency } from '../../utils/format'
import './CashTodayCard.css'

// Lo esperado del día por medio de pago (con devoluciones restando)
export default function CashTodayCard({ today }) {
  return (
    <section className="cash-today" aria-labelledby="cash-today-title">
      <h2 id="cash-today-title" className="cash-today__title">Esperado en caja</h2>
      <dl className="cash-today__list">
        <div className="cash-today__row"><dt>Efectivo</dt><dd>{formatCurrency(today.expectedCash)}</dd></div>
        <div className="cash-today__row"><dt>Transferencia</dt><dd>{formatCurrency(today.expectedTransfer)}</dd></div>
        <div className="cash-today__row"><dt>Tarjeta</dt><dd>{formatCurrency(today.expectedCard)}</dd></div>
        <div className="cash-today__row cash-today__row--total"><dt>Total</dt><dd>{formatCurrency(today.expectedTotal)}</dd></div>
      </dl>
      <p className="cash-today__note text-sm text-muted">
        {today.payments} {today.payments === 1 ? 'pago pendiente' : 'pagos pendientes'} de cierre
        {today.fromPreviousDays > 0 && `, ${today.fromPreviousDays} de días anteriores sin cerrar`}.
      </p>
    </section>
  )
}
