import FormField from '../../../components/FormField/FormField'
import PasswordInput from '../../../components/PasswordInput/PasswordInput'
import { AVAILABILITY_LABELS, SERVICE_TYPE_LABELS, toOptions } from '../../../utils/labels'
import './ServiceSection.css'

// Sección del servicio: estado físico, desbloqueo, servicio inicial, problema, técnico, presupuesto y entrega
export default function ServiceSection({ value, errors, technicians, minDate, onChange }) {
  return (
    <fieldset className="intake-section">
      <legend className="intake-section__title">3. Servicio</legend>
      <div className="form-grid">
        <FormField id="intake-service-type" label="Servicio inicial" error={errors.serviceType} required>
          {(props) => (
            <select {...props} className="form-input" value={value.serviceType} onChange={(event) => onChange('serviceType', event.target.value)}>
              <option value="">Elija el servicio</option>
              {toOptions(SERVICE_TYPE_LABELS).map(({ value: option, label }) => <option key={option} value={option}>{label}</option>)}
            </select>
          )}
        </FormField>
        <FormField id="intake-unlock" label="Contraseña o patrón de desbloqueo" hint="Opcional. Se guarda cifrada y se borra al entregar.">
          {(props) => (
            <PasswordInput {...props} maxLength={100} autoComplete="off" value={value.unlockCode} onChange={(event) => onChange('unlockCode', event.target.value)} />
          )}
        </FormField>
        <FormField id="intake-problem" label="Descripción del problema" error={errors.problemDescription} required className="form-grid__full">
          {(props) => <textarea {...props} className="form-input" maxLength={1000} value={value.problemDescription} onChange={(event) => onChange('problemDescription', event.target.value)} />}
        </FormField>
        <FormField id="intake-physical" label="Estado físico al recibirlo" error={errors.physicalCondition} required className="form-grid__full" hint="Rayones, golpes, piezas faltantes…">
          {(props) => <textarea {...props} className="form-input" maxLength={1000} value={value.physicalCondition} onChange={(event) => onChange('physicalCondition', event.target.value)} />}
        </FormField>
        <FormField id="intake-technician" label="Técnico" hint="Opcional; se puede asignar después.">
          {(props) => (
            <select {...props} className="form-input" value={value.technicianId} onChange={(event) => onChange('technicianId', event.target.value)}>
              <option value="">Sin asignar</option>
              {technicians.map((technician) => (
                <option key={technician.id} value={technician.id} disabled={technician.availability === 'ABSENT'}>
                  {technician.fullName} — {AVAILABILITY_LABELS[technician.availability] ?? 'Sin disponibilidad'}
                </option>
              ))}
            </select>
          )}
        </FormField>
        <FormField id="intake-price" label="Presupuesto inicial" error={errors.estimatedPrice} required>
          {(props) => <input {...props} className="form-input" inputMode="decimal" value={value.estimatedPrice} onChange={(event) => onChange('estimatedPrice', event.target.value.replace(',', '.'))} />}
        </FormField>
        <FormField id="intake-delivery" label="Fecha estimada de entrega" error={errors.estimatedDeliveryDate} required>
          {(props) => <input {...props} type="date" className="form-input" min={minDate} value={value.estimatedDeliveryDate} onChange={(event) => onChange('estimatedDeliveryDate', event.target.value)} />}
        </FormField>
      </div>
    </fieldset>
  )
}
