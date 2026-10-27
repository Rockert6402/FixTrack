import { todayInBogota } from './format'

function iso(date) {
  return date.toISOString().slice(0, 10)
}

// Rangos rápidos para los informes, calculados desde hoy en Bogotá
export function dateRangePresets() {
  const today = todayInBogota()
  const [year, month] = today.split('-').map(Number)
  const firstOfMonth = new Date(Date.UTC(year, month - 1, 1))
  const firstOfLastMonth = new Date(Date.UTC(year, month - 2, 1))
  const lastOfLastMonth = new Date(Date.UTC(year, month - 1, 0))
  const thirtyDaysAgo = new Date(`${today}T00:00:00Z`)
  thirtyDaysAgo.setUTCDate(thirtyDaysAgo.getUTCDate() - 29)
  return [
    { id: 'month', label: 'Este mes', from: iso(firstOfMonth), to: today },
    { id: 'lastMonth', label: 'Mes pasado', from: iso(firstOfLastMonth), to: iso(lastOfLastMonth) },
    { id: 'last30', label: 'Últimos 30 días', from: iso(thirtyDaysAgo), to: today },
  ]
}
