import { useRef, useState } from 'react'
import Modal from '../Modal/Modal'
import './ConfirmDialog.css'

// Confirmación de acciones irreversibles; onConfirm puede ser async y maneja sus propios errores
// children agrega campos bajo el mensaje (por ejemplo, el motivo) y confirmDisabled bloquea el botón mientras falten
export default function ConfirmDialog({
  open,
  title,
  message,
  confirmLabel,
  cancelLabel = 'Volver',
  onConfirm,
  onCancel,
  danger = true,
  confirmDisabled = false,
  children,
}) {
  const [submitting, setSubmitting] = useState(false)
  const cancelRef = useRef(null)

  async function handleConfirm() {
    setSubmitting(true)
    try {
      await onConfirm()
    } finally {
      setSubmitting(false)
    }
  }

  function handleClose() {
    if (!submitting) {
      onCancel()
    }
  }

  return (
    <Modal
      open={open}
      title={title}
      onClose={handleClose}
      size="sm"
      initialFocusRef={cancelRef}
      footer={
        <>
          <button ref={cancelRef} type="button" className="btn btn--secondary" onClick={handleClose} disabled={submitting}>
            {cancelLabel}
          </button>
          <button
            type="button"
            className={`btn ${danger ? 'btn--danger' : 'btn--primary'}`}
            onClick={handleConfirm}
            disabled={submitting || confirmDisabled}
          >
            {submitting ? 'Procesando…' : confirmLabel}
          </button>
        </>
      }
    >
      <p className="confirm-dialog__message">{message}</p>
      {children}
    </Modal>
  )
}
