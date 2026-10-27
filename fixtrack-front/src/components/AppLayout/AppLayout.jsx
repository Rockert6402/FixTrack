import { Outlet } from 'react-router'
import Sidebar from '../Sidebar/Sidebar'
import './AppLayout.css'

// Estructura de las pantallas internas: menú lateral fijo y contenido claro
export default function AppLayout() {
  return (
    <div className="app-layout">
      <a className="app-layout__skip-link" href="#main-content">
        Saltar al contenido
      </a>
      <Sidebar />
      <main id="main-content" className="app-layout__content" tabIndex={-1}>
        <Outlet />
      </main>
    </div>
  )
}
