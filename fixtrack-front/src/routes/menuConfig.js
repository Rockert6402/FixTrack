import {
  MdAssignment,
  MdBarChart,
  MdExitToApp,
  MdCategory,
  MdHome,
  MdInventory2,
  MdLocalShipping,
  MdManageAccounts,
  MdPeople,
  MdPerson,
  MdPointOfSale,
  MdSettings,
  MdShoppingCart,
} from 'react-icons/md'
import { ROLES } from '../utils/labels'

const { ADMIN, COORDINATOR, ADVISOR, TECHNICIAN } = ROLES

// Opciones del menú lateral y roles que las ven, según los permisos de fixtrack.md; labels cambia el nombre por rol
export const MENU_ITEMS = [
  { path: '/', label: 'Inicio', icon: MdHome, roles: [ADMIN, COORDINATOR, ADVISOR, TECHNICIAN] },
  {
    path: '/ordenes',
    label: 'Órdenes',
    labels: { [TECHNICIAN]: 'Mis órdenes' },
    icon: MdAssignment,
    roles: [ADMIN, COORDINATOR, ADVISOR, TECHNICIAN],
  },
  { path: '/salida', label: 'Salida', icon: MdExitToApp, roles: [ADMIN, COORDINATOR, ADVISOR] },
  { path: '/clientes', label: 'Clientes y equipos', icon: MdPeople, roles: [ADMIN, COORDINATOR, ADVISOR] },
  { path: '/caja', label: 'Caja', icon: MdPointOfSale, roles: [ADMIN, COORDINATOR, ADVISOR] },
  { path: '/inventario', label: 'Inventario', icon: MdInventory2, roles: [ADMIN, COORDINATOR] },
  {
    path: '/solicitudes',
    label: 'Solicitudes de repuestos',
    labels: { [TECHNICIAN]: 'Mis solicitudes' },
    icon: MdShoppingCart,
    roles: [ADMIN, COORDINATOR, TECHNICIAN],
  },
  { path: '/proveedores', label: 'Proveedores', icon: MdLocalShipping, roles: [ADMIN, COORDINATOR] },
  { path: '/catalogos', label: 'Catálogos', icon: MdCategory, roles: [ADMIN, COORDINATOR] },
  { path: '/personas', label: 'Personas', icon: MdManageAccounts, roles: [ADMIN] },
  { path: '/informes', label: 'Informes', labels: { [TECHNICIAN]: 'Mis ganancias' }, icon: MdBarChart, roles: [ADMIN, COORDINATOR, TECHNICIAN] },
  { path: '/parametros', label: 'Parámetros', icon: MdSettings, roles: [ADMIN] },
  { path: '/perfil', label: 'Mi perfil', icon: MdPerson, roles: [ADMIN, COORDINATOR, ADVISOR, TECHNICIAN] },
]
