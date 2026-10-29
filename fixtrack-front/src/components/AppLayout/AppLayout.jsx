import { Outlet } from 'react-router'
import AccessibilityPanel from '../AccessibilityPanel/AccessibilityPanel'
import Sidebar from '../Sidebar/Sidebar'
import './AppLayout.css'

// Estructura de las pantallas internas: menú lateral fijo, contenido claro y ajustes de accesibilidad
export default function AppLayout() {
  return (
    <div className="app-layout">
      <a className="skip-link" href="#main-content">
        Saltar al contenido
      </a>
      <Sidebar />
      <main id="main-content" className="app-layout__content" tabIndex={-1}>
        <Outlet />
      </main>
      <AccessibilityPanel />
    </div>
  )
}
