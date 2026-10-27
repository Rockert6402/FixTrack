import { API_CONFIG } from '../config/apiConfig'

// Quita los filtros vacíos para no enviarlos
function clean(params) {
  return Object.fromEntries(Object.entries(params).filter(([, value]) => value !== '' && value != null && value !== false))
}

export async function searchSuppliers({ search, active = true, page = 0, size = 20 } = {}) {
  const response = await API_CONFIG.get('/suppliers', { params: clean({ search, active, page, size }) })
  return response.data
}

export async function createSupplier(supplier) {
  const response = await API_CONFIG.post('/suppliers', supplier)
  return response.data
}

export async function updateSupplier(id, supplier) {
  const response = await API_CONFIG.put(`/suppliers/${id}`, supplier)
  return response.data
}

export async function deactivateSupplier(id) {
  await API_CONFIG.delete(`/suppliers/${id}`)
}
