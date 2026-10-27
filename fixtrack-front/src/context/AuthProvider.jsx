import { useCallback, useEffect, useMemo, useState } from 'react'
import { UNAUTHORIZED_EVENT } from '../config/apiConfig'
import * as authService from '../services/authService'
import { AuthContext } from './AuthContext'
import { clearSession, getSession, saveSession } from './session'

// Sesión del usuario interno: inicia, cierra y se pierde sola si el backend rechaza las credenciales
export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => getSession())
  // true cuando el usuario cerró sesión a propósito: el siguiente login no vuelve a la página del anterior
  const [endedByUser, setEndedByUser] = useState(false)
  // true cuando el backend rechazó el token: el login explica que debe ingresar de nuevo
  const [sessionExpired, setSessionExpired] = useState(false)

  // Guarda el token JWT; al vencer, el backend responde 401 y la sesión se cierra sola
  const login = useCallback(async ({ email, password }) => {
    const { token, user } = await authService.login({ email, password })
    const nextSession = { authorization: `Bearer ${token}`, user }
    saveSession(nextSession)
    setSession(nextSession)
    setEndedByUser(false)
    setSessionExpired(false)
    return user
  }, [])

  const logout = useCallback(() => {
    clearSession()
    setEndedByUser(true)
    setSession(null)
  }, [])

  useEffect(() => {
    const handleUnauthorized = () => {
      setSession(null)
      setSessionExpired(true)
    }
    window.addEventListener(UNAUTHORIZED_EVENT, handleUnauthorized)
    return () => window.removeEventListener(UNAUTHORIZED_EVENT, handleUnauthorized)
  }, [])

  const value = useMemo(() => {
    const user = session?.user ?? null
    return {
      user,
      isAuthenticated: Boolean(user && session?.authorization),
      endedByUser,
      sessionExpired,
      hasRole: (...roles) => Boolean(user) && roles.includes(user.role),
      login,
      logout,
    }
  }, [session, endedByUser, sessionExpired, login, logout])

  return <AuthContext value={value}>{children}</AuthContext>
}
