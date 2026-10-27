import './DiscardPrompt.css'

// Aviso dentro del modal o panel cuando se intenta cerrar con cambios sin guardar
export default function DiscardPrompt({ onKeepEditing, onDiscard }) {
  return (
    <div className="discard-prompt" role="alert">
      <p>Hay cambios sin guardar. ¿Desea descartarlos?</p>
      <div className="discard-prompt__actions">
        <button type="button" className="btn btn--secondary" onClick={onKeepEditing}>
          Seguir editando
        </button>
        <button type="button" className="btn btn--danger" onClick={onDiscard}>
          Descartar cambios
        </button>
      </div>
    </div>
  )
}
