import FormField from '../../../components/FormField/FormField'
import './ContactField.css'

// Celular o correo con su casilla «No tiene»: al marcarla el campo se desactiva y muestra NT
export default function ContactField({ id, label, error, value, noValue, noValueLabel, inputProps, onValueChange, onNoValueChange }) {
  return (
    <div className="contact-field">
      <FormField id={id} label={label} error={error} required={!noValue}>
        {(props) => (
          <input {...props} {...inputProps} className="form-input" disabled={noValue} value={noValue ? 'NT' : value} onChange={(event) => onValueChange(event.target.value)} />
        )}
      </FormField>
      <label className="contact-field__no-data">
        <input type="checkbox" checked={noValue} onChange={(event) => onNoValueChange(event.target.checked)} />
        {noValueLabel}
      </label>
    </div>
  )
}
