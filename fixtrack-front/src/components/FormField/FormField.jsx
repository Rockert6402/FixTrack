import './FormField.css'

// Campo con label, ayuda y error enlazados; children recibe las props de accesibilidad para el control
export default function FormField({ id, label, error, hint, required = false, className = '', children }) {
  const hintId = hint ? `${id}-hint` : undefined
  const errorId = error ? `${id}-error` : undefined
  const controlProps = {
    id,
    'aria-invalid': Boolean(error),
    'aria-describedby': [errorId, hintId].filter(Boolean).join(' ') || undefined,
    'aria-required': required || undefined,
  }

  return (
    <div className={`form-field ${className}`}>
      <label className="form-label" htmlFor={id}>
        {label}
        {required && <span className="form-field__required" aria-hidden="true"> *</span>}
      </label>
      {children(controlProps)}
      {error && <span id={errorId} className="form-error">{error}</span>}
      {hint && !error && <span id={hintId} className="form-hint">{hint}</span>}
    </div>
  )
}
