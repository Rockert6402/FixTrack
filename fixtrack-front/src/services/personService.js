import { API_CONFIG } from '../config/apiConfig'

export async function searchPeople({ search, role, active = true, page = 0, size = 20 }) {
  const response = await API_CONFIG.get('/people', {
    params: { search: search || undefined, role: role || undefined, active, page, size },
  })
  return response.data
}

// Búsqueda exacta por tipo y número de documento; responde 404 si no existe
export async function findPersonByDocument(documentType, documentNumber) {
  const response = await API_CONFIG.get('/people/lookup', { params: { documentType, documentNumber } })
  return response.data
}

export async function getPerson(id) {
  const response = await API_CONFIG.get(`/people/${id}`)
  return response.data
}

export async function createPerson(person) {
  const response = await API_CONFIG.post('/people', person)
  return response.data
}

export async function updatePerson(id, person) {
  const response = await API_CONFIG.put(`/people/${id}`, person)
  return response.data
}

export async function deactivatePerson(id) {
  await API_CONFIG.delete(`/people/${id}`)
}

export async function changeAvailability(id, availability) {
  const response = await API_CONFIG.patch(`/people/${id}/availability`, { availability })
  return response.data
}

export async function listSpecialties() {
  const response = await API_CONFIG.get('/specialties')
  return response.data
}

export async function createSpecialty(specialty) {
  const response = await API_CONFIG.post('/specialties', specialty)
  return response.data
}

export async function updateSpecialty(id, specialty) {
  const response = await API_CONFIG.put(`/specialties/${id}`, specialty)
  return response.data
}

export async function deleteSpecialty(id) {
  await API_CONFIG.delete(`/specialties/${id}`)
}
