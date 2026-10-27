import { useState } from 'react'
import { MdDownload } from 'react-icons/md'
import { useToast } from '../../hooks/useToast'
import * as portalService from '../../services/portalService'
import { getErrorMessage } from '../../utils/errors'
import { downloadFile } from '../../utils/files'
import './PortalReceiptButtons.css'

// Descarga de los comprobantes de una orden o garantía; el de entrega solo cuando ya se entregó
export default function PortalReceiptButtons({ code, accessKey, delivered }) {
  const { showError } = useToast()
  const [downloading, setDownloading] = useState(null)

  async function handleDownload(type) {
    setDownloading(type)
    try {
      const blob = await portalService.downloadReceipt(type, { code, accessKey })
      downloadFile(blob, `${type === 'intake' ? 'ingreso' : 'entrega'}-${code}.pdf`)
    } catch (error) {
      showError(getErrorMessage(error))
    } finally {
      setDownloading(null)
    }
  }

  return (
    <div className="portal-receipts">
      <button type="button" className="btn btn--secondary" disabled={downloading !== null} onClick={() => handleDownload('intake')}>
        <MdDownload aria-hidden="true" />
        {downloading === 'intake' ? 'Descargando…' : 'Comprobante de ingreso'}
      </button>
      {delivered && (
        <button type="button" className="btn btn--secondary" disabled={downloading !== null} onClick={() => handleDownload('delivery')}>
          <MdDownload aria-hidden="true" />
          {downloading === 'delivery' ? 'Descargando…' : 'Comprobante de entrega'}
        </button>
      )}
    </div>
  )
}
