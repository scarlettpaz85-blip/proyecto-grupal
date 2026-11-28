/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_FLOWABLE_TARGET?: string
  readonly VITE_WS_TARGET?: string
  readonly VITE_API_BASE_URL?: string
  readonly VITE_FLOWABLE_BASE_URL?: string
  readonly VITE_FLOWABLE_USER?: string
  readonly VITE_FLOWABLE_PASSWORD?: string
  readonly VITE_UI_PUBLIC_ORIGIN?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
