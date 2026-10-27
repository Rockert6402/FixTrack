import Logo from '../Logo/Logo'
import './PublicLayout.css'

// Pantalla centrada de las rutas públicas (login y portal del cliente)
export default function PublicLayout({ title, description, children, footer }) {
  return (
    <main className="public-layout">
      <div className="public-layout__card">
        <Logo />
        <header className="public-layout__header">
          <h1 className="public-layout__title">{title}</h1>
          {description && <p className="public-layout__description">{description}</p>}
        </header>
        {children}
      </div>
      {footer && <div className="public-layout__footer">{footer}</div>}
    </main>
  )
}
