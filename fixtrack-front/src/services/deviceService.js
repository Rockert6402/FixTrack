import { API_CONFIG } from '../config/apiConfig'

export async function listDevicesByOwner(ownerId) {
  const response = await API_CONFIG.get('/devices', { params: { ownerId } })
  return response.data
}
