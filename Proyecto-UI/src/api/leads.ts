export interface LeadWs {
  id: number
  nombre: string
  telefono?: string
  correo?: string
  canal?: string
  interes?: string
  estado?: string
  registrado?: boolean
  fechaRegistro?: string
}

export async function listarLeadsWs(): Promise<LeadWs[]> {
  const response = await fetch('/api/leads', {
    headers: { Accept: 'application/json' },
  })
  if (!response.ok) {
    throw new Error(`WS ${response.status}: no se pudo listar leads`)
  }
  return response.json()
}

export async function healthWs(): Promise<boolean> {
  try {
    const response = await fetch('/api/health')
    return response.ok
  } catch {
    return false
  }
}
