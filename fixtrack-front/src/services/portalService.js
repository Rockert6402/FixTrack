import { API_CONFIG } from '../config/apiConfig'

// Consulta pública del cliente con el código de la orden y su clave de 4 dígitos
// Respuesta esperada: { code, status, device, estimatedDeliveryDate, estimatedPrice, totalPrice }
export async function lookupOrder({ code, accessKey }) {
  const response = await API_CONFIG.post('/public/orders/lookup', { code, accessKey }, { skipAuth: true })
  return response.data
}
