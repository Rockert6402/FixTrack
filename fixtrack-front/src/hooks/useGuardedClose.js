import { useState } from 'react'

// Cierra directo si no hay cambios; si los hay, primero pide confirmar el descarte
export function useGuardedClose({ isDirty, onClose }) {
  const [confirmingDiscard, setConfirmingDiscard] = useState(false)

  function requestClose() {
    if (isDirty) {
      setConfirmingDiscard(true)
    } else {
      onClose()
    }
  }

  function discard() {
    setConfirmingDiscard(false)
    onClose()
  }

  return {
    confirmingDiscard,
    requestClose,
    keepEditing: () => setConfirmingDiscard(false),
    discard,
  }
}
