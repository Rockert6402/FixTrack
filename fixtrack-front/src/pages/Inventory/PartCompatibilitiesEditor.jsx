import { useMemo, useState } from 'react'
import { useToast } from '../../hooks/useToast'
import * as sparePartService from '../../services/sparePartService'
import { getErrorMessage } from '../../utils/errors'
import './PartCompatibilitiesEditor.css'

// Modelos en los que también sirve el repuesto, agrupados por marca; el modelo principal no se repite
export default function PartCompatibilitiesEditor({ part, compatibilities, models, onSaved }) {
  const { showError, showSuccess } = useToast()
  const [selected, setSelected] = useState(() => new Set(compatibilities.map((model) => model.id)))
  const [filter, setFilter] = useState('')
  const [saving, setSaving] = useState(false)

  const groups = useMemo(() => {
    const text = filter.trim().toLowerCase()
    const byBrand = new Map()
    models
      .filter((model) => model.id !== part.deviceModelId)
      .filter((model) => !text || `${model.brandName} ${model.name}`.toLowerCase().includes(text))
      .forEach((model) => byBrand.set(model.brandName, [...(byBrand.get(model.brandName) ?? []), model]))
    return [...byBrand.entries()].sort(([a], [b]) => a.localeCompare(b))
  }, [models, part.deviceModelId, filter])

  function toggle(id) {
    setSelected((current) => {
      const next = new Set(current)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  async function handleSave() {
    setSaving(true)
    try {
      const saved = await sparePartService.replaceCompatibilities(part.id, [...selected])
      showSuccess(`«${part.name}» sirve ahora en ${saved.length} ${saved.length === 1 ? 'modelo' : 'modelos'} además del principal.`)
      onSaved(saved)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="part-compat">
      <p className="text-sm text-muted">Modelo principal: <strong>{part.brandName} {part.deviceModelName}</strong>. Marque los demás modelos donde sirve.</p>
      <label className="part-compat__filter">
        <span className="form-label">Filtrar modelos</span>
        <input type="search" className="form-input" value={filter} onChange={(event) => setFilter(event.target.value)} />
      </label>
      <div className="part-compat__groups">
        {groups.length === 0 && <p className="text-sm text-muted">No hay modelos con ese filtro.</p>}
        {groups.map(([brand, brandModels]) => (
          <fieldset key={brand} className="part-compat__group">
            <legend className="part-compat__brand">{brand}</legend>
            {brandModels.map((model) => (
              <label key={model.id} className="part-compat__option">
                <input type="checkbox" checked={selected.has(model.id)} onChange={() => toggle(model.id)} />
                {model.name}
              </label>
            ))}
          </fieldset>
        ))}
      </div>
      <div className="part-compat__actions">
        <span className="text-sm text-muted">{selected.size} {selected.size === 1 ? 'modelo compatible' : 'modelos compatibles'}</span>
        <button type="button" className="btn btn--primary" disabled={saving} onClick={handleSave}>
          {saving ? 'Guardando…' : 'Guardar compatibles'}
        </button>
      </div>
    </div>
  )
}
