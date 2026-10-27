import { useCallback, useEffect, useState } from 'react'
import { getErrorMessage } from '../utils/errors'

// Carga datos del backend con estado de carga y error; vuelve a cargar cuando cambian deps o se llama reload
export function useRemoteData(loader, deps) {
  const [state, setState] = useState({ data: null, loading: true, error: null })
  const [version, setVersion] = useState(0)

  useEffect(() => {
    let active = true
    setState((current) => ({ ...current, loading: true, error: null }))
    loader()
      .then((data) => active && setState({ data, loading: false, error: null }))
      .catch((error) => active && setState((current) => ({ ...current, loading: false, error: getErrorMessage(error) })))
    return () => {
      active = false
    }
    // loader cambia en cada render; las dependencias reales llegan en deps
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, version])

  const reload = useCallback(() => setVersion((current) => current + 1), [])
  return { ...state, reload }
}
