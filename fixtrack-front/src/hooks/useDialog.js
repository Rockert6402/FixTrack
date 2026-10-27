import { useEffect, useRef } from 'react'

const FOCUSABLE = [
  'a[href]',
  'button:not([disabled])',
  'input:not([disabled]):not([type="hidden"])',
  'select:not([disabled])',
  'textarea:not([disabled])',
  '[tabindex]:not([tabindex="-1"])',
].join(', ')

const FIELDS = 'input:not([disabled]):not([type="hidden"]), select:not([disabled]), textarea:not([disabled])'

// Ventanas abiertas en orden; solo la de más arriba responde al teclado
const openDialogs = []

// Comportamiento común de modales y paneles: Esc, foco inicial, foco atrapado, foco de regreso y sin scroll de fondo
export function useDialog({ open, onRequestClose, initialFocusRef }) {
  const dialogRef = useRef(null)
  const onRequestCloseRef = useRef(onRequestClose)

  useEffect(() => {
    onRequestCloseRef.current = onRequestClose
  })

  useEffect(() => {
    const dialog = dialogRef.current
    if (!open || !dialog) {
      return undefined
    }

    openDialogs.push(dialog)
    const previouslyFocused = document.activeElement
    const initialTarget = initialFocusRef?.current ?? dialog.querySelector(FIELDS)
      ?? dialog.querySelector(FOCUSABLE) ?? dialog
    initialTarget.focus()

    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'

    // Escucha en el documento para que Esc y Tab funcionen aunque el foco haya salido (por ejemplo, a un toast)
    function handleKeyDown(event) {
      if (openDialogs.at(-1) !== dialog) {
        return
      }
      if (event.key === 'Escape') {
        event.stopPropagation()
        onRequestCloseRef.current?.()
      } else if (event.key === 'Tab') {
        keepFocusInside(event, dialog)
      }
    }

    document.addEventListener('keydown', handleKeyDown)
    return () => {
      document.removeEventListener('keydown', handleKeyDown)
      openDialogs.splice(openDialogs.indexOf(dialog), 1)
      document.body.style.overflow = previousOverflow
      previouslyFocused?.focus?.()
    }
  }, [open, initialFocusRef])

  return dialogRef
}

function keepFocusInside(event, dialog) {
  const items = [...dialog.querySelectorAll(FOCUSABLE)]
  if (items.length === 0) {
    event.preventDefault()
    dialog.focus()
    return
  }
  const first = items[0]
  const last = items.at(-1)
  if (!dialog.contains(document.activeElement)) {
    event.preventDefault()
    first.focus()
  } else if (event.shiftKey && document.activeElement === first) {
    event.preventDefault()
    last.focus()
  } else if (!event.shiftKey && document.activeElement === last) {
    event.preventDefault()
    first.focus()
  }
}
