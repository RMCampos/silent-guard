import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from "@tailwindcss/vite";
import fs from 'fs';

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: 5173,
    https: {
      key: fs.readFileSync('./certs/localhost.key'),
      cert: fs.readFileSync('./certs/localhost.crt'),
    },
    allowedHosts: ['silentguard-local.ricardocampos.dev.br']
  },
  plugins: [
    {
      name: 'build-html',
      apply: 'build',
      transformIndexHtml: (html) => {
        return {
          html,
          tags: [
            {
              tag: 'script',
              attrs: {
                src: '/env.js'
              },
              injectTo: 'head'
            }
          ]
        };
      }
    },
    tailwindcss(),
    react()
  ],
});
