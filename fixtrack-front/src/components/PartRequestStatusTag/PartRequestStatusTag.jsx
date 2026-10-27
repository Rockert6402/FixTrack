import { PART_REQUEST_STATUS_LABELS } from '../../utils/labels'
import './PartRequestStatusTag.css'

// Estado de una solicitud de repuesto con su color
export default function PartRequestStatusTag({ status }) {
  return <span className={`part-request-tag part-request-tag--${status.toLowerCase()}`}>{PART_REQUEST_STATUS_LABELS[status]}</span>
}
