import { useState } from 'react'
import NuevoLead from './components/NuevoLead'
import Campanas from './components/Campanas'
import Bandeja from './components/Bandeja'
import ListaLeads from './components/ListaLeads'

type Vista = 'anotar' | 'campanas' | 'bandeja' | 'lista'

const FLUJO = [
  { id: 'registrar', label: 'Registrar' },
  { id: 'seguimiento', label: 'Seguimiento' },
  { id: 'campana', label: 'Campaña' },
  { id: 'contacto', label: 'Contacto / pedido' },
] as const

export default function App() {
  const [vista, setVista] = useState<Vista>('anotar')
  const [refresco, setRefresco] = useState(0)

  function trasRegistro() {
    setRefresco((n) => n + 1)
    setVista('bandeja')
  }

  function trasCampana() {
    setRefresco((n) => n + 1)
    setVista('bandeja')
  }

  const pasoActivo =
    vista === 'anotar' ? 0 : vista === 'bandeja' ? 1 : vista === 'campanas' ? 2 : 3

  return (
    <div className="app">
      <header className="top">
        <div className="brand">
          <span className="brand__mark" aria-hidden="true">
            P
          </span>
          <div>
            <p className="brand__name">Peluditos Pet Chile</p>
            <p className="brand__tag">
              Leads y oportunidades · peluditosLead / peluditosOportunidad
            </p>
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
            className={vista === 'campanas' ? 'nav__btn nav__btn--on' : 'nav__btn'}
            onClick={() => {
              setVista('campanas')
              setRefresco((n) => n + 1)
            }}
          >
            Campañas
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
          {vista === 'anotar' &&
            'Anota la consulta, clasifícala por servicio y haz el seguimiento sin perder el hilo entre canales.'}
          {vista === 'campanas' &&
            'Inicia una campaña por fecha relevante; el worker genera oportunidades y la bandeja muestra a quién contactar.'}
          {vista === 'bandeja' &&
            'Completa tareas de leads (seguimiento, motivo, pedido) y de campañas (contactar, pedido).'}
          {vista === 'lista' &&
            'Consulta en el web service los leads ya persistidos por el worker.'}
        </p>

        {vista === 'anotar' && <NuevoLead onRegistrado={trasRegistro} />}
        {vista === 'campanas' && (
          <Campanas onIniciada={trasCampana} refrescarToken={refresco} />
        )}
        {vista === 'bandeja' && <Bandeja refrescarToken={refresco} />}
        {vista === 'lista' && <ListaLeads refrescarToken={refresco} />}
      </main>

      <footer className="pie">
        <span className="pie__paso">
          {vista === 'anotar' && 'Paso: registrar y clasificar lead'}
          {vista === 'campanas' && 'Paso: iniciar campaña de oportunidades'}
          {vista === 'bandeja' && 'Paso: bandeja Flowable (ambos procesos)'}
          {vista === 'lista' && 'Paso: lista de leads del web service'}
        </span>
        <span>localhost:3000</span>
      </footer>
    </div>
  )
}
