import { useState } from 'react'
import NameCatalog from '../../components/NameCatalog/NameCatalog'
import PageHeader from '../../components/PageHeader/PageHeader'
import Tabs from '../../components/Tabs/Tabs'
import * as catalogService from '../../services/catalogService'
import DeviceModelsTab from './DeviceModelsTab'
import './CatalogsPage.css'

const TABS = [
  { id: 'brands', label: 'Marcas' },
  { id: 'models', label: 'Modelos' },
  { id: 'partTypes', label: 'Tipos de repuesto' },
]

const BRAND_SERVICE = {
  list: catalogService.listBrands,
  create: catalogService.createBrand,
  update: catalogService.updateBrand,
  remove: catalogService.deleteBrand,
}

const PART_TYPE_SERVICE = {
  list: catalogService.listPartTypes,
  create: catalogService.createPartType,
  update: catalogService.updatePartType,
  remove: catalogService.deletePartType,
}

// Catálogos de equipos y repuestos que gestionan el coordinador y el administrador
export default function CatalogsPage() {
  const [activeTab, setActiveTab] = useState('brands')

  return (
    <div className="page">
      <PageHeader title="Catálogos" description="Marcas, modelos de equipo y tipos de repuesto." />
      <Tabs tabs={TABS} activeTab={activeTab} onChange={setActiveTab} label="Catálogos">
        {activeTab === 'brands' && (
          <NameCatalog title="Marcas" itemLabel="Marca" createLabel="Nueva marca" service={BRAND_SERVICE} />
        )}
        {activeTab === 'models' && <DeviceModelsTab />}
        {activeTab === 'partTypes' && (
          <NameCatalog title="Tipos de repuesto" itemLabel="Tipo de repuesto" createLabel="Nuevo tipo de repuesto" service={PART_TYPE_SERVICE} />
        )}
      </Tabs>
    </div>
  )
}
