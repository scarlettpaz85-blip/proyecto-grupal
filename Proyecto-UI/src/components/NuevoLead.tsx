import { FormEvent, useState } from 'react'
import {
  Canal,
  LeadDatos,
  Servicio,
  registrarYClasificarLead,
} from '../api/flowable'

const CANALES: { value: Canal; label: string }[] = [
  { value: 'instagram', label: 'Instagram' },
  { value: 'whatsapp', label: 'WhatsApp' },
  { value: 'facebook', label: 'Facebook' },
  { value: 'tiktok', label: 'TikTok' },
  { value: 'web', label: 'Tienda web' },
  { value: 'telefono', label: 'Teléfono' },
]

const SERVICIOS: { value: Servicio; label: string }[] = [
  { value: 'reposteria', label: 'Repostería para mascotas' },
  { value: 'banos', label: 'Baños para perros' },
  { value: 'repuestos', label: 'Repuestos de baños ecológicos' },
]

interface Props {
  onRegistrado: () => void
}

const vacio: LeadDatos = {
  nombre: '',
  telefono: '',
  correo: '',
  canal: 'instagram',
  interes: '',
  servicioRequerido: 'reposteria',
}

export default function NuevoLead({ onRegistrado }: Props) {
  const [form, setForm] = useState<LeadDatos>(vacio)
  const [enviando, setEnviando] = useState(false)
  const [mensaje, setMensaje] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  function setCampo<K extends keyof LeadDatos>(campo: K, valor: LeadDatos[K]) {
    setForm((prev) => ({ ...prev, [campo]: valor }))
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setMensaje(null)

    if (!form.nombre.trim()) {
      setError('El nombre es obligatorio.')
      return
    }
    if (!form.interes.trim()) {
      setError('Indica qué consulta o producto le interesa.')
      return
    }

    setEnviando(true)
    try {
      await registrarYClasificarLead({
        ...form,
        nombre: form.nombre.trim(),
        telefono: form.telefono.trim(),
        correo: form.correo.trim(),
        interes: form.interes.trim(),
      })
      setMensaje(
        'Consulta anotada y clasificada. El sistema la envió al proceso; en la bandeja verás el seguimiento cuando corresponda.',
      )
      setForm(vacio)
      onRegistrado()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo registrar el lead.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section className="panel" aria-labelledby="titulo-nuevo">
      <header className="panel__header">
        <h2 id="titulo-nuevo">Anotar interesado</h2>
        <p>
          Cada consulta queda en una sola lista: quién es, por dónde llegó y qué servicio
          busca (repostería, baños o repuestos).
        </p>
      </header>

      <form className="form" onSubmit={onSubmit}>
        <div className="form__grid">
          <label>
            Nombre
            <input
              required
              value={form.nombre}
              onChange={(e) => setCampo('nombre', e.target.value)}
              placeholder="Ej. María González"
              autoComplete="name"
            />
          </label>

          <label>
            Teléfono
            <input
              value={form.telefono}
              onChange={(e) => setCampo('telefono', e.target.value)}
              placeholder="+569..."
              autoComplete="tel"
            />
          </label>

          <label>
            Correo
            <input
              type="email"
              value={form.correo}
              onChange={(e) => setCampo('correo', e.target.value)}
              placeholder="correo@ejemplo.cl"
              autoComplete="email"
            />
          </label>

          <label>
            Canal de llegada
            <select
              value={form.canal}
              onChange={(e) => setCampo('canal', e.target.value as Canal)}
            >
              {CANALES.map((c) => (
                <option key={c.value} value={c.value}>
                  {c.label}
                </option>
              ))}
            </select>
          </label>

          <label className="form__full">
            ¿Qué consulta?
            <input
              required
              value={form.interes}
              onChange={(e) => setCampo('interes', e.target.value)}
              placeholder="Ej. Torta mediana para cumpleaños del perro"
            />
          </label>

          <label className="form__full">
            Servicio (ruta)
            <select
              value={form.servicioRequerido}
              onChange={(e) =>
                setCampo('servicioRequerido', e.target.value as Servicio)
              }
            >
              {SERVICIOS.map((s) => (
                <option key={s.value} value={s.value}>
                  {s.label}
                </option>
              ))}
            </select>
          </label>
        </div>

        {error && <p className="aviso aviso--error" role="alert">{error}</p>}
        {mensaje && <p className="aviso aviso--ok" role="status">{mensaje}</p>}

        <div className="form__actions">
          <button type="submit" className="btn btn--primary" disabled={enviando}>
            {enviando ? 'Guardando…' : 'Anotar y clasificar'}
          </button>
        </div>
      </form>
    </section>
  )
}
