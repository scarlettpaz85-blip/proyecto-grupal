const FLOWABLE_BASE = '/flowable-rest/service'
const PROCESS_KEY = 'peluditosLead'
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

export interface FlowableTask {
  id: string
  name: string
  taskDefinitionKey: string
  processInstanceId: string
  createTime: string
  assignee?: string | null
}

export interface FlowableVariable {
  name: string
  type?: string
  value: string | number | boolean | null
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

function toVariables(data: VarMap | LeadDatos): FlowableVariable[] {
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
      processDefinitionKey: PROCESS_KEY,
      businessKey: `LEAD-${Date.now()}`,
      variables: toVariables(datos),
    }),
  })
}

export async function listarTareas(): Promise<FlowableTask[]> {
  const data = await request<{ data: FlowableTask[] }>(
    `/runtime/tasks?processDefinitionKey=${PROCESS_KEY}&sort=createTime&order=desc`,
  )
  return data.data ?? []
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
  const tareas = await listarTareas()
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
