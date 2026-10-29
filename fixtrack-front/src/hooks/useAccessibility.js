import { useContext } from 'react'
import { AccessibilityContext } from '../context/AccessibilityContext'

export function useAccessibility() {
  const context = useContext(AccessibilityContext)
  if (!context) {
    throw new Error('useAccessibility debe usarse dentro de AccessibilityProvider')
  }
  return context
}
