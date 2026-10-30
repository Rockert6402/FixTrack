import { useEffect, useRef, useState } from 'react'
import { MdExpandMore } from 'react-icons/md'
import './SearchSelect.css'

// Quita tildes y mayúsculas para que «samsung» encuentre «Samsung» y «telefono» encuentre «Teléfono»
function normalize(text) {
  return text.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase()
}

// Lista desplegable en la que se escribe para filtrar (combobox ARIA); sirve cuando hay muchas opciones
// options: [{ value, label }]; recibe las props de FormField (id, aria-*) para quedar enlazada a su label
export default function SearchSelect({
  id,
  options,
  value,
  onChange,
  placeholder = 'Escriba para buscar',
  emptyMessage = 'No hay coincidencias',
  disabled = false,
  ...fieldProps
}) {
  const [text, setText] = useState(null)
  const [open, setOpen] = useState(false)
  const [activeIndex, setActiveIndex] = useState(-1)
  const listRef = useRef(null)
  const listId = `${id}-list`

  const selected = options.find((option) => String(option.value) === String(value))
  const query = text ?? ''
  const filtered = query ? options.filter((option) => normalize(option.label).includes(normalize(query))) : options
  const activeOption = open ? filtered[activeIndex] : undefined

  // Mantiene visible la opción activa al moverse con las flechas
  useEffect(() => {
    if (activeOption) {
      listRef.current?.querySelector(`[data-index="${activeIndex}"]`)?.scrollIntoView({ block: 'nearest' })
    }
  }, [activeOption, activeIndex])

  function openList(index = 0) {
    setOpen(true)
    setActiveIndex(index)
  }

  function choose(option) {
    onChange(String(option.value))
    setText(null)
    setOpen(false)
  }

  function handleKeyDown(event) {
    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault()
        if (open) {
          setActiveIndex((current) => Math.min(current + 1, filtered.length - 1))
        } else {
          openList(Math.max(filtered.findIndex((option) => option === selected), 0))
        }
        break
      case 'ArrowUp':
        event.preventDefault()
        setActiveIndex((current) => Math.max(current - 1, 0))
        break
      case 'Enter':
        if (open && activeOption) {
          event.preventDefault()
          choose(activeOption)
        }
        break
      case 'Escape':
        // Cierra solo la lista; el modal de fondo sigue abierto
        if (open || text !== null) {
          event.stopPropagation()
          setOpen(false)
          setText(null)
        }
        break
      case 'Tab':
        setOpen(false)
        break
      default:
    }
  }

  // Al salir sin elegir vuelve a mostrar la opción elegida; si se borró el texto, quita la selección
  function handleBlur() {
    if (text === '') {
      onChange('')
    }
    setText(null)
    setOpen(false)
  }

  return (
    <div className="search-select">
      <input
        {...fieldProps}
        id={id}
        type="text"
        className="form-input search-select__input"
        role="combobox"
        aria-autocomplete="list"
        aria-expanded={open}
        aria-controls={listId}
        aria-activedescendant={activeOption ? `${id}-option-${activeIndex}` : undefined}
        autoComplete="off"
        placeholder={placeholder}
        disabled={disabled}
        value={text ?? selected?.label ?? ''}
        onChange={(event) => {
          setText(event.target.value)
          openList(0)
        }}
        onClick={() => !open && openList(Math.max(filtered.indexOf(selected), 0))}
        onKeyDown={handleKeyDown}
        onBlur={handleBlur}
      />
      <MdExpandMore className="search-select__arrow" aria-hidden="true" />
      {open && (
        <ul ref={listRef} id={listId} className="search-select__list" role="listbox">
          {filtered.length === 0 && <li className="search-select__empty">{emptyMessage}</li>}
          {filtered.map((option, index) => (
            <li
              key={option.value}
              id={`${id}-option-${index}`}
              data-index={index}
              role="option"
              aria-selected={option === selected}
              className={`search-select__option ${index === activeIndex ? 'search-select__option--active' : ''}`}
              onMouseDown={(event) => event.preventDefault()}
              onMouseEnter={() => setActiveIndex(index)}
              onClick={() => choose(option)}
            >
              {option.label}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
