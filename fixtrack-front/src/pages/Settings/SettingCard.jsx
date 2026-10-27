import { useId, useState } from 'react'
import { useToast } from '../../hooks/useToast'
import * as settingService from '../../services/settingService'
import { getErrorMessage } from '../../utils/errors'
import { formatDateTime } from '../../utils/format'
import './SettingCard.css'

// Un parámetro del sistema: número con su rango o texto largo; muestra el valor por defecto y quién lo cambió
export default function SettingCard({ setting, onSaved }) {
  const id = useId()
  const { showError, showSuccess } = useToast()
  const [value, setValue] = useState(setting.value)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)
  const isNumber = setting.type === 'INTEGER'

  async function handleSubmit(event) {
    event.preventDefault()
    if (isNumber && (!/^\d+$/.test(value) || Number(value) < setting.min || Number(value) > setting.max)) {
      setError(`Debe ser un número entero entre ${setting.min} y ${setting.max}.`)
      return
    }
    setSaving(true)
    try {
      const saved = await settingService.updateSetting(setting.key, value)
      showSuccess('Parámetro guardado.')
      onSaved(saved)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="setting-card" onSubmit={handleSubmit} noValidate>
      <label className="setting-card__label" htmlFor={id}>{setting.description}</label>
      {isNumber ? (
        <input id={id} className="form-input setting-card__number" inputMode="numeric" value={value} aria-invalid={Boolean(error)} aria-describedby={`${id}-meta`} onChange={(event) => { setValue(event.target.value.replace(/\D/g, '')); setError(null) }} />
      ) : (
        <textarea id={id} className="form-input setting-card__text" rows={6} maxLength={setting.max} value={value} aria-describedby={`${id}-meta`} onChange={(event) => setValue(event.target.value)} />
      )}
      {error && <span className="form-error" role="alert">{error}</span>}
      <p id={`${id}-meta`} className="setting-card__meta">
        {isNumber ? `Entre ${setting.min} y ${setting.max}. ` : `Máximo ${setting.max} caracteres. `}
        Por defecto: {setting.defaultValue === '' ? '(vacío)' : setting.defaultValue}.
        {setting.customized && setting.updatedByName && ` Cambiado por ${setting.updatedByName} el ${formatDateTime(setting.updatedAt)}`}
      </p>
      <div className="setting-card__actions">
        <button type="submit" className="btn btn--primary" disabled={saving || value === setting.value}>{saving ? 'Guardando…' : 'Guardar'}</button>
      </div>
    </form>
  )
}
