import { lazy, Suspense } from 'react'
import { Route, Routes } from 'react-router'
import AppLayout from '../components/AppLayout/AppLayout'
import CashPage from '../pages/Cash/CashPage'
import CatalogsPage from '../pages/Catalogs/CatalogsPage'
import CustomersPage from '../pages/Customers/CustomersPage'
import DeliveryPage from '../pages/Delivery/DeliveryPage'
import HomePage from '../pages/Home/HomePage'
import InventoryPage from '../pages/Inventory/InventoryPage'
import LoginPage from '../pages/Login/LoginPage'
import NotFoundPage from '../pages/NotFound/NotFoundPage'
import OrdersPage from '../pages/Orders/OrdersPage'
import ChangePasswordPage from '../pages/Password/ChangePasswordPage'
import PendingModulePage from '../pages/PendingModule/PendingModulePage'
import PeoplePage from '../pages/People/PeoplePage'
import PartRequestsPage from '../pages/PartRequests/PartRequestsPage'
import PortalPage from '../pages/Portal/PortalPage'
import ProfilePage from '../pages/Profile/ProfilePage'
import SettingsPage from '../pages/Settings/SettingsPage'
import SuppliersPage from '../pages/Suppliers/SuppliersPage'
import { MENU_ITEMS } from './menuConfig'
import ProtectedRoute from './ProtectedRoute'

// Los informes cargan Recharts: se descargan solo al entrar a esa página
const ReportsPage = lazy(() => import('../pages/Reports/ReportsPage'))

// Pantallas ya construidas; los roles de cada ruta salen de menuConfig
const PAGES = {
  '/ordenes': OrdersPage,
  '/salida': DeliveryPage,
  '/clientes': CustomersPage,
  '/perfil': ProfilePage,
  '/personas': PeoplePage,
  '/catalogos': CatalogsPage,
  '/inventario': InventoryPage,
  '/proveedores': SuppliersPage,
  '/solicitudes': PartRequestsPage,
  '/caja': CashPage,
  '/parametros': SettingsPage,
  '/informes': ReportsPage,
}

const MODULE_ROUTES = MENU_ITEMS.filter((item) => item.path !== '/')

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/consulta" element={<PortalPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<HomePage />} />
          <Route path="/perfil/contrasena" element={<ChangePasswordPage />} />
          {MODULE_ROUTES.map(({ path, label, roles }) => {
            // Los módulos sin pantalla muestran la página provisional hasta su sprint
            const Page = PAGES[path]
            return (
              <Route
                key={path}
                path={path}
                element={
                  <ProtectedRoute roles={roles}>
                    {Page ? (
                      <Suspense fallback={<p className="page-loading" role="status">Cargando…</p>}>
                        <Page />
                      </Suspense>
                    ) : <PendingModulePage title={label} />}
                  </ProtectedRoute>
                }
              />
            )
          })}
          {/* Dirección inexistente: con sesión se avisa dentro del layout; sin sesión ProtectedRoute lleva al login */}
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Route>
    </Routes>
  )
}
