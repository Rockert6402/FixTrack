import { useId, useState } from 'react'
import { MdAccessibilityNew } from 'react-icons/md'
import AccessibilityDialog from './AccessibilityDialog'
import './AccessibilityPanel.css'

// Botón fijo que abre los ajustes de accesibilidad: tamaño de letra, contraste, tema, animaciones y foco
export default function AccessibilityPanel() {
  const [open, setOpen] = useState(false)
  const panelId = useId()

  return (
    <>
      <button
        type="button"
        className="accessibility-panel__trigger"
        aria-label="Opciones de accesibilidad"
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-controls={open ? panelId : undefined}
        onClick={() => setOpen((current) => !current)}
      >
        <MdAccessibilityNew aria-hidden="true" />
      </button>
      {open && <AccessibilityDialog id={panelId} onClose={() => setOpen(false)} />}
    </>
  )
}
