import { createPortal } from 'react-dom'
import { MdCheckCircleOutline, MdClose, MdErrorOutline, MdInfoOutline } from 'react-icons/md'
import './Toast.css'

const ICONS = {
  error: MdErrorOutline,
  success: MdCheckCircleOutline,
  info: MdInfoOutline,
}

// Pila de mensajes emergentes en la esquina; los errores se anuncian de inmediato al lector de pantalla
export default function ToastContainer({ toasts, onDismiss }) {
  return createPortal(
    <div className="toast-stack">
      {toasts.map(({ id, type, message }) => {
        const Icon = ICONS[type]
        return (
          <div key={id} className={`toast toast--${type}`} role={type === 'error' ? 'alert' : 'status'}>
            <Icon className="toast__icon" aria-hidden="true" />
            <p className="toast__message">{message}</p>
            <button type="button" className="toast__close" aria-label="Cerrar mensaje" onClick={() => onDismiss(id)}>
              <MdClose aria-hidden="true" />
            </button>
          </div>
        )
      })}
    </div>,
    document.body,
  )
}
