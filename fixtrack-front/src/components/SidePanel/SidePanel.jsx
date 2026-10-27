import { useId } from 'react'
import { createPortal } from 'react-dom'
import { MdClose } from 'react-icons/md'
import { useDialog } from '../../hooks/useDialog'
import { useGuardedClose } from '../../hooks/useGuardedClose'
import DiscardPrompt from '../DiscardPrompt/DiscardPrompt'
import './SidePanel.css'

// Panel que se desliza desde la derecha dejando visible el listado; tabs opcionales: [{ id, label }]
export default function SidePanel({
  open,
  title,
  subtitle,
  onClose,
  children,
  footer,
  tabs,
  activeTab,
  onTabChange,
  isDirty = false,
}) {
  const titleId = useId()
  const tabsId = useId()
  const { confirmingDiscard, requestClose, keepEditing, discard } = useGuardedClose({ isDirty, onClose })
  const dialogRef = useDialog({ open, onRequestClose: requestClose })

  if (!open) {
    return null
  }

  function handleOverlayMouseDown(event) {
    if (event.target === event.currentTarget) {
      requestClose()
    }
  }

  // Flechas izquierda y derecha para moverse entre pestañas
  function handleTabKeyDown(event, index) {
    const offset = { ArrowRight: 1, ArrowLeft: -1 }[event.key]
    if (!offset) {
      return
    }
    event.preventDefault()
    const next = tabs[(index + offset + tabs.length) % tabs.length]
    onTabChange(next.id)
    document.getElementById(`${tabsId}-${next.id}`)?.focus()
  }

  return createPortal(
    <div className="side-panel-overlay" onMouseDown={handleOverlayMouseDown}>
      <aside
        ref={dialogRef}
        className="side-panel"
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        tabIndex={-1}
      >
        <header className="side-panel__header">
          <div className="side-panel__heading">
            <h2 id={titleId} className="side-panel__title">{title}</h2>
            {subtitle && <div className="side-panel__subtitle">{subtitle}</div>}
          </div>
          <button type="button" className="btn btn--ghost btn--icon" aria-label="Cerrar" onClick={requestClose}>
            <MdClose aria-hidden="true" />
          </button>
        </header>

        {tabs?.length > 0 && (
          <div className="side-panel__tabs" role="tablist" aria-label={title}>
            {tabs.map((tab, index) => {
              const selected = tab.id === activeTab
              return (
                <button
                  key={tab.id}
                  id={`${tabsId}-${tab.id}`}
                  type="button"
                  role="tab"
                  aria-selected={selected}
                  aria-controls={`${tabsId}-panel`}
                  tabIndex={selected ? 0 : -1}
                  className={`side-panel__tab ${selected ? 'side-panel__tab--active' : ''}`}
                  onClick={() => onTabChange(tab.id)}
                  onKeyDown={(event) => handleTabKeyDown(event, index)}
                >
                  {tab.label}
                </button>
              )
            })}
          </div>
        )}

        {confirmingDiscard && <DiscardPrompt onKeepEditing={keepEditing} onDiscard={discard} />}

        <div
          className="side-panel__body"
          id={`${tabsId}-panel`}
          role={tabs?.length ? 'tabpanel' : undefined}
          aria-labelledby={tabs?.length ? `${tabsId}-${activeTab}` : undefined}
        >
          {children}
        </div>
        {footer && <footer className="side-panel__footer">{footer}</footer>}
      </aside>
    </div>,
    document.body,
  )
}
