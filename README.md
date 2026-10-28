# FixTrack

Sistema web de gestión de reparaciones e inventario para una tienda de reparación de dispositivos móviles: teléfonos, tablets, relojes inteligentes, parlantes y otros equipos.

Cubre el ciclo completo de una reparación: el ingreso del equipo, el diagnóstico, los repuestos usados, el cobro y la entrega, y después la garantía. También lleva el inventario, los proveedores, el cierre de caja y los informes de ganancias. Los clientes consultan el estado de su equipo en un portal público, sin crear cuenta.

## Funcionalidades

- **Órdenes de reparación** con código consecutivo (`FT-00001`), fecha estimada de entrega reprogramable, historial de estados y comprobantes en PDF.
- **Portal del cliente** (`/consulta`): el cliente ve el estado de su orden con el código y una clave de 4 dígitos.
- **Diagnóstico y daños adicionales**, con aprobación del cliente cuando el total supera el presupuesto.
- **Inventario de repuestos** con calidades (original, OEM, genérico), stock mínimo, alertas, entradas de mercancía y movimientos.
- **Solicitudes de repuestos** del técnico al coordinador cuando no hay unidades en inventario.
- **Pagos y caja**: abonos, cobro final, entrega y cierre de caja diario.
- **Garantías** anexadas a la orden original (`FT-00001-G1`), a precio cero.
- **Informes** de ganancias y de trabajo por técnico, con gráficos.
- **Panel de inicio** con los indicadores de cada rol.

## Roles

| Rol | Qué hace |
|---|---|
| Administrador | Todo, más personas, roles, especialidades, informes y parámetros |
| Coordinador | Todo lo del asesor, más inventario, catálogos, proveedores y solicitudes de repuestos |
| Asesor | Recibe equipos, abre órdenes, registra pagos, entrega equipos y cierra la caja |
| Técnico | Diagnostica, repara, registra repuestos y mano de obra, y solicita repuestos |

Los clientes no inician sesión: consultan su orden en el portal público.

## Tecnologías

| Parte | Tecnologías |
|---|---|
| Backend (`fixtrackback`) | Java 25, Spring Boot 4.1, Spring Data JPA, Spring Security con JWT, Bean Validation, Lombok, OpenPDF |
| Base de datos | MySQL 8 |
| Frontend (`fixtrack-front`) | React 19, Vite 8, React Router, Axios, Recharts, React Icons (Material Design), CSS propio |

## Estructura

```
FixTrack/
├── fixtrackback/     # API REST con Spring Boot (http://localhost:8080/api)
└── fixtrack-front/   # Aplicación web con React y Vite (http://localhost:5173)
```

El backend está organizado por dominio (`person`, `device`, `inventory`, `intervention`, `cash`, `setting`). Cada dominio tiene sus capas de controlador, servicio, repositorio, entidad, DTO y mapper.

## Requisitos

- JDK 25
- MySQL 8 corriendo en `localhost:3306` con el usuario `root`
- Node.js 20.19 o superior (lo exige Vite 8)

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Rockert6402/FixTrack.git
cd FixTrack
```

### 2. Configurar y arrancar el backend

Copia `fixtrackback/.env.example` como `fixtrackback/.env` y completa los valores:

| Variable | Obligatoria | Descripción |
|---|---|---|
| `DB_PASSWORD` | Sí | Contraseña del usuario `root` de MySQL |
| `ENCRYPTION_KEY` | Sí | Llave AES de 32 bytes en Base64, para cifrar la contraseña de desbloqueo de los equipos |
| `JWT_SECRET` | Sí | Llave HMAC de al menos 32 bytes en Base64, para firmar el token de sesión |
| `ADMIN_PASSWORD` | La primera vez | Contraseña del administrador inicial; solo se usa si no hay ningún administrador activo |
| `ADMIN_EMAIL` | No | Correo del administrador inicial (por defecto `admin@fixtrack.local`) |
| `JWT_EXPIRATION` | No | Duración de la sesión (por defecto `8h`) |
| `DEMO_USERS_PASSWORD` | No | Si tiene valor, crea los usuarios de prueba de coordinador, asesor y técnico |

Las llaves se pueden generar con:

```bash
openssl rand -base64 32
```

Luego arranca el backend:

```bash
cd fixtrackback
./mvnw spring-boot:run      # en Windows: mvnw.cmd spring-boot:run
```

La base de datos `fixtrack` y sus tablas se crean solas al arrancar.

### 3. Configurar y arrancar el frontend

```bash
cd fixtrack-front
cp .env.example .env        # VITE_API_URL=http://localhost:8080/api
npm install
npm run dev
```

Abre http://localhost:5173. El frontend usa siempre el puerto 5173, porque el backend solo acepta peticiones (CORS) desde ese origen.

## Usuarios de prueba

Al arrancar, el backend crea estas cuentas si no existen:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@fixtrack.local` | la de `ADMIN_PASSWORD` |
| Coordinador | `coordinador@fixtrack.local` | la de `DEMO_USERS_PASSWORD` |
| Asesor | `asesor.prueba@fixtrack.local` | la de `DEMO_USERS_PASSWORD` |
| Técnico | `tecnico@fixtrack.local` | la de `DEMO_USERS_PASSWORD` |

Las cuentas de prueba son solo para desarrollo. En producción deja `DEMO_USERS_PASSWORD` vacío.

## Comandos útiles

| Dónde | Comando | Qué hace |
|---|---|---|
| `fixtrackback` | `./mvnw test` | Ejecuta las pruebas del backend |
| `fixtrack-front` | `npm run dev` | Servidor de desarrollo |
| `fixtrack-front` | `npm run build` | Compila para producción en `dist/` |
| `fixtrack-front` | `npm run lint` | Revisa el código con oxlint |

## Configuración general

- Toda la aplicación trabaja en la zona horaria `America/Bogota`.
- El dinero se muestra en pesos colombianos (COP).
- Los secretos solo se leen de variables de entorno: los archivos `.env` no se suben al repositorio.
