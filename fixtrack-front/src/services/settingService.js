import { API_CONFIG } from '../config/apiConfig'

export async function listSettings() {
  const response = await API_CONFIG.get('/settings')
  return response.data
}

export async function updateSetting(key, value) {
  const response = await API_CONFIG.put(`/settings/${key}`, { value })
  return response.data
}
