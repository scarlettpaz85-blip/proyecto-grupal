import { API_BASE } from '../config'

export interface FechaRelevante {
  idFechaRelevante: number
  nombre: string
  tipo?: string
  fecha?: string
  diasAnticipacion?: number
  segmento?: string
  activa?: boolean
}

export interface OportunidadWs {
  idOportunidad: number
  idCliente: number
  cliente: string
  telefono?: string
  mascota?: string
  fechaRelevante?: string
  estado?: string
  resultado?: string
}

export async function listarFechasRelevantes(): Promise<FechaRelevante[]> {
  const response = await fetch(`${API_BASE}/fechas-relevantes`, {
    headers: { Accept: 'application/json' },
  })
  if (!response.ok) {
    throw new Error(`WS ${response.status}: no se pudieron listar fechas relevantes`)
  }
  return response.json()
}

export async function listarOportunidades(
  idFechaRelevante?: number,
): Promise<OportunidadWs[]> {
  const qs =
    idFechaRelevante != null && idFechaRelevante > 0
      ? `?idFechaRelevante=${idFechaRelevante}`
      : ''
  const response = await fetch(`${API_BASE}/oportunidades${qs}`, {
    headers: { Accept: 'application/json' },
  })
  if (!response.ok) {
    throw new Error(`WS ${response.status}: no se pudieron listar oportunidades`)
  }
  return response.json()
}
