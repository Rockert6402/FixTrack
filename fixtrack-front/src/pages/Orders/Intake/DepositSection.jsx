import FormField from '../../../components/FormField/FormField'
import { PAYMENT_METHOD_LABELS, toOptions } from '../../../utils/labels'
import './DepositSection.css'

// Abono opcional al recibir el equipo; se registra como pago DEPOSIT apenas se crea la orden
export default function DepositSection({ value, errors, onChange }) {
  return (
    <fieldset className="intake-section">
      <legend className="intake-section__title">4. Abono (opcional)</legend>
      <div className="form-grid">
        <FormField id="intake-deposit" label="Valor del abono" error={errors.depositAmount} hint="Déjelo vacío si el cliente no abona.">
          {(props) => <input {...props} className="form-input" inputMode="decimal" value={value.amount} onChange={(event) => onChange('amount', event.target.value.replace(',', '.').replace(/[^\d.]/g, ''))} />}
        </FormField>
        <FormField id="intake-deposit-method" label="Medio de pago">
          {(props) => (
            <select {...props} className="form-input" value={value.method} disabled={!value.amount} onChange={(event) => onChange('method', event.target.value)}>
              {toOptions(PAYMENT_METHOD_LABELS).map(({ value: option, label }) => <option key={option} value={option}>{label}</option>)}
            </select>
          )}
        </FormField>
      </div>
    </fieldset>
  )
}
