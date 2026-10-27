import { useState } from 'react'
import { MdLock } from 'react-icons/md'
import { Link } from 'react-router'
import PageHeader from '../../components/PageHeader/PageHeader'
import { useAuth } from '../../hooks/useAuth'
import { useRemoteData } from '../../hooks/useRemoteData'
import { useToast } from '../../hooks/useToast'
import * as personService from '../../services/personService'
import { getErrorMessage } from '../../utils/errors'
import { displayOrNT } from '../../utils/format'
import { AVAILABILITY_LABELS, DOCUMENT_TYPE_LABELS, ROLE_LABELS, ROLES } from '../../utils/labels'
import './ProfilePage.css'

// Datos del usuario que inició sesión y, para el técnico, el cambio de su disponibilidad
export default function ProfilePage() {
  const { user } = useAuth()
  const { showError, showSuccess } = useToast()
  const { data: person, loading, error, reload } = useRemoteData(() => personService.getPerson(user.id), [user.id])
  const [availability, setAvailability] = useState(null)
  const [saving, setSaving] = useState(false)

  const currentAvailability = availability ?? person?.availability

  async function handleAvailability(value) {
    if (value === currentAvailability || saving) {
      return
    }
    setSaving(true)
    try {
      const updated = await personService.changeAvailability(user.id, value)
      setAvailability(updated.availability)
      showSuccess(`Su disponibilidad ahora es «${AVAILABILITY_LABELS[updated.availability]}».`)
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="page">
      <PageHeader
        title="Mi perfil"
        description="Sus datos en FixTrack."
        actions={
          <Link to="/perfil/contrasena" className="btn btn--secondary">
            <MdLock aria-hidden="true" />
            Cambiar contraseña
          </Link>
        }
      />

      {loading && <p className="text-muted">Cargando…</p>}
      {error && (
        <p className="profile-page__error">
          {error}{' '}
          <button type="button" className="btn btn--secondary" onClick={reload}>Reintentar</button>
        </p>
      )}

      {person && (
        <div className="profile-page__grid">
          <section className="profile-page__card" aria-labelledby="profile-data-title">
            <h2 id="profile-data-title" className="profile-page__card-title">Datos personales</h2>
            <dl className="profile-page__data">
              <div><dt>Nombre</dt><dd>{person.fullName}</dd></div>
              <div><dt>Documento</dt><dd>{DOCUMENT_TYPE_LABELS[person.documentType] ?? person.documentType} {person.documentNumber ?? 'sin número'}</dd></div>
              <div><dt>Correo</dt><dd>{displayOrNT(person.email)}</dd></div>
              <div><dt>Celular</dt><dd>{displayOrNT(person.phone)}</dd></div>
              <div><dt>Rol</dt><dd>{ROLE_LABELS[person.role]}</dd></div>
              {person.role === ROLES.TECHNICIAN && (
                <div>
                  <dt>Especialidades</dt>
                  <dd>{person.specialties.length ? person.specialties.map((specialty) => specialty.name).join(', ') : 'Sin especialidades'}</dd>
                </div>
              )}
            </dl>
          </section>

          {person.role === ROLES.TECHNICIAN && (
            <section className="profile-page__card" aria-labelledby="profile-availability-title">
              <h2 id="profile-availability-title" className="profile-page__card-title">Disponibilidad</h2>
              <p className="text-sm text-muted">Indica si puede recibir órdenes nuevas.</p>
              <div className="profile-page__availability" role="radiogroup" aria-labelledby="profile-availability-title">
                {Object.entries(AVAILABILITY_LABELS).map(([value, label]) => (
                  <button
                    key={value}
                    type="button"
                    role="radio"
                    aria-checked={currentAvailability === value}
                    className={`profile-page__option profile-page__option--${value.toLowerCase()} ${currentAvailability === value ? 'profile-page__option--selected' : ''}`}
                    disabled={saving}
                    onClick={() => handleAvailability(value)}
                  >
                    {label}
                  </button>
                ))}
              </div>
              {!currentAvailability && <p className="form-hint">Todavía no ha indicado su disponibilidad.</p>}
            </section>
          )}
        </div>
      )}
    </div>
  )
}
