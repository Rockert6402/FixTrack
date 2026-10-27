import { API_CONFIG } from '../config/apiConfig'

// Quita los filtros vacíos para no enviarlos
function clean(params) {
  return Object.fromEntries(Object.entries(params).filter(([, value]) => value !== '' && value != null && value !== false))
}

// Kárdex de solo lectura: { sparePartId, type, from, to } opcionales
export async function searchMovements({ sparePartId, type, from, to, page = 0, size = 20 } = {}) {
  const response = await API_CONFIG.get('/stock-movements', { params: clean({ sparePartId, type, from, to, page, size }) })
  return response.data
}
