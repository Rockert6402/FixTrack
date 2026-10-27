// Mensaje para el usuario a partir de un error de Axios con ProblemDetail del backend
export function getErrorMessage(error, fallback = 'No se pudo completar la operación. Intente de nuevo.') {
  if (!error?.response) {
    return 'No se pudo conectar con el servidor. Revise su conexión e intente de nuevo.'
  }
  return error.response.data?.detail ?? fallback
}

// Errores de validación por campo: { name: 'no debe estar vacío' }
export function getFieldErrors(error) {
  const errors = error?.response?.data?.errors ?? []
  return Object.fromEntries(errors.map(({ field, message }) => [field, message]))
}
