import { useCallback, useLayoutEffect, useMemo, useState } from 'react'
import { AccessibilityContext, FONT_SCALES } from './AccessibilityContext'

const STORAGE_KEY = 'fixtrack.accessibility'

function prefers(query) {
  return typeof window !== 'undefined' && window.matchMedia?.(query).matches === true
}

// Valores iniciales cuando el usuario no ha guardado nada: los toma de la preferencia del sistema
function systemDefaults() {
  return {
    fontScale: 1,
    highContrast: prefers('(prefers-contrast: more)'),
    darkMode: prefers('(prefers-color-scheme: dark)'),
    reduceMotion: prefers('(prefers-reduced-motion: reduce)'),
    highlightFocus: false,
  }
}

// Lee los ajustes guardados y descarta los valores que no son válidos
function loadSettings() {
  const defaults = systemDefaults()
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY))
    if (!saved || typeof saved !== 'object') {
      return defaults
    }
    return {
      fontScale: FONT_SCALES.includes(saved.fontScale) ? saved.fontScale : defaults.fontScale,
      highContrast: typeof saved.highContrast === 'boolean' ? saved.highContrast : defaults.highContrast,
      darkMode: typeof saved.darkMode === 'boolean' ? saved.darkMode : defaults.darkMode,
      reduceMotion: typeof saved.reduceMotion === 'boolean' ? saved.reduceMotion : defaults.reduceMotion,
      highlightFocus: typeof saved.highlightFocus === 'boolean' ? saved.highlightFocus : defaults.highlightFocus,
    }
  } catch {
    return defaults
  }
}

// Ajustes de accesibilidad del usuario: se guardan en el navegador y se aplican como atributos de <html>
export function AccessibilityProvider({ children }) {
  const [settings, setSettings] = useState(loadSettings)

  useLayoutEffect(() => {
    const root = document.documentElement
    root.dataset.fontScale = String(settings.fontScale)
    root.dataset.contrast = settings.highContrast ? 'high' : 'normal'
    root.dataset.theme = settings.darkMode ? 'dark' : 'light'
    root.dataset.motion = settings.reduceMotion ? 'reduce' : 'normal'
    root.dataset.focus = settings.highlightFocus ? 'strong' : 'normal'
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(settings))
    } catch {
      // Sin almacenamiento (modo privado o bloqueado) los ajustes duran solo esta visita
    }
  }, [settings])

  const updateSetting = useCallback((name, value) => {
    setSettings((current) => ({ ...current, [name]: value }))
  }, [])

  // Restablecer vuelve a las preferencias del sistema y borra lo guardado
  const resetSettings = useCallback(() => {
    try {
      localStorage.removeItem(STORAGE_KEY)
    } catch {
      // Nada que borrar si el almacenamiento no está disponible
    }
    setSettings(systemDefaults())
  }, [])

  const value = useMemo(() => ({ settings, updateSetting, resetSettings }), [settings, updateSetting, resetSettings])

  return <AccessibilityContext value={value}>{children}</AccessibilityContext>
}
