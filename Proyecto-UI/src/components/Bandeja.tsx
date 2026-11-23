import { useCallback, useEffect, useState } from 'react'
import {
  FlowableTask,
  completarTarea,
  etiquetaCanal,
  etiquetaServicio,
  listarTareas,
  mapaVariables,
  parseOportunidad,
  procesoDeTarea,
  variablesDeTarea,
} from '../api/flowable'

const MOTIVOS = [
  'Precio',
  'Fecha no disponible',
  'Sin respuesta',
  'Compró en otro lugar',
  'Otro',
] as const

interface Props {
  refrescarToken: number
}

type VarMap = Record<string, string | number | boolean | Record<string, unknown> | null>

export default function Bandeja({ refrescarToken }: Props) {
  const [tareas, setTareas] = useState<FlowableTask[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [seleccionada, setSeleccionada] = useState<FlowableTask | null>(null)
  const [vars, setVars] = useState<VarMap>({})
  const [compro, setCompro] = useState<boolean | null>(null)
  const [motivo, setMotivo] = useState('')
  const [comentarioNoCompra, setComentarioNoCompra] = useState('')
  const [interesado, setInteresado] = useState<boolean | null>(null)
  const [comentarioContacto, setComentarioContacto] = useState('')
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
    setCompro(null)
    setMotivo('')
    setComentarioNoCompra('')
    setInteresado(null)
    setComentarioContacto('')
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
    if (compro === null) {
      setFeedback('Indica si compró o no.')
      return
    }
    setGuardando(true)
    setFeedback(null)
    try {
      await completarTarea(seleccionada.id, { compro })
      setSeleccionada(null)
      await cargar()
      setFeedback(
        compro
          ? 'Seguimiento registrado (compró).'
          : 'Seguimiento registrado. Sigue la tarea de motivo de no compra.',
      )
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Error al completar.')
    } finally {
      setGuardando(false)
    }
  }

  async function enviarMotivo() {
    if (!seleccionada) return
    if (!motivo.trim()) {
      setFeedback('El motivo de no compra es obligatorio.')
      return
    }
    setGuardando(true)
    setFeedback(null)
    try {
      await completarTarea(seleccionada.id, {
        motivoNoCompra: motivo.trim(),
        comentarioNoCompra: comentarioNoCompra.trim() || null,
      })
      setSeleccionada(null)
      await cargar()
      setFeedback('Motivo de no compra registrado.')
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
      setFeedback('Pedido marcado como gestionado.')
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Error al completar.')
    } finally {
      setGuardando(false)
    }
  }

  async function enviarContacto() {
    if (!seleccionada) return
    if (interesado === null) {
      setFeedback('Indica si el cliente está interesado.')
      return
    }
    setGuardando(true)
    setFeedback(null)
    try {
      await completarTarea(seleccionada.id, {
        interesado,
        comentarioContacto: comentarioContacto.trim() || null,
      })
      setSeleccionada(null)
      await cargar()
      setFeedback(
        interesado
          ? 'Contacto registrado (interesado). Puede seguir gestionar pedido de campaña.'
          : 'Contacto registrado (sin compra).',
      )
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
  const proceso = seleccionada ? procesoDeTarea(seleccionada) : 'lead'
  const oportunidad = parseOportunidad(vars)
  const esSeguimiento =
    key === 'realizarSeguimiento' ||
    seleccionada?.name.toLowerCase().includes('seguimiento') === true
  const esMotivo =
    key === 'registrarMotivo' ||
    seleccionada?.name.toLowerCase().includes('motivo') === true
  const esPedidoLead =
    key === 'gestionarPedido' ||
    (seleccionada?.name.toLowerCase().includes('pedido') === true &&
      key !== 'gestionarPedidoCampana' &&
      proceso === 'lead')
  const esPedidoCampana =
    key === 'gestionarPedidoCampana' ||
    (proceso === 'campana' &&
      seleccionada?.name.toLowerCase().includes('pedido') === true)
  const esContacto =
    key === 'contactarCliente' ||
    seleccionada?.name.toLowerCase().includes('contactar') === true
  const esRegistrar =
    key === 'registrarLead' ||
    (seleccionada?.name.toLowerCase().includes('registrar') === true &&
      !esMotivo)

  return (
    <section className="panel" aria-labelledby="titulo-bandeja">
      <header className="panel__header panel__header--row">
        <div>
          <h2 id="titulo-bandeja">Bandeja de tareas</h2>
          <p>
            Pendientes de leads (<code>peluditosLead</code>) y campañas (
            <code>peluditosOportunidad</code>).
          </p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={() => void cargar()}>
          Actualizar
        </button>
      </header>

      {cargando && <p className="muted">Cargando tareas…</p>}
      {error && <p className="aviso aviso--error">{error}</p>}
      {!cargando && !error && tareas.length === 0 && (
        <p className="vacio">
          No hay tareas pendientes. Anota un lead o inicia una campaña para empezar.
        </p>
      )}

      <div className="bandeja">
        <ul className="bandeja__lista" role="list">
          {tareas.map((t) => {
            const tipo = procesoDeTarea(t)
            return (
              <li key={t.id}>
                <button
                  type="button"
                  className={
                    seleccionada?.id === t.id ? 'tarea tarea--activa' : 'tarea'
                  }
                  onClick={() => void abrirTarea(t)}
                >
                  <span className="tarea__nombre">{t.name}</span>
                  <span className="tarea__meta">
                    <span
                      className={
                        tipo === 'campana' ? 'badge badge--campana' : 'badge badge--lead'
                      }
                    >
                      {tipo === 'campana' ? 'Campaña' : 'Lead'}
                    </span>
                    {new Date(t.createTime).toLocaleString('es-CL')}
                  </span>
                </button>
              </li>
            )
          })}
        </ul>

        {seleccionada && (
          <div className="bandeja__detalle">
            <h3>{seleccionada.name}</h3>
            <p className="muted">
              Proceso:{' '}
              {proceso === 'campana' ? 'Oportunidad comercial' : 'Gestión de leads'}
              {vars.nombreCampana != null && (
                <> · Campaña: {String(vars.nombreCampana)}</>
              )}
            </p>

            {proceso === 'lead' && (
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
            )}

            {proceso === 'campana' && oportunidad && (
              <dl className="ficha">
                <div>
                  <dt>Cliente</dt>
                  <dd>{oportunidad.cliente || '—'}</dd>
                </div>
                <div>
                  <dt>Teléfono</dt>
                  <dd>{oportunidad.telefono || '—'}</dd>
                </div>
                <div>
                  <dt>Mascota</dt>
                  <dd>{oportunidad.mascota || '—'}</dd>
                </div>
                <div>
                  <dt>Fecha relevante</dt>
                  <dd>{oportunidad.fechaRelevante || '—'}</dd>
                </div>
                <div>
                  <dt>ID oportunidad</dt>
                  <dd>{oportunidad.idOportunidad || '—'}</dd>
                </div>
              </dl>
            )}

            {esSeguimiento && (
              <div className="form form--compact">
                <fieldset>
                  <legend>¿Compró?</legend>
                  <label className="radio">
                    <input
                      type="radio"
                      name="compro"
                      checked={compro === true}
                      onChange={() => setCompro(true)}
                    />
                    Sí
                  </label>
                  <label className="radio">
                    <input
                      type="radio"
                      name="compro"
                      checked={compro === false}
                      onChange={() => setCompro(false)}
                    />
                    No
                  </label>
                </fieldset>
                <p className="muted">
                  Si no compró, el motivo se pedirá en la siguiente tarea del proceso.
                </p>
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

            {esMotivo && (
              <div className="form form--compact">
                <label>
                  Motivo de no compra
                  <select
                    value={motivo}
                    onChange={(e) => setMotivo(e.target.value)}
                    required
                  >
                    <option value="">Selecciona un motivo</option>
                    {MOTIVOS.map((m) => (
                      <option key={m} value={m}>
                        {m}
                      </option>
                    ))}
                  </select>
                </label>
                <label>
                  Comentario (opcional)
                  <input
                    value={comentarioNoCompra}
                    onChange={(e) => setComentarioNoCompra(e.target.value)}
                    placeholder="Detalle adicional"
                  />
                </label>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={guardando}
                  onClick={() => void enviarMotivo()}
                >
                  {guardando ? 'Guardando…' : 'Registrar motivo'}
                </button>
              </div>
            )}

            {esPedidoLead && (
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

            {esContacto && (
              <div className="form form--compact">
                <fieldset>
                  <legend>¿Interesado?</legend>
                  <label className="radio">
                    <input
                      type="radio"
                      name="interesado"
                      checked={interesado === true}
                      onChange={() => setInteresado(true)}
                    />
                    Sí
                  </label>
                  <label className="radio">
                    <input
                      type="radio"
                      name="interesado"
                      checked={interesado === false}
                      onChange={() => setInteresado(false)}
                    />
                    No
                  </label>
                </fieldset>
                <label>
                  Comentario del contacto (opcional)
                  <input
                    value={comentarioContacto}
                    onChange={(e) => setComentarioContacto(e.target.value)}
                    placeholder="Notas de la llamada o mensaje"
                  />
                </label>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={guardando}
                  onClick={() => void enviarContacto()}
                >
                  {guardando ? 'Guardando…' : 'Completar contacto'}
                </button>
              </div>
            )}

            {esPedidoCampana && (
              <div className="form form--compact">
                <p className="muted">Gestiona el pedido de la campaña.</p>
                <label>
                  Nota (opcional)
                  <input
                    value={notaPedido}
                    onChange={(e) => setNotaPedido(e.target.value)}
                    placeholder="Ej. pedido de campaña confirmado"
                  />
                </label>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={guardando}
                  onClick={() => void enviarPedido()}
                >
                  {guardando ? 'Guardando…' : 'Marcar pedido de campaña'}
                </button>
              </div>
            )}

            {esRegistrar && (
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
                  feedback.toLowerCase().includes('flowable') ||
                  feedback.toLowerCase().includes('indica') ||
                  feedback.toLowerCase().includes('obligatorio')
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
