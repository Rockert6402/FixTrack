import { MdChevronLeft, MdChevronRight } from 'react-icons/md'
import './DataTable.css'

// Tabla paginada con estados de carga, vacío y error; en celular cada fila se muestra como tarjeta
// rowClassName(row) permite resaltar filas, por ejemplo con data-table__row--danger
// page es base 0, igual que el Page de Spring
export default function DataTable({
  columns,
  rows,
  caption,
  rowKey = 'id',
  loading = false,
  error = null,
  onRetry,
  emptyMessage = 'No hay registros para mostrar.',
  page = 0,
  totalPages = 1,
  totalElements,
  onPageChange,
  onRowClick,
  rowClassName,
}) {
  const showPagination = onPageChange && totalPages > 1

  function handleRowKeyDown(event, row) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      onRowClick(row)
    }
  }

  function renderBodyMessage() {
    if (loading) {
      return 'Cargando…'
    }
    if (error) {
      return (
        <span className="data-table__error">
          {error}
          {onRetry && (
            <button type="button" className="btn btn--secondary" onClick={onRetry}>
              Reintentar
            </button>
          )}
        </span>
      )
    }
    return rows.length === 0 ? emptyMessage : null
  }

  const bodyMessage = renderBodyMessage()

  return (
    <div className="data-table">
      <div className="data-table__scroll">
        <table className="data-table__table" aria-busy={loading}>
          {caption && <caption className="visually-hidden">{caption}</caption>}
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key} scope="col" className={column.align ? `data-table__cell--${column.align}` : undefined}>
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {bodyMessage ? (
              <tr className="data-table__message-row">
                <td colSpan={columns.length}>{bodyMessage}</td>
              </tr>
            ) : (
              rows.map((row) => (
                <tr
                  key={row[rowKey]}
                  className={[onRowClick && 'data-table__row--clickable', rowClassName?.(row)].filter(Boolean).join(' ') || undefined}
                  onClick={onRowClick ? () => onRowClick(row) : undefined}
                  onKeyDown={onRowClick ? (event) => handleRowKeyDown(event, row) : undefined}
                  tabIndex={onRowClick ? 0 : undefined}
                >
                  {columns.map((column) => (
                    <td
                      key={column.key}
                      data-label={column.header}
                      className={column.align ? `data-table__cell--${column.align}` : undefined}
                    >
                      {column.render ? column.render(row) : row[column.key]}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showPagination && (
        <nav className="data-table__pagination" aria-label="Paginación">
          <span className="data-table__summary">
            Página {page + 1} de {totalPages}
            {totalElements != null && ` · ${totalElements} registros`}
          </span>
          <div className="data-table__pages">
            <button
              type="button"
              className="btn btn--secondary btn--icon"
              aria-label="Página anterior"
              disabled={page === 0 || loading}
              onClick={() => onPageChange(page - 1)}
            >
              <MdChevronLeft aria-hidden="true" />
            </button>
            <button
              type="button"
              className="btn btn--secondary btn--icon"
              aria-label="Página siguiente"
              disabled={page >= totalPages - 1 || loading}
              onClick={() => onPageChange(page + 1)}
            >
              <MdChevronRight aria-hidden="true" />
            </button>
          </div>
        </nav>
      )}
    </div>
  )
}
