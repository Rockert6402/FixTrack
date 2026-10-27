import { useState } from 'react'
import { MdCheck, MdClose } from 'react-icons/md'
import ConfirmDialog from '../../../components/ConfirmDialog/ConfirmDialog'
import { useAuth } from '../../../hooks/useAuth'
import { useToast } from '../../../hooks/useToast'
import * as interventionService from '../../../services/interventionService'
import { getErrorMessage } from '../../../utils/errors'
import { formatCurrency, formatDateTime } from '../../../utils/format'
import { APPROVAL_LABELS, FINDING_ORIGIN_LABELS, SERVICE_TYPE_LABELS } from '../../../utils/labels'
import { canWorkOnRepair, isFrontDesk, isOpen } from '../../../utils/permissions'
import AdditionalFindingForm from './AdditionalFindingForm'
import './OrderFindingsTab.css'

const APPROVAL_TONES = { APPROVED: 'tag--success', PENDING: 'tag--warning', REJECTED: 'tag--danger' }

// Daños de la orden: el técnico agrega los adicionales y el asesor registra la respuesta del cliente a los pendientes
export default function OrderFindingsTab({ order, onUpdated }) {
  const { user } = useAuth()
  const { showError, showSuccess } = useToast()
  const [answer, setAnswer] = useState(null)
  const canAnswer = isFrontDesk(user) && isOpen(order)
  const overBudget = Number(order.charges.total) > Number(order.estimatedPrice ?? 0)

  async function handleAnswer() {
    try {
      await interventionService.answerFinding(order.id, answer.finding.id, answer.decision)
      showSuccess(answer.decision === 'APPROVED' ? 'Se registró que el cliente aprobó el daño.' : 'Se registró que el cliente rechazó el daño; no se reparará.')
      onUpdated()
    } catch (requestError) {
      showError(getErrorMessage(requestError))
    } finally {
      setAnswer(null)
    }
  }

  return (
    <div className="order-findings">
      <p className={`order-findings__budget ${overBudget ? 'order-findings__budget--over' : ''}`}>
        Presupuesto inicial {formatCurrency(order.estimatedPrice)} · total actual {formatCurrency(order.charges.total)}.
        {overBudget ? ' El total supera el presupuesto: los daños adicionales esperan la respuesta del cliente.' : ' Los daños adicionales que quepan en el presupuesto se aprueban solos.'}
      </p>

      <ul className="order-findings__list">
        {order.findings.map((finding) => (
          <li key={finding.id} className="order-findings__item">
            <div className="order-findings__head">
              <strong>{SERVICE_TYPE_LABELS[finding.serviceType]}</strong>
              <span className="tag">{FINDING_ORIGIN_LABELS[finding.origin]}</span>
              <span className={`tag ${APPROVAL_TONES[finding.approvalStatus]}`}>{APPROVAL_LABELS[finding.approvalStatus]}</span>
            </div>
            <p className="order-findings__description">{finding.description}</p>
            {finding.respondedBy && (
              <p className="order-findings__meta">Respuesta registrada por {finding.respondedBy.fullName} el {formatDateTime(finding.respondedAt)}</p>
            )}
            {canAnswer && finding.approvalStatus === 'PENDING' && (
              <div className="order-findings__answer">
                <button type="button" className="btn btn--secondary" onClick={() => setAnswer({ finding, decision: 'APPROVED' })}>
                  <MdCheck aria-hidden="true" />
                  El cliente aprueba
                </button>
                <button type="button" className="btn btn--secondary" onClick={() => setAnswer({ finding, decision: 'REJECTED' })}>
                  <MdClose aria-hidden="true" />
                  El cliente rechaza
                </button>
              </div>
            )}
          </li>
        ))}
      </ul>

      {canWorkOnRepair(order, user) && <AdditionalFindingForm order={order} onSaved={onUpdated} />}

      <ConfirmDialog
        open={answer !== null}
        title={answer?.decision === 'APPROVED' ? 'Aprobación del cliente' : 'Rechazo del cliente'}
        message={answer?.decision === 'APPROVED'
          ? `Se registrará que el cliente aprobó «${SERVICE_TYPE_LABELS[answer?.finding.serviceType]}». El técnico podrá repararlo.`
          : `Se registrará que el cliente rechazó «${SERVICE_TYPE_LABELS[answer?.finding.serviceType]}». Ese daño no se reparará.`}
        confirmLabel={answer?.decision === 'APPROVED' ? 'Registrar aprobación' : 'Registrar rechazo'}
        danger={answer?.decision === 'REJECTED'}
        onConfirm={handleAnswer}
        onCancel={() => setAnswer(null)}
      />
    </div>
  )
}
