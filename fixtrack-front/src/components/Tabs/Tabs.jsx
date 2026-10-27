import { useId } from 'react'
import './Tabs.css'

// Pestañas de una página; tabs: [{ id, label }] y children es el contenido de la pestaña activa
export default function Tabs({ tabs, activeTab, onChange, label, children }) {
  const baseId = useId()

  // Flechas izquierda y derecha para moverse entre pestañas
  function handleKeyDown(event, index) {
    const offset = { ArrowRight: 1, ArrowLeft: -1 }[event.key]
    if (!offset) {
      return
    }
    event.preventDefault()
    const next = tabs[(index + offset + tabs.length) % tabs.length]
    onChange(next.id)
    document.getElementById(`${baseId}-${next.id}`)?.focus()
  }

  return (
    <div className="tabs">
      <div className="tabs__list" role="tablist" aria-label={label}>
        {tabs.map((tab, index) => {
          const selected = tab.id === activeTab
          return (
            <button
              key={tab.id}
              id={`${baseId}-${tab.id}`}
              type="button"
              role="tab"
              aria-selected={selected}
              aria-controls={`${baseId}-panel`}
              tabIndex={selected ? 0 : -1}
              className={`tabs__tab ${selected ? 'tabs__tab--active' : ''}`}
              onClick={() => onChange(tab.id)}
              onKeyDown={(event) => handleKeyDown(event, index)}
            >
              {tab.label}
            </button>
          )
        })}
      </div>
      <div id={`${baseId}-panel`} role="tabpanel" aria-labelledby={`${baseId}-${activeTab}`} className="tabs__panel">
        {children}
      </div>
    </div>
  )
}
