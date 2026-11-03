import { useCallback, useEffect, useState } from 'react'
import { LeadWs, healthWs, listarLeadsWs } from '../api/leads'
import { etiquetaCanal } from '../api/flowable'

interface Props {
  refrescarToken: number
}

export default function ListaLeads({ refrescarToken }: Props) {
  const [leads, setLeads] = useState<LeadWs[]>([])
  const [ok, setOk] = useState<boolean | null>(null)
  const [error, setError] = useState<string | null>(null)

  const cargar = useCallback(async () => {
    setError(null)
    const vivo = await healthWs()
    setOk(vivo)
    if (!vivo) {
      setLeads([])
      return
    }
    try {
      setLeads(await listarLeadsWs())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al listar leads')
    }
  }, [])

  useEffect(() => {
    void cargar()
  }, [cargar, refrescarToken])

  return (
    <section className="panel" aria-labelledby="titulo-leads">
      <header className="panel__header panel__header--row">
        <div>
          <h2 id="titulo-leads">Lista de interesados (WS)</h2>
          <p>
            Vista de lo guardado en el web service. Se llena cuando el worker registra el
            lead.
          </p>
        </div>
        <span
          className={ok ? 'pill pill--ok' : 'pill pill--warn'}
          title="Estado de http://localhost:8081/api/health"
        >
          WS {ok === null ? '…' : ok ? 'activo' : 'sin respuesta'}
        </span>
      </header>

      {error && <p className="aviso aviso--error">{error}</p>}
      {ok === false && (
        <p className="muted">
          El web service no responde en el puerto 8081. La bandeja de Flowable sigue
          funcionando igual.
        </p>
      )}
      {ok && leads.length === 0 && (
        <p className="vacio">Aún no hay leads en el WS.</p>
      )}

      {leads.length > 0 && (
        <div className="tabla-wrap">
          <table className="tabla">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Canal</th>
                <th>Interés</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {leads.map((l) => (
                <tr key={l.id}>
                  <td>{l.id}</td>
                  <td>{l.nombre}</td>
                  <td>{etiquetaCanal(l.canal ?? '')}</td>
                  <td>{l.interes ?? '—'}</td>
                  <td>{l.estado ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
