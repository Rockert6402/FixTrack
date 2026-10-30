import { useState } from 'react'
import NameCatalog from '../../components/NameCatalog/NameCatalog'
import PageHeader from '../../components/PageHeader/PageHeader'
import Tabs from '../../components/Tabs/Tabs'
import * as personService from '../../services/personService'
import PeopleList from './PeopleList'
import './PeoplePage.css'

const TABS = [
  { id: 'people', label: 'Usuarios' },
  { id: 'specialties', label: 'Especialidades' },
]

const SPECIALTY_SERVICE = {
  list: personService.listSpecialties,
  create: personService.createSpecialty,
  update: personService.updateSpecialty,
  remove: personService.deleteSpecialty,
}

// Personal interno (usuarios, roles y accesos) y especialidades de los técnicos, del administrador
export default function PeoplePage() {
  const [activeTab, setActiveTab] = useState('people')

  return (
    <div className="page">
      <PageHeader title="Personal" description="Usuarios internos, sus roles y accesos, y las especialidades de los técnicos. Los clientes se gestionan en Clientes y equipos." />
      <Tabs tabs={TABS} activeTab={activeTab} onChange={setActiveTab} label="Personal y especialidades">
        {activeTab === 'people' ? (
          <PeopleList />
        ) : (
          <NameCatalog title="Especialidades" itemLabel="Especialidad" createLabel="Nueva especialidad" service={SPECIALTY_SERVICE} />
        )}
      </Tabs>
    </div>
  )
}
