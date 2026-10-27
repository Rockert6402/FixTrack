import { API_CONFIG } from '../config/apiConfig'

// Respuesta: { token, expiresAt, user: { id, fullName, email, role } }
export async function login(credentials) {
  const response = await API_CONFIG.post('/auth/login', credentials, { skipAuth: true })
  return response.data
}

// Cambia la contraseña propia; el backend exige la actual y rechaza que la nueva sea igual
export async function changePassword({ currentPassword, newPassword }) {
  await API_CONFIG.patch('/auth/password', { currentPassword, newPassword })
}
