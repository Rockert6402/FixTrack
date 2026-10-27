import FormField from '../../components/FormField/FormField'
import { QUALITY_LABELS, STOCK_STATUS_LABELS, toOptions } from '../../utils/labels'
import './InventoryFilters.css'

const SORT_OPTIONS = [
  { value: 'name,asc', label: 'Nombre (A-Z)' },
  { value: 'name,desc', label: 'Nombre (Z-A)' },
  { value: 'stock,asc', label: 'Stock (menor primero)' },
  { value: 'stock,desc', label: 'Stock (mayor primero)' },
  { value: 'salePrice,asc', label: 'Precio de venta (menor primero)' },
  { value: 'salePrice,desc', label: 'Precio de venta (mayor primero)' },
  { value: 'brand,asc', label: 'Marca' },
  { value: 'partType,asc', label: 'Tipo' },
  { value: 'sku,asc', label: 'SKU' },
]

// Búsqueda, filtros combinados y orden del inventario; los modelos se limitan a la marca elegida
export default function InventoryFilters({ value, partTypes, brands, models, onChange, onClear }) {
  const brandModels = value.brandId ? models.filter((model) => String(model.brandId) === value.brandId) : models
  return (
    <div className="filters inventory-filters">
      <FormField id="inventory-search" label="Buscar">
        {(props) => <input {...props} type="search" className="form-input" placeholder="Nombre, descripción o SKU" value={value.search} onChange={(event) => onChange('search', event.target.value)} />}
      </FormField>
      <FormField id="inventory-type" label="Tipo">
        {(props) => (
          <select {...props} className="form-input" value={value.partTypeId} onChange={(event) => onChange('partTypeId', event.target.value)}>
            <option value="">Todos</option>
            {partTypes.map((type) => <option key={type.id} value={type.id}>{type.name}</option>)}
          </select>
        )}
      </FormField>
      <FormField id="inventory-brand" label="Marca">
        {(props) => (
          <select {...props} className="form-input" value={value.brandId} onChange={(event) => onChange('brandId', event.target.value)}>
            <option value="">Todas</option>
            {brands.map((brand) => <option key={brand.id} value={brand.id}>{brand.name}</option>)}
          </select>
        )}
      </FormField>
      <FormField id="inventory-model" label="Modelo">
        {(props) => (
          <select {...props} className="form-input" value={value.deviceModelId} onChange={(event) => onChange('deviceModelId', event.target.value)}>
            <option value="">Todos</option>
            {brandModels.map((model) => <option key={model.id} value={model.id}>{model.brandName} {model.name}</option>)}
          </select>
        )}
      </FormField>
      <FormField id="inventory-quality" label="Calidad">
        {(props) => (
          <select {...props} className="form-input" value={value.quality} onChange={(event) => onChange('quality', event.target.value)}>
            <option value="">Todas</option>
            {toOptions(QUALITY_LABELS).map(({ value: option, label }) => <option key={option} value={option}>{label}</option>)}
          </select>
        )}
      </FormField>
      <FormField id="inventory-stock" label="Stock">
        {(props) => (
          <select {...props} className="form-input" value={value.stockStatus} disabled={value.alertsOnly} onChange={(event) => onChange('stockStatus', event.target.value)}>
            <option value="">Todos</option>
            {toOptions(STOCK_STATUS_LABELS).map(({ value: option, label }) => <option key={option} value={option}>{label}</option>)}
          </select>
        )}
      </FormField>
      <FormField id="inventory-sort" label="Ordenar por">
        {(props) => (
          <select {...props} className="form-input" value={value.sort} onChange={(event) => onChange('sort', event.target.value)}>
            {SORT_OPTIONS.map(({ value: option, label }) => <option key={option} value={option}>{label}</option>)}
          </select>
        )}
      </FormField>
      <label className="inventory-filters__check">
        <input type="checkbox" checked={value.alertsOnly} onChange={(event) => onChange('alertsOnly', event.target.checked)} />
        Solo en alerta (bajos o agotados)
      </label>
      {onClear && (
        <button type="button" className="btn btn--ghost" onClick={onClear}>Limpiar filtros</button>
      )}
    </div>
  )
}
