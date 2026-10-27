import { API_CONFIG } from '../config/apiConfig'

// Respuesta: { total, paid, balance, refundDue, payments }
export async function listPayments(interventionId) {
  const response = await API_CONFIG.get(`/interventions/${interventionId}/payments`)
  return response.data
}

// type: DEPOSIT, FINAL o REFUND; method: CASH, TRANSFER o CARD
export async function registerPayment(interventionId, { type, method, amount }) {
  const response = await API_CONFIG.post(`/interventions/${interventionId}/payments`, { type, method, amount })
  return response.data
}
