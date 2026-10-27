import PageHeader from '../../components/PageHeader/PageHeader'
import './PendingModulePage.css'

// Página provisional de los módulos del menú que se construyen en sprints siguientes
export default function PendingModulePage({ title }) {
  return (
    <div className="page">
      <PageHeader title={title} />
      <p className="pending-module">Este módulo se habilita en su sprint.</p>
    </div>
  )
}
