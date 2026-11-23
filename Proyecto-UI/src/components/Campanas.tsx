import { FormEvent, useCallback, useEffect, useState } from 'react'
import { iniciarCampana } from '../api/flowable'
import {
  FechaRelevante,
  OportunidadWs,
  listarFechasRelevantes,
  listarOportunidades,
} from '../api/oportunidades'
import { healthWs } from '../api/leads'

interface Props {
  onIniciada: () => void
  refrescarToken: number
}

export default function Campanas({ onIniciada, refrescarToken }: Props) {
  const [fechas, setFechas] = useState<FechaRelevante[]>([])
  const [idFecha, setIdFecha] = useState<number | ''>('')
  const [nombreCampana, setNombreCampana] = useState('')
  const [oportunidades, setOportunidades] = useState<OportunidadWs[]>([])
  const [wsOk, setWsOk] = useState<boolean | null>(null)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [mensaje, setMensaje] = useState<string | null>(null)

  const cargarOportunidades = useCallback(async (fechaId: number | '') => {
    if (fechaId === '' || fechaId <= 0) {
      setOportunidades([])
      return
    }
    try {
      setOportunidades(await listarOportunidades(fechaId))
    } catch {
      setOportunidades([])
    }
  }, [])

  const cargarFechas = useCallback(async () => {
    setError(null)
    const vivo = await healthWs()
    setWsOk(vivo)
    if (!vivo) {
      setFechas([])
      setOportunidades([])
      return
    }
    try {
      const lista = await listarFechasRelevantes()
      setFechas(lista)
      setIdFecha((actual) => {
        if (actual !== '' || lista.length === 0) return actual
        return lista[0].idFechaRelevante
      })
      setNombreCampana((actual) =>
        actual.trim() !== '' || lista.length === 0 ? actual : lista[0].nombre,
      )
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudieron cargar las fechas')
    }
  }, [])

  useEffect(() => {
    void cargarFechas()
  }, [cargarFechas, refrescarToken])

  useEffect(() => {
    void cargarOportunidades(idFecha)
  }, [idFecha, cargarOportunidades, refrescarToken])

  function onCambioFecha(valor: string) {
    const id = Number(valor)
    setIdFecha(id)
    const fecha = fechas.find((f) => f.idFechaRelevante === id)
    if (fecha) {
      setNombreCampana(fecha.nombre)
    }
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setMensaje(null)

    if (idFecha === '' || idFecha <= 0) {
      setError('Elige una fecha relevante para la campaña.')
      return
    }
    if (!nombreCampana.trim()) {
      setError('Indica un nombre para la campaña.')
      return
    }

    setEnviando(true)
    try {
      await iniciarCampana({
        idFechaRelevante: idFecha,
        nombreCampana: nombreCampana.trim(),
      })
      setMensaje(
        'Campaña iniciada. El worker generará las oportunidades; revisa la bandeja para contactar clientes.',
      )
      await cargarOportunidades(idFecha)
      onIniciada()
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No se pudo iniciar la campaña. ¿Está desplegado peluditosOportunidad?',
      )
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section className="panel" aria-labelledby="titulo-campanas">
      <header className="panel__header panel__header--row">
        <div>
          <h2 id="titulo-campanas">Campañas / oportunidades</h2>
          <p>
            Elige una fecha relevante (Navidad, cumpleaños, etc.), inicia el proceso{' '}
            <code>peluditosOportunidad</code> y consulta el estado en el web service.
          </p>
        </div>
        <span
          className={wsOk ? 'pill pill--ok' : 'pill pill--warn'}
          title="Estado de /api/health"
        >
          WS {wsOk === null ? '…' : wsOk ? 'activo' : 'sin respuesta'}
        </span>
      </header>

      {wsOk === false && (
        <p className="muted">
          El web service no responde. Sin fechas relevantes no se puede iniciar una campaña.
        </p>
      )}

      <form className="form" onSubmit={onSubmit}>
        <div className="form__grid">
          <label>
            Fecha relevante
            <select
              value={idFecha === '' ? '' : String(idFecha)}
              onChange={(e) => onCambioFecha(e.target.value)}
              disabled={fechas.length === 0}
            >
              {fechas.length === 0 && <option value="">Sin fechas activas</option>}
              {fechas.map((f) => (
                <option key={f.idFechaRelevante} value={f.idFechaRelevante}>
                  {f.nombre}
                  {f.fecha ? ` (${f.fecha})` : ''}
                </option>
              ))}
            </select>
          </label>

          <label>
            Nombre de la campaña
            <input
              required
              value={nombreCampana}
              onChange={(e) => setNombreCampana(e.target.value)}
              placeholder="Ej. Navidad 2026"
            />
          </label>
        </div>

        {error && (
          <p className="aviso aviso--error" role="alert">
            {error}
          </p>
        )}
        {mensaje && (
          <p className="aviso aviso--ok" role="status">
            {mensaje}
          </p>
        )}

        <div className="form__actions">
          <button
            type="submit"
            className="btn btn--primary"
            disabled={enviando || fechas.length === 0}
          >
            {enviando ? 'Iniciando…' : 'Iniciar campaña'}
          </button>
          <button
            type="button"
            className="btn btn--ghost"
            onClick={() => void cargarOportunidades(idFecha)}
          >
            Actualizar lista
          </button>
        </div>
      </form>

      <h3 className="subtitulo">Oportunidades en el WS</h3>
      {oportunidades.length === 0 ? (
        <p className="vacio">
          Aún no hay oportunidades para esta fecha. Aparecen cuando el worker ejecuta
          generar.
        </p>
      ) : (
        <div className="tabla-wrap">
          <table className="tabla">
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Mascota</th>
                <th>Teléfono</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {oportunidades.map((o) => (
                <tr key={o.idOportunidad}>
                  <td>{o.idOportunidad}</td>
                  <td>{o.cliente}</td>
                  <td>{o.mascota ?? '—'}</td>
                  <td>{o.telefono ?? '—'}</td>
                  <td>{o.estado ?? o.resultado ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
