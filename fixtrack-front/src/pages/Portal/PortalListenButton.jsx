import { useEffect, useState } from 'react'
import { MdStop, MdVolumeUp } from 'react-icons/md'

const SUPPORTED = typeof window !== 'undefined' && 'speechSynthesis' in window && 'SpeechSynthesisUtterance' in window

// Voz en español disponible en el navegador, de preferencia la de Colombia
function spanishVoice() {
  const voices = window.speechSynthesis.getVoices().filter((voice) => voice.lang.toLowerCase().startsWith('es'))
  return voices.find((voice) => voice.lang === 'es-CO') ?? voices.find((voice) => voice.lang === 'es-US') ?? voices[0] ?? null
}

// Lee en voz alta el resumen de la orden; si el navegador no tiene síntesis de voz no se muestra
export default function PortalListenButton({ text }) {
  const [speaking, setSpeaking] = useState(false)

  useEffect(() => () => {
    if (SUPPORTED) {
      window.speechSynthesis.cancel()
    }
  }, [])

  if (!SUPPORTED) {
    return null
  }

  function handleClick() {
    window.speechSynthesis.cancel()
    if (speaking) {
      setSpeaking(false)
      return
    }
    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'es-CO'
    utterance.voice = spanishVoice()
    utterance.onend = () => setSpeaking(false)
    utterance.onerror = () => setSpeaking(false)
    setSpeaking(true)
    window.speechSynthesis.speak(utterance)
  }

  return (
    <button type="button" className="btn btn--secondary btn--block" onClick={handleClick}>
      {speaking ? <MdStop aria-hidden="true" /> : <MdVolumeUp aria-hidden="true" />}
      {speaking ? 'Detener lectura' : 'Escuchar'}
    </button>
  )
}
