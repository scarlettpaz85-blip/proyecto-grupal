import { useCallback, useEffect, useState } from 'react'
import {
  FlowableTask,
  FlowableVariable,
  completarTarea,
  etiquetaCanal,
  etiquetaServicio,
  listarTareas,
  variablesDeTarea,
} from '../api/flowable'

interface Props {
  refrescarToken: number
}

function mapaVariables(vars: FlowableVariable[]): Record<string, string | boolean | number | null> {
  const out: Record<string, string | boolean | number | null> = {}
  for (const v of vars) {
    out[v.name] = v.value
  }
  return out
}

export default function Bandeja({ refrescarToken }: Props) {
  const [tareas, setTareas] = useState<FlowableTask[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [seleccionada, setSeleccionada] = useState<FlowableTask | null>(null)
  const [vars, setVars] = useState<Record<string, string | boolean | number | null>>({})
  const [compro, setCompro] = useState(true)
  const [motivo, setMotivo] = useState('')
  const [notaPedido, setNotaPedido] = useState('')
  const [guardando, setGuardando] = useState(false)
  const [feedback, setFeedback] = useState<string | null>(null)

  const cargar = useCallback(async () => {
    setCargando(true)
    setError(null)
    try {
      const lista = await listarTareas()
      setTareas(lista)
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No se pudo cargar la bandeja. ¿Flowable está en :8080?',
      )
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    void cargar()
  }, [cargar, refrescarToken])

  async function abrirTarea(tarea: FlowableTask) {
    setSeleccionada(tarea)
    setFeedback(null)
    setCompro(true)
    setMotivo('')
    setNotaPedido('')
    try {
      const lista = await variablesDeTarea(tarea.id)
      setVars(mapaVariables(lista))
    } catch {
      setVars({})
    }
  }

  async function enviarSeguimiento() {
    if (!seleccionada) return
    if (!compro && !motivo.trim()) {
      setFeedback('Si no compró, indica el motivo.')
      return
    }
    setGuardando(true)
    setFeedback(null)
    try {
      await completarTarea(seleccionada.id, {
        compro,
        motivoNoCompra: compro ? null : motivo.trim(),
      })
      setSeleccionada(null)
      await cargar()
      setFeedback('Seguimiento registrado.')
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Error al completar.')
    } finally {
      setGuardando(false)
    }
  }

  async function enviarPedido() {
    if (!seleccionada) return
    setGuardando(true)
    setFeedback(null)
    try {
      await completarTarea(seleccionada.id, {
        notaPedido: notaPedido.trim() || 'Pedido gestionado',
      })
      setSeleccionada(null)
      await cargar()
      setFeedback('Pedido marcado como gestionado. Lead convertido.')
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Error al completar.')
    } finally {
      setGuardando(false)
    }
  }

  async function completarRegistroManual() {
    if (!seleccionada) return
    setGuardando(true)
    setFeedback(null)
    try {
      await completarTarea(seleccionada.id, {
        nombre: String(vars.nombre ?? ''),
        telefono: String(vars.telefono ?? ''),
        correo: String(vars.correo ?? ''),
        canal: String(vars.canal ?? 'instagram'),
        interes: String(vars.interes ?? ''),
        servicioRequerido: String(vars.servicioRequerido ?? 'reposteria'),
      })
      setSeleccionada(null)
      await cargar()
      setFeedback('Lead clasificado y enviado al sistema.')
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Error al completar.')
    } finally {
      setGuardando(false)
    }
  }

  const key = seleccionada?.taskDefinitionKey ?? ''

  return (
    <section className="panel" aria-labelledby="titulo-bandeja">
      <header className="panel__header panel__header--row">
        <div>
          <h2 id="titulo-bandeja">Bandeja de tareas</h2>
          <p>Pendientes del proceso: seguimiento y gestión de pedido.</p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={() => void cargar()}>
          Actualizar
        </button>
      </header>

      {cargando && <p className="muted">Cargando tareas…</p>}
      {error && <p className="aviso aviso--error">{error}</p>}
      {!cargando && !error && tareas.length === 0 && (
        <p className="vacio">No hay tareas pendientes. Anota una consulta para empezar.</p>
      )}

      <div className="bandeja">
        <ul className="bandeja__lista" role="list">
          {tareas.map((t) => (
            <li key={t.id}>
              <button
                type="button"
                className={
                  seleccionada?.id === t.id
                    ? 'tarea tarea--activa'
                    : 'tarea'
                }
                onClick={() => void abrirTarea(t)}
              >
                <span className="tarea__nombre">{t.name}</span>
                <span className="tarea__meta">
                  {new Date(t.createTime).toLocaleString('es-CL')}
                </span>
              </button>
            </li>
          ))}
        </ul>

        {seleccionada && (
          <div className="bandeja__detalle">
            <h3>{seleccionada.name}</h3>
            <dl className="ficha">
              <div>
                <dt>Nombre</dt>
                <dd>{String(vars.nombre ?? '—')}</dd>
              </div>
              <div>
                <dt>Canal</dt>
                <dd>{etiquetaCanal(String(vars.canal ?? ''))}</dd>
              </div>
              <div>
                <dt>Interés</dt>
                <dd>{String(vars.interes ?? '—')}</dd>
              </div>
              <div>
                <dt>Servicio</dt>
                <dd>{etiquetaServicio(String(vars.servicioRequerido ?? ''))}</dd>
              </div>
              {vars.leadId != null && (
                <div>
                  <dt>Lead ID</dt>
                  <dd>{String(vars.leadId)}</dd>
                </div>
              )}
              <div>
                <dt>Teléfono</dt>
                <dd>{String(vars.telefono ?? '—')}</dd>
              </div>
              <div>
                <dt>Correo</dt>
                <dd>{String(vars.correo ?? '—')}</dd>
              </div>
            </dl>

            {(key === 'realizarSeguimiento' ||
              seleccionada.name.toLowerCase().includes('seguimiento')) && (
              <div className="form form--compact">
                <fieldset>
                  <legend>¿Compró?</legend>
                  <label className="radio">
                    <input
                      type="radio"
                      name="compro"
                      checked={compro}
                      onChange={() => setCompro(true)}
                    />
                    Sí
                  </label>
                  <label className="radio">
                    <input
                      type="radio"
                      name="compro"
                      checked={!compro}
                      onChange={() => setCompro(false)}
                    />
                    No
                  </label>
                </fieldset>
                {!compro && (
                  <label>
                    Motivo de no compra
                    <input
                      value={motivo}
                      onChange={(e) => setMotivo(e.target.value)}
                      placeholder="Ej. precio, demora, eligió otro"
                    />
                  </label>
                )}
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={guardando}
                  onClick={() => void enviarSeguimiento()}
                >
                  {guardando ? 'Guardando…' : 'Completar seguimiento'}
                </button>
              </div>
            )}

            {(key === 'gestionarPedido' ||
              seleccionada.name.toLowerCase().includes('pedido')) && (
              <div className="form form--compact">
                <p className="muted">
                  Coordina el pedido según la ruta:{' '}
                  <strong>
                    {etiquetaServicio(String(vars.servicioRequerido ?? ''))}
                  </strong>
                </p>
                <label>
                  Nota (opcional)
                  <input
                    value={notaPedido}
                    onChange={(e) => setNotaPedido(e.target.value)}
                    placeholder="Ej. pedido listo para retiro"
                  />
                </label>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={guardando}
                  onClick={() => void enviarPedido()}
                >
                  {guardando ? 'Guardando…' : 'Marcar pedido gestionado'}
                </button>
              </div>
            )}

            {(key === 'registrarLead' ||
              seleccionada.name.toLowerCase().includes('registrar')) && (
              <div className="form form--compact">
                <p className="muted">
                  Confirma los datos y envía al sistema (clasificación).
                </p>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={guardando}
                  onClick={() => void completarRegistroManual()}
                >
                  {guardando ? 'Guardando…' : 'Confirmar clasificación'}
                </button>
              </div>
            )}

            {feedback && (
              <p
                className={
                  feedback.toLowerCase().includes('error') ||
                  feedback.toLowerCase().includes('flowable')
                    ? 'aviso aviso--error'
                    : 'aviso aviso--ok'
                }
              >
                {feedback}
              </p>
            )}
          </div>
        )}
      </div>
    </section>
  )
}
