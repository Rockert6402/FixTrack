import { MdClose, MdRestartAlt } from 'react-icons/md'
import { FONT_SCALES } from '../../context/AccessibilityContext'
import { useAccessibility } from '../../hooks/useAccessibility'
import { useDialog } from '../../hooks/useDialog'
import './AccessibilityPanel.css'

const TOGGLES = [
  { name: 'highContrast', label: 'Alto contraste', hint: 'Colores más fuertes e íconos en los estados.' },
  { name: 'darkMode', label: 'Modo oscuro', hint: 'Fondo oscuro con texto claro.' },
  { name: 'reduceMotion', label: 'Reducir animaciones', hint: 'Quita los movimientos de ventanas y mensajes.' },
  { name: 'highlightFocus', label: 'Resaltar el foco', hint: 'Marca más visible al navegar con el teclado.' },
]

// Ventana de ajustes; se cierra con Esc, con su botón o al hacer clic afuera, y devuelve el foco al botón que la abrió
export default function AccessibilityDialog({ id, onClose }) {
  const { settings, updateSetting, resetSettings } = useAccessibility()
  const dialogRef = useDialog({ open: true, onRequestClose: onClose })
  const titleId = `${id}-title`

  return (
    <div className="accessibility-panel__backdrop" onClick={onClose}>
      <div
        ref={dialogRef}
        id={id}
        className="accessibility-panel"
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        tabIndex={-1}
        onClick={(event) => event.stopPropagation()}
      >
        <div className="accessibility-panel__header">
          <h2 id={titleId} className="accessibility-panel__title">Accesibilidad</h2>
          <button type="button" className="btn btn--ghost btn--icon" aria-label="Cerrar opciones de accesibilidad" onClick={onClose}>
            <MdClose aria-hidden="true" />
          </button>
        </div>

        <fieldset className="accessibility-panel__group">
          <legend className="form-label">Tamaño de letra</legend>
          <div className="accessibility-panel__scales">
            {FONT_SCALES.map((scale) => (
              <label key={scale} className="accessibility-panel__scale">
                <input
                  type="radio"
                  name={`${id}-font-scale`}
                  className="accessibility-panel__scale-input"
                  checked={settings.fontScale === scale}
                  onChange={() => updateSetting('fontScale', scale)}
                />
                <span className="accessibility-panel__scale-text">{Math.round(scale * 100)} %</span>
              </label>
            ))}
          </div>
        </fieldset>

        <ul className="accessibility-panel__toggles">
          {TOGGLES.map(({ name, label, hint }) => (
            <li key={name}>
              <label className="accessibility-panel__toggle">
                <span className="accessibility-panel__toggle-text">
                  <span className="accessibility-panel__toggle-label">{label}</span>
                  <span id={`${id}-${name}-hint`} className="form-hint">{hint}</span>
                </span>
                <input
                  type="checkbox"
                  role="switch"
                  className="accessibility-panel__switch"
                  checked={settings[name]}
                  aria-describedby={`${id}-${name}-hint`}
                  onChange={(event) => updateSetting(name, event.target.checked)}
                />
              </label>
            </li>
          ))}
        </ul>

        <button type="button" className="btn btn--secondary btn--block" onClick={resetSettings}>
          <MdRestartAlt aria-hidden="true" />
          Restablecer
        </button>
      </div>
    </div>
  )
}
