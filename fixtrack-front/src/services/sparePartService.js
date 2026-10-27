import { API_CONFIG } from '../config/apiConfig'

// Búsqueda sin mayúsculas ni tildes, filtros combinables, orden (campo,dirección) y paginación en el servidor
export async function searchSpareParts({ page = 0, size = 20, ...filters } = {}) {
  const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== '' && value != null && value !== false))
  const response = await API_CONFIG.get('/spare-parts', { params: { ...params, page, size } })
  return response.data
}

// Repuestos en el mínimo o agotados, primero los agotados
export async function listLowStock() {
  const response = await API_CONFIG.get('/spare-parts/low-stock')
  return response.data
}

export async function createSparePart(sparePart) {
  const response = await API_CONFIG.post('/spare-parts', sparePart)
  return response.data
}

// Edición parcial: los campos en null conservan su valor
export async function updateSparePart(id, changes) {
  const response = await API_CONFIG.patch(`/spare-parts/${id}`, changes)
  return response.data
}

// Entrada de mercancía; respuesta: { sparePart, movement }
export async function registerEntry(id, { quantity, unitCost, reason }) {
  const response = await API_CONFIG.post(`/spare-parts/${id}/entries`, { quantity, unitCost, reason })
  return response.data
}

export async function deactivateSparePart(id) {
  await API_CONFIG.delete(`/spare-parts/${id}`)
}
