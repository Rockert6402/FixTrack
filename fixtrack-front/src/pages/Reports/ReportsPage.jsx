import { useState } from 'react'
import PageHeader from '../../components/PageHeader/PageHeader'
import Tabs from '../../components/Tabs/Tabs'
import { useAuth } from '../../hooks/useAuth'
import { ROLES } from '../../utils/labels'
import InventoryReportTab from './InventoryReportTab'
import MaintenanceReportTab from './MaintenanceReportTab'
import ProfitReportTab from './ProfitReportTab'
import './ReportsPage.css'

// Informes según el rol: el administrador ve todos; el coordinador, el de inventario; el técnico, sus ganancias
const TABS_BY_ROLE = {
  [ROLES.ADMIN]: [
    { id: 'inventory', label: 'Inventario' },
    { id: 'maintenance', label: 'Mantenimientos' },
    { id: 'profit', label: 'Ganancias' },
  ],
  [ROLES.COORDINATOR]: [{ id: 'inventory', label: 'Inventario' }],
  [ROLES.TECHNICIAN]: [{ id: 'profit', label: 'Mis ganancias' }],
}

export default function ReportsPage() {
  const { user } = useAuth()
  const tabs = TABS_BY_ROLE[user.role] ?? []
  const [tab, setTab] = useState(tabs[0]?.id)
  const technicianView = user.role === ROLES.TECHNICIAN

  return (
    <div className="page">
      <PageHeader
        title={technicianView ? 'Mis ganancias' : 'Informes'}
        description={technicianView ? 'Lo cobrado, el costo de repuestos y la ganancia de sus órdenes entregadas.' : 'Inventario, mantenimientos y ganancias del taller.'}
      />
      {tabs.length > 1 ? (
        <Tabs tabs={tabs} activeTab={tab} onChange={setTab} label="Informes">
          {tab === 'inventory' && <InventoryReportTab />}
          {tab === 'maintenance' && <MaintenanceReportTab />}
          {tab === 'profit' && <ProfitReportTab role={user.role} />}
        </Tabs>
      ) : (
        <>
          {tab === 'inventory' && <InventoryReportTab />}
          {tab === 'profit' && <ProfitReportTab role={user.role} />}
        </>
      )}
    </div>
  )
}
