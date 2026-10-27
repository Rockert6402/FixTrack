import FormField from '../../../components/FormField/FormField'
import { DEVICE_TYPE_LABELS } from '../../../utils/labels'
import './DeviceSection.css'

// Sección del equipo: elegir uno del cliente o registrar uno nuevo con marca, modelo, IMEI o serie y color
export default function DeviceSection({ value, errors, devices, brands, models, onChange }) {
  const isNew = value.deviceId === ''
  const brandModels = models.filter((model) => String(model.brandId) === value.brandId)
  const selectedModel = models.find((model) => String(model.id) === value.deviceModelId)
  const isPhone = selectedModel?.deviceType === 'PHONE'

  return (
    <fieldset className="intake-section">
      <legend className="intake-section__title">2. Equipo</legend>

      {devices.length > 0 && (
        <div className="device-section__options" role="radiogroup" aria-label="Equipos del cliente">
          {devices.map((device) => (
            <label key={device.id} className={`device-section__option ${value.deviceId === String(device.id) ? 'device-section__option--selected' : ''}`}>
              <input type="radio" name="intake-device" checked={value.deviceId === String(device.id)} onChange={() => onChange('deviceId', String(device.id))} />
              <span>
                <strong>{device.brandName} {device.modelName}</strong>
                <span className="device-section__meta">
                  {DEVICE_TYPE_LABELS[device.deviceType]}
                  {device.imei && ` · IMEI ${device.imei}`}
                  {device.serialNumber && ` · Serie ${device.serialNumber}`}
                  {device.color && ` · ${device.color}`}
                </span>
              </span>
            </label>
          ))}
          <label className={`device-section__option ${isNew ? 'device-section__option--selected' : ''}`}>
            <input type="radio" name="intake-device" checked={isNew} onChange={() => onChange('deviceId', '')} />
            <span><strong>Registrar otro equipo</strong></span>
          </label>
        </div>
      )}
      {errors.deviceId && <span className="form-error">{errors.deviceId}</span>}

      {isNew && (
        <div className="form-grid">
          <FormField id="intake-brand" label="Marca" required>
            {(props) => (
              <select {...props} className="form-input" value={value.brandId} onChange={(event) => onChange('brandId', event.target.value)}>
                <option value="">Elija una marca</option>
                {brands.map((brand) => <option key={brand.id} value={brand.id}>{brand.name}</option>)}
              </select>
            )}
          </FormField>
          <FormField id="intake-model" label="Modelo" error={errors.deviceModelId} required>
            {(props) => (
              <select {...props} className="form-input" value={value.deviceModelId} disabled={!value.brandId} onChange={(event) => onChange('deviceModelId', event.target.value)}>
                <option value="">{value.brandId ? 'Elija un modelo' : 'Primero elija la marca'}</option>
                {brandModels.map((model) => (
                  <option key={model.id} value={model.id}>{model.name} ({DEVICE_TYPE_LABELS[model.deviceType]})</option>
                ))}
              </select>
            )}
          </FormField>
          <FormField id="intake-imei" label="IMEI" error={errors.imei} required={isPhone} hint={isPhone ? '15 dígitos; obligatorio en teléfonos.' : 'Solo para teléfonos.'}>
            {(props) => (
              <input {...props} className="form-input" inputMode="numeric" maxLength={15} value={value.imei} onChange={(event) => onChange('imei', event.target.value.replace(/\D/g, ''))} />
            )}
          </FormField>
          <FormField id="intake-serial" label="Número de serie" error={errors.serialNumber}>
            {(props) => <input {...props} className="form-input" maxLength={50} value={value.serialNumber} onChange={(event) => onChange('serialNumber', event.target.value)} />}
          </FormField>
          <FormField id="intake-color" label="Color">
            {(props) => <input {...props} className="form-input" maxLength={30} value={value.color} onChange={(event) => onChange('color', event.target.value)} />}
          </FormField>
          <FormField id="intake-observations" label="Observaciones del equipo">
            {(props) => <input {...props} className="form-input" maxLength={500} value={value.observations} onChange={(event) => onChange('observations', event.target.value)} />}
          </FormField>
        </div>
      )}
    </fieldset>
  )
}
