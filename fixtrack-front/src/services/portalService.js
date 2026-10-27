import { API_CONFIG } from '../config/apiConfig'

// Consulta pública con el código de la orden (o de una garantía) y la clave de 4 dígitos.
// Respuesta: { code, deviceType, brandName, modelName, status, repairOutcome, receivedAt, estimatedDeliveryDate,
// deliveredAt, estimatedPrice, total, warrantyDays, warrantyUntil, warrantyActive, timeline, warranties }
export async function lookupOrder({ code, accessKey }) {
  const response = await API_CONFIG.post('/public/interventions/lookup', { code, accessKey }, { skipAuth: true })
  return response.data
}

// Comprobante en PDF de la orden o garantía del código; type: 'intake' o 'delivery'
export async function downloadReceipt(type, { code, accessKey }) {
  try {
    const response = await API_CONFIG.post(`/public/interventions/receipts/${type}`, { code, accessKey }, {
      skipAuth: true,
      responseType: 'blob',
    })
    return response.data
  } catch (error) {
    // Con responseType blob el ProblemDetail llega como Blob: se convierte para poder mostrar su mensaje
    if (error.response?.data instanceof Blob) {
      try {
        error.response.data = JSON.parse(await error.response.data.text())
      } catch {
        // Queda el mensaje genérico
      }
    }
    throw error
  }
}
