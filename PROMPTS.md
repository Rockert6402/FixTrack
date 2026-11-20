# Prompts de construcción de FixTrack

Prompts con los que se construyó FixTrack usando Claude Code, en el orden en que se ejecutaron: la preparación del proyecto y los tres sprints.

## Contenido

- [Preparación del proyecto](#preparación-del-proyecto)
  - [P-0. Reglas del proyecto FixTrack](#p-0-reglas-del-proyecto-fixtrack)
  - [P-1. Reglas de Claude Code para Spring Boot](#p-1-reglas-de-claude-code-para-spring-boot)
  - [P-2. Conexión a MySQL](#p-2-conexión-a-mysql)
  - [P-3. Modelo de datos completo](#p-3-modelo-de-datos-completo)
  - [P-4. Repositorios](#p-4-repositorios)
  - [P-5. SecurityConfig, CorsConfig y manejo de errores](#p-5-securityconfig-corsconfig-y-manejo-de-errores)
  - [P-6. Reglas de Claude Code para React](#p-6-reglas-de-claude-code-para-react)
  - [P-7. Proyecto React y cliente Axios](#p-7-proyecto-react-y-cliente-axios)
- [Sprint 1: una reparación completa](#sprint-1-una-reparación-completa)
- [Sprint 2: inventario, cierre de órdenes y seguridad](#sprint-2-inventario-cierre-de-órdenes-y-seguridad)
- [Sprint 3: proveedores, caja, informes y portal](#sprint-3-proveedores-caja-informes-y-portal)

---

## Preparación del proyecto

### P-0. Reglas del proyecto FixTrack

Crea un archivo de reglas para Claude Code (md) llamado 'fixtrack' en `.claude/rules/fixtrack.md`, que siempre se aplique, con el contexto del proyecto FixTrack:

- Qué es: sistema web de gestión de reparaciones e inventario para una tienda de reparación de dispositivos móviles (teléfonos, tablets, relojes inteligentes, parlantes y otros).
- Roles internos, que inician sesión: ADMIN, COORDINATOR (coordinador de inventario), ADVISOR (asesor) y TECHNICIAN (técnico). Los clientes (CUSTOMER) se registran como personas pero no inician sesión; consultan su orden en un portal público, enlazado desde la pantalla de inicio de sesión, con el código de la orden (consecutivo, por ejemplo FT-00001) y su clave de consulta de 4 dígitos.
- Permisos por rol:
  - Asesor: registrar clientes y equipos, recibir equipos con su estado físico, dar el presupuesto inicial, abrir órdenes y asignar técnico, registrar la respuesta del cliente sobre los daños adicionales, registrar abonos y el cobro final, entregar equipos, reprogramar la fecha estimada de entrega, abrir garantías y hacer su cierre de caja.
  - Técnico: ver sus órdenes asignadas, actualizar su disponibilidad, reprogramar la fecha estimada de entrega de sus órdenes, registrar el diagnóstico y la solución, agregar daños adicionales, definir la mano de obra, cambiar el estado, registrar y corregir los repuestos usados, y solicitar repuestos que no hay en inventario.
  - Coordinador: todo lo del asesor, más gestionar el inventario, los catálogos, los proveedores, las entradas y las solicitudes de repuestos.
  - Administrador: todo lo anterior, más gestionar personas, roles y especialidades, informes y parámetros.
- Estados de la orden: RECEIVED, IN_DIAGNOSIS, WAITING_PARTS, IN_REPAIR, READY, DELIVERED y CANCELLED. "No reparable" y "rechazado por el cliente" son resultados de la orden, no estados.
- Equipos: el IMEI es obligatorio solo en teléfonos; los demás se identifican por número de serie cuando lo tienen. La contraseña o patrón de desbloqueo se guarda cifrado en la orden, solo lo ven el asesor y el técnico asignado, y se borra al entregar o cancelar.
- Código de la orden: no se guarda como columna. Se genera a partir del id de la base de datos como `FT-` + id con 5 dígitos (id 1 → FT-00001; desde el id 100000 crece sin recortarse), así la única llave única es el id y nunca hay duplicados. Las garantías usan el código de la orden original + `-G` + su número de garantía. Para buscar por código, se convierte de vuelta a id.
- Zona horaria: America/Bogota en todo el sistema (backend, base de datos y front). «Hoy», las órdenes atrasadas, las no reclamadas, los informes por fecha y el cierre de caja se calculan con esa hora.
- Clientes: el tipo y número de documento son obligatorios para guardarlos y son la forma de identificarlos cuando regresan. El celular y el correo son opcionales, pero cada uno se llena o se marca explícitamente «No tiene»: se guarda vacío y se muestra como NT. Celular: solo números, de 7 a 10 dígitos, no único (lo pueden compartir familiares). Correo: formato válido y único cuando existe; obligatorio para los usuarios internos, porque con él inician sesión.
- Clave de consulta: se asigna al abrir la orden y no cambia aunque el cliente cambie sus datos. Son los últimos 4 dígitos del celular o, si no tiene celular, 4 dígitos al azar impresos en el comprobante de ingreso. Se guarda con hash; al reimprimir el comprobante se genera una nueva y la anterior deja de servir. Si el cliente no tiene celular ni correo, el comprobante advierte «Conserve este comprobante, no podremos contactarlo».
- Fecha estimada de entrega: obligatoria al abrir la orden, no anterior a hoy y visible en los comprobantes y el portal. Se puede reprogramar con un motivo y queda registro de quién, cuándo, la fecha anterior y la nueva. Una orden está atrasada si pasó esa fecha y no está en READY, DELIVERED ni CANCELLED.
- Garantías: se anexan a la orden original con su código más un sufijo (FT-00001-G1, FT-00001-G2) y se consultan en el portal con el código y la clave de la original. Solo se abren mientras la garantía de la original esté vigente, no se abren sobre otra garantía y no la extienden. Siguen los mismos estados de una orden, con mano de obra y repuestos a precio 0. Al abrirla se pueden actualizar los datos de contacto del cliente.
- Repuestos: la calidad (ORIGINAL, OEM, GENERIC) es opcional; cada calidad es un repuesto distinto con su propio SKU, stock y precio.
- Dinero:
  - Total de la orden = mano de obra + suma de cantidad × precio de venta de cada repuesto usado, con el precio guardado al momento del uso.
  - Costo de repuestos = suma de cantidad × costo guardado al momento del uso.
  - Ganancia = total − costo de repuestos. Se calcula, no se guarda. Cuentan las órdenes entregadas; las canceladas no; las no reparables cuentan con el valor del diagnóstico; las garantías van a precio 0 y su costo lo asume el almacén.
  - El cliente y el asesor solo ven el presupuesto y el total, sin desglose. El técnico ve la ganancia de sus órdenes; el coordinador y el administrador, la de todas.
- Fuera del alcance: correo al cliente, procedimiento legal de abandono, facturación electrónica y comisiones de los técnicos.
- Comentarios: en español y de una línea, diciendo qué hace cada área importante del código. Van en la responsabilidad de las clases y componentes principales, en los métodos con lógica de negocio y en los pasos principales de los procesos largos (recibir una orden, registrar un repuesto usado, cerrar la caja). No se comenta cada método ni cada línea, ni se explica cómo funciona por dentro; los CRUD simples, getters, DTOs y repositorios sin consultas especiales no llevan comentarios.

### P-1. Reglas de Claude Code para Spring Boot

Crea un archivo de reglas para Claude Code (md) llamado 'springboot' en `.claude/rules/springboot.md`, que siempre se aplique, con las buenas prácticas de Java 25 en el desarrollo de un servicio en Spring Boot, para aplicarlas en el desarrollo de FixTrack.

Ten en cuenta que:

- Los nombres de clases, métodos y atributos deben ser en inglés. Los comentarios van en español y siguen la regla de comentarios de `.claude/rules/fixtrack.md`.
- Para los DTO usamos Java records, de entrada y de salida. Las entidades nunca se exponen en la API.
- El paquete base es `com.fixtrack`, organizado por dominio (`person`, `device`, `inventory`, `intervention`, `cash` y `setting`) y, dentro de cada uno, por capas: `controller`, `service` (la interfaz) con `service/impl` (su implementación), `repository`, `entity`, `dto` y `mapper`.
- El paquete `config` contiene `SecurityConfig` y `CorsConfig`; el paquete `infra.security`, el filtro JWT (`JwtAuthFilter`); y el paquete `common`, las excepciones de negocio y el `@RestControllerAdvice`.
- Flujo de dependencias: Controller → interfaz del Service → Repository (extiende `JpaRepository`) → Spring Data JPA → MySQL. El controlador nunca usa un repositorio, y un servicio solo usa repositorios de su propio dominio; para datos de otro dominio usa el servicio de ese dominio.
- Todos los endpoints empiezan por `/api` y usan sustantivos en plural en inglés (por ejemplo, `/api/people`).
- Las contraseñas se codifican con el `PasswordEncoder` (bcrypt) definido en `SecurityConfig`.
- Las tablas y columnas de la base de datos van en snake_case.
- Los valores de dinero usan BigDecimal con dos decimales y redondeo `HALF_UP`.
- Las operaciones que escriben en varias tablas van en una sola transacción.
- Las bajas son lógicas: nada que tenga historial se borra.
- Las credenciales van en variables de entorno.
- Toda entrada se valida en el servidor con Bean Validation.

Agrega la estructura actual del proyecto dentro de las reglas.

### P-2. Conexión a MySQL

Crea la configuración en `application.yml` para la conexión a la base de datos MySQL de nombre 'fixtrack' con el usuario 'root'. La contraseña debe leerse de la variable de entorno `DB_PASSWORD`; agrega un archivo `.env.example` y asegúrate de que `.env` esté en el `.gitignore`. Habilita la actualización automática del esquema en Hibernate, el dialecto de MySQL e imprime las sentencias SQL de JPA en consola. Configura la zona horaria America/Bogota en la URL de conexión, en Hibernate (`hibernate.jdbc.time_zone`) y en Jackson, y fija la zona por defecto de la JVM al arrancar la aplicación.

### P-3. Modelo de datos completo

Crea las entidades JPA de FixTrack con las anotaciones correctas, getters, setters y constructores (puedes usar Lombok). Los nombres de tablas y columnas van en snake_case:

- **Person**: id, documentType (enum `CC`, `TI`, `CE`, `PASSPORT`, `PPT`), documentNumber (obligatorio; único junto con el tipo), fullName, phone (opcional, solo números, de 7 a 10 dígitos), email (opcional y único cuando existe; obligatorio para usuarios internos), password (solo para usuarios internos), role (enum `ADMIN`, `COORDINATOR`, `ADVISOR`, `TECHNICIAN`, `CUSTOMER`), availability (enum `AVAILABLE`, `BUSY`, `ABSENT`, solo para técnicos), active y createdAt.
- **Specialty**: id y name (único), por ejemplo pantallas, software, microsoldadura o audio.
- **PersonSpecialty**: tabla intermedia entre Person (técnicos) y Specialty, con llave compuesta.
- **Brand**: id y name (único). **DeviceModel**: id, brand, name y deviceType (enum `PHONE`, `TABLET`, `SMARTWATCH`, `SPEAKER`, `OTHER`); marca y nombre son únicos en conjunto.
- **Device**: id, owner (Person de rol `CUSTOMER`), deviceModel, imei (opcional y único cuando existe; obligatorio si el modelo es `PHONE`), serialNumber (opcional), color y observations.
- **PartType**: id y name (único), por ejemplo pantalla, batería, pin de carga, cámara, parlante o flex.
- **SparePart**: id, name, description, sku (único), partType, deviceModel (modelo principal), quality (enum `ORIGINAL`, `OEM`, `GENERIC`, opcional), stock, minStock, costPrice, salePrice y active. Tipo, modelo y calidad son únicos en conjunto.
- **SparePartCompatibility**: tabla intermedia entre SparePart y DeviceModel, con llave compuesta, para los otros modelos en los que sirve el repuesto.
- **Supplier**: id, name, taxId (NIT, único), contactName, phone, email y active.
- **SupplierSparePart**: tabla intermedia entre Supplier y SparePart, con llave compuesta: supplierSku, lastCost, leadTimeDays y preferred.
- **Intervention**: id (el código visible no se guarda: se genera como `FT-` + id con 5 dígitos), warrantyNumber (1, 2… solo en garantías, consecutivo por orden original, para el código `FT-00001-G1`), accessKeyHash (clave de consulta con hash), failedLookupAttempts y lookupLockedUntil (bloqueo del portal), device, advisor y technician (relaciones con Person), status, problemDescription, physicalCondition, unlockCode (contraseña o patrón de desbloqueo, opcional, cifrado con AES y una clave de variable de entorno), diagnosis, solution, repairOutcome (enum `REPAIRED`, `NOT_REPAIRABLE`, `REJECTED_BY_CUSTOMER`), outcomeReason, estimatedPrice (presupuesto inicial), estimatedDeliveryDate (obligatoria), laborCost (mano de obra), totalPrice (se guarda al entregar), warrantyDays, warrantyUntil, warrantyOf (orden original cuando es una garantía; siempre apunta a la original, nunca a otra garantía), deliveryNotes, receivedAt, readyAt, deliveredAt, updatedAt y version (bloqueo optimista).
- **InterventionFinding**: id, intervention, serviceType, description, origin (enum `INITIAL`, `ADDITIONAL`), approvalStatus (enum `APPROVED`, `PENDING`, `REJECTED`), reportedBy y createdAt.
- **InterventionStatusHistory**: id, intervention, fromStatus, toStatus, changedBy, note y changedAt.
- **DeliveryDateChange**: id, intervention, previousDate, newDate, reason, changedBy y changedAt.
- **InterventionSparePart**: tabla intermedia entre Intervention y SparePart: id, quantity, unitPrice (precio de venta al momento del uso), costPrice (costo al momento del uso), createdBy y createdAt.
- **PartRequest**: id, intervention, requestedBy, sparePart (opcional), supplier (opcional), description, quantity, status (enum `PENDING`, `APPROVED`, `ORDERED`, `RECEIVED`, `REJECTED`, `CANCELLED`), estimatedCost, actualCost, rejectionReason, handledBy, requestedAt y receivedAt.
- **StockMovement**: id, sparePart, type (enum `IN_PURCHASE`, `OUT_REPAIR`, `RETURN`, `ADJUSTMENT`), quantity (positiva si entra, negativa si sale), balanceAfter (saldo resultante), unitCost, intervention y partRequest (opcionales), reason, createdBy y createdAt. Los movimientos no se editan.
- **Payment**: id, intervention, cashClosing (opcional), amount, method (enum `CASH`, `TRANSFER`, `CARD`), type (enum `DEPOSIT`, `FINAL`, `REFUND`), receivedBy y createdAt.
- **CashClosing**: id, closedBy, closingDate (única junto con closedBy), expectedCash, expectedTransfer, expectedCard, countedCash, notes y createdAt. La diferencia se calcula, no se guarda.
- **SystemSetting**: settingKey (llave), settingValue, updatedBy y updatedAt: `DEFAULT_MIN_STOCK`, `DEFAULT_WARRANTY_DAYS` (30), `UNCLAIMED_ALERT_DAYS` y las condiciones del servicio para los comprobantes.

Enum `InterventionStatus`: `RECEIVED`, `IN_DIAGNOSIS`, `WAITING_PARTS`, `IN_REPAIR`, `READY`, `DELIVERED`, `CANCELLED`.

Enum `ServiceType`: `SCREEN_REPLACEMENT`, `BATTERY_REPLACEMENT`, `CHARGING_PORT`, `CAMERA`, `SOFTWARE_FLASHING`, `WATER_DAMAGE`, `AUDIO`, `GENERAL_DIAGNOSIS`, `OTHER`.

### P-4. Repositorios

Crea los repositorios Spring Data JPA de cada entidad, extendiendo JpaRepository. Agrega solo las consultas básicas de existencia y búsqueda por llave única (tipo y número de documento, correo, IMEI, SKU, nombres de catálogo); las consultas propias de cada historia se agregan en el sprint correspondiente. Usa `@Query` con proyecciones cuando solo se necesiten totales.

### P-5. SecurityConfig, CorsConfig y manejo de errores

Crea en el paquete `com.fixtrack.config`:

- `CorsConfig`: un filtro de CORS para todas las rutas (`/**`) que acepte peticiones del origen `http://localhost:5173` (Vite), con los métodos GET, POST, PUT, PATCH y DELETE y la cabecera `Authorization`.
- `SecurityConfig`: el bean `PasswordEncoder` con bcrypt, que usarán los servicios; CSRF desactivado y sesiones sin estado, porque es una API REST; y la integración con `CorsConfig` para que no se bloqueen las peticiones de preflight. Por ahora permite las rutas `/api/**`; en el Sprint 2 se cierran con JWT y permisos por rol (S2-1).

Crea también en `com.fixtrack.common` el `@RestControllerAdvice` con un formato de error uniforme, las excepciones de negocio personalizadas y los códigos HTTP 400, 401, 403, 404 y 409.

### P-6. Reglas de Claude Code para React

Crea un archivo de reglas para Claude Code (md) llamado 'react' en `.claude/rules/react.md`, que siempre se aplique al frontend de FixTrack (`fixtrack-front`), con las buenas prácticas de React con Vite:

- Componentes funcionales con hooks. Variables y métodos en camelCase, componentes en PascalCase; los nombres van en inglés y los comentarios en español.
- Cada componente tiene su archivo `.jsx` y su archivo `.css` separados.
- Estructura, según el diagrama de componentes: `src/config` (`apiConfig.js`, el API_CONFIG con el cliente Axios), `src/services` (servicios de API, uno por dominio), `src/pages` y `src/components` (módulos de vista), `src/context` (sesión guardada en `sessionStorage`), `src/routes` y `src/styles` (variables, base, layout y utilidades).
- Flujo: módulos de vista → servicios de API → API_CONFIG. Las vistas nunca usan Axios directamente.
- Diseño: CSS propio, sin Bootstrap ni librerías de componentes. Variables de diseño en `src/styles/variables.css` (colores, tipografía, espacios, radios y sombras). Íconos con `react-icons`.
- Esquema: menú lateral oscuro fijo con el logo de FixTrack, las opciones según el rol, el nombre del usuario y cerrar sesión; contenido claro con encabezado de página; tarjetas de indicadores con ícono, título y número; tablas con el estado en etiquetas de color.
- Ventanas sobre la pantalla actual, para no salir del listado:
  - Modal centrado para crear y editar: personas, especialidades, marcas, modelos, tipos de repuesto, repuestos, proveedores, entradas de mercancía, pagos, daños adicionales y solicitudes de repuestos. Al guardar, se cierra y la tabla de fondo se actualiza sin recargar.
  - Modal grande por secciones (cliente, equipo y servicio) para el ingreso de una orden.
  - Panel lateral deslizable desde la derecha para el detalle de la orden, con pestañas (información, diagnóstico, repuestos, pagos e historial), dejando visible el listado detrás.
  - Modal pequeño de confirmación para acciones irreversibles (cancelar una orden, dar de baja, entregar).
  - Todos se cierran con Esc, con el botón cerrar o al hacer clic afuera (salvo si hay cambios sin guardar, que piden confirmación), y llevan el foco al primer campo. En celular ocupan la pantalla completa.
- Un solo componente `Modal` y un `SidePanel` reutilizables en `src/components`.
- Colores por estado de la orden, iguales en todo el sistema: RECEIVED gris, IN_DIAGNOSIS azul, WAITING_PARTS naranja, IN_REPAIR morado, READY verde, DELIVERED verde oscuro y CANCELLED rojo.
- No hay página de Inicio vacía ni página de indicadores aparte: la ruta `/` de cada usuario interno es su panel de Inicio según el rol.
- Responsive: en celular el menú lateral se vuelve un menú desplegable y las tablas pasan a tarjetas. Accesibilidad básica (labels, foco visible y contraste).

Agrega la estructura actual del proyecto dentro de las reglas.

### P-7. Proyecto React y cliente Axios

Crea el proyecto `fixtrack-front` en React con Vite. Configura en `src/config/apiConfig.js` el cliente Axios central (API_CONFIG), que lea la URL del backend de la variable `VITE_API_URL` (agrega un `.env.example`). Crea las variables de diseño y el layout base con el menú lateral definido en `.claude/rules/react.md`, y los componentes reutilizables: toast para mensajes de error, tabla paginada, tarjeta de indicador, etiqueta de estado, modal, panel lateral y confirmación. Define las rutas: `/login` (pública, con el enlace «Consulta tu equipo»), `/consulta` (portal público del cliente) y `/` (Inicio según el rol, protegida). Realiza las configuraciones adecuadas para que todo quede en funcionamiento.

---

## Sprint 1: una reparación completa

### S1-1. Catálogos e inventario básico

Crea la lógica y los endpoints del catálogo y el inventario básico de FixTrack:

- En el dominio `device`: CRUD de marcas y modelos con su tipo de equipo (`/api/brands` con `BrandController`, `/api/device-models` con `DeviceModelController`).
- En el dominio `inventory`: CRUD de tipos de repuesto (`/api/part-types` con `PartTypeController`).
- No se admiten nombres repetidos dentro de cada catálogo.
- En el dominio `inventory` (`/api/spare-parts` con `SparePartController`): registrar, editar y dar de baja repuestos. No se admiten dos repuestos con el mismo tipo, modelo y calidad. Al editar, los campos que no se envían conservan su valor. La baja es lógica: el repuesto sale del listado pero conserva sus movimientos.
- Si no se indica el stock mínimo, se usa el parámetro `DEFAULT_MIN_STOCK`.

Usa DTO (records) y devuelve 409 cuando haya duplicados.

### S1-2. Personas e inicio de sesión

Crea en el dominio `person` la gestión de personas y el inicio de sesión de FixTrack, con las capas `PersonController` → interfaz `PersonService` (implementada en `PersonServiceImpl`) → `PersonRepository`:

- `/api/people`: alta, edición y baja lógica de personas, con tipo y número de documento obligatorios y únicos, correo único cuando existe (obligatorio para usuarios internos), celular opcional, rol y especialidades (para técnicos). Para clientes, el celular y el correo llegan con su valor o con la marca explícita de que no tiene (`noPhone`, `noEmail`), que se guarda como vacío; si no llega ni el valor ni la marca, se rechaza con 400. `/api/specialties`: catálogo de especialidades. `PATCH /api/people/{id}/availability`: el técnico cambia su propia disponibilidad y el administrador la de cualquiera.
- Las contraseñas se codifican con el `PasswordEncoder` de `SecurityConfig`.
- `POST /api/auth/login`: verifica el correo y la contraseña y devuelve los datos del usuario y su rol. Si falla, responde siempre el mismo mensaje, sin revelar si el correo existe. Solo pueden iniciar sesión los roles internos activos.
- Un usuario administrador inicial creado al arrancar si no existe, con la contraseña tomada de una variable de entorno.

En el Sprint 2 el inicio de sesión pasa a emitir un JWT, así que deja la verificación de credenciales en un servicio que luego se pueda reutilizar.

### S1-3. Clientes, equipos y órdenes

Crea en los dominios `device` e `intervention` el flujo de órdenes de reparación de FixTrack:

- Ingreso del equipo (`POST /api/interventions`): reutiliza al cliente si ya existe por tipo y número de documento (obligatorios) y permite actualizar su celular y correo con la regla de «No tiene» de `.claude/rules/fixtrack.md`, y al equipo si ya existe por IMEI o número de serie. Registra el estado físico, la contraseña o patrón de desbloqueo (opcional, cifrado), el servicio inicial (como hallazgo `INITIAL` aprobado), el presupuesto inicial, la fecha estimada de entrega (obligatoria, no anterior a hoy) y el técnico asignado. Devuelve el código generado a partir del id (`FT-00001`, `FT-00002`…, sin guardarlo como columna) y deja la orden en `RECEIVED`. Asigna la clave de consulta (últimos 4 dígitos del celular, o 4 al azar si no tiene) y la devuelve una sola vez en la respuesta para imprimirla en el comprobante.
- El IMEI es obligatorio cuando el modelo es `PHONE`.
- La contraseña de desbloqueo solo se devuelve al asesor y al técnico asignado, en el detalle de la orden; en los listados nunca aparece.
- `PATCH /{id}/technician`: asignar o cambiar el técnico.
- `PATCH /{id}/estimated-delivery`: el asesor o el técnico asignado reprograma la fecha con un motivo obligatorio y se guarda un `DeliveryDateChange`. No se permite en órdenes entregadas o canceladas.
- `PATCH /{id}/diagnosis`: el técnico registra el diagnóstico y la solución aplicada.
- `PATCH /{id}/status`: cambiar el estado solo por transiciones válidas: `RECEIVED` → `IN_DIAGNOSIS` → `WAITING_PARTS` o `IN_REPAIR` → `READY` → `DELIVERED`. Cada cambio guarda un `InterventionStatusHistory` con usuario y fecha, y registra readyAt al pasar a `READY`. `GET /{id}/history` devuelve el historial.
- `GET /api/interventions`: consulta paginada con filtros combinables por código, documento del cliente, técnico, estado, rango de fechas y atrasadas.
- `GET /api/interventions/mine`: las órdenes del técnico que hace la petición, ordenadas por fecha de ingreso.

### S1-4. Consumo de repuestos

Crea el registro de repuestos usados en una orden de FixTrack (`POST /api/interventions/{id}/spare-parts`, `GET` para listarlos y `DELETE /{id}/spare-parts/{usageId}` para eliminarlos):

- Solo puede registrarlos el técnico asignado a la orden.
- El repuesto se elige por tipo y modelo; los compatibles con el modelo del equipo aparecen primero, con su calidad cuando la tengan.
- La cantidad debe ser un entero mayor que cero. Si supera el stock, la operación se rechaza indicando la cantidad disponible.
- En una sola transacción: se guarda el consumo con el precio de venta y el costo del momento, se descuenta el stock y se registra un movimiento `OUT_REPAIR` con el saldo resultante. Un fallo en cualquiera de las escrituras revierte las demás.
- El total de la orden se calcula como mano de obra más cantidad por precio de venta de los repuestos usados.
- Eliminar un consumo restituye el stock con un movimiento `RETURN`, en la misma transacción, sin afectar el artículo del catálogo.
- Usa bloqueo optimista en el repuesto para que dos técnicos no dejen el stock en negativo.

### S1-5. Front: sesión, personas e inventario

Crea en React, usando los servicios por dominio (`authService`, `personService`, `catalogService` y `sparePartService`):

- La página de login, con el diseño de `.claude/rules/react.md`, el enlace «Consulta tu equipo» hacia el portal del cliente, un contexto de sesión que guarde el usuario y su rol en `sessionStorage`, y el menú lateral según el rol.
- La página «Mi perfil»: datos personales y, para el técnico, su disponibilidad.
- La gestión de personas y especialidades (administrador), con tipo y número de documento obligatorios.
- Los catálogos de marcas, modelos y tipos de repuesto, y el inventario de repuestos con registro, edición y baja (coordinador y administrador).

Cada componente tiene su `.jsx` y su `.css` separados.

### S1-6. Front: órdenes y consumo de repuestos

Crea en React, usando `deviceService` e `interventionService`:

- El formulario de ingreso: buscar al cliente por documento o registrarlo (tipo y número de documento obligatorios; celular y correo con su casilla «No tiene», que desactiva el campo y se muestra como NT), elegir o registrar el equipo (marca y modelo del catálogo, IMEI o número de serie, color), estado físico, contraseña o patrón de desbloqueo (oculto, con opción de mostrar), servicio inicial (cambio de pantalla, batería, pin de carga, cámara, software/flasheo, daño por humedad, audio, diagnóstico general u otro), descripción del problema, técnico (mostrando su disponibilidad), presupuesto y fecha estimada de entrega. Al guardar muestra el código de la orden y la clave de consulta para el comprobante; si hay un error, un toast con el mensaje del backend.
- La tabla de órdenes con filtros combinados, y «Mis órdenes» para el técnico al iniciar sesión.
- El detalle de la orden: diagnóstico y solución, cambio de estado solo a las transiciones válidas e historial de estados, y reprogramación de la fecha de entrega con motivo y su historial. Las órdenes atrasadas se resaltan en rojo en las tablas.
- El consumo de repuestos: selección de repuesto y cantidad, lista de repuestos usados con su precio, y el mensaje de rechazo con la cantidad disponible cuando no alcanza el stock.

Cada componente tiene su `.jsx` y su `.css` separados.

### S1-7. Pruebas del Sprint 1

Crea un apartado de pruebas en el que queden los resultados y el paso de los test.

Crea pruebas unitarias con JUnit 5 y Mockito para los servicios del Sprint 1: duplicados en catálogos, repuestos, personas y equipos; transiciones de estado válidas e inválidas; descuento exacto del stock; rechazo por stock insuficiente; restitución al eliminar un consumo; la reversión de las escrituras del consumo ante un fallo simulado; cliente sin documento o sin valor ni marca de «No tiene» en celular o correo; clave de consulta por celular y al azar; y fecha estimada anterior a hoy o reprogramada sin motivo.

---

## Sprint 2: inventario, cierre de órdenes y seguridad

### S2-1. Seguridad con JWT y permisos

Actualiza `SecurityConfig` para usar JWT en FixTrack:

- `POST /api/auth/login` ahora devuelve un token con el rol del usuario. La clave y la expiración se leen de variables de entorno.
- Un filtro, `JwtAuthFilter` en `com.fixtrack.infra.security`, verifica el token en cada petición; sin token válido, expirado o de un usuario desactivado, responde 401.
- Las rutas `/api/**` dejan de estar abiertas: los permisos se definen por endpoint con `@PreAuthorize`, aplicados en el servidor según los permisos por rol de `.claude/rules/fixtrack.md`. El coordinador hereda todo lo del asesor.
- `PATCH /api/auth/password`: cambiar la contraseña exigiendo la actual y rechazando que la nueva sea igual.
- Deja abierta, sin token, la ruta pública `/api/public/**` para el portal del Sprint 3.

### S2-2. Búsqueda, entradas y alertas de stock

Amplía el dominio `inventory` de FixTrack:

- Búsqueda de repuestos por nombre o descripción con coincidencias parciales, sin distinguir mayúsculas ni tildes (usa una collation de MySQL insensible a tildes).
- Filtros combinables por tipo, modelo, marca, calidad y estado de stock, con orden y paginación resueltos en el servidor.
- `POST /api/spare-parts/{id}/entries`: entrada de mercancía que aumenta el stock y registra un movimiento `IN_PURCHASE` con fecha, motivo, costo y responsable.
- `PATCH /api/interventions/{id}/spare-parts/{usageId}`: corregir la cantidad de un consumo; el stock y el total se ajustan por la diferencia, en una sola transacción.
- Stock mínimo editable por repuesto.
- Estado de stock calculado en el servidor (`OK`, `LOW`, `OUT`) comparando el stock con el mínimo, y `GET /api/spare-parts/low-stock` para las alertas.

### S2-3. Daños adicionales, cobro y cierre de la orden

Amplía el dominio `intervention` (endpoints bajo `/api/interventions`) y crea el dominio `cash` de FixTrack:

- `POST /{id}/findings`: el técnico agrega daños adicionales (`ADDITIONAL`). `PATCH /{id}/labor`: define la mano de obra.
- Si el total (mano de obra más repuestos a precio de venta) supera el presupuesto inicial, los daños adicionales quedan en `PENDING` hasta que el asesor registre la respuesta del cliente (`PATCH /{id}/findings/{findingId}/approval`). Un daño rechazado no se repara.
- `PATCH /{id}/outcome`: al terminar, el técnico marca el resultado (`REPAIRED`, `NOT_REPAIRABLE` o `REJECTED_BY_CUSTOMER`) y los días de garantía (por defecto `DEFAULT_WARRANTY_DAYS`, 0 si no aplica). No se pasa a `READY` sin resultado ni con daños pendientes.
- No reparable o rechazado: exige un motivo, la mano de obra es el valor del diagnóstico (puede ser 0), los repuestos no usados vuelven al inventario con un movimiento `RETURN`, no hay garantía y la orden no se puede reabrir.
- `POST /{id}/payments` (dominio `cash`): abonos (`DEPOSIT`), cobro final (`FINAL`) y devoluciones (`REFUND`), con medio de pago (efectivo, transferencia o tarjeta).
- `PATCH /{id}/deliver`: guarda el total cobrado, borra la contraseña de desbloqueo, calcula `warrantyUntil` y pasa la orden a `DELIVERED`. No se permite si los pagos no cubren el total.
- `PATCH /{id}/cancel`: borra la contraseña de desbloqueo, devuelve los repuestos usados al inventario y deja registrado el abono por devolver. No se puede cancelar una orden entregada.
- `GET /{id}/receipts/intake` y `GET /{id}/receipts/delivery`: comprobantes en PDF. El de ingreso (código y clave de consulta destacados, con las instrucciones para el portal, cliente con documento y contactos (NT si no tiene; sin ningún contacto, la advertencia «Conserve este comprobante, no podremos contactarlo»), equipo, estado físico, servicio inicial, presupuesto, fecha estimada de entrega, abono y condiciones del servicio) sirve para imprimir y descargar; el de entrega (fecha estimada y fecha real de entrega, servicios realizados, repuestos reemplazados sin precio, total, pagos y garantía) solo para descargar. Las garantías usan los mismos comprobantes, con el código de la orden original.
- `POST /{id}/access-key`: al reimprimir el comprobante de ingreso genera una clave nueva y anula la anterior.

El cliente y el asesor solo ven el presupuesto y el total, sin desglose.

### S2-4. Panel de Inicio por rol

Crea en el dominio `intervention` el `ReportController` con `GET /api/dashboard`, que devuelve el panel de Inicio de FixTrack según el rol del usuario que hace la petición:

- Administrador: órdenes de hoy, atrasadas, en proceso y listas para entrega; órdenes recientes; y repuestos en alerta de stock.
- Asesor: órdenes listas para entrega, ingresadas hoy, atrasadas, en proceso y no reclamadas; órdenes recientes; y técnicos con su disponibilidad.
- Coordinador: lo del asesor, más los repuestos en alerta de stock.
- Técnico: sus órdenes asignadas, atrasadas, en proceso y esperando repuestos, y su lista de órdenes abiertas.

Usa consultas agregadas y records.

### S2-5. Front del Sprint 2

Actualiza el front de FixTrack en React:

- Interceptores en `apiConfig.js`: leer el token de `sessionStorage` y agregarlo en `Authorization` y, ante un 401, cerrar la sesión y volver al login. Rutas protegidas por rol y página para cambiar la contraseña.
- Inventario con búsqueda, filtros combinados, orden y paginación; registro de entradas; edición del stock mínimo; y alertas de stock bajo que se actualizan sin recargar la página.
- En el detalle de la orden: corrección de consumos, daños adicionales, mano de obra, aprobación del cliente (asesor), resultado y garantía (técnico), cancelación con confirmación y reimpresión del comprobante de ingreso con clave nueva.
- Página de salida: órdenes en `READY` con total, abonado y saldo; registro del cobro o la devolución; entrega habilitada solo con saldo 0; y descarga del comprobante de entrega. En el ingreso, abono opcional e impresión del comprobante.
- Inicio según el rol, con tarjetas de indicadores y tablas, en la ruta `/`. Cada tarjeta lleva al listado filtrado correspondiente. No se crea una página de indicadores aparte.

Cada componente tiene su `.jsx` y su `.css` separados.

### S2-6. Pruebas del Sprint 2

Crea pruebas unitarias con JUnit 5 y Mockito para los servicios del Sprint 2: token inválido o expirado, acceso denegado por rol, cambio de contraseña, corrección de consumos, entradas, estado de stock, bloqueo de `READY` con daños pendientes, cálculo del total, entrega con saldo pendiente, no reparable con restitución de repuestos y cancelación.

---

## Sprint 3: proveedores, caja, informes y portal

### S3-1. Proveedores y compatibilidad

Amplía el dominio `inventory` de FixTrack:

- `/api/suppliers` con `SupplierController`: CRUD de proveedores con NIT único y baja lógica.
- `GET` y `PUT /api/spare-parts/{id}/suppliers`: proveedores de cada repuesto, con su código, último costo, días de entrega y cuál es el preferido.
- `PUT /api/spare-parts/{id}/compatibilities`: modelos compatibles además del principal, y filtro `?deviceModelId=` para los repuestos que sirven en un modelo.

### S3-2. Solicitudes de repuestos

Crea las solicitudes de repuestos de FixTrack en el dominio `inventory`, con `PartRequestController`:

- `POST /api/interventions/{id}/part-requests`: el técnico solicita un repuesto que no hay, del catálogo o descrito a mano; la orden pasa a `WAITING_PARTS`.
- `/api/part-requests`: listado por estado, `PATCH /{id}/approve`, `PATCH /{id}/reject` (con motivo), `PATCH /{id}/order` (asigna proveedor, se sugiere el preferido) y `PATCH /{id}/receive` (costo real).
- Al recibir, en una sola transacción: entrada `IN_PURCHASE`, consumo en la orden que lo pidió con salida `OUT_REPAIR`, y actualización del último costo del proveedor. Así el repuesto no queda disponible para otra orden.
- Al cancelar una orden, se cancelan sus solicitudes que aún no se han pedido.
- Agrega al panel de Inicio del coordinador (`GET /api/dashboard`) las solicitudes pendientes.

### S3-3. Garantías, no reclamados, caja y parámetros

Amplía FixTrack con:

- En el dominio `intervention`, `POST /api/interventions/{id}/warranty`: abre una garantía anexada a la orden original (código `FT-00001-G1`, `-G2`…, con `warrantyNumber` asignado en la misma transacción), solo si la original está entregada y su `warrantyUntil` sigue vigente; no se abre sobre otra garantía ni extiende `warrantyUntil`. Registra el problema reportado, el estado físico, la fecha estimada de entrega y el técnico, y en el mismo paso permite actualizar el celular y el correo del cliente con la regla de «No tiene». Sigue los mismos estados de una orden, con mano de obra y repuestos a precio 0 y sin cobro. El detalle de la orden original incluye sus garantías.
- En el dominio `intervention`, `GET /api/interventions/unclaimed`: órdenes en `READY` desde hace más de `UNCLAIMED_ALERT_DAYS` días, con el celular y el correo del cliente, o marcadas «Sin contacto» si no tiene ninguno.
- En el dominio `cash`, `/api/cash-closings` con `CashClosingController`: `GET /today` (lo esperado del día por medio de pago para el usuario, restando devoluciones), `POST` (guarda el cierre con el efectivo contado y liga los pagos) y `GET` (listado). Un solo cierre por usuario y día, que no se edita. El asesor y el coordinador ven los suyos; el administrador, todos.
- En el dominio `setting`, `/api/settings` con `SystemSettingController`: consultar y actualizar los parámetros del sistema. Solo `ADMIN`.

### S3-4. Historial e informes

Agrega los informes de FixTrack al `ReportController` del dominio `intervention`, con consultas agregadas en la base de datos y records:

- `GET /api/stock-movements?sparePartId=`, en el `StockMovementController` del dominio `inventory`: historial paginado con fecha, tipo, cantidad, saldo resultante y responsable. Los movimientos no se editan.
- Informe de inventario: existencias por repuesto, valor total a costo y artículos en alerta, con filtros.
- Informe de mantenimientos: órdenes por rango de fechas, con totales por estado y por técnico.
- Informe de ganancias: para las órdenes entregadas en un rango de fechas, el total cobrado, el costo de repuestos y la ganancia, en total, por técnico y por orden, incluido el costo de las garantías. Exportable a Excel.
- El técnico ve el costo de repuestos y la ganancia solo de sus propias órdenes.

Los informes son solo para `ADMIN`, salvo el de inventario, que también ve el coordinador.

### S3-5. Portal del cliente

Crea en el dominio `intervention` el endpoint `POST /api/public/interventions/lookup`, sin token, que recibe el código de la orden (`FT-00001`) y la clave de consulta de 4 dígitos. Si se escribe el código de una garantía, se busca su orden original. Si coinciden, devuelve el equipo (tipo, marca y modelo), el estado, la línea de tiempo con las fechas del historial, la fecha de ingreso, la fecha estimada de entrega vigente, el presupuesto, el total, la garantía y las garantías anexadas con su propio estado y línea de tiempo. Nunca expone costos internos, datos del técnico ni la contraseña de desbloqueo. Si no coinciden, responde el mismo mensaje genérico. Limita los intentos por IP y bloquea el código 15 minutos tras 5 intentos fallidos (`failedLookupAttempts`, `lookupLockedUntil`), respondiendo el mismo mensaje genérico. Incluye la descarga de los comprobantes de esa orden.

### S3-6. Front del Sprint 3

Actualiza el front de FixTrack en React:

- Coordinador: proveedores, proveedores y modelos compatibles de cada repuesto, bandeja de solicitudes de repuestos e historial de movimientos.
- Técnico: solicitar repuestos desde el detalle de la orden, ver el estado de sus solicitudes, y ver el costo de repuestos y la ganancia de sus órdenes.
- Asesor: abrir garantías desde el detalle de la orden original (con actualización de los datos de contacto) y verlas como una pestaña de esa orden, equipos no reclamados resaltados en la página de salida y cierre de caja.
- Administrador: parámetros del sistema e informes de inventario, mantenimientos y ganancias, con selector de fechas, gráficos en Recharts y exportación a Excel.
- Portal público del cliente en `/consulta`, enlazado desde el login: código y clave de consulta, equipo, fecha estimada de entrega, garantías anexadas, estado actual destacado con su color, línea de tiempo, comprobantes y mensaje claro si los datos no coinciden. Debe ser cómodo desde el celular.

Cada componente tiene su `.jsx` y su `.css` separados.

### S3-7. Pruebas del Sprint 3

Crea pruebas unitarias con JUnit 5 y Mockito para los servicios del Sprint 3: recepción de una solicitud (entrada y salida en la misma operación), garantía vencida o sobre otra garantía, consulta con el código de una garantía, bloqueo del portal tras 5 intentos fallidos, cierre de caja duplicado, cálculo de la ganancia (incluidas garantías y órdenes no reparables) y portal con código y clave que no coinciden.
