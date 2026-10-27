import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import ToastContainer from '../components/Toast/ToastContainer'
import { ToastContext } from './ToastContext'

const DURATION_MS = { error: 7000, success: 4000, info: 5000 }

// Cola de mensajes emergentes; cada uno se cierra solo o con su botón
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const nextId = useRef(0)
  const timers = useRef(new Map())

  const dismiss = useCallback((id) => {
    clearTimeout(timers.current.get(id))
    timers.current.delete(id)
    setToasts((current) => current.filter((toast) => toast.id !== id))
  }, [])

  const show = useCallback((type, message) => {
    const id = nextId.current++
    setToasts((current) => [...current, { id, type, message }])
    timers.current.set(id, setTimeout(() => dismiss(id), DURATION_MS[type]))
  }, [dismiss])

  useEffect(() => {
    const pending = timers.current
    return () => pending.forEach(clearTimeout)
  }, [])

  const value = useMemo(() => ({
    showError: (message) => show('error', message),
    showSuccess: (message) => show('success', message),
    showInfo: (message) => show('info', message),
  }), [show])

  return (
    <ToastContext value={value}>
      {children}
      <ToastContainer toasts={toasts} onDismiss={dismiss} />
    </ToastContext>
  )
}
