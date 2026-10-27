import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // Puerto fijo: el CORS del backend solo acepta http://localhost:5173
  server: {
    port: 5173,
    strictPort: true,
  },
})
