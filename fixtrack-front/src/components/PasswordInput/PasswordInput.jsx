import { useState } from 'react'
import { MdVisibility, MdVisibilityOff } from 'react-icons/md'
import './PasswordInput.css'

// Campo oculto con botón para mostrar u ocultar su contenido; recibe las props del input
export default function PasswordInput({ className = '', ...inputProps }) {
  const [visible, setVisible] = useState(false)

  return (
    <span className="password-input">
      <input {...inputProps} type={visible ? 'text' : 'password'} className={`form-input password-input__field ${className}`} />
      <button
        type="button"
        className="password-input__toggle"
        aria-label={visible ? 'Ocultar' : 'Mostrar'}
        aria-pressed={visible}
        onClick={() => setVisible((current) => !current)}
      >
        {visible ? <MdVisibilityOff aria-hidden="true" /> : <MdVisibility aria-hidden="true" />}
      </button>
    </span>
  )
}
