import { useState } from 'react'
import { MdClose, MdLogout, MdMenu } from 'react-icons/md'
import { NavLink, useNavigate } from 'react-router'
import { useAuth } from '../../hooks/useAuth'
import { MENU_ITEMS } from '../../routes/menuConfig'
import { ROLE_LABELS } from '../../utils/labels'
import Logo from '../Logo/Logo'
import './Sidebar.css'

// Menú lateral oscuro con las opciones del rol; en celular se vuelve barra superior con menú desplegable
export default function Sidebar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const items = MENU_ITEMS.filter((item) => item.roles.includes(user.role))

  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <aside className={`sidebar ${menuOpen ? 'sidebar--open' : ''}`}>
      <div className="sidebar__top">
        <Logo variant="dark" />
        <button
          type="button"
          className="sidebar__toggle"
          aria-label={menuOpen ? 'Cerrar menú' : 'Abrir menú'}
          aria-expanded={menuOpen}
          aria-controls="sidebar-menu"
          onClick={() => setMenuOpen((open) => !open)}
        >
          {menuOpen ? <MdClose aria-hidden="true" /> : <MdMenu aria-hidden="true" />}
        </button>
      </div>

      <div id="sidebar-menu" className="sidebar__menu">
        <nav className="sidebar__nav" aria-label="Menú principal">
          <ul>
            {items.map(({ path, label, labels, icon: Icon }) => (
              <li key={path}>
                <NavLink
                  to={path}
                  end={path === '/'}
                  className={({ isActive }) => `sidebar__link ${isActive ? 'sidebar__link--active' : ''}`}
                  onClick={() => setMenuOpen(false)}
                >
                  <Icon className="sidebar__link-icon" aria-hidden="true" />
                  {labels?.[user.role] ?? label}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        <div className="sidebar__user">
          <div className="sidebar__user-info">
            <span className="sidebar__user-name">{user.fullName}</span>
            <span className="sidebar__user-role">{ROLE_LABELS[user.role] ?? user.role}</span>
          </div>
          <button type="button" className="sidebar__logout" onClick={handleLogout}>
            <MdLogout aria-hidden="true" />
            Cerrar sesión
          </button>
        </div>
      </div>
    </aside>
  )
}
