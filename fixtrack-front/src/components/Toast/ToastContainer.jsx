import { createPortal } from 'react-dom'
import { MdCheckCircleOutline, MdClose, MdErrorOutline, MdInfoOutline } from 'react-icons/md'
import './Toast.css'

const ICONS = {
  error: MdErrorOutline,
  success: MdCheckCircleOutline,
  info: MdInfoOutline,
}

// Pila de mensajes emergentes en la esquina: región aria-live siempre presente para que el lector anuncie
// cada mensaje nuevo; los errores, además, se anuncian de inmediato con role="alert"
export default function ToastContainer({ toasts, onDismiss }) {
  return createPortal(
    <div className="toast-stack" aria-live="polite" aria-relevant="additions">
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
