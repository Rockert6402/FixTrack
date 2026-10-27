import { API_CONFIG } from '../config/apiConfig'

// Quita los filtros vacíos para no enviarlos
function clean(params) {
  return Object.fromEntries(Object.entries(params).filter(([, value]) => value !== '' && value != null && value !== false))
}

// Lo esperado del día por medio de pago; closing llega si ya se cerró hoy
export async function getToday() {
  const response = await API_CONFIG.get('/cash-closings/today')
  return response.data
}

export async function closeCash({ countedCash, notes }) {
  const response = await API_CONFIG.post('/cash-closings', { countedCash, notes })
  return response.data
}

export async function listClosings({ from, to, page = 0, size = 20 } = {}) {
  const response = await API_CONFIG.get('/cash-closings', { params: clean({ from, to, page, size }) })
  return response.data
}
