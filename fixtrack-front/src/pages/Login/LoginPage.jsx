import { useState } from 'react'
import { MdSearch } from 'react-icons/md'
import { Link, Navigate, useLocation, useNavigate } from 'react-router'
import PublicLayout from '../../components/PublicLayout/PublicLayout'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import { getErrorMessage, getFieldErrors } from '../../utils/errors'
import './LoginPage.css'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validate({ email, password }) {
  const errors = {}
  if (!email.trim()) {
    errors.email = 'Ingrese su correo.'
  } else if (!EMAIL_PATTERN.test(email.trim())) {
    errors.email = 'Ingrese un correo válido.'
  }
  if (!password) {
    errors.password = 'Ingrese su contraseña.'
  }
  return errors
}

// Inicio de sesión de los usuarios internos, con acceso al portal de consulta del cliente
export default function LoginPage() {
  const { isAuthenticated, sessionExpired, login } = useAuth()
  const { showError } = useToast()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [fieldErrors, setFieldErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const redirectTo = location.state?.from?.pathname ?? '/'

  if (isAuthenticated) {
    return <Navigate to={redirectTo} replace />
  }

  function handleChange(event) {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const errors = validate(form)
    setFieldErrors(errors)
    if (Object.keys(errors).length > 0) {
      return
    }

    setSubmitting(true)
    try {
      await login({ email: form.email.trim(), password: form.password })
      navigate(redirectTo, { replace: true })
    } catch (error) {
      setFieldErrors(getFieldErrors(error))
      showError(getErrorMessage(error, 'Correo o contraseña incorrectos.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <PublicLayout title="Iniciar sesión" description="Ingrese con su correo y contraseña de FixTrack.">
      {sessionExpired && (
        <p className="login-page__notice" role="status">Su sesión expiró o ya no es válida. Ingrese de nuevo para continuar.</p>
      )}
      <form className="login-form" onSubmit={handleSubmit} noValidate>
        <div className="form-field">
          <label className="form-label" htmlFor="login-email">Correo</label>
          <input
            id="login-email"
            name="email"
            type="email"
            className="form-input"
            autoComplete="username"
            value={form.email}
            onChange={handleChange}
            aria-invalid={Boolean(fieldErrors.email)}
            aria-describedby={fieldErrors.email ? 'login-email-error' : undefined}
          />
          {fieldErrors.email && <span id="login-email-error" className="form-error">{fieldErrors.email}</span>}
        </div>

        <div className="form-field">
          <label className="form-label" htmlFor="login-password">Contraseña</label>
          <input
            id="login-password"
            name="password"
            type="password"
            className="form-input"
            autoComplete="current-password"
            value={form.password}
            onChange={handleChange}
            aria-invalid={Boolean(fieldErrors.password)}
            aria-describedby={fieldErrors.password ? 'login-password-error' : undefined}
          />
          {fieldErrors.password && (
            <span id="login-password-error" className="form-error">{fieldErrors.password}</span>
          )}
        </div>

        <button type="submit" className="btn btn--primary btn--block" disabled={submitting}>
          {submitting ? 'Ingresando…' : 'Ingresar'}
        </button>
      </form>

      <div className="login-page__portal">
        <p className="text-sm text-muted">¿Dejó un equipo en reparación?</p>
        <Link to="/consulta" className="btn btn--secondary btn--block">
          <MdSearch aria-hidden="true" />
          Consulta tu equipo
        </Link>
      </div>
    </PublicLayout>
  )
}
