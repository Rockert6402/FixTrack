import { Navigate, Route, Routes } from 'react-router'
import AppLayout from '../components/AppLayout/AppLayout'
import CatalogsPage from '../pages/Catalogs/CatalogsPage'
import DeliveryPage from '../pages/Delivery/DeliveryPage'
import HomePage from '../pages/Home/HomePage'
import InventoryPage from '../pages/Inventory/InventoryPage'
import LoginPage from '../pages/Login/LoginPage'
import OrdersPage from '../pages/Orders/OrdersPage'
import ChangePasswordPage from '../pages/Password/ChangePasswordPage'
import PendingModulePage from '../pages/PendingModule/PendingModulePage'
import PeoplePage from '../pages/People/PeoplePage'
import PortalPage from '../pages/Portal/PortalPage'
import ProfilePage from '../pages/Profile/ProfilePage'
import { MENU_ITEMS } from './menuConfig'
import ProtectedRoute from './ProtectedRoute'

// Pantallas ya construidas; los roles de cada ruta salen de menuConfig
const PAGES = {
  '/ordenes': OrdersPage,
  '/salida': DeliveryPage,
  '/perfil': ProfilePage,
  '/personas': PeoplePage,
  '/catalogos': CatalogsPage,
  '/inventario': InventoryPage,
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
                    {Page ? <Page /> : <PendingModulePage title={label} />}
                  </ProtectedRoute>
                }
              />
            )
          })}
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
