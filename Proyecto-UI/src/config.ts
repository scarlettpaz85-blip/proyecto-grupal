/**
 * Configuración de conexión (valores desde `.env` / `.env.production`).
 *
 * Desarrollo: deja VITE_API_BASE_URL y VITE_FLOWABLE_BASE_URL vacíos
 * para usar rutas relativas (/api, /flowable-rest) vía proxy de Vite.
 *
 * Producción / Azure: completar esas URLs con las direcciones reales
 * acordadas por el equipo (no inventar hosts).
 */

const trimSlash = (value: string) => value.replace(/\/+$/, '')

/** Base del web service (sin barra final). Vacío = ruta relativa `/api`. */
export const API_BASE = trimSlash(
  import.meta.env.VITE_API_BASE_URL?.trim() || '/api',
)

/**
 * Base REST de Flowable incluyendo `/service` (sin barra final).
 * Vacío = ruta relativa `/flowable-rest/service`.
 */
export const FLOWABLE_BASE = trimSlash(
  import.meta.env.VITE_FLOWABLE_BASE_URL?.trim() || '/flowable-rest/service',
)

const flowableUser = import.meta.env.VITE_FLOWABLE_USER?.trim() || 'rest-admin'
const flowablePassword =
  import.meta.env.VITE_FLOWABLE_PASSWORD?.trim() || 'test'

export const FLOWABLE_AUTH =
  'Basic ' + btoa(`${flowableUser}:${flowablePassword}`)

/**
 * Origen público de la UI para CORS.
 * Por defecto el valor acordado de desarrollo: http://localhost:3000
 * En producción se define en VITE_UI_PUBLIC_ORIGIN cuando el equipo lo tenga.
 */
export const UI_PUBLIC_ORIGIN =
  import.meta.env.VITE_UI_PUBLIC_ORIGIN?.trim() || 'http://localhost:3000'
