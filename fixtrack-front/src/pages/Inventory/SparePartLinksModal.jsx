import { useState } from 'react'
import Modal from '../../components/Modal/Modal'
import Tabs from '../../components/Tabs/Tabs'
import { useRemoteData } from '../../hooks/useRemoteData'
import * as sparePartService from '../../services/sparePartService'
import * as supplierService from '../../services/supplierService'
import PartCompatibilitiesEditor from './PartCompatibilitiesEditor'
import PartSuppliersEditor from './PartSuppliersEditor'
import './SparePartLinksModal.css'

const TABS = [
  { id: 'suppliers', label: 'Proveedores' },
  { id: 'models', label: 'Modelos compatibles' },
]

// Proveedores y modelos compatibles de un repuesto; cada pestaña guarda su lista completa
export default function SparePartLinksModal({ part, models, onClose }) {
  const [tab, setTab] = useState('suppliers')
  const links = useRemoteData(() => sparePartService.listPartSuppliers(part.id), [part.id])
  const suppliers = useRemoteData(() => supplierService.searchSuppliers({ size: 200 }), [])
  const compatibilities = useRemoteData(() => sparePartService.listCompatibilities(part.id), [part.id])

  return (
    <Modal open size="lg" title={`${part.name} · ${part.sku}`} onClose={onClose}>
      <Tabs tabs={TABS} activeTab={tab} onChange={setTab} label="Datos del repuesto">
        {tab === 'suppliers' && (links.data && suppliers.data
          ? <PartSuppliersEditor key={JSON.stringify(links.data)} part={part} links={links.data} suppliers={suppliers.data.content} onSaved={links.reload} />
          : <p className="text-muted">{links.error ?? suppliers.error ?? 'Cargando…'}</p>)}
        {tab === 'models' && (compatibilities.data
          ? <PartCompatibilitiesEditor key={JSON.stringify(compatibilities.data)} part={part} compatibilities={compatibilities.data} models={models} onSaved={compatibilities.reload} />
          : <p className="text-muted">{compatibilities.error ?? 'Cargando…'}</p>)}
      </Tabs>
    </Modal>
  )
}
