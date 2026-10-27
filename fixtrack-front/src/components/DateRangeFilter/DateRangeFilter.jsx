import { useId } from 'react'
import { dateRangePresets } from '../../utils/dates'
import './DateRangeFilter.css'

// Rango de fechas con atajos (este mes, mes pasado, últimos 30 días); value: { from, to } en YYYY-MM-DD
export default function DateRangeFilter({ value, onChange }) {
  const id = useId()
  const presets = dateRangePresets()
  const activePreset = presets.find((preset) => preset.from === value.from && preset.to === value.to)?.id

  return (
    <div className="date-range">
      <div className="date-range__presets" role="group" aria-label="Rangos rápidos">
        {presets.map((preset) => (
          <button
            key={preset.id}
            type="button"
            className={`date-range__preset ${activePreset === preset.id ? 'date-range__preset--active' : ''}`}
            aria-pressed={activePreset === preset.id}
            onClick={() => onChange({ from: preset.from, to: preset.to })}
          >
            {preset.label}
          </button>
        ))}
      </div>
      <div className="date-range__inputs">
        <label className="date-range__field" htmlFor={`${id}-from`}>
          <span className="form-label">Desde</span>
          <input id={`${id}-from`} type="date" className="form-input" max={value.to} value={value.from} onChange={(event) => onChange({ ...value, from: event.target.value })} />
        </label>
        <label className="date-range__field" htmlFor={`${id}-to`}>
          <span className="form-label">Hasta</span>
          <input id={`${id}-to`} type="date" className="form-input" min={value.from} value={value.to} onChange={(event) => onChange({ ...value, to: event.target.value })} />
        </label>
      </div>
    </div>
  )
}
