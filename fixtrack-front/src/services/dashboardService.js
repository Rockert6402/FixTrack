import { API_CONFIG } from '../config/apiConfig'

// Panel de Inicio del usuario de la sesión; la forma depende del rol:
// ADMIN { counters, recentOrders, stockAlerts }; ADVISOR { counters, recentOrders, technicians };
// COORDINATOR lo del asesor más stockAlerts; TECHNICIAN { counters, openOrders }
export async function getDashboard() {
  const response = await API_CONFIG.get('/dashboard')
  return response.data
}
