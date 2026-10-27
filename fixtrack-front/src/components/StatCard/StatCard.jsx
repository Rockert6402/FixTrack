import { Link } from 'react-router'
import { formatNumber } from '../../utils/format'
import './StatCard.css'

// Tarjeta de indicador con ícono, título y número; con to, la tarjeta lleva al listado filtrado
export default function StatCard({ icon: Icon, title, value, hint, tone = 'primary', to }) {
  const displayValue = typeof value === 'string' ? value : formatNumber(value)
  const content = (
    <>
      <span className={`stat-card__icon stat-card__icon--${tone}`} aria-hidden="true">
        <Icon />
      </span>
      <div className="stat-card__body">
        <h3 className="stat-card__title">{title}</h3>
        <p className="stat-card__value">{displayValue}</p>
        {hint && <p className="stat-card__hint">{hint}</p>}
      </div>
    </>
  )
  return to ? (
    <Link to={to} className="stat-card stat-card--link">{content}</Link>
  ) : (
    <article className="stat-card">{content}</article>
  )
}
