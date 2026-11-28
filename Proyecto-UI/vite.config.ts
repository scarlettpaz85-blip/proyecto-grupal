import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const flowableTarget = env.VITE_FLOWABLE_TARGET || 'http://localhost:8080'
  const wsTarget = env.VITE_WS_TARGET || 'http://localhost:8081'

  return {
    plugins: [react()],
    server: {
      port: 3000,
      strictPort: true,
      proxy: {
        '/flowable-rest': {
          target: flowableTarget,
          changeOrigin: true,
        },
        '/api': {
          target: wsTarget,
          changeOrigin: true,
        },
      },
    },
    preview: {
      port: 3000,
      strictPort: true,
    },
  }
})
