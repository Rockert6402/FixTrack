const SESSION_KEY = 'fixtrack.session'

// Lee la sesión guardada (token y usuario); devuelve null si no hay o el navegador bloquea el almacenamiento
export function getSession() {
  try {
    const stored = sessionStorage.getItem(SESSION_KEY)
    return stored ? JSON.parse(stored) : null
  } catch {
    return null
  }
}

export function saveSession(session) {
  try {
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(session))
  } catch {
    // Sin almacenamiento la sesión dura solo mientras la página esté abierta
  }
}

export function clearSession() {
  try {
    sessionStorage.removeItem(SESSION_KEY)
  } catch {
    // Nada que borrar si el almacenamiento está bloqueado
  }
}
