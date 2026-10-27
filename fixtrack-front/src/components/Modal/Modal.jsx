import { useId } from 'react'
import { createPortal } from 'react-dom'
import { MdClose } from 'react-icons/md'
import { useDialog } from '../../hooks/useDialog'
import { useGuardedClose } from '../../hooks/useGuardedClose'
import DiscardPrompt from '../DiscardPrompt/DiscardPrompt'
import './Modal.css'

// Ventana centrada sobre la pantalla actual; size: sm (confirmación), md (formularios) o lg (ingreso de orden)
export default function Modal({
  open,
  title,
  onClose,
  children,
  footer,
  size = 'md',
  isDirty = false,
  initialFocusRef,
}) {
  const titleId = useId()
  const { confirmingDiscard, requestClose, keepEditing, discard } = useGuardedClose({ isDirty, onClose })
  const dialogRef = useDialog({ open, onRequestClose: requestClose, initialFocusRef })

  if (!open) {
    return null
  }

  function handleOverlayMouseDown(event) {
    if (event.target === event.currentTarget) {
      requestClose()
    }
  }

  return createPortal(
    <div className="modal-overlay" onMouseDown={handleOverlayMouseDown}>
      <div
        ref={dialogRef}
        className={`modal modal--${size}`}
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        tabIndex={-1}
      >
        <header className="modal__header">
          <h2 id={titleId} className="modal__title">{title}</h2>
          <button type="button" className="btn btn--ghost btn--icon" aria-label="Cerrar" onClick={requestClose}>
            <MdClose aria-hidden="true" />
          </button>
        </header>
        {confirmingDiscard && <DiscardPrompt onKeepEditing={keepEditing} onDiscard={discard} />}
        <div className="modal__body">{children}</div>
        {footer && <footer className="modal__footer">{footer}</footer>}
      </div>
    </div>,
    document.body,
  )
}
