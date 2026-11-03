import { useState } from 'react'
import NuevoLead from './components/NuevoLead'
import Bandeja from './components/Bandeja'
import ListaLeads from './components/ListaLeads'

type Vista = 'anotar' | 'bandeja' | 'lista'

const FLUJO = [
  { id: 'registrar', label: 'Registrar' },
  { id: 'clasificar', label: 'Clasificar' },
  { id: 'seguimiento', label: 'Seguimiento' },
  { id: 'pedido', label: 'Pedido / cierre' },
] as const

export default function App() {
  const [vista, setVista] = useState<Vista>('anotar')
  const [refresco, setRefresco] = useState(0)

  function trasRegistro() {
    setRefresco((n) => n + 1)
    setVista('bandeja')
  }

  const pasoActivo = vista === 'anotar' ? 0 : vista === 'bandeja' ? 2 : 1

  return (
    <div className="app">
      <header className="top">
        <div className="brand">
          <span className="brand__mark" aria-hidden="true">
            P
          </span>
          <div>
            <p className="brand__name">Peluditos Pet Chile</p>
            <p className="brand__tag">Gestión de interesados · proceso peluditosLead</p>
          </div>
        </div>
        <nav className="nav" aria-label="Secciones">
          <button
            type="button"
            className={vista === 'anotar' ? 'nav__btn nav__btn--on' : 'nav__btn'}
            onClick={() => setVista('anotar')}
          >
            Anotar
          </button>
          <button
            type="button"
            className={vista === 'bandeja' ? 'nav__btn nav__btn--on' : 'nav__btn'}
            onClick={() => {
              setVista('bandeja')
              setRefresco((n) => n + 1)
            }}
          >
            Bandeja
          </button>
          <button
            type="button"
            className={vista === 'lista' ? 'nav__btn nav__btn--on' : 'nav__btn'}
            onClick={() => {
              setVista('lista')
              setRefresco((n) => n + 1)
            }}
          >
            Lista
          </button>
        </nav>
      </header>

      <main className="main">
        <section className="flujo" aria-label="Recorrido del proceso">
          {FLUJO.map((paso, i) => (
            <div key={paso.id} className="flujo__item">
              <div
                className={
                  i === pasoActivo
                    ? 'flujo__nodo flujo__nodo--activo'
                    : 'flujo__nodo'
                }
              >
                <span className="flujo__num">{i + 1}</span>
                <span className="flujo__label">{paso.label}</span>
              </div>
              {i < FLUJO.length - 1 && (
                <span className="flujo__flecha" aria-hidden="true" />
              )}
            </div>
          ))}
        </section>

        <p className="lede">
          Una sola lista: anotas la consulta, la clasificas por servicio (repostería, baños o
          repuestos) y haces el seguimiento sin perder el hilo entre canales.
        </p>

        {vista === 'anotar' && <NuevoLead onRegistrado={trasRegistro} />}
        {vista === 'bandeja' && <Bandeja refrescarToken={refresco} />}
        {vista === 'lista' && <ListaLeads refrescarToken={refresco} />}
      </main>

      <footer className="pie">
        <span className="pie__paso">
          {vista === 'anotar' && 'Paso: registrar y clasificar'}
          {vista === 'bandeja' && 'Paso: bandeja de tareas Flowable'}
          {vista === 'lista' && 'Paso: lista del web service'}
        </span>
        <span>localhost:3000</span>
      </footer>
    </div>
  )
}
