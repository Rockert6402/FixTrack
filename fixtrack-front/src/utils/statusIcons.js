import {
  MdBuild,
  MdCancel,
  MdCheckCircle,
  MdDoneAll,
  MdHourglassTop,
  MdMoveToInbox,
  MdSearch,
} from 'react-icons/md'

// Ícono de cada estado de la orden; en alto contraste acompaña al color para no depender solo de él
export const STATUS_ICONS = {
  RECEIVED: MdMoveToInbox,
  IN_DIAGNOSIS: MdSearch,
  WAITING_PARTS: MdHourglassTop,
  IN_REPAIR: MdBuild,
  READY: MdCheckCircle,
  DELIVERED: MdDoneAll,
  CANCELLED: MdCancel,
}
