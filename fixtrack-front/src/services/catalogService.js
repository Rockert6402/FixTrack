import { API_CONFIG } from '../config/apiConfig'

export async function listBrands() {
  const response = await API_CONFIG.get('/brands')
  return response.data
}

export async function createBrand(brand) {
  const response = await API_CONFIG.post('/brands', brand)
  return response.data
}

export async function updateBrand(id, brand) {
  const response = await API_CONFIG.put(`/brands/${id}`, brand)
  return response.data
}

export async function deleteBrand(id) {
  await API_CONFIG.delete(`/brands/${id}`)
}

export async function listDeviceModels({ brandId, deviceType } = {}) {
  const response = await API_CONFIG.get('/device-models', {
    params: { brandId: brandId || undefined, deviceType: deviceType || undefined },
  })
  return response.data
}

export async function createDeviceModel(model) {
  const response = await API_CONFIG.post('/device-models', model)
  return response.data
}

export async function updateDeviceModel(id, model) {
  const response = await API_CONFIG.put(`/device-models/${id}`, model)
  return response.data
}

export async function deleteDeviceModel(id) {
  await API_CONFIG.delete(`/device-models/${id}`)
}

export async function listPartTypes() {
  const response = await API_CONFIG.get('/part-types')
  return response.data
}

export async function createPartType(partType) {
  const response = await API_CONFIG.post('/part-types', partType)
  return response.data
}

export async function updatePartType(id, partType) {
  const response = await API_CONFIG.put(`/part-types/${id}`, partType)
  return response.data
}

export async function deletePartType(id) {
  await API_CONFIG.delete(`/part-types/${id}`)
}
