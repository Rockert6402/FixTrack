import './ChartCard.css'

// Contenedor de un gráfico con título y un resumen en texto para lectores de pantalla
export default function ChartCard({ title, summary, children }) {
  return (
    <figure className="chart-card">
      <figcaption className="chart-card__title">{title}</figcaption>
      <div className="chart-card__body" aria-hidden={summary ? 'true' : undefined}>{children}</div>
      {summary && <p className="visually-hidden">{summary}</p>}
    </figure>
  )
}
