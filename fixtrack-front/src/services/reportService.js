import { API_CONFIG } from '../config/apiConfig'

// Quita los filtros vacíos para no enviarlos
function clean(params) {
  return Object.fromEntries(Object.entries(params).filter(([, value]) => value !== '' && value != null && value !== false))
}

export async function inventoryReport(filters = {}) {
  const response = await API_CONFIG.get('/reports/inventory', { params: clean(filters) })
  return response.data
}

export async function maintenanceReport({ from, to }) {
  const response = await API_CONFIG.get('/reports/maintenance', { params: { from, to } })
  return response.data
}

// El técnico recibe solo sus órdenes aunque envíe technicianId
export async function profitReport({ from, to, technicianId }) {
  const response = await API_CONFIG.get('/reports/profit', { params: clean({ from, to, technicianId }) })
  return response.data
}

export async function exportProfit({ from, to, technicianId }) {
  const response = await API_CONFIG.get('/reports/profit/export', { params: clean({ from, to, technicianId }), responseType: 'blob' })
  return response.data
}
