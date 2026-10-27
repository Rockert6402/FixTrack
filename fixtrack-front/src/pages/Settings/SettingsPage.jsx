import PageHeader from '../../components/PageHeader/PageHeader'
import { useRemoteData } from '../../hooks/useRemoteData'
import * as settingService from '../../services/settingService'
import SettingCard from './SettingCard'
import './SettingsPage.css'

// Parámetros del sistema que usa FixTrack: stock mínimo, garantía, alerta de no reclamados y condiciones del servicio
export default function SettingsPage() {
  const settings = useRemoteData(settingService.listSettings, [])

  return (
    <div className="page">
      <PageHeader title="Parámetros" description="Valores que usa el sistema mientras no se indique otro." />
      {settings.loading && !settings.data && <p className="text-muted">Cargando…</p>}
      {settings.error && <p className="form-error">{settings.error}</p>}
      <div className="settings-page__list">
        {(settings.data ?? []).map((setting) => (
          <SettingCard key={`${setting.key}-${setting.updatedAt}`} setting={setting} onSaved={settings.reload} />
        ))}
      </div>
    </div>
  )
}
