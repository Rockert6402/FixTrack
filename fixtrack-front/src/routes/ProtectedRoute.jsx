import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from '../hooks/useAuth'

// Exige sesión y, si se indican, uno de los roles; sin sesión va al login y sin rol vuelve al Inicio
// Si la sesión venció, el login regresa a esta página; si el usuario la cerró, el siguiente entra al Inicio
export default function ProtectedRoute({ roles, children }) {
  const { isAuthenticated, endedByUser, hasRole } = useAuth()
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={endedByUser ? undefined : { from: location }} />
  }
  if (roles && !hasRole(...roles)) {
    return <Navigate to="/" replace />
  }
  return children ?? <Outlet />
}
