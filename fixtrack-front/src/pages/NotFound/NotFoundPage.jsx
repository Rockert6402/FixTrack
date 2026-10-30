import { MdHome } from 'react-icons/md'
import { Link } from 'react-router'
import PageHeader from '../../components/PageHeader/PageHeader'
import './NotFoundPage.css'

// Dirección que no existe dentro del sistema: lo dice y ofrece volver al Inicio
export default function NotFoundPage() {
  return (
    <div className="page">
      <PageHeader title="Página no encontrada" />
      <div className="not-found">
        <p>La dirección que abrió no existe o ya no está disponible.</p>
        <Link to="/" className="btn btn--primary">
          <MdHome aria-hidden="true" />
          Ir al Inicio
        </Link>
      </div>
    </div>
  )
}
