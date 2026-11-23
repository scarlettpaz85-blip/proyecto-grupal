const FLOWABLE_BASE = '/flowable-rest/service'
export const PROCESS_KEY_LEAD = 'peluditosLead'
export const PROCESS_KEY_OPORTUNIDAD = 'peluditosOportunidad'
const AUTH = 'Basic ' + btoa('rest-admin:test')

export type Canal = 'instagram' | 'whatsapp' | 'facebook' | 'tiktok' | 'web' | 'telefono'
export type Servicio = 'reposteria' | 'banos' | 'repuestos'

export interface LeadDatos {
  nombre: string
  telefono: string
  correo: string
  canal: Canal
  interes: string
  servicioRequerido: Servicio
}

export interface CampanaDatos {
  idFechaRelevante: number
  nombreCampana: string
}

export interface FlowableTask {
  id: string
  name: string
  taskDefinitionKey: string
  processInstanceId: string
  processDefinitionId?: string
  createTime: string
  assignee?: string | null
}

export interface FlowableVariable {
  name: string
  type?: string
  value: string | number | boolean | Record<string, unknown> | null
}

export interface OportunidadVar {
  idOportunidad: number
  idCliente: number
  cliente: string
  telefono: string
  mascota: string
  fechaRelevante: string
}

type VarMap = Record<string, string | number | boolean | null | undefined>

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${FLOWABLE_BASE}${path}`, {
    ...init,
    headers: {
      Accept: 'application/json',
      Authorization: AUTH,
      ...(init?.body ? { 'Content-Type': 'application/json' } : {}),
      ...init?.headers,
    },
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(`Flowable ${response.status}: ${text || response.statusText}`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  const contentType = response.headers.get('content-type') ?? ''
  if (!contentType.includes('application/json')) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

function toVariables(data: VarMap | LeadDatos | CampanaDatos): FlowableVariable[] {
  return Object.entries(data as VarMap)
    .filter(([, value]) => value !== undefined && value !== null && value !== '')
    .map(([name, value]) => {
      if (typeof value === 'boolean') {
        return { name, type: 'boolean', value }
      }
      if (typeof value === 'number') {
        return { name, type: 'integer', value }
      }
      return { name, type: 'string', value: String(value) }
    })
}

export async function iniciarProceso(datos: LeadDatos): Promise<{ id: string }> {
  return request('/runtime/process-instances', {
    method: 'POST',
    body: JSON.stringify({
      processDefinitionKey: PROCESS_KEY_LEAD,
      businessKey: `LEAD-${Date.now()}`,
      variables: toVariables(datos),
    }),
  })
}

export async function iniciarCampana(datos: CampanaDatos): Promise<{ id: string }> {
  return request('/runtime/process-instances', {
    method: 'POST',
    body: JSON.stringify({
      processDefinitionKey: PROCESS_KEY_OPORTUNIDAD,
      businessKey: `CAMP-${datos.idFechaRelevante}-${Date.now()}`,
      variables: toVariables({
        idFechaRelevante: datos.idFechaRelevante,
        nombreCampana: datos.nombreCampana,
      }),
    }),
  })
}

async function listarTareasPorProceso(processKey: string): Promise<FlowableTask[]> {
  const data = await request<{ data: FlowableTask[] }>(
    `/runtime/tasks?processDefinitionKey=${processKey}&sort=createTime&order=desc`,
  )
  return data.data ?? []
}

export async function listarTareas(): Promise<FlowableTask[]> {
  const [leads, campañas] = await Promise.all([
    listarTareasPorProceso(PROCESS_KEY_LEAD),
    listarTareasPorProceso(PROCESS_KEY_OPORTUNIDAD),
  ])
  return [...leads, ...campañas].sort(
    (a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime(),
  )
}

export function procesoDeTarea(tarea: FlowableTask): 'lead' | 'campana' {
  const def = tarea.processDefinitionId ?? ''
  if (def.includes(PROCESS_KEY_OPORTUNIDAD)) return 'campana'
  if (def.includes(PROCESS_KEY_LEAD)) return 'lead'
  const key = tarea.taskDefinitionKey
  if (
    key === 'contactarCliente' ||
    key === 'gestionarPedidoCampana'
  ) {
    return 'campana'
  }
  return 'lead'
}

export async function variablesDeTarea(taskId: string): Promise<FlowableVariable[]> {
  return request(`/runtime/tasks/${taskId}/variables`)
}

export async function completarTarea(
  taskId: string,
  variables: VarMap | LeadDatos = {},
): Promise<void> {
  await request(`/runtime/tasks/${taskId}`, {
    method: 'POST',
    body: JSON.stringify({
      action: 'complete',
      variables: toVariables(variables),
    }),
  })
}

export async function registrarYClasificarLead(datos: LeadDatos): Promise<void> {
  const instancia = await iniciarProceso(datos)
  await new Promise((r) => setTimeout(r, 400))
  const tareas = await listarTareasPorProceso(PROCESS_KEY_LEAD)
  const registrar = tareas.find(
    (t) =>
      t.processInstanceId === instancia.id &&
      (t.taskDefinitionKey === 'registrarLead' ||
        t.name.toLowerCase().includes('registrar')),
  )
  if (!registrar) {
    throw new Error(
      'Se inició el proceso, pero no apareció la tarea "Registrar y clasificar lead". Revisa que el BPMN esté desplegado.',
    )
  }
  await completarTarea(registrar.id, datos)
}

export function mapaVariables(
  vars: FlowableVariable[],
): Record<string, string | number | boolean | Record<string, unknown> | null> {
  const out: Record<string, string | number | boolean | Record<string, unknown> | null> = {}
  for (const v of vars) {
    out[v.name] = v.value
  }
  return out
}

export function parseOportunidad(
  vars: Record<string, string | number | boolean | Record<string, unknown> | null>,
): OportunidadVar | null {
  const raw = vars.oportunidad
  if (raw == null) return null

  let obj: Record<string, unknown>
  if (typeof raw === 'string') {
    try {
      obj = JSON.parse(raw) as Record<string, unknown>
    } catch {
      return null
    }
  } else if (typeof raw === 'object') {
    obj = raw
  } else {
    return null
  }

  return {
    idOportunidad: Number(obj.idOportunidad ?? 0),
    idCliente: Number(obj.idCliente ?? 0),
    cliente: String(obj.cliente ?? ''),
    telefono: String(obj.telefono ?? ''),
    mascota: String(obj.mascota ?? ''),
    fechaRelevante: String(obj.fechaRelevante ?? ''),
  }
}

export function etiquetaServicio(valor: string): string {
  switch (valor) {
    case 'reposteria':
      return 'Repostería para mascotas'
    case 'banos':
      return 'Baños para perros'
    case 'repuestos':
      return 'Repuestos de baños ecológicos'
    default:
      return valor || '—'
  }
}

export function etiquetaCanal(valor: string): string {
  const map: Record<string, string> = {
    instagram: 'Instagram',
    whatsapp: 'WhatsApp',
    facebook: 'Facebook',
    tiktok: 'TikTok',
    web: 'Tienda web',
    telefono: 'Teléfono',
  }
  return map[valor] ?? valor ?? '—'
}
