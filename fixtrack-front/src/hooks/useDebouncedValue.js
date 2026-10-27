import { useEffect, useState } from 'react'

// Devuelve el valor solo cuando deja de cambiar durante delay ms; evita una petición por cada tecla
export function useDebouncedValue(value, delay = 350) {
  const [debounced, setDebounced] = useState(value)

  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay)
    return () => clearTimeout(timer)
  }, [value, delay])

  return debounced
}
